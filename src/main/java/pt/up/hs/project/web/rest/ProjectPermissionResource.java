package pt.up.hs.project.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.web.rest.errors.BadRequestException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.ProjectPermission}.
 */
@RestController
@RequestMapping("/api")
public class ProjectPermissionResource {

    private final Logger log = LoggerFactory.getLogger(ProjectPermissionResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectPermissionService projectPermissionService;

    public ProjectPermissionResource(ProjectPermissionService projectPermissionService) {
        this.projectPermissionService = projectPermissionService;
    }

    /**
     * {@code POST  /projects/{projectId}/permissions} : Create a new permissions.
     *
     * @param bulkProjectPermissionDTO {@link BulkProjectPermissionDTO} the permissions to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectPermissionDTO, or with status {@code 400 (Bad Request)} if the projectPermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projects/{projectId}/permissions/{user:^[_.@A-Za-z0-9-]*$}")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'ADMIN')"
    )
    public ResponseEntity<BulkProjectPermissionDTO> createProjectPermissions(
        @PathVariable("projectId") Long projectId,
        @PathVariable("user") String user,
        @Valid @RequestBody BulkProjectPermissionDTO bulkProjectPermissionDTO
    ) throws URISyntaxException {
        log.debug("REST request to create project permissions {}", bulkProjectPermissionDTO);
        if (projectPermissionService.isOwner(bulkProjectPermissionDTO.getUser(), projectId)) {
            throw new BadRequestException(
                "Cannot modify owner's permissions directly",
                EntityNames.PERMISSION,
                ErrorKeys.ERR_UNMODIFIABLE_OWNER_PERMISSION
            );
        }
        BulkProjectPermissionDTO result = projectPermissionService
            .create(projectId, user, bulkProjectPermissionDTO);
        return ResponseEntity
            .created(
                new URI("/api/permissions/project/" + result.getProjectId() + "/permissions/" + user)
            )
            .body(result);
    }

    /**
     * {@code PUT  /projects/{projectId}/permissions/:user} : Replaces existing permissions.
     *
     * @param bulkProjectPermissionDTO the new permissions to replace.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the projectPermissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectPermissionDTO couldn't be updated.
     */
    @PutMapping("/projects/{projectId}/permissions/{user:^[_.@A-Za-z0-9-]*$}")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'ADMIN')"
    )
    public ResponseEntity<BulkProjectPermissionDTO> updateProjectPermission(
        @PathVariable("projectId") Long projectId,
        @PathVariable("user") String user,
        @Valid @RequestBody BulkProjectPermissionDTO bulkProjectPermissionDTO
    ) {
        log.debug(
            "REST request to replace project permissions {}",
            bulkProjectPermissionDTO
        );
        if (projectPermissionService.isOwner(user, projectId)) {
            throw new BadRequestException(
                "Cannot modify owner's permissions directly",
                EntityNames.PERMISSION,
                ErrorKeys.ERR_UNMODIFIABLE_OWNER_PERMISSION
            );
        }
        BulkProjectPermissionDTO result = projectPermissionService
            .replace(projectId, user, bulkProjectPermissionDTO);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /permissions} : get all the permissions of project.
     *
     * @param projectId the ID of the project.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
     *                                    list of permissions of project by user in body.
     */
    @GetMapping("/projects/{projectId}/permissions")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')"
    )
    public ResponseEntity<List<BulkProjectPermissionDTO>> getAllProjectPermissions(
        @PathVariable("projectId") Long projectId
    ) {
        log.debug("REST request to get permissions for project {}", projectId);
        List<BulkProjectPermissionDTO> result = projectPermissionService.findAll(projectId);
        return ResponseEntity.ok().body(result);
    }
    /**
     * {@code GET  /permissions/:user} : get all the permissions of user.
     *
     * @param user the user login.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
     *                                    list of permissions of user in body.
     */
    @GetMapping("/permissions/{user:^[_.@A-Za-z0-9-]*$}")
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN') or (" +
            "hasAnyAuthority('ROLE_USER', 'ROLE_GUEST', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "principal == #user" +
        ")"
    )
    public ResponseEntity<List<BulkProjectPermissionDTO>> getUserPermissions(
        @PathVariable("user") String user
    ) {
        log.debug("REST request to get permissions for user {}", user);
        List<BulkProjectPermissionDTO> result = projectPermissionService.findAll(user);
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code GET  /permissions/:user} : get all users connected to user by a.
     *
     * @param user the user login.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
     *                                    list of permissions of user in body.
     */
    @GetMapping("/permissions/{user:^[_.@A-Za-z0-9-]*$}/connections")
    @PreAuthorize(
        "hasAnyAuthority('ROLE_ADMIN') or (" +
            "hasAnyAuthority('ROLE_USER', 'ROLE_GUEST', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "principal == #user" +
        ")"
    )
    public ResponseEntity<Set<String>> getUserConnections (
        @PathVariable("user") String user
    ) {
        log.debug("REST request to get connections of user {}", user);
        Set<String> connections = new HashSet<>();
        List<BulkProjectPermissionDTO> userPermissions = projectPermissionService.findAll(user);
        for (BulkProjectPermissionDTO permissionDTO: userPermissions) {
            Long projectId = permissionDTO.getProjectId();
            List<BulkProjectPermissionDTO> projectPermissions = projectPermissionService.findAll(projectId);
            connections.addAll(projectPermissions.parallelStream()
                .map(BulkProjectPermissionDTO::getUser)
                .filter(login -> !login.equalsIgnoreCase(user))
                .collect(Collectors.toList()));
        }
        return ResponseEntity.ok().body(connections);
    }

    /**
     * {@code GET  /projects/{projectId}/permissions/:user} : get all the
     * permissions of user in project.
     *
     * @param projectId the ID of the project.
     * @param user the user login.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
     *                                    list of permissions of user in project
     *                                    in body.
     */
    @GetMapping("/projects/{projectId}/permissions/{user:^[_.@A-Za-z0-9-]*$}")
    @PreAuthorize(
        "(" +
            "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'ADMIN')" +
            ") or (hasAnyRole('ROLE_USER', 'ROLE_GUEST', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and principal == #user)")
    public ResponseEntity<BulkProjectPermissionDTO> getUserPermissionsInProject(
        @PathVariable("projectId") Long projectId,
        @PathVariable("user") String user
    ) {
        log.debug("REST request to get permissions of user {} in project {}", user, projectId);
        BulkProjectPermissionDTO entityList = projectPermissionService.findAll(projectId, user);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code DELETE  /projects/{projectId}/permissions/:user} : delete all the
     * permissions of user in project.
     *
     * @param projectId the ID of the project.
     * @param user the user login.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and
     * empty body.
     */
    @DeleteMapping("/projects/{projectId}/permissions/{user:^[_.@A-Za-z0-9-]*$}")
    @PreAuthorize(
        "(" +
            "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'ADMIN')" +
        ") or (hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and principal == #user)")
    public ResponseEntity<Void> deleteUserPermissionsInProject(
        @PathVariable("user") String user,
        @PathVariable("projectId") Long projectId
    ) {
        log.debug("REST request to delete permissions of user {} in project {}", user, projectId);
        if (projectPermissionService.isOwner(user, projectId)) {
            throw new BadRequestException(
                "Cannot modify owner's permissions directly",
                EntityNames.PERMISSION,
                ErrorKeys.ERR_UNMODIFIABLE_OWNER_PERMISSION
            );
        }
        projectPermissionService.deleteAll(projectId, user);
        return ResponseEntity.noContent().build();
    }
}
