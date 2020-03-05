package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.repository.TaskRepository;
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.service.importer.dto.TaskCsvDTO;
import pt.up.hs.project.service.importer.reader.CsvReader;
import pt.up.hs.project.service.mapper.TaskMapper;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private final LabelService labelService;

    public TaskServiceImpl(
        TaskRepository taskRepository,
        TaskMapper taskMapper,
        LabelService labelService
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.labelService = labelService;
    }

    /**
     * Save a task.
     *
     * @param projectId the ID of the project containing the task.
     * @param taskDTO   the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TaskDTO save(Long projectId, TaskDTO taskDTO) {
        log.debug("Request to save Task {} from project {}", taskDTO, projectId);
        Task task = taskMapper.toEntity(taskDTO);
        task.setProjectId(projectId);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Save all tasks.
     *
     * @param projectId the ID of the project containing the participant.
     * @param taskDTOs  the entities to save.
     * @return the persisted entities.
     */
    @Override
    public List<TaskDTO> saveAll(Long projectId, List<TaskDTO> taskDTOs) {
        log.debug("Request to save all Tasks in project {}", projectId);
        return taskRepository
            .saveAll(
                taskDTOs.parallelStream()
                    .map(taskDTO -> {
                        Task task = taskMapper.toEntity(taskDTO);
                        task.setProjectId(projectId);
                        return task;
                    })
                    .collect(Collectors.toList())
            ).parallelStream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Get all the tasks.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Long projectId, Pageable pageable) {
        log.debug("Request to get all Tasks from project {}", projectId);
        return taskRepository.findAllByProjectId(projectId, pageable)
            .map(taskMapper::toDto);
    }

    /**
     * Get all the tasks with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAllWithEagerRelationships(Long projectId, Pageable pageable) {
        log.debug("Request to get all Tasks with eager relationships from project {}", projectId);
        return taskRepository
            .findAllWithEagerRelationships(projectId, pageable)
            .map(taskMapper::toDto);
    }

    /**
     * Get the "id" task.
     *
     * @param projectId the ID of the project containing the task.
     * @param id        the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long projectId, Long id) {
        log.debug("Request to get Task {} from project {}", id, projectId);
        return taskRepository.findOneWithEagerRelationships(projectId, id)
            .map(taskMapper::toDto);
    }

    /**
     * Upload tasks from CSV.
     *
     * @param projectId the ID of the project of the tasks.
     * @param is        {@link InputStream} the file input stream.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @return {@link BulkImportResultDTO} response to CSV upload.
     */
    @Override
    public BulkImportResultDTO<TaskDTO> importFromCsv(
        Long projectId, InputStream is, String sep, String arraySep, boolean useHeader
    ) {
        log.debug("Request to import Tasks from CSV to project {}", projectId);
        long startTime = new Date().getTime();
        CsvReader<TaskCsvDTO> reader = CsvReader.fromInputStream(
            TaskCsvDTO.class, is, sep, useHeader
        );
        List<TaskDTO> taskDTOs = reader.getAll().stream().map(taskCsvDTO -> {
            try {
                TaskDTO taskDTO = new TaskDTO();
                taskDTO.setProjectId(projectId);
                taskDTO.setName(taskCsvDTO.getName());
                taskDTO.setStartDate(taskCsvDTO.getStartDate());
                taskDTO.setEndDate(taskCsvDTO.getEndDate());
                taskDTO.setDescription(taskCsvDTO.getDescription());
                if (taskCsvDTO.getLabels() != null) {
                    taskDTO.setLabels(
                        Arrays.stream(taskCsvDTO.getLabels().split(arraySep))
                            .map(String::trim)
                            .filter(name -> !name.isEmpty())
                            .map(name -> labelService.createIfNameNotExists(projectId, name))
                            .collect(Collectors.toSet())
                    );
                }
                return taskDTO;
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<TaskDTO> savedTaskDTOs = saveAll(projectId, taskDTOs);
        long endTime = new Date().getTime();
        return new BulkImportResultDTO<>(
            savedTaskDTOs.size(),
            reader.getExceptionLines().length + (taskDTOs.size() - savedTaskDTOs.size()),
            savedTaskDTOs,
            endTime - startTime
        );
    }

    /**
     * Delete the "id" task.
     *
     * @param projectId the ID of the project containing the task.
     * @param id        the id of the entity.
     */
    @Override
    public void delete(Long projectId, Long id) {
        log.debug("Request to delete Task {} from project {}", id, projectId);
        taskRepository.deleteAllByProjectIdAndId(projectId, id);
    }
}
