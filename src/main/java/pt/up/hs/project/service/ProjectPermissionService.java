package pt.up.hs.project.service;

import pt.up.hs.project.service.dto.ProjectPermissionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.ProjectPermission}.
 */
public interface ProjectPermissionService {

    /**
     * Save a projectPermission.
     *
     * @param projectPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectPermissionDTO save(ProjectPermissionDTO projectPermissionDTO);

    /**
     * Get all the projectPermissions.
     *
     * @return the list of entities.
     */
    List<ProjectPermissionDTO> findAll();

    /**
     * Get the "id" projectPermission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectPermissionDTO> findOne(Long id);

    /**
     * Delete the "id" projectPermission.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
