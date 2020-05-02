package pt.up.hs.project.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Status;
import pt.up.hs.project.constants.EntityNames;
import pt.up.hs.project.constants.ErrorKeys;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.repository.TaskRepository;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.TaskBasicDTO;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.service.exceptions.ServiceException;
import pt.up.hs.project.service.importer.dto.TaskCsvDTO;
import pt.up.hs.project.service.importer.reader.CsvReader;
import pt.up.hs.project.service.mapper.LabelMapper;
import pt.up.hs.project.service.mapper.TaskBasicMapper;
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
    private final TaskBasicMapper taskBasicMapper;

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public TaskServiceImpl(
        TaskRepository taskRepository,
        TaskMapper taskMapper,
        TaskBasicMapper taskBasicMapper,
        LabelRepository labelRepository,
        LabelMapper labelMapper
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskBasicMapper = taskBasicMapper;
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
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
        populateAndSaveLabels(projectId, task);
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
                        populateAndSaveLabels(projectId, task);
                        return task;
                    })
                    .collect(Collectors.toList())
            ).parallelStream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Get all the tasks' basic info.
     *
     * @param projectId the ID of the project containing the tasks.
     * @return the list of entities' basic info.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskBasicDTO> findAllBasic(Long projectId) {
        log.debug("Request to get all Tasks' basic info from project {}", projectId);
        return taskRepository.findAllByProjectId(projectId)
            .parallelStream()
            .map(taskBasicMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Get all the tasks.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labels    the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Long projectId, String search, List<Long> labels, Pageable pageable) {
        log.debug("Request to get all Tasks from project {}", projectId);
        return taskRepository.findAllByProjectId(projectId, search, labels, pageable)
            .map(taskMapper::toDto);
    }

    /**
     * Get all the tasks with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labels    the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAllWithEagerRelationships(Long projectId, String search, List<Long> labels, Pageable pageable) {
        log.debug("Request to get all Tasks with eager relationships from project {}", projectId);
        return taskRepository
            .findAllWithEagerRelationships(projectId, search, labels, pageable)
            .map(taskMapper::toDto);
    }

    /**
     * Count the tasks with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search    the search string.
     * @param labels    the ids of the labels to filter by.
     * @return the number of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public long count(Long projectId, String search, List<Long> labels) {
        log.debug("Request to count Tasks from project {}", projectId);
        return taskRepository.count(projectId, search, labels);
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

        List<Task> tasks = reader.getAll().stream().map(taskCsv -> {
            try {
                Task task = new Task();
                task.setProjectId(projectId);
                task.setName(taskCsv.getName());
                task.setStartDate(taskCsv.getStartDate());
                task.setEndDate(taskCsv.getEndDate());
                task.setDescription(taskCsv.getDescription());
                if (taskCsv.getLabels() != null) {
                    task.setLabels(
                        Arrays.stream(taskCsv.getLabels().split(arraySep))
                            .map(String::trim)
                            .filter(name -> !name.isEmpty())
                            .map(name -> {
                                Optional<Label> labelOpt = labelRepository.findByProjectIdAndName(projectId, name);
                                if (labelOpt.isPresent()) {
                                    return labelOpt.get();
                                } else {
                                    Label label = new Label();
                                    label.setProjectId(projectId);
                                    label.setName(name);
                                    return labelRepository.saveAndFlush(label);
                                }
                            })
                            .collect(Collectors.toSet())
                    );
                }
                return task;
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<Task> savedTasks = taskRepository.saveAll(tasks);

        long endTime = new Date().getTime();

        return new BulkImportResultDTO<>(
            savedTasks.size(),
            reader.getExceptionLines().length + (tasks.size() - savedTasks.size()),
            savedTasks.parallelStream().map(taskMapper::toDto).collect(Collectors.toList()),
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
        Optional<Task> taskOpt = taskRepository.findByProjectIdAndId(projectId, id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getLabels().parallelStream().forEach(task::removeLabels);
        }
        taskRepository.deleteByProjectIdAndId(projectId, id);
    }

    private void populateAndSaveLabels(Long projectId, Task task) {
        Set<Label> labels = new HashSet<>();
        for (Label label : task.getLabels()) {
            label.setProjectId(projectId);
            if (label.getId() != null) { // existing labels
                Optional<Label> labelOpt =
                    labelRepository.findByProjectIdAndId(projectId, label.getId());
                if (!labelOpt.isPresent()) {
                    throw new ServiceException(
                        Status.BAD_REQUEST,
                        EntityNames.TASK,
                        ErrorKeys.ERR_RELATED_ENTITY_NOT_FOUND,
                        "The related label does not exist."
                    );
                }
                labels.add(labelOpt.get().addTasks(task));
            } else { // new labels
                labelRepository.save(label);
                labels.add(label.addTasks(task));
            }
        }
        task.setLabels(labels);
    }
}
