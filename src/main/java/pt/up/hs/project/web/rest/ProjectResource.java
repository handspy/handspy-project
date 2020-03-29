package pt.up.hs.project.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.security.SecurityUtils;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.web.rest.errors.BadRequestAlertException;
import pt.up.hs.project.service.dto.ProjectDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private static final String ENTITY_NAME = "projectProject";

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
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (projectDTO.getOwner() == null) {
            Optional<String> login = SecurityUtils.getCurrentUserLogin();
            if (!login.isPresent()) {
                throw new BadRequestAlertException("Owner not provided", ENTITY_NAME, "ownermissing");
            }
            projectDTO.setOwner(login.get());
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.created(new URI("/api/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /projects} : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projects")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#projectDTO.id, 'pt.up.hs.project.domain.Project', 'ADMIN')")
    public ResponseEntity<ProjectDTO> updateProject(@Valid @RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to update Project : {}", projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GetMapping("/projects")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "status", required = false) List<ProjectStatus> statuses,
        Pageable pageable
    ) {
        log.debug("REST request to get projects");
        Page<ProjectDTO> page = projectService.findAll(search, statuses, pageable);
        HttpHeaders headers = PaginationUtil
            .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /projects/count} : count all the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/projects/count")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> countProjects(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "status", required = false) List<ProjectStatus> statuses
    ) {
        log.debug("REST request to count projects");
        return ResponseEntity.ok().body(projectService.count(search, statuses));
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/projects/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#id, 'pt.up.hs.project.domain.Project', 'READ')")
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
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and hasPermission(#id, 'pt.up.hs.project.domain.Project', 'ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
