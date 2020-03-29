package pt.up.hs.project.service;

import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;

import java.util.List;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.ProjectPermission}.
 *
 * @author José Carlos Paiva
 */
public interface ProjectPermissionService {

    /**
     * Save permissions of user in a project, replacing current permissions.
     *
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to save.
     * @return {@link BulkProjectPermissionDTO} permissions of user in a project.
     */
    BulkProjectPermissionDTO replaceAll(BulkProjectPermissionDTO bulkProjectPermissionDTO);

    /**
     * Create permissions of user in a project.
     *
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to save.
     * @return {@link BulkProjectPermissionDTO} permissions of user in a project.
     */
    BulkProjectPermissionDTO create(BulkProjectPermissionDTO bulkProjectPermissionDTO);

    /**
     * Find all permissions of user.
     *
     * @param user {@link String} user login.
     * @return {@link List} list of permissions of user.
     */
    List<BulkProjectPermissionDTO> findAll(String user);

    /**
     * Find all permissions of project.
     *
     * @param projectId {@link Long} ID of the project.
     * @return {@link List} list of permissions of users in project.
     */
    List<BulkProjectPermissionDTO> findAll(Long projectId);

    /**
     * Find all permissions of user in project.
     *
     * @param user {@link String} user login.
     * @param projectId {@link Long} ID of the project.
     * @return {@link ProjectPermissionDTO} permissions of user in a project.
     */
    BulkProjectPermissionDTO findAll(String user, Long projectId);

    /**
     * Delete permissions of user in a project.
     *
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} permissions to delete.
     */
    void delete(BulkProjectPermissionDTO bulkProjectPermissionDTO);

    /**
     * Delete permissions of user in a project.
     *
     * @param user {@link String} user login.
     * @param projectId {@link Long} ID of the project
     */
    void deleteAll(String user, Long projectId);

    /**
     * Delete permissions of project.
     *
     * @param projectId {@link Long} ID of the project
     */
    void deleteAll(Long projectId);

    /**
     * Check if user is owner of project.
     *
     * @param user the user login to check.
     * @param projectId the ID of the project.
     * @return {@code true} if user owns project, {@code false} otherwise.
     */
    boolean isOwner(String user, Long projectId);
}
