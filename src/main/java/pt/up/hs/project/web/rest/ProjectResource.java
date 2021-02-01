package pt.up.hs.project.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.ProjectDTO;
import pt.up.hs.project.web.rest.errors.BadRequestException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.Project}.
 */
@RestController
@RequestMapping("/api")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDTO, or with status {@code 400 (Bad Request)} if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projects")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN')") // TODO remove user from here
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new BadRequestException("A new project cannot already have an ID", EntityNames.PROJECT, ErrorKeys.ERR_ID_EXISTS);
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.created(new URI("/api/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, EntityNames.PROJECT, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /projects} : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     */
    @PutMapping("/projects")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#projectDTO.id, 'pt.up.hs.project.domain.Project', 'ADMIN')"
    )
    public ResponseEntity<ProjectDTO> updateProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.debug("REST request to update Project : {}", projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestException("Invalid id", EntityNames.PROJECT, ErrorKeys.ERR_ID_NULL);
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, EntityNames.PROJECT, projectDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GetMapping("/projects")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        log.debug("REST request to get projects");
        List<ProjectDTO> projects = projectService.findAll();
        /*HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);*/
        return ResponseEntity.ok().body(projects);
    }

    /**
     * {@code GET  /projects/count} : count all the projects.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/projects/count")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> countProjects() {
        log.debug("REST request to count projects");
        return ResponseEntity.ok().body(projectService.count());
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/projects/{id}")
    @PreAuthorize(
        "hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#id, 'pt.up.hs.project.domain.Project', 'READ')"
    )
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get Project : {}", id);
        Optional<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/projects/{id}")
    @PreAuthorize(
        "hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
            "hasPermission(#id, 'pt.up.hs.project.domain.Project', 'ADMIN')"
    )
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        Optional<ProjectDTO> result = projectService.delete(id);
        return ResponseUtil.wrapOrNotFound(result,
            result.map(projectDTO -> HeaderUtil.createEntityUpdateAlert(applicationName, true, EntityNames.PROJECT, projectDTO.getId().toString())).orElse(null));
    }


}
