package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Status;
import pt.up.hs.project.cloner.ClonerJobLauncher;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.repository.ProjectRepository;
import pt.up.hs.project.security.PermissionsConstants;
import pt.up.hs.project.security.SecurityUtils;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectDTO;
import pt.up.hs.project.service.exceptions.ServiceException;
import pt.up.hs.project.service.mapper.ProjectMapper;

import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final ClonerJobLauncher clonerJobLauncher;

    private ProjectPermissionService projectPermissionService;

    public ProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ClonerJobLauncher clonerJobLauncher
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.clonerJobLauncher = clonerJobLauncher;
    }

    @Autowired
    public void setProjectPermissionService(ProjectPermissionService projectPermissionService) {
        this.projectPermissionService = projectPermissionService;
    }

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);

        if (project.getOwner() == null) {
            Optional<String> login = SecurityUtils.getCurrentUserLogin();
            if (!login.isPresent()) {
                throw new IllegalArgumentException("Owner not provided");
            }
            project.setOwner(login.get());
        }

        // if project exists, check if owner changed
        boolean changed = false;
        if (project.getId() != null) {
            Optional<ProjectDTO> oldProject = findOne(project.getId());
            if (oldProject.isPresent()) {
                // if project owner changed, remove his/her permissions
                if (!Objects.equals(oldProject.get().getOwner(), projectDTO.getOwner())) {
                    projectPermissionService.deleteAll(project.getId(), oldProject.get().getOwner());
                    changed = true;
                }
            }
        }

        project = projectRepository.saveAndFlush(project);

        // save project owner's permissions
        if (projectDTO.getId() == null || changed) {
            projectPermissionService.replace(
                project.getId(),
                project.getOwner(),
                new BulkProjectPermissionDTO(
                    project.getOwner(),
                    project.getId(),
                    Arrays.stream(PermissionsConstants.ALL).collect(Collectors.toList())
                )
            );
        }

        return projectMapper.toDto(project);
    }

    /**
     * Get all the projects.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findAll() {
        log.debug("Request to get all Projects");
        return projectRepository.findAll()
            .stream()
            .map(projectMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Count the projects.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Request to count Projects");
        return projectRepository.count();
    }

    /**
     * Get one project by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        return projectRepository.findById(id)
            .map(projectMapper::toDto);
    }

    /**
     * Delete the project by id.
     *
     * @param id the id of the entity.
     * @return the project.
     */
    @Override
    public Optional<ProjectDTO> delete(Long id) {
        log.debug("Request to delete Project : {}", id);

        Optional<Project> projectOptional = projectRepository.findById(id);

        return projectOptional.flatMap(project -> Optional.of(projectRepository.saveAndFlush(
            project.status(ProjectStatus.DISCARDED)
        )).map(projectMapper::toDto));
    }

    @Override
    public ProjectDTO copy(Long id, boolean copyPermissions, boolean move) {

        // find old previous DTO
        ProjectDTO oldProjectDTO = findOne(id).orElse(null);
        if (oldProjectDTO == null) {
            throw new ServiceException(Status.NOT_FOUND, EntityNames.PROJECT, ErrorKeys.ERR_NOT_FOUND, "Project does not exist");
        }

        // copy old previous DTO to new
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(oldProjectDTO.getName());
        projectDTO.setDescription(oldProjectDTO.getDescription());
        projectDTO.setStatus(oldProjectDTO.getStatus());
        projectDTO.setColor(oldProjectDTO.getColor());
        projectDTO.setOwner(SecurityUtils.getCurrentUserLogin().orElse(null));
        projectDTO = save(projectDTO);

        // run copy job
        clonerJobLauncher.run(id, projectDTO.getId(), copyPermissions, move);

        // if to move? delete
        if (move) {
            delete(id);
        }

        return projectDTO;
    }
}
