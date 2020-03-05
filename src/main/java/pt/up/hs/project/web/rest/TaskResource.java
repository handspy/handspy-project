package pt.up.hs.project.web.rest;

import org.springframework.web.multipart.MultipartFile;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.web.rest.errors.BadRequestAlertException;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.service.dto.TaskCriteria;
import pt.up.hs.project.service.TaskQueryService;

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

    private static final String ENTITY_NAME = "projectTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskService taskService;
    private final TaskQueryService taskQueryService;

    public TaskResource(TaskService taskService, TaskQueryService taskQueryService) {
        this.taskService = taskService;
        this.taskQueryService = taskQueryService;
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
    public ResponseEntity<TaskDTO> createTask(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        log.debug("REST request to save Task {} in project {}", taskDTO, projectId);
        if (taskDTO.getId() != null) {
            throw new BadRequestAlertException("A new task cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskDTO result = taskService.save(projectId, taskDTO);
        return ResponseEntity.created(new URI("/api/projects/" + projectId + "/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
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
    public ResponseEntity<TaskDTO> updateTask(
        @PathVariable("projectId") Long projectId,
        @Valid @RequestBody TaskDTO taskDTO
    ) {
        log.debug("REST request to update Task {} in project {}", taskDTO, projectId);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TaskDTO result = taskService.save(projectId, taskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param projectId ID of the project to which the tasks belong.
     * @param pageable  the pagination information.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks(
        @PathVariable("projectId") Long projectId,
        TaskCriteria criteria, Pageable pageable
    ) {
        log.debug("REST request to get Tasks by criteria {} in project {}", criteria, projectId);
        Page<TaskDTO> page = taskQueryService.findByCriteria(projectId, criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/count} : count all the tasks.
     *
     * @param projectId ID of the project to which the tasks belong.
     * @param criteria  the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tasks/count")
    public ResponseEntity<Long> countTasks(
        @PathVariable("projectId") Long projectId,
        TaskCriteria criteria
    ) {
        log.debug("REST request to count Tasks by criteria {} in project {}", criteria, projectId);
        return ResponseEntity.ok().body(taskQueryService.countByCriteria(projectId, criteria));
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
    public ResponseEntity<Void> deleteTask(
        @PathVariable("projectId") Long projectId,
        @PathVariable Long id
    ) {
        log.debug("REST request to delete Task {} in project {}", id, projectId);
        taskService.delete(projectId, id);
        return ResponseEntity.noContent().headers(
            HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())
        ).build();
    }
}
