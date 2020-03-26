package pt.up.hs.project.web.rest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.repository.ProjectRepository;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.ProjectDTO;
import pt.up.hs.project.service.mapper.ProjectMapper;
import pt.up.hs.project.web.rest.errors.ExceptionTranslator;
import pt.up.hs.project.service.ProjectQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;

import static pt.up.hs.project.web.rest.ProjectResourceIT.TEST_USER_LOGIN;
import static pt.up.hs.project.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pt.up.hs.project.domain.enumeration.ProjectStatus;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(value = TEST_USER_LOGIN)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class ProjectResourceIT {
    static final String TEST_USER_LOGIN = "test";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ProjectStatus DEFAULT_STATUS = ProjectStatus.DRAFT;
    private static final ProjectStatus UPDATED_STATUS = ProjectStatus.OPEN;

    private static final String DEFAULT_OWNER = TEST_USER_LOGIN;
    private static final String UPDATED_OWNER = "user";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final String DEFAULT_USERNAME = TEST_USER_LOGIN;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectQueryService projectQueryService;

    @Autowired
    private ParticipantResource participantResource;

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

    private MockMvc restProjectMockMvc;

    private Project project;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProjectResource projectResource = new ProjectResource(projectService, projectQueryService);
        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectResource)
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
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .owner(DEFAULT_OWNER)
            .color(DEFAULT_COLOR);
        return project;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity(EntityManager em) {
        Project project = new Project()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .owner(UPDATED_OWNER)
            .color(UPDATED_COLOR);
        return project;
    }

    @BeforeEach
    public void initTest() {
        project = createEntity(em);
    }

    @Test
    @Transactional
    public void createProject() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // date before create
        Instant beforeInstant = Instant.now();

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProject.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testProject.getColor()).isEqualTo(DEFAULT_COLOR);
        assertThat(testProject.getCreatedBy()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testProject.getCreatedDate()).isStrictlyBetween(beforeInstant, Instant.now());
    }

    @Test
    @Transactional
    public void createProjectWithoutOwner() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // date before create
        Instant beforeInstant = Instant.now();

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        projectDTO.setOwner(null);
        restProjectMockMvc.perform(post("/api/projects")
            .with(request -> {
                request.setRemoteUser(TEST_USER_LOGIN);
                return request;
            })
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isCreated());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProject.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProject.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testProject.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testProject.getColor()).isEqualTo(DEFAULT_COLOR);
        assertThat(testProject.getCreatedBy()).isEqualTo(TEST_USER_LOGIN);
        assertThat(testProject.getCreatedDate()).isStrictlyBetween(beforeInstant, Instant.now());
    }

    @Test
    @Transactional
    public void createProjectWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectRepository.findAll().size();

        // Create the Project with an existing ID
        project.setId(1L);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setStatus(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkColorIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRepository.findAll().size();
        // set the field null
        project.setColor(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc.perform(post("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjects() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());
    }

    @Test
    @Transactional
    public void getProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.createdDate").exists());
    }


    @Test
    @Transactional
    public void getProjectsByIdFiltering() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        Long id = project.getId();

        defaultProjectShouldBeFound("id.equals=" + id);
        defaultProjectShouldNotBeFound("id.notEquals=" + id);

        defaultProjectShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllProjectsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name equals to DEFAULT_NAME
        defaultProjectShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the projectList where name equals to UPDATED_NAME
        defaultProjectShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProjectsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name not equals to DEFAULT_NAME
        defaultProjectShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the projectList where name not equals to UPDATED_NAME
        defaultProjectShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProjectsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProjectShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the projectList where name equals to UPDATED_NAME
        defaultProjectShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProjectsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name is not null
        defaultProjectShouldBeFound("name.specified=true");

        // Get all the projectList where name is null
        defaultProjectShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllProjectsByNameContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name contains DEFAULT_NAME
        defaultProjectShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the projectList where name contains UPDATED_NAME
        defaultProjectShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProjectsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where name does not contain DEFAULT_NAME
        defaultProjectShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the projectList where name does not contain UPDATED_NAME
        defaultProjectShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description equals to DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description not equals to DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description not equals to UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the projectList where description equals to UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description is not null
        defaultProjectShouldBeFound("description.specified=true");

        // Get all the projectList where description is null
        defaultProjectShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllProjectsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description contains DEFAULT_DESCRIPTION
        defaultProjectShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description contains UPDATED_DESCRIPTION
        defaultProjectShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllProjectsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where description does not contain DEFAULT_DESCRIPTION
        defaultProjectShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the projectList where description does not contain UPDATED_DESCRIPTION
        defaultProjectShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllProjectsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status equals to DEFAULT_STATUS
        defaultProjectShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the projectList where status equals to UPDATED_STATUS
        defaultProjectShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProjectsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status not equals to DEFAULT_STATUS
        defaultProjectShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the projectList where status not equals to UPDATED_STATUS
        defaultProjectShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProjectsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultProjectShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the projectList where status equals to UPDATED_STATUS
        defaultProjectShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllProjectsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where status is not null
        defaultProjectShouldBeFound("status.specified=true");

        // Get all the projectList where status is null
        defaultProjectShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllProjectsByOwnerIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where owner equals to DEFAULT_OWNER
        defaultProjectShouldBeFound("owner.equals=" + DEFAULT_OWNER);

        // Get all the projectList where owner equals to UPDATED_OWNER
        defaultProjectShouldNotBeFound("owner.equals=" + UPDATED_OWNER);
    }

    @Test
    @Transactional
    public void getAllProjectsByOwnerIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where owner not equals to DEFAULT_OWNER
        defaultProjectShouldNotBeFound("owner.notEquals=" + DEFAULT_OWNER);

        // Get all the projectList where owner not equals to UPDATED_OWNER
        defaultProjectShouldBeFound("owner.notEquals=" + UPDATED_OWNER);
    }

    @Test
    @Transactional
    public void getAllProjectsByOwnerIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where owner in DEFAULT_OWNER or UPDATED_OWNER
        defaultProjectShouldBeFound("owner.in=" + DEFAULT_OWNER + "," + UPDATED_OWNER);

        // Get all the projectList where owner equals to UPDATED_OWNER
        defaultProjectShouldNotBeFound("owner.in=" + UPDATED_OWNER);
    }

    @Test
    @Transactional
    public void getAllProjectsByOwnerIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where owner is not null
        defaultProjectShouldBeFound("owner.specified=true");

        // Get all the projectList where owner is null
        defaultProjectShouldNotBeFound("owner.specified=false");
    }

    @Test
    @Transactional
    public void getAllProjectsByColorIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color equals to DEFAULT_COLOR
        defaultProjectShouldBeFound("color.equals=" + DEFAULT_COLOR);

        // Get all the projectList where color equals to UPDATED_COLOR
        defaultProjectShouldNotBeFound("color.equals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllProjectsByColorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color not equals to DEFAULT_COLOR
        defaultProjectShouldNotBeFound("color.notEquals=" + DEFAULT_COLOR);

        // Get all the projectList where color not equals to UPDATED_COLOR
        defaultProjectShouldBeFound("color.notEquals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllProjectsByColorIsInShouldWork() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color in DEFAULT_COLOR or UPDATED_COLOR
        defaultProjectShouldBeFound("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR);

        // Get all the projectList where color equals to UPDATED_COLOR
        defaultProjectShouldNotBeFound("color.in=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllProjectsByColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color is not null
        defaultProjectShouldBeFound("color.specified=true");

        // Get all the projectList where color is null
        defaultProjectShouldNotBeFound("color.specified=false");
    }
                @Test
    @Transactional
    public void getAllProjectsByColorContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color contains DEFAULT_COLOR
        defaultProjectShouldBeFound("color.contains=" + DEFAULT_COLOR);

        // Get all the projectList where color contains UPDATED_COLOR
        defaultProjectShouldNotBeFound("color.contains=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllProjectsByColorNotContainsSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        // Get all the projectList where color does not contain DEFAULT_COLOR
        defaultProjectShouldNotBeFound("color.doesNotContain=" + DEFAULT_COLOR);

        // Get all the projectList where color does not contain UPDATED_COLOR
        defaultProjectShouldBeFound("color.doesNotContain=" + UPDATED_COLOR);
    }


    @Test
    @Transactional
    public void getAllProjectsByTasksIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        Task tasks = TaskResourceIT.createEntity(project.getId());
        em.persist(tasks);
        em.flush();
        project.addTasks(tasks);
        projectRepository.saveAndFlush(project);
        Long tasksId = tasks.getId();

        // Get all the projectList where tasks equals to tasksId
        defaultProjectShouldBeFound("tasksId.equals=" + tasksId);

        // Get all the projectList where tasks equals to tasksId + 1
        defaultProjectShouldNotBeFound("tasksId.equals=" + (tasksId + 1));
    }


    @Test
    @Transactional
    public void getAllProjectsByParticipantsIsEqualToSomething() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);
        Participant participants = ParticipantResourceIT.createEntity(project.getId());
        em.persist(participants);
        em.flush();
        project.addParticipants(participants);
        projectRepository.saveAndFlush(project);
        Long participantsId = participants.getId();

        // Get all the projectList where participants equals to participantsId
        defaultProjectShouldBeFound("participantsId.equals=" + participantsId);

        // Get all the projectList where participants equals to participantsId + 1
        defaultProjectShouldNotBeFound("participantsId.equals=" + (participantsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectShouldBeFound(String filter) throws Exception {
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());

        // Check, that the count call also returns 1
        restProjectMockMvc.perform(get("/api/projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectShouldNotBeFound(String filter) throws Exception {
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMockMvc.perform(get("/api/projects/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProject() throws Exception {

        Instant beforeCreateInstant = Instant.now();

        // Initialize the database
        projectRepository.saveAndFlush(project);

        Instant afterCreateInstant = Instant.now();

        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).get();

        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .owner(UPDATED_OWNER)
            .color(UPDATED_COLOR);

        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        Instant beforeUpdateInstant = Instant.now();

        restProjectMockMvc.perform(put("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isOk());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
        Project testProject = projectList.get(projectList.size() - 1);
        assertThat(testProject.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProject.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProject.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testProject.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testProject.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testProject.getCreatedBy()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testProject.getLastModifiedBy()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testProject.getCreatedDate()).isStrictlyBetween(beforeCreateInstant, afterCreateInstant);
        assertThat(testProject.getLastModifiedDate()).isStrictlyBetween(beforeUpdateInstant, Instant.now());
    }

    @Test
    @Transactional
    public void updateNonExistingProject() throws Exception {
        int databaseSizeBeforeUpdate = projectRepository.findAll().size();

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(put("/api/projects")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        // Initialize the database
        projectRepository.saveAndFlush(project);

        int databaseSizeBeforeDelete = projectRepository.findAll().size();

        // Delete the project
        restProjectMockMvc.perform(delete("/api/projects/{id}", project.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
