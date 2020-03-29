package pt.up.hs.project.web.rest;

import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.Permission;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.repository.ProjectPermissionRepository;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;
import pt.up.hs.project.service.mapper.ProjectPermissionMapper;
import pt.up.hs.project.web.rest.errors.ExceptionTranslator;

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
import java.util.List;

import static pt.up.hs.project.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProjectPermissionResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class ProjectPermissionResourceIT {

    private static final String DEFAULT_USER = "system";
    private static final String UPDATED_USER = "user";

    private static final String DEFAULT_PERMISSION = "READ";
    private static final String UPDATED_PERMISSION = "WRITE";

    @Autowired
    private ProjectPermissionRepository projectPermissionRepository;

    @Autowired
    private ProjectPermissionMapper projectPermissionMapper;

    @Autowired
    private ProjectPermissionService projectPermissionService;

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

    private MockMvc restProjectPermissionMockMvc;

    private ProjectPermission projectPermission;

    // @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProjectPermissionResource projectPermissionResource = new ProjectPermissionResource(projectPermissionService);
        this.restProjectPermissionMockMvc = MockMvcBuilders.standaloneSetup(projectPermissionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectPermission createEntity(EntityManager em) {
        ProjectPermission projectPermission = new ProjectPermission()
            .user(DEFAULT_USER);
        // Add required entities
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectPermission.setProjectId(project.getId());
        Permission permission;
        if (TestUtil.findAll(em, Permission.class).isEmpty()) {
            permission = new Permission().name(DEFAULT_PERMISSION);
            em.persist(permission);
            em.flush();
        } else {
            permission = TestUtil.findAll(em, Permission.class).get(0);
        }
        projectPermission.setPermissionName(permission.getName());
        return projectPermission;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectPermission createUpdatedEntity(EntityManager em) {
        ProjectPermission projectPermission = new ProjectPermission()
            .user(UPDATED_USER);
        // Add required entities
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectPermission.setProjectId(project.getId());
        Permission permission;
        if (TestUtil.findAll(em, Permission.class).isEmpty()) {
            permission = new Permission().name(UPDATED_PERMISSION);
            em.persist(permission);
            em.flush();
        } else {
            permission = TestUtil.findAll(em, Permission.class).get(0);
        }
        projectPermission.setPermissionName(permission.getName());
        return projectPermission;
    }

    @BeforeEach
    public void initTest() {
        projectPermission = createEntity(em);
    }

    // @Test
    @Transactional
    public void createProjectPermission() throws Exception {
        int databaseSizeBeforeCreate = projectPermissionRepository.findAll().size();

        // Create the ProjectPermission
        ProjectPermissionDTO projectPermissionDTO = projectPermissionMapper.toDto(projectPermission);
        restProjectPermissionMockMvc.perform(post("/api/project-permissions")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectPermissionDTO)))
            .andExpect(status().isCreated());

        // Validate the ProjectPermission in the database
        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectPermission testProjectPermission = projectPermissionList.get(projectPermissionList.size() - 1);
        assertThat(testProjectPermission.getUser()).isEqualTo(DEFAULT_USER);
        assertThat(testProjectPermission.getPermission().getName()).isEqualTo(DEFAULT_PERMISSION);
    }

    // @Test
    @Transactional
    public void createProjectPermissionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectPermissionRepository.findAll().size();

        // Create the ProjectPermission with an existing ID
        ProjectPermissionDTO projectPermissionDTO = projectPermissionMapper.toDto(projectPermission);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectPermissionMockMvc.perform(post("/api/project-permissions")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectPermissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectPermission in the database
        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeCreate);
    }


    // @Test
    @Transactional
    public void checkUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectPermissionRepository.findAll().size();
        // set the field null
        projectPermission.setUser(null);

        // Create the ProjectPermission, which fails.
        ProjectPermissionDTO projectPermissionDTO = projectPermissionMapper.toDto(projectPermission);

        restProjectPermissionMockMvc.perform(post("/api/project-permissions")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectPermissionDTO)))
            .andExpect(status().isBadRequest());

        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeTest);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissions() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList
        restProjectPermissionMockMvc.perform(get("/api/project-permissions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            //.andExpect(jsonPath("$.[*].id").value(hasItem(projectPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)))
            .andExpect(jsonPath("$.[*].permission").value(hasItem(DEFAULT_PERMISSION)));
    }

    // @Test
    @Transactional
    public void getProjectPermission() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get the projectPermission
        /*restProjectPermissionMockMvc.perform(get("/api/project-permissions/{id}", projectPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectPermission.getId().intValue()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER))
            .andExpect(jsonPath("$.permission").value(DEFAULT_PERMISSION));*/
    }


    // @Test
    @Transactional
    public void getProjectPermissionsByIdFiltering() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        /* Long id = projectPermission.getId();

        defaultProjectPermissionShouldBeFound("id.equals=" + id);
        defaultProjectPermissionShouldNotBeFound("id.notEquals=" + id);

        defaultProjectPermissionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProjectPermissionShouldNotBeFound("id.greaterThan=" + id);

        defaultProjectPermissionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProjectPermissionShouldNotBeFound("id.lessThan=" + id);*/
    }


    // @Test
    @Transactional
    public void getAllProjectPermissionsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where user equals to DEFAULT_USER
        defaultProjectPermissionShouldBeFound("user.equals=" + DEFAULT_USER);

        // Get all the projectPermissionList where user equals to UPDATED_USER
        defaultProjectPermissionShouldNotBeFound("user.equals=" + UPDATED_USER);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByUserIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where user not equals to DEFAULT_USER
        defaultProjectPermissionShouldNotBeFound("user.notEquals=" + DEFAULT_USER);

        // Get all the projectPermissionList where user not equals to UPDATED_USER
        defaultProjectPermissionShouldBeFound("user.notEquals=" + UPDATED_USER);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByUserIsInShouldWork() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where user in DEFAULT_USER or UPDATED_USER
        defaultProjectPermissionShouldBeFound("user.in=" + DEFAULT_USER + "," + UPDATED_USER);

        // Get all the projectPermissionList where user equals to UPDATED_USER
        defaultProjectPermissionShouldNotBeFound("user.in=" + UPDATED_USER);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByUserIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where user is not null
        defaultProjectPermissionShouldBeFound("user.specified=true");

        // Get all the projectPermissionList where user is null
        defaultProjectPermissionShouldNotBeFound("user.specified=false");
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByPermissionIsEqualToSomething() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where permission equals to DEFAULT_PERMISSION
        defaultProjectPermissionShouldBeFound("permission.equals=" + DEFAULT_PERMISSION);

        // Get all the projectPermissionList where permission equals to UPDATED_PERMISSION
        defaultProjectPermissionShouldNotBeFound("permission.equals=" + UPDATED_PERMISSION);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByPermissionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where permission not equals to DEFAULT_PERMISSION
        defaultProjectPermissionShouldNotBeFound("permission.notEquals=" + DEFAULT_PERMISSION);

        // Get all the projectPermissionList where permission not equals to UPDATED_PERMISSION
        defaultProjectPermissionShouldBeFound("permission.notEquals=" + UPDATED_PERMISSION);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByPermissionIsInShouldWork() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where permission in DEFAULT_PERMISSION or UPDATED_PERMISSION
        defaultProjectPermissionShouldBeFound("permission.in=" + DEFAULT_PERMISSION + "," + UPDATED_PERMISSION);

        // Get all the projectPermissionList where permission equals to UPDATED_PERMISSION
        defaultProjectPermissionShouldNotBeFound("permission.in=" + UPDATED_PERMISSION);
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByPermissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        // Get all the projectPermissionList where permission is not null
        defaultProjectPermissionShouldBeFound("permission.specified=true");

        // Get all the projectPermissionList where permission is null
        defaultProjectPermissionShouldNotBeFound("permission.specified=false");
    }

    // @Test
    @Transactional
    public void getAllProjectPermissionsByProjectIsEqualToSomething() throws Exception {
        // Get already existing entity
        Project project = projectPermission.getProject();
        projectPermissionRepository.saveAndFlush(projectPermission);
        Long projectId = project.getId();

        // Get all the projectPermissionList where project equals to projectId
        defaultProjectPermissionShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectPermissionList where project equals to projectId + 1
        defaultProjectPermissionShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectPermissionShouldBeFound(String filter) throws Exception {
        restProjectPermissionMockMvc.perform(get("/api/project-permissions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(projectPermission.getProject().getId())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER)))
            .andExpect(jsonPath("$.[*].permission").value(hasItem(DEFAULT_PERMISSION)));

        // Check, that the count call also returns 1
        restProjectPermissionMockMvc.perform(get("/api/project-permissions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectPermissionShouldNotBeFound(String filter) throws Exception {
        restProjectPermissionMockMvc.perform(get("/api/project-permissions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectPermissionMockMvc.perform(get("/api/project-permissions/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    // @Test
    @Transactional
    public void getNonExistingProjectPermission() throws Exception {
        // Get the projectPermission
        restProjectPermissionMockMvc.perform(get("/api/project-permissions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    // @Test
    @Transactional
    public void updateProjectPermission() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        int databaseSizeBeforeUpdate = projectPermissionRepository.findAll().size();

        // Update the projectPermission
        ProjectPermission updatedProjectPermission = createUpdatedEntity(em);
        // updatedProjectPermission.setId(projectPermission.getId());
        ProjectPermissionDTO projectPermissionDTO = projectPermissionMapper.toDto(updatedProjectPermission);

        restProjectPermissionMockMvc.perform(put("/api/project-permissions")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectPermissionDTO)))
            .andExpect(status().isOk());

        // Validate the ProjectPermission in the database
        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeUpdate);
        ProjectPermission testProjectPermission = projectPermissionList.get(projectPermissionList.size() - 1);
        assertThat(testProjectPermission.getUser()).isEqualTo(UPDATED_USER);
        assertThat(testProjectPermission.getPermission().getName()).isEqualTo(DEFAULT_PERMISSION);
    }

    // @Test
    @Transactional
    public void updateNonExistingProjectPermission() throws Exception {
        int databaseSizeBeforeUpdate = projectPermissionRepository.findAll().size();

        // Create the ProjectPermission
        ProjectPermissionDTO projectPermissionDTO = projectPermissionMapper.toDto(projectPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectPermissionMockMvc.perform(put("/api/project-permissions")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(projectPermissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProjectPermission in the database
        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    // @Test
    @Transactional
    public void deleteProjectPermission() throws Exception {
        // Initialize the database
        projectPermissionRepository.saveAndFlush(projectPermission);

        int databaseSizeBeforeDelete = projectPermissionRepository.findAll().size();

        // Delete the projectPermission
        /*restProjectPermissionMockMvc.perform(delete("/api/project-permissions/{id}", projectPermission.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());*/

        // Validate the database contains one less item
        List<ProjectPermission> projectPermissionList = projectPermissionRepository.findAll();
        assertThat(projectPermissionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
