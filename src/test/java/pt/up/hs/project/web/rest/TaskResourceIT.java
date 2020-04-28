package pt.up.hs.project.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.repository.TaskRepository;
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.TaskService;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.service.mapper.TaskMapper;
import pt.up.hs.project.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pt.up.hs.project.web.rest.TestUtil.createFormattingConversionService;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class TaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final String CSV_TASK_1_NAME = "Analyst Technician";
    private static final String CSV_TASK_2_NAME = "Washington";
    private static final String CSV_TASK_3_NAME = "Baby morph";
    private static final String CSV_TASK_4_NAME = "Architect";
    private static final String CSV_TASK_1_STARTDATE = "2020-02-23";
    private static final String CSV_TASK_2_STARTDATE = "2020-02-22";
    private static final String CSV_TASK_3_STARTDATE = "2020-02-23";
    private static final String CSV_TASK_4_STARTDATE = "2020-02-22";
    private static final String CSV_TASK_1_ENDDATE = "2020-02-23";
    private static final String CSV_TASK_2_ENDDATE = "2020-02-23";
    private static final String CSV_TASK_3_ENDDATE = "2020-02-24";
    private static final String CSV_TASK_4_ENDDATE = "2020-03-22";
    private static final String CSV_TASK_1_DESCRIPTION = "withdrawal Moldovan, Leu Intelligent";
    private static final String CSV_TASK_2_DESCRIPTION = "Savings Account Grocery";
    private static final String CSV_TASK_3_DESCRIPTION = "South Dakota";
    private static final String CSV_TASK_4_DESCRIPTION = "Stream circuit PNG";

    private static final String DEFAULT_USERNAME = "system";

    @Autowired
    private TaskRepository taskRepository;

    @Mock
    private TaskRepository taskRepositoryMock;

    @Autowired
    private TaskMapper taskMapper;

    @Mock
    private TaskService taskServiceMock;

    @Autowired
    private TaskService taskService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTaskMockMvc;

    private Long projectId;
    private Long labelId;
    private Task task;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TaskResource taskResource = new TaskResource(taskService);
        this.restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator)
            .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(Long projectId, Long[] labelIds) {
        Task task = new Task()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .projectId(projectId);

        Set<Label> labels = new HashSet<>();
        for (Long labelId: labelIds) {
            Label label = new Label();
            label.setId(labelId);
            label.addTasks(task);
            labels.add(label);
        }
        task.setLabels(labels);

        return task;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(Long projectId, Long[] labelIds) {
        Task task = new Task()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .projectId(projectId);

        Set<Label> labels = new HashSet<>();
        for (Long labelId: labelIds) {
            Label label = new Label();
            label.setId(labelId);
            labels.add(label);
        }
        task.setLabels(labels);

        return task;
    }

    private static Project getProject(EntityManager em, Project entity) {
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = entity;
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        return project;
    }

    private static Label getLabel(EntityManager em, Label entity) {
        // Add required entity
        Label label;
        if (TestUtil.findAll(em, Label.class).isEmpty()) {
            label = entity;
            em.persist(label);
            em.flush();
        } else {
            label = TestUtil.findAll(em, Label.class).get(0);
        }
        return label;
    }

    @BeforeEach
    public void initTest() {
        projectId = getProject(em, ProjectResourceIT.createEntity(em)).getId();
        labelId = getLabel(em, LabelResourceIT.createEntity(projectId)).getId();
        task = createEntity(projectId, new Long[] { labelId });
    }

    @Test
    @Transactional
    public void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // date before create
        Instant beforeInstant = Instant.now();

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        restTaskMockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTask.getCreatedBy()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testTask.getCreatedDate()).isStrictlyBetween(beforeInstant, Instant.now());
    }

    @Test
    @Transactional
    public void createTaskWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setName(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/projects/{projectId}/tasks", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks?sort=id,desc", projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].labels.[*].id").value(hasItem(labelId.intValue())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());
    }

    public void getAllTasksWithEagerRelationshipsIsEnabled() throws Exception {
        TaskResource taskResource = new TaskResource(taskServiceMock);
        when(
            taskServiceMock
                .findAllWithEagerRelationships(projectId, null, null, any())
        ).thenReturn(new PageImpl<>(new ArrayList<>()));

        MockMvc restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTaskMockMvc
            .perform(get("/api/projects/{projectId}/tasks?eagerload=true", projectId))
            .andExpect(status().isOk());

        verify(taskServiceMock, times(1))
            .findAllWithEagerRelationships(projectId, null, null, any());
    }

    public void getAllTasksWithEagerRelationshipsIsNotEnabled() throws Exception {
        TaskResource taskResource = new TaskResource(taskServiceMock);
            when(
                taskServiceMock
                    .findAllWithEagerRelationships(projectId, null, null, any())
            ).thenReturn(new PageImpl<>(new ArrayList<>()));
            MockMvc restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTaskMockMvc
            .perform(get("/api/projects/{projectId}/tasks?eagerload=true", projectId))
            .andExpect(status().isOk());

        verify(
            taskServiceMock, times(1)
        ).findAllWithEagerRelationships(projectId, null, null, any());
    }

    @Test
    @Transactional
    public void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks/{id}", projectId, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.labels.[*].id").value(hasItem(labelId.intValue())))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.createdDate").exists());
    }


    /*@Test
    @Transactional
    public void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name equals to DEFAULT_NAME
        defaultTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTasksByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name not equals to DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the taskList where name not equals to UPDATED_NAME
        defaultTaskShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name is not null
        defaultTaskShouldBeFound("name.specified=true");

        // Get all the taskList where name is null
        defaultTaskShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name contains DEFAULT_NAME
        defaultTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskList where name contains UPDATED_NAME
        defaultTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name does not contain DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskList where name does not contain UPDATED_NAME
        defaultTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTasksByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description not equals to DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description not equals to UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the taskList where description equals to UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskShouldBeFound("description.specified=true");

        // Get all the taskList where description is null
        defaultTaskShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains DEFAULT_DESCRIPTION
        defaultTaskShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description contains UPDATED_DESCRIPTION
        defaultTaskShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain DEFAULT_DESCRIPTION
        defaultTaskShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the taskList where description does not contain UPDATED_DESCRIPTION
        defaultTaskShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllTasksByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate equals to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate equals to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate not equals to DEFAULT_START_DATE
        defaultTaskShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate not equals to UPDATED_START_DATE
        defaultTaskShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultTaskShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the taskList where startDate equals to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is not null
        defaultTaskShouldBeFound("startDate.specified=true");

        // Get all the taskList where startDate is null
        defaultTaskShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is greater than or equal to UPDATED_START_DATE
        defaultTaskShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is less than or equal to DEFAULT_START_DATE
        defaultTaskShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is less than or equal to SMALLER_START_DATE
        defaultTaskShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is less than DEFAULT_START_DATE
        defaultTaskShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is less than UPDATED_START_DATE
        defaultTaskShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where startDate is greater than DEFAULT_START_DATE
        defaultTaskShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the taskList where startDate is greater than SMALLER_START_DATE
        defaultTaskShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }


    @Test
    @Transactional
    public void getAllTasksByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate equals to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate equals to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate not equals to DEFAULT_END_DATE
        defaultTaskShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate not equals to UPDATED_END_DATE
        defaultTaskShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultTaskShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the taskList where endDate equals to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is not null
        defaultTaskShouldBeFound("endDate.specified=true");

        // Get all the taskList where endDate is null
        defaultTaskShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is greater than or equal to UPDATED_END_DATE
        defaultTaskShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is less than or equal to DEFAULT_END_DATE
        defaultTaskShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is less than or equal to SMALLER_END_DATE
        defaultTaskShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is less than DEFAULT_END_DATE
        defaultTaskShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is less than UPDATED_END_DATE
        defaultTaskShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllTasksByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where endDate is greater than DEFAULT_END_DATE
        defaultTaskShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the taskList where endDate is greater than SMALLER_END_DATE
        defaultTaskShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }*/


    /*@Test
    @Transactional
    public void getAllTasksByLabelsIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Label labels = LabelResourceIT.createEntity(projectId);
        em.persist(labels);
        em.flush();
        task.addLabels(labels);
        taskRepository.saveAndFlush(task);
        Long labelsId = labels.getId();

        // Get all the taskList where labels equals to labelsId
        defaultTaskShouldBeFound("labelsId.equals=" + labelsId);

        // Get all the taskList where labels equals to labelsId + 1
        defaultTaskShouldNotBeFound("labelsId.equals=" + (labelsId + 1));
    }*/

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].labels").isArray())
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());

        // Check, that the count call also returns 1
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get("/api/projects/{projectId}/tasks/{id}", projectId, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTask() throws Exception {

        Instant beforeCreateInstant = Instant.now();

        // Initialize the database
        taskRepository.saveAndFlush(task);

        Instant afterCreateInstant = Instant.now();

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .setLabels(new HashSet<>());
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        Instant beforeUpdateInstant = Instant.now();

        restTaskMockMvc.perform(put("/api/projects/{projectId}/tasks", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTask.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTask.getLabels()).isEmpty();
        assertThat(testTask.getCreatedDate()).isStrictlyBetween(beforeCreateInstant, afterCreateInstant);
        assertThat(testTask.getLastModifiedDate()).isStrictlyBetween(beforeUpdateInstant, Instant.now());
    }

    @Test
    @Transactional
    public void updateNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc.perform(put("/api/projects/{projectId}/tasks", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc.perform(delete("/api/projects/{projectId}/tasks/{id}", projectId, task.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void importTasksCsv() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks.csv", null, content);

        // Import the tasks' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_1_NAME, CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(CSV_TASK_1_STARTDATE, CSV_TASK_2_STARTDATE, CSV_TASK_3_STARTDATE, CSV_TASK_4_STARTDATE)))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(CSV_TASK_1_ENDDATE, CSV_TASK_2_ENDDATE, CSV_TASK_3_ENDDATE, CSV_TASK_4_ENDDATE)))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_1_DESCRIPTION, CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importTasksCsvNoHeader() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks-no-header.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks-no-header.csv", null, content);

        // Import the tasks' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
                    .queryParam("use-header", "false")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_1_NAME, CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(CSV_TASK_1_STARTDATE, CSV_TASK_2_STARTDATE, CSV_TASK_3_STARTDATE, CSV_TASK_4_STARTDATE)))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(CSV_TASK_1_ENDDATE, CSV_TASK_2_ENDDATE, CSV_TASK_3_ENDDATE, CSV_TASK_4_ENDDATE)))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_1_DESCRIPTION, CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importTasksCsvDiffColumnOrder() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks-diff-column-order.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks-diff-column-order.csv", null, content);

        // Import the tasks' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_1_NAME, CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(CSV_TASK_1_STARTDATE, CSV_TASK_2_STARTDATE, CSV_TASK_3_STARTDATE, CSV_TASK_4_STARTDATE)))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(CSV_TASK_1_ENDDATE, CSV_TASK_2_ENDDATE, CSV_TASK_3_ENDDATE, CSV_TASK_4_ENDDATE)))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_1_DESCRIPTION, CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importTasksCsvDiffSep() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks-diff-sep.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks-diff-sep.csv", null, content);

        // Import the tasks' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
                    .queryParam("sep", ";")
                    .queryParam("array-sep", ",")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_1_NAME, CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(CSV_TASK_1_STARTDATE, CSV_TASK_2_STARTDATE, CSV_TASK_3_STARTDATE, CSV_TASK_4_STARTDATE)))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(CSV_TASK_1_ENDDATE, CSV_TASK_2_ENDDATE, CSV_TASK_3_ENDDATE, CSV_TASK_4_ENDDATE)))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_1_DESCRIPTION, CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importTasksCsvWrongColumns() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks-wrong-columns.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks-wrong-columns.csv", null, content);

        // Import the participants' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_1_NAME, CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue())))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue())))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_1_DESCRIPTION, CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importTasksCsvInvalidRecord() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/tasks/tasks-invalid-record.csv");
        MockMultipartFile file = new MockMultipartFile("file", "tasks-invalid-record.csv", null, content);

        // Import the tasks' CSV
        restTaskMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/tasks/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(3))
            .andExpect(jsonPath("$.invalid").value(1))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_TASK_2_NAME, CSV_TASK_3_NAME, CSV_TASK_4_NAME)))
            .andExpect(jsonPath("$.data.[*].startDate").value(containsInAnyOrder(CSV_TASK_2_STARTDATE, CSV_TASK_3_STARTDATE, CSV_TASK_4_STARTDATE)))
            .andExpect(jsonPath("$.data.[*].endDate").value(containsInAnyOrder(CSV_TASK_2_ENDDATE, CSV_TASK_3_ENDDATE, CSV_TASK_4_ENDDATE)))
            .andExpect(jsonPath("$.data.[*].description").value(containsInAnyOrder(CSV_TASK_2_DESCRIPTION, CSV_TASK_3_DESCRIPTION, CSV_TASK_4_DESCRIPTION)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(1, 0, 1)));
    }
}
