package pt.up.hs.project.web.rest;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.TaskBasicDTO;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.web.rest.errors.BadRequestException;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link pt.up.hs.project.domain.Task}.
 */
@RestController
@RequestMapping("/api/projects/{projectId}")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskService taskService;

    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * {@code POST  /tasks} : Create a new task.
     *
     * @param projectId ID of the project to which the task will belong.
     * @param taskDTO   the taskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskDTO, or with status {@code 400 (Bad Request)} if the task has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tasks")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<TaskDTO> createTask(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        log.debug("REST request to save Task {} in project {}", taskDTO, projectId);
        if (taskDTO.getId() != null) {
            throw new BadRequestException("A new task cannot already have an ID", EntityNames.TASK, ErrorKeys.ERR_ID_EXISTS);
        }
        TaskDTO result = taskService.save(projectId, taskDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectId + "/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, EntityNames.TASK, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tasks} : Updates an existing task.
     *
     * @param projectId ID of the project to which the task belongs.
     * @param taskDTO   the taskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDTO,
     * or with status {@code 400 (Bad Request)} if the taskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskDTO couldn't be updated.
     */
    @PutMapping("/tasks")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<TaskDTO> updateTask(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody TaskDTO taskDTO
    ) {
        log.debug("REST request to update Task {} in project {}", taskDTO, projectId);
        if (taskDTO.getId() == null) {
            throw new BadRequestException("Invalid id", EntityNames.TASK, ErrorKeys.ERR_ID_NULL);
        }
        TaskDTO result = taskService.save(projectId, taskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, EntityNames.TASK, taskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param projectId ID of the project to which the tasks belong.
     * @param search    the search string.
     * @param labels    the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and" +
        " hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<List<TaskDTO>> getAllTasks(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "search", required = false, defaultValue = "") String search,
        @RequestParam(value = "labels", required = false) List<Long> labels,
        Pageable pageable
    ) {
        log.debug("REST request to get Tasks in project {}", projectId);
        Page<TaskDTO> page = taskService.findAll(projectId, search, labels, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/basic} : get basic info of all the tasks (for selectors).
     *
     * @param projectId ID of the project to which the tasks belong.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks' basic info in body.
     */
    @GetMapping("/tasks/basic")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and" +
        " hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<List<TaskBasicDTO>> getAllTasksBasic(
        @PathVariable("projectId") Long projectId
    ) {
        log.debug("REST request to get Tasks in project {}", projectId);
        List<TaskBasicDTO> taskDTOs = taskService.findAllBasic(projectId);
        return ResponseEntity.ok().body(taskDTOs);
    }

    /**
     * {@code GET  /tasks/count} : count all the tasks.
     *
     * @param projectId ID of the project to which the tasks belong.
     * @param search    the search string.
     * @param labels    the ids of the labels to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tasks/count")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<Long> countTasks(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "search", required = false, defaultValue = "") String search,
        @RequestParam(value = "labels", required = false) List<Long> labels
    ) {
        log.debug("REST request to count Tasks in project {}", projectId);
        return ResponseEntity.ok().body(taskService.count(projectId, search, labels));
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" task.
     *
     * @param projectId ID of the project to which the task belongs.
     * @param id        the id of the taskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskDTO,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST', 'ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'READ')")
    public ResponseEntity<TaskDTO> getTask(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to get Task {} in project {}", id, projectId);
        Optional<TaskDTO> taskDTO = taskService.findOne(projectId, id);
        return ResponseUtil.wrapOrNotFound(taskDTO);
    }

    /**
     * {@code POST /tasks/import} : import CSV sent in body.
     *
     * @param projectId ID of the project to which this task belongs.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @param is        {@link InputStream} input stream of the request body.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body the {@link BulkImportResultDTO}.
     */
    @PostMapping(value = "/tasks/import", consumes = "text/csv")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<BulkImportResultDTO<TaskDTO>> importSimple(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "sep", defaultValue = ",") String sep,
        @RequestParam(value = "array-sep", defaultValue = ";") String arraySep,
        @RequestParam(value = "use-header", defaultValue = "true") boolean useHeader,
        @RequestBody InputStream is
    ) {
        log.debug("REST request to import Tasks from CSV sent in body in project {}", projectId);
        return ResponseEntity.ok(taskService.importFromCsv(projectId, is, sep, arraySep, useHeader));
    }

    /**
     * {@code POST /tasks/import} : import CSV sent in multipart/form-data.
     *
     * @param projectId ID of the project to which this task belongs.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @param file      {@link MultipartFile} file from multipart/form-data.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with
     * body the {@link BulkImportResultDTO}.
     */
    @PostMapping(value = "/tasks/import", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'WRITE')")
    public ResponseEntity<BulkImportResultDTO<TaskDTO>> importMultipart(
        @PathVariable("projectId") Long projectId,
        @RequestParam(value = "sep", defaultValue = ",") String sep,
        @RequestParam(value = "array-sep", defaultValue = ";") String arraySep,
        @RequestParam(value = "use-header", defaultValue = "true") boolean useHeader,
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        log.debug("REST request to import Tasks from CSV sent in multipart/form-data in project {}", projectId);
        return ResponseEntity.ok(
            taskService.importFromCsv(projectId, file.getInputStream(), sep, arraySep, useHeader)
        );
    }

    /**
     * {@code DELETE  /tasks/:id} : delete the "id" task.
     *
     * @param projectId ID of the project to which the task belongs.
     * @param id        the id of the taskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADVANCED_USER', 'ROLE_ADMIN') and " +
        "hasPermission(#projectId, 'pt.up.hs.project.domain.Project', 'MANAGE')")
    public ResponseEntity<Void> deleteTask(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to delete Task {} in project {}", id, projectId);
        taskService.delete(projectId, id);
        return ResponseEntity.noContent().headers(
            HeaderUtil.createEntityDeletionAlert(applicationName, true, EntityNames.TASK, id.toString())
        ).build();
    }
}
