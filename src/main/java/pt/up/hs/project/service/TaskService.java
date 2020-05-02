package pt.up.hs.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.TaskBasicDTO;
import pt.up.hs.project.service.dto.TaskDTO;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.Task}.
 */
public interface TaskService {

    /**
     * Save a task.
     *
     * @param projectId the ID of the project containing the task.
     * @param taskDTO   the entity to save.
     * @return the persisted entity.
     */
    TaskDTO save(Long projectId, TaskDTO taskDTO);

    /**
     * Save all tasks.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param taskDTOs  the entities to save.
     * @return the persisted entities.
     */
    List<TaskDTO> saveAll(Long projectId, List<TaskDTO> taskDTOs);

    /**
     * Get all the tasks.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labelIds  the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    Page<TaskDTO> findAll(Long projectId, String search, List<Long> labelIds, Pageable pageable);

    /**
     * Get all the tasks' basic info.
     *
     * @param projectId the ID of the project containing the tasks.
     * @return the list of entities.
     */
    List<TaskBasicDTO> findAllBasic(Long projectId);

    /**
     * Get all the tasks with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labelIds  the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    Page<TaskDTO> findAllWithEagerRelationships(Long projectId, String search, List<Long> labelIds, Pageable pageable);

    /**
     * Count the tasks with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labelIds  the ids of the labels to filter by.
     * @return the list of entities.
     */
    long count(Long projectId, String search, List<Long> labelIds);

    /**
     * Get the "id" task.
     *
     * @param projectId the ID of the project containing the task.
     * @param id        the id of the entity.
     * @return the entity.
     */
    Optional<TaskDTO> findOne(Long projectId, Long id);

    /**
     * Upload tasks from CSV.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param is        {@link InputStream} the file input stream.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @return {@link BulkImportResultDTO} response to CSV upload.
     */
    BulkImportResultDTO<TaskDTO> importFromCsv(
        Long projectId, InputStream is, String sep, String arraySep, boolean useHeader);

    /**
     * Delete the "id" task.
     *
     * @param projectId the ID of the project containing the task.
     * @param id        the id of the entity.
     */
    void delete(Long projectId, Long id);
}
