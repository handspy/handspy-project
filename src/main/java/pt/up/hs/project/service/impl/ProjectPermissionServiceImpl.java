package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.domain.Project;
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

    @Override
    public BulkProjectPermissionDTO replaceAll(BulkProjectPermissionDTO bulkProjectPermissionDTO) {
        log.debug(
            "Request to replace permissions of user {} in project {} by {}",
            bulkProjectPermissionDTO.getUser(),
            bulkProjectPermissionDTO.getProjectId(),
            bulkProjectPermissionDTO.getPermissions()
        );
        deleteAll(bulkProjectPermissionDTO.getUser(), bulkProjectPermissionDTO.getProjectId());
        return create(bulkProjectPermissionDTO);
    }

    @Override
    public BulkProjectPermissionDTO create(BulkProjectPermissionDTO bulkProjectPermissionDTO) {
        log.debug(
            "Request to create permissions {} for user {} in project {}",
            bulkProjectPermissionDTO.getUser(),
            bulkProjectPermissionDTO.getProjectId(),
            bulkProjectPermissionDTO.getPermissions()
        );

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
            bulkProjectPermissionDTO.getUser(),
            bulkProjectPermissionDTO.getProjectId(),
            projectPermissionDTOs
        );
    }

    @Override
    public List<BulkProjectPermissionDTO> findAll(String user) {
        log.debug("Request to find all permissions of user {}", user);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByUser(user)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionDTOs.parallelStream()
            .collect(Collectors.groupingBy(ProjectPermissionDTO::getProjectId))
            .entrySet().parallelStream()
            .map(e -> projectPermissionsToBulkProjectPermissionDTO(user, e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public List<BulkProjectPermissionDTO> findAll(Long projectId) {
        log.debug("Request to find all permissions in project {}", projectId);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByProjectId(projectId)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionDTOs.parallelStream()
            .collect(Collectors.groupingBy(ProjectPermissionDTO::getUser))
            .entrySet().parallelStream()
            .map(e -> projectPermissionsToBulkProjectPermissionDTO(e.getKey(), projectId, e.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public BulkProjectPermissionDTO findAll(String user, Long projectId) {
        log.debug("Request to find all permissions of user {} in project {}", user, projectId);

        List<ProjectPermissionDTO> projectPermissionDTOs =
            projectPermissionRepository.findAllByUserAndProjectId(user, projectId)
                .parallelStream()
                .map(projectPermissionMapper::toDto)
                .collect(Collectors.toList());

        return projectPermissionsToBulkProjectPermissionDTO(user, projectId, projectPermissionDTOs);
    }

    @Override
    public void delete(BulkProjectPermissionDTO bulkProjectPermissionDTO) {
        log.debug("Request to delete permissions {}", bulkProjectPermissionDTO);
        projectPermissionRepository.deleteAllByUserAndProjectIdAndPermissionNameIn(
            bulkProjectPermissionDTO.getUser(),
            bulkProjectPermissionDTO.getProjectId(),
            bulkProjectPermissionDTO.getPermissions()
        );
    }

    @Override
    public void deleteAll(Long projectId) {
        log.debug("Request to delete permissions of project {}", projectId);
        projectPermissionRepository.deleteAllByProjectId(projectId);
    }

    @Override
    public void deleteAll(String user, Long projectId) {
        log.debug("Request to delete permissions of user {} in project {}", user, projectId);
        projectPermissionRepository.deleteAllByUserAndProjectId(user, projectId);
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
     * @param user User login.
     * @param projectId ID of the project.
     * @param projectPermissionDTOs list of {@link ProjectPermissionDTO}.
     * @return {@link BulkProjectPermissionDTO} result.
     */
    private BulkProjectPermissionDTO projectPermissionsToBulkProjectPermissionDTO(
        String user, Long projectId,
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
