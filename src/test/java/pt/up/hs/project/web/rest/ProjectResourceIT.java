package pt.up.hs.project.web.rest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;
import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.*;
import pt.up.hs.project.repository.ProjectRepository;
import pt.up.hs.project.security.PermissionsConstants;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import pt.up.hs.project.domain.enumeration.ProjectStatus;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(value = TEST_USER_LOGIN, authorities = {"ROLE_USER"})
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class ProjectResourceIT {
    static final String TEST_USER_LOGIN = "test";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "CCCCCCCCCC";
    private static final String UPDATED_DESCRIPTION = "DDDDDDDDDD";

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

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc restProjectMockMvc;

    private Project project;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.restProjectMockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
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

        for (String permissionName: PermissionsConstants.ALL) {
            Permission permission = new Permission().name(permissionName);
            em.persist(permission);
            em.flush();
        }

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
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

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
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

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
    public void getAllProjectsBySearchQueryWithPartialName() throws Exception {

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

        // Get all the projectList where name equals to DEFAULT_NAME
        defaultProjectShouldBeFound("search=" + DEFAULT_NAME.substring(1, 3));

        // Get all the projectList where name equals to UPDATED_NAME
        defaultProjectShouldNotBeFound("search=" + UPDATED_NAME.substring(1, 3));
    }

    @Test
    @Transactional
    public void getAllProjectsBySearchQueryWithPartialDescription() throws Exception {

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

        // Get all the projectList where name equals to DEFAULT_NAME
        defaultProjectShouldBeFound("search=" + DEFAULT_DESCRIPTION.substring(1, 3));

        // Get all the projectList where name equals to UPDATED_NAME
        defaultProjectShouldNotBeFound("search=" + UPDATED_DESCRIPTION.substring(1, 3));
    }

    @Test
    @Transactional
    public void getAllProjectsByStatus() throws Exception {

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

        // Get all the projectList where status in [DRAFT]
        defaultProjectShouldBeFound("status=" + String.join(",", new String[] {ProjectStatus.DRAFT.toString()}));

        // Get all the projectList where status in [OPEN]
        defaultProjectShouldNotBeFound("status=" + String.join(",", new String[] {ProjectStatus.OPEN.toString()}));

        // Get all the projectList where status in [CLOSED]
        defaultProjectShouldNotBeFound("status=" + String.join(",", new String[] {ProjectStatus.CLOSED.toString()}));

        // Get all the projectList where status in [DISCARDED]
        defaultProjectShouldNotBeFound("status=" + String.join(",", new String[] {ProjectStatus.DISCARDED.toString()}));
    }

    @Test
    @Transactional
    public void getAllProjectsByMultipleStatus() throws Exception {

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

        // Get all the projectList where status in [DRAFT,OPEN,CLOSED]
        defaultProjectShouldBeFound("status=" + String.join(",", new String[] {
            ProjectStatus.DRAFT.toString(),
            ProjectStatus.OPEN.toString(),
            ProjectStatus.CLOSED.toString()
        }));

        // Get all the projectList where status in [OPEN,CLOSED,DISCARDED]
        defaultProjectShouldNotBeFound("status=" + String.join(",", new String[] {
            ProjectStatus.OPEN.toString(),
            ProjectStatus.CLOSED.toString(),
            ProjectStatus.DISCARDED.toString()
        }));
    }

    @Test
    @Transactional
    public void getAllProjectsByMultipleStatusAndSearch() throws Exception {

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

        // Get all the projectList where search contains name of default project and status in [DRAFT,OPEN,CLOSED]
        defaultProjectShouldBeFound("search=" + DEFAULT_NAME + "&status=" + String.join(",", new String[] {
            ProjectStatus.DRAFT.toString(),
            ProjectStatus.OPEN.toString(),
            ProjectStatus.CLOSED.toString()
        }));

        // Get all the projectList where search contains name of default project and status in [OPEN,CLOSE,DISCARDEDD]
        defaultProjectShouldNotBeFound("search=" + DEFAULT_NAME + "&status=" + String.join(",", new String[] {
            ProjectStatus.OPEN.toString(),
            ProjectStatus.CLOSED.toString(),
            ProjectStatus.DISCARDED.toString()
        }));
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
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void updateProject() throws Exception {

        Instant beforeCreateInstant = Instant.now();

        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

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
            .andExpect(status().isForbidden());

        // Validate the Project in the database
        List<Project> projectList = projectRepository.findAll();
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProject() throws Exception {
        // Initialize the database
        project = projectMapper.toEntity(projectService.save(projectMapper.toDto(project)));

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
