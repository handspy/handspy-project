package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.repository.ProjectRepository;
import pt.up.hs.project.security.PermissionsConstants;
import pt.up.hs.project.security.SecurityUtils;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectDTO;
import pt.up.hs.project.service.mapper.ProjectMapper;

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

    private ProjectPermissionService projectPermissionService;

    public ProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
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

        project = projectRepository.save(project);

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        return projectRepository.findAll(pageable)
            .map(projectMapper::toDto);
    }

    /**
     * Get all the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(String search, List<ProjectStatus> statuses, Pageable pageable) {
        log.debug("Request to get all Projects matching search {} and statuses {}", search, statuses);
        return projectRepository
            .findAllByStatusAndSearch(statuses, search, pageable)
            .map(projectMapper::toDto);
    }

    /**
     * Count the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public long count(String search, List<ProjectStatus> statuses) {
        log.debug("Request to count Projects matching search {} and statuses {}", search, statuses);
        return projectRepository.countByStatusAndSearch(statuses, search);
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
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);

        // delete all permissions of project
        projectPermissionService.deleteAll(id);

        projectRepository.deleteById(id);
    }
}
