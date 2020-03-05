package pt.up.hs.project.web.rest;

import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.mapper.LabelMapper;
import pt.up.hs.project.web.rest.errors.ExceptionTranslator;
import pt.up.hs.project.service.dto.LabelCriteria;
import pt.up.hs.project.service.LabelQueryService;

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
 * Integration tests for the {@link LabelResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class LabelResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelService labelService;

    @Autowired
    private LabelQueryService labelQueryService;

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

    private MockMvc restLabelMockMvc;

    private Long projectId;
    private Label label;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LabelResource labelResource = new LabelResource(labelService, labelQueryService);
        this.restLabelMockMvc = MockMvcBuilders.standaloneSetup(labelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Label createEntity(Long projectId) {
        return new Label()
            .name(DEFAULT_NAME)
            .color(DEFAULT_COLOR)
            .projectId(projectId);
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Label createUpdatedEntity(Long projectId) {
        return new Label()
            .name(UPDATED_NAME)
            .color(UPDATED_COLOR)
            .projectId(projectId);
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

    @BeforeEach
    public void initTest() {
        projectId = getProject(em, ProjectResourceIT.createEntity(em)).getId();
        label = createEntity(projectId);
    }

    @Test
    @Transactional
    public void createLabel() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();

        // Create the Label
        LabelDTO labelDTO = labelMapper.toDto(label);
        restLabelMockMvc.perform(post("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isCreated());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeCreate + 1);
        Label testLabel = labelList.get(labelList.size() - 1);
        assertThat(testLabel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLabel.getColor()).isEqualTo(DEFAULT_COLOR);
    }

    @Test
    @Transactional
    public void createLabelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();

        // Create the Label with an existing ID
        label.setId(1L);
        LabelDTO labelDTO = labelMapper.toDto(label);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLabelMockMvc.perform(post("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = labelRepository.findAll().size();
        // set the field null
        label.setName(null);

        // Create the Label, which fails.
        LabelDTO labelDTO = labelMapper.toDto(label);

        restLabelMockMvc.perform(post("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isBadRequest());

        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkColorIsRequired() throws Exception {
        int databaseSizeBeforeTest = labelRepository.findAll().size();
        // set the field null
        label.setColor(null);

        // Create the Label, which fails.
        LabelDTO labelDTO = labelMapper.toDto(label);

        restLabelMockMvc.perform(post("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isBadRequest());

        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLabels() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels?sort=id,desc", projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(label.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));
    }

    @Test
    @Transactional
    public void getLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get the label
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels/{id}", projectId, label.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(label.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR));
    }


    @Test
    @Transactional
    public void getLabelsByIdFiltering() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        Long id = label.getId();

        defaultLabelShouldBeFound("id.equals=" + id);
        defaultLabelShouldNotBeFound("id.notEquals=" + id);

        defaultLabelShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLabelShouldNotBeFound("id.greaterThan=" + id);

        defaultLabelShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLabelShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllLabelsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name equals to DEFAULT_NAME
        defaultLabelShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the labelList where name equals to UPDATED_NAME
        defaultLabelShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLabelsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name not equals to DEFAULT_NAME
        defaultLabelShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the labelList where name not equals to UPDATED_NAME
        defaultLabelShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLabelsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLabelShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the labelList where name equals to UPDATED_NAME
        defaultLabelShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLabelsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name is not null
        defaultLabelShouldBeFound("name.specified=true");

        // Get all the labelList where name is null
        defaultLabelShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllLabelsByNameContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name contains DEFAULT_NAME
        defaultLabelShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the labelList where name contains UPDATED_NAME
        defaultLabelShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllLabelsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where name does not contain DEFAULT_NAME
        defaultLabelShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the labelList where name does not contain UPDATED_NAME
        defaultLabelShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllLabelsByColorIsEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color equals to DEFAULT_COLOR
        defaultLabelShouldBeFound("color.equals=" + DEFAULT_COLOR);

        // Get all the labelList where color equals to UPDATED_COLOR
        defaultLabelShouldNotBeFound("color.equals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllLabelsByColorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color not equals to DEFAULT_COLOR
        defaultLabelShouldNotBeFound("color.notEquals=" + DEFAULT_COLOR);

        // Get all the labelList where color not equals to UPDATED_COLOR
        defaultLabelShouldBeFound("color.notEquals=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllLabelsByColorIsInShouldWork() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color in DEFAULT_COLOR or UPDATED_COLOR
        defaultLabelShouldBeFound("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR);

        // Get all the labelList where color equals to UPDATED_COLOR
        defaultLabelShouldNotBeFound("color.in=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllLabelsByColorIsNullOrNotNull() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color is not null
        defaultLabelShouldBeFound("color.specified=true");

        // Get all the labelList where color is null
        defaultLabelShouldNotBeFound("color.specified=false");
    }

    @Test
    @Transactional
    public void getAllLabelsByColorContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color contains DEFAULT_COLOR
        defaultLabelShouldBeFound("color.contains=" + DEFAULT_COLOR);

        // Get all the labelList where color contains UPDATED_COLOR
        defaultLabelShouldNotBeFound("color.contains=" + UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void getAllLabelsByColorNotContainsSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        // Get all the labelList where color does not contain DEFAULT_COLOR
        defaultLabelShouldNotBeFound("color.doesNotContain=" + DEFAULT_COLOR);

        // Get all the labelList where color does not contain UPDATED_COLOR
        defaultLabelShouldBeFound("color.doesNotContain=" + UPDATED_COLOR);
    }


    @Test
    @Transactional
    public void getAllLabelsByParticipantsIsEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        Participant participants = ParticipantResourceIT.createEntity(projectId);
        em.persist(participants);
        em.flush();
        label.addParticipants(participants);
        labelRepository.saveAndFlush(label);
        Long participantsId = participants.getId();

        // Get all the labelList where participants equals to participantsId
        defaultLabelShouldBeFound("participantsId.equals=" + participantsId);

        // Get all the labelList where participants equals to participantsId + 1
        defaultLabelShouldNotBeFound("participantsId.equals=" + (participantsId + 1));
    }


    @Test
    @Transactional
    public void getAllLabelsByTasksIsEqualToSomething() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        Task tasks = TaskResourceIT.createEntity(projectId);
        em.persist(tasks);
        em.flush();
        label.addTasks(tasks);
        labelRepository.saveAndFlush(label);
        Long tasksId = tasks.getId();

        // Get all the labelList where tasks equals to tasksId
        defaultLabelShouldBeFound("tasksId.equals=" + tasksId);

        // Get all the labelList where tasks equals to tasksId + 1
        defaultLabelShouldNotBeFound("tasksId.equals=" + (tasksId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLabelShouldBeFound(String filter) throws Exception {
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(label.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)));

        // Check, that the count call also returns 1
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLabelShouldNotBeFound(String filter) throws Exception {
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingLabel() throws Exception {
        // Get the label
        restLabelMockMvc.perform(get("/api/projects/{projectId}/labels/{id}", projectId, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        int databaseSizeBeforeUpdate = labelRepository.findAll().size();

        // Update the label
        Label updatedLabel = labelRepository.findById(label.getId()).get();
        // Disconnect from session so that the updates on updatedLabel are not directly saved in db
        em.detach(updatedLabel);
        updatedLabel
            .name(UPDATED_NAME)
            .color(UPDATED_COLOR);
        LabelDTO labelDTO = labelMapper.toDto(updatedLabel);

        restLabelMockMvc.perform(put("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isOk());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
        Label testLabel = labelList.get(labelList.size() - 1);
        assertThat(testLabel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLabel.getColor()).isEqualTo(UPDATED_COLOR);
    }

    @Test
    @Transactional
    public void updateNonExistingLabel() throws Exception {
        int databaseSizeBeforeUpdate = labelRepository.findAll().size();

        // Create the Label
        LabelDTO labelDTO = labelMapper.toDto(label);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLabelMockMvc.perform(put("/api/projects/{projectId}/labels", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(labelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);

        int databaseSizeBeforeDelete = labelRepository.findAll().size();

        // Delete the label
        restLabelMockMvc.perform(delete("/api/projects/{projectId}/labels/{id}", projectId, label.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
