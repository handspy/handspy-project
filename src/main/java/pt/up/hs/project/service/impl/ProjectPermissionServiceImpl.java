package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.repository.ProjectPermissionRepository;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectDTO;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;
import pt.up.hs.project.service.mapper.ProjectPermissionMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ProjectPermission}.
 */
@Service
@Transactional
public class ProjectPermissionServiceImpl implements ProjectPermissionService {

    private final Logger log = LoggerFactory.getLogger(ProjectPermissionServiceImpl.class);

    private final ProjectPermissionRepository projectPermissionRepository;
    private final ProjectPermissionMapper projectPermissionMapper;

    private ProjectService projectService;

    public ProjectPermissionServiceImpl(
        ProjectPermissionRepository projectPermissionRepository,
        ProjectPermissionMapper projectPermissionMapper
    ) {
        this.projectPermissionRepository = projectPermissionRepository;
        this.projectPermissionMapper = projectPermissionMapper;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Create permissions of user in a project.
     *
     * @param projectId ID of the project to manage.
     * @param user User login to manage.
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to save.
     * @return {@link BulkProjectPermissionDTO} permissions of user in a project.
     */
    @Override
    public BulkProjectPermissionDTO create(
        Long projectId, String user,
        BulkProjectPermissionDTO bulkProjectPermissionDTO
    ) {
        log.debug(
            "Request to create permissions {} for user {} in project {}",
            bulkProjectPermissionDTO.getPermissions(), user, projectId
        );
        bulkProjectPermissionDTO.setProjectId(projectId);
        bulkProjectPermissionDTO.setUser(user);
        List<ProjectPermissionDTO> projectPermissionDTOs = projectPermissionRepository
            .saveAll(
                bulkProjectPermissionDTOToProjectPermissionDTOs(bulkProjectPermissionDTO)
                    .parallelStream()
                    .map(projectPermissionMapper::toEntity)
                    .collect(Collectors.toList())
            )
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionsToBulkProjectPermissionDTO(
            projectId, user, projectPermissionDTOs
        );
    }

    /**
     * Save permissions of user in a project, replacing current permissions.
     *
     * @param projectId ID of the project to manage.
     * @param user User login to manage.
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to save.
     * @return {@link BulkProjectPermissionDTO} permissions of user in a project.
     */
    @Override
    public BulkProjectPermissionDTO replace(Long projectId, String user, BulkProjectPermissionDTO bulkProjectPermissionDTO) {
        log.debug(
            "Request to replace permissions of user {} in project {} by {}",
            user, projectId, bulkProjectPermissionDTO.getPermissions()
        );
        deleteAll(projectId, user);
        return create(projectId, user, bulkProjectPermissionDTO);
    }

    /**
     * Find all permissions of user.
     *
     * @param user User login to manage.
     * @return {@link List} list of permissions of user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BulkProjectPermissionDTO> findAll(String user) {
        log.debug("Request to find all permissions of user {}", user);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByIdUser(user)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionDTOs.parallelStream()
            .collect(Collectors.groupingBy(ProjectPermissionDTO::getProjectId))
            .entrySet().parallelStream()
            .map(e -> projectPermissionsToBulkProjectPermissionDTO(e.getKey(), user, e.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Find all permissions of project.
     *
     * @param projectId {@link Long} ID of the project.
     * @return {@link List} list of permissions of users in project.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BulkProjectPermissionDTO> findAll(Long projectId) {
        log.debug("Request to find all permissions in project {}", projectId);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByIdProjectId(projectId)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionDTOs.parallelStream()
            .collect(Collectors.groupingBy(ProjectPermissionDTO::getUser))
            .entrySet().parallelStream()
            .map(e -> projectPermissionsToBulkProjectPermissionDTO(projectId, e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Find all permissions of user in project.
     *
     * @param user {@link String} user login.
     * @param projectId {@link Long} ID of the project.
     * @return {@link ProjectPermissionDTO} permissions of user in a project.
     */
    @Override
    @Transactional(readOnly = true)
    public BulkProjectPermissionDTO findAll(Long projectId, String user) {
        log.debug("Request to find all permissions of user {} in project {}", user, projectId);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByIdUserAndIdProjectId(user, projectId)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionsToBulkProjectPermissionDTO(projectId, user, projectPermissionDTOs);
    }

    /**
     * Delete permissions of user in a project.
     *
     * @param projectId ID of the project to manage.
     * @param user User login to manage.
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to delete.
     */
    @Override
    public void delete(Long projectId, String user, BulkProjectPermissionDTO bulkProjectPermissionDTO) {
        log.debug("Request to delete permissions {}", bulkProjectPermissionDTO);
        projectPermissionRepository.deleteAllByIdUserAndIdProjectIdAndIdPermissionNameIn(
            user, projectId, bulkProjectPermissionDTO.getPermissions()
        );
    }

    /**
     * Delete permissions of user in a project.
     *
     * @param projectId ID of the project to manage.
     * @param user User login to manage.
     */
    @Override
    public void deleteAll(Long projectId, String user) {
        log.debug("Request to delete permissions of user {} in project {}", user, projectId);
        projectPermissionRepository.deleteAllByIdUserAndIdProjectId(user, projectId);
    }

    /**
     * Delete permissions of project.
     *
     * @param projectId ID of the project to manage.
     */
    @Override
    public void deleteAll(Long projectId) {
        log.debug("Request to delete permissions of project {}", projectId);
        projectPermissionRepository.deleteAllByIdProjectId(projectId);
    }

    /**
     * Check if user is owner of project.
     *
     * @param user the user login to check.
     * @param projectId the ID of the project.
     * @return {@code true} if user owns project, {@code false} otherwise.
     */
    @Override
    public boolean isOwner(String user, Long projectId) {
        Optional<ProjectDTO> project = projectService.findOne(projectId);
        return project.isPresent() && Objects.equals(project.get().getOwner(), user);
    }

    /**
     * Convert {@link BulkProjectPermissionDTO} to list of {@link ProjectPermissionDTO}.
     *
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} project permissions.
     * @return list of {@link ProjectPermissionDTO}.
     */
    private List<ProjectPermissionDTO> bulkProjectPermissionDTOToProjectPermissionDTOs(
        BulkProjectPermissionDTO bulkProjectPermissionDTO
    ) {
        return bulkProjectPermissionDTO.getPermissions()
            .parallelStream()
            .map(permission -> {
                ProjectPermissionDTO projectPermissionDTO = new ProjectPermissionDTO();
                projectPermissionDTO.setUser(bulkProjectPermissionDTO.getUser());
                projectPermissionDTO.setProjectId(bulkProjectPermissionDTO.getProjectId());
                projectPermissionDTO.setPermissionName(permission);
                return projectPermissionDTO;
            })
            .collect(Collectors.toList());
    }

    /**
     * Convert list of {@link ProjectPermissionDTO} to {@link BulkProjectPermissionDTO}.
     *
     * @param projectId ID of the project.
     * @param user User login.
     * @param projectPermissionDTOs list of {@link ProjectPermissionDTO}.
     * @return {@link BulkProjectPermissionDTO} result.
     */
    private BulkProjectPermissionDTO projectPermissionsToBulkProjectPermissionDTO(
        Long projectId, String user,
        List<ProjectPermissionDTO> projectPermissionDTOs
    ) {
        BulkProjectPermissionDTO bulkProjectPermissionDTO = new BulkProjectPermissionDTO();
        bulkProjectPermissionDTO.setUser(user);
        bulkProjectPermissionDTO.setProjectId(projectId);
        bulkProjectPermissionDTO.setPermissions(
            projectPermissionDTOs.parallelStream()
                .map(ProjectPermissionDTO::getPermissionName)
                .collect(Collectors.toList())
        );
        return bulkProjectPermissionDTO;
    }
}
