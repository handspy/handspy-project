package pt.up.hs.project.web.rest;

import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.web.rest.errors.BadRequestAlertException;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectPermissionCriteria;
import pt.up.hs.project.service.ProjectPermissionQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.ProjectPermission}.
 */
@RestController
@RequestMapping("/api")
public class ProjectPermissionResource {

    private final Logger log = LoggerFactory.getLogger(ProjectPermissionResource.class);

    private static final String ENTITY_NAME = "projectProjectPermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectPermissionService projectPermissionService;

    private final ProjectPermissionQueryService projectPermissionQueryService;

    public ProjectPermissionResource(ProjectPermissionService projectPermissionService, ProjectPermissionQueryService projectPermissionQueryService) {
        this.projectPermissionService = projectPermissionService;
        this.projectPermissionQueryService = projectPermissionQueryService;
    }

    /**
     * {@code POST  /project-permissions} : Create a new projectPermission.
     *
     * @param projectPermissionDTO the projectPermissionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectPermissionDTO, or with status {@code 400 (Bad Request)} if the projectPermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/project-permissions")
    public ResponseEntity<ProjectPermissionDTO> createProjectPermission(@Valid @RequestBody ProjectPermissionDTO projectPermissionDTO) throws URISyntaxException {
        log.debug("REST request to save ProjectPermission : {}", projectPermissionDTO);
        if (projectPermissionDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectPermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectPermissionDTO result = projectPermissionService.save(projectPermissionDTO);
        return ResponseEntity.created(new URI("/api/project-permissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /project-permissions} : Updates an existing projectPermission.
     *
     * @param projectPermissionDTO the projectPermissionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectPermissionDTO,
     * or with status {@code 400 (Bad Request)} if the projectPermissionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectPermissionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/project-permissions")
    public ResponseEntity<ProjectPermissionDTO> updateProjectPermission(@Valid @RequestBody ProjectPermissionDTO projectPermissionDTO) throws URISyntaxException {
        log.debug("REST request to update ProjectPermission : {}", projectPermissionDTO);
        if (projectPermissionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProjectPermissionDTO result = projectPermissionService.save(projectPermissionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectPermissionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /project-permissions} : get all the projectPermissions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectPermissions in body.
     */
    @GetMapping("/project-permissions")
    public ResponseEntity<List<ProjectPermissionDTO>> getAllProjectPermissions(ProjectPermissionCriteria criteria) {
        log.debug("REST request to get ProjectPermissions by criteria: {}", criteria);
        List<ProjectPermissionDTO> entityList = projectPermissionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /project-permissions/count} : count all the projectPermissions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/project-permissions/count")
    public ResponseEntity<Long> countProjectPermissions(ProjectPermissionCriteria criteria) {
        log.debug("REST request to count ProjectPermissions by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectPermissionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /project-permissions/:id} : get the "id" projectPermission.
     *
     * @param id the id of the projectPermissionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectPermissionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project-permissions/{id}")
    public ResponseEntity<ProjectPermissionDTO> getProjectPermission(@PathVariable Long id) {
        log.debug("REST request to get ProjectPermission : {}", id);
        Optional<ProjectPermissionDTO> projectPermissionDTO = projectPermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectPermissionDTO);
    }

    /**
     * {@code DELETE  /project-permissions/:id} : delete the "id" projectPermission.
     *
     * @param id the id of the projectPermissionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/project-permissions/{id}")
    public ResponseEntity<Void> deleteProjectPermission(@PathVariable Long id) {
        log.debug("REST request to delete ProjectPermission : {}", id);
        projectPermissionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
