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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;
import pt.up.hs.project.ProjectApp;
import pt.up.hs.project.config.SecurityBeanOverrideConfiguration;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.enumeration.Gender;
import pt.up.hs.project.domain.enumeration.HandwritingMean;
import pt.up.hs.project.repository.ParticipantRepository;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.service.mapper.ParticipantMapper;
import pt.up.hs.project.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pt.up.hs.project.web.rest.TestUtil.createFormattingConversionService;

/**
 * Integration tests for the {@link ParticipantResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProjectApp.class})
public class ParticipantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final LocalDate DEFAULT_BIRTHDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTHDATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTHDATE = LocalDate.ofEpochDay(-1L);

    private static final HandwritingMean DEFAULT_HANDWRITING_MEAN = HandwritingMean.LEFT_HAND;
    private static final HandwritingMean UPDATED_HANDWRITING_MEAN = HandwritingMean.RIGHT_HAND;

    private static final String DEFAULT_ADDITIONAL_INFO = "AAAAAAAAAA";
    private static final String UPDATED_ADDITIONAL_INFO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String CSV_PARTICIPANT_1_NAME = "John Doe";
    private static final String CSV_PARTICIPANT_2_NAME = "Jane Doe";
    private static final String CSV_PARTICIPANT_3_NAME = "John";
    private static final String CSV_PARTICIPANT_4_NAME = "Jane";
    private static final Gender CSV_PARTICIPANT_1_GENDER = Gender.MALE;
    private static final Gender CSV_PARTICIPANT_2_GENDER = Gender.FEMALE;
    private static final Gender CSV_PARTICIPANT_3_GENDER = Gender.MALE;
    private static final Gender CSV_PARTICIPANT_4_GENDER = Gender.FEMALE;
    private static final HandwritingMean CSV_PARTICIPANT_1_HANDEDNESS = HandwritingMean.LEFT_HAND;
    private static final HandwritingMean CSV_PARTICIPANT_2_HANDEDNESS = HandwritingMean.OTHER;
    private static final HandwritingMean CSV_PARTICIPANT_3_HANDEDNESS = HandwritingMean.LEFT_HAND;
    private static final HandwritingMean CSV_PARTICIPANT_4_HANDEDNESS = HandwritingMean.RIGHT_HAND;
    private static final String CSV_PARTICIPANT_1_BIRTHDATE = "2020-02-23";
    private static final String CSV_PARTICIPANT_2_BIRTHDATE = "2020-02-23";
    private static final String CSV_PARTICIPANT_3_BIRTHDATE = "2020-02-22";
    private static final String CSV_PARTICIPANT_4_BIRTHDATE = "2020-02-23";
    private static final String CSV_PARTICIPANT_1_ADDITIONAL_INFO = "multi-tasking";
    private static final String CSV_PARTICIPANT_2_ADDITIONAL_INFO = "Run Avon, overriding";
    private static final String CSV_PARTICIPANT_3_ADDITIONAL_INFO = "Buckinghamshire";
    private static final String CSV_PARTICIPANT_4_ADDITIONAL_INFO = "Buckinghamshire";

    private static final String DEFAULT_USERNAME = "system";

    @Autowired
    private ParticipantRepository participantRepository;

    @Mock
    private ParticipantRepository participantRepositoryMock;

    @Autowired
    private ParticipantMapper participantMapper;

    @Mock
    private ParticipantService participantServiceMock;

    @Autowired
    private ParticipantService participantService;

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

    private MockMvc restParticipantMockMvc;

    private Long projectId;
    private Long labelId;
    private Participant participant;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParticipantResource participantResource = new ParticipantResource(participantService);
        this.restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
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
    public static Participant createEntity(Long projectId, Long[] labelIds) {
        Participant participant = new Participant()
            .name(DEFAULT_NAME)
            .gender(DEFAULT_GENDER)
            .birthdate(DEFAULT_BIRTHDATE)
            .handedness(DEFAULT_HANDWRITING_MEAN)
            .additionalInfo(DEFAULT_ADDITIONAL_INFO)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .projectId(projectId);

        Set<Label> labels = new HashSet<>();
        for (Long labelId: labelIds) {
            Label label = new Label();
            label.setId(labelId);
            label.addParticipants(participant);
            labels.add(label);
        }
        participant.setLabels(labels);

        return participant;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createUpdatedEntity(Long projectId, Long[] labelIds) {
        Participant participant = new Participant()
            .name(UPDATED_NAME)
            .gender(UPDATED_GENDER)
            .birthdate(UPDATED_BIRTHDATE)
            .handedness(UPDATED_HANDWRITING_MEAN)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .projectId(projectId);

        Set<Label> labels = new HashSet<>();
        for (Long labelId: labelIds) {
            Label label = new Label();
            label.setId(labelId);
            labels.add(label);
        }
        participant.setLabels(labels);

        return participant;
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
        participant = createEntity(projectId, new Long[] { labelId });
    }

    @Test
    @Transactional
    public void createParticipant() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // date before create
        Instant beforeInstant = Instant.now();

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);
        restParticipantMockMvc.perform(post("/api/projects/{projectId}/participants", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isCreated());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate + 1);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testParticipant.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testParticipant.getBirthdate()).isEqualTo(DEFAULT_BIRTHDATE);
        assertThat(testParticipant.getHandedness()).isEqualTo(DEFAULT_HANDWRITING_MEAN);
        assertThat(testParticipant.getAdditionalInfo()).isEqualTo(DEFAULT_ADDITIONAL_INFO);
        assertThat(testParticipant.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testParticipant.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testParticipant.getCreatedBy()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testParticipant.getCreatedDate()).isStrictlyBetween(beforeInstant, Instant.now());
    }

    @Test
    @Transactional
    public void createParticipantWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // Create the Participant with an existing ID
        participant.setId(1L);
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc.perform(post("/api/projects/{projectId}/participants", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = participantRepository.findAll().size();
        // set the field null
        participant.setName(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc.perform(post("/api/projects/{projectId}/participants", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParticipants() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList
        ResultActions resultActions = restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants?sort=id,desc", projectId));
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].birthdate").value(hasItem(DEFAULT_BIRTHDATE.toString())))
            .andExpect(jsonPath("$.[*].handedness").value(hasItem(DEFAULT_HANDWRITING_MEAN.toString())))
            .andExpect(jsonPath("$.[*].additionalInfo").value(hasItem(DEFAULT_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].labels").isArray())
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());
    }

    @Test
    @Transactional
    public void getAllParticipantsBasic() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants/basic", projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].labelIds.[*]").value(hasItem(labelId.intValue())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllParticipantsWithEagerRelationshipsIsEnabled() throws Exception {
        ParticipantResource participantResource = new ParticipantResource(participantServiceMock);
        when(participantServiceMock.findAllWithEagerRelationships(projectId, null, null, any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        MockMvc restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restParticipantMockMvc.perform(get("/api/participants?eagerload=true"))
            .andExpect(status().isOk());

        verify(participantServiceMock, times(1)).findAllWithEagerRelationships(projectId, null, null, any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllParticipantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        ParticipantResource participantResource = new ParticipantResource(participantServiceMock);
        when(participantServiceMock.findAllWithEagerRelationships(projectId, null, null, any())).thenReturn(new PageImpl<>(new ArrayList<>()));
        MockMvc restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants?eagerload=true", projectId))
            .andExpect(status().isOk());

        verify(participantServiceMock, times(1)).findAllWithEagerRelationships(projectId, null, null, any());
    }

    @Test
    @Transactional
    public void getParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get the participant
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants/{id}", projectId, participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participant.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.birthdate").value(DEFAULT_BIRTHDATE.toString()))
            .andExpect(jsonPath("$.handedness").value(DEFAULT_HANDWRITING_MEAN.toString()))
            .andExpect(jsonPath("$.additionalInfo").value(DEFAULT_ADDITIONAL_INFO))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.labels").isArray())
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.createdDate").exists());
    }

    /*@Test
    @Transactional
    public void getParticipantsByIdFiltering() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        Long id = participant.getId();

        defaultParticipantShouldBeFound("id.equals=" + id);
        defaultParticipantShouldNotBeFound("id.notEquals=" + id);

        defaultParticipantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultParticipantShouldNotBeFound("id.greaterThan=" + id);

        defaultParticipantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultParticipantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name equals to DEFAULT_NAME
        defaultParticipantShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the participantList where name equals to UPDATED_NAME
        defaultParticipantShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name not equals to DEFAULT_NAME
        defaultParticipantShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the participantList where name not equals to UPDATED_NAME
        defaultParticipantShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name in DEFAULT_NAME or UPDATED_NAME
        defaultParticipantShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the participantList where name equals to UPDATED_NAME
        defaultParticipantShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name is not null
        defaultParticipantShouldBeFound("name.specified=true");

        // Get all the participantList where name is null
        defaultParticipantShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameContainsSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name contains DEFAULT_NAME
        defaultParticipantShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the participantList where name contains UPDATED_NAME
        defaultParticipantShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllParticipantsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where name does not contain DEFAULT_NAME
        defaultParticipantShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the participantList where name does not contain UPDATED_NAME
        defaultParticipantShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllParticipantsByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where gender equals to DEFAULT_GENDER
        defaultParticipantShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the participantList where gender equals to UPDATED_GENDER
        defaultParticipantShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllParticipantsByGenderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where gender not equals to DEFAULT_GENDER
        defaultParticipantShouldNotBeFound("gender.notEquals=" + DEFAULT_GENDER);

        // Get all the participantList where gender not equals to UPDATED_GENDER
        defaultParticipantShouldBeFound("gender.notEquals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllParticipantsByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultParticipantShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the participantList where gender equals to UPDATED_GENDER
        defaultParticipantShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    public void getAllParticipantsByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where gender is not null
        defaultParticipantShouldBeFound("gender.specified=true");

        // Get all the participantList where gender is null
        defaultParticipantShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate equals to DEFAULT_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.equals=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate equals to UPDATED_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.equals=" + UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate not equals to DEFAULT_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.notEquals=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate not equals to UPDATED_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.notEquals=" + UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate in DEFAULT_BIRTHDATE or UPDATED_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.in=" + DEFAULT_BIRTHDATE + "," + UPDATED_BIRTHDATE);

        // Get all the participantList where birthdate equals to UPDATED_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.in=" + UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate is not null
        defaultParticipantShouldBeFound("birthdate.specified=true");

        // Get all the participantList where birthdate is null
        defaultParticipantShouldNotBeFound("birthdate.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate is greater than or equal to DEFAULT_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.greaterThanOrEqual=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate is greater than or equal to UPDATED_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.greaterThanOrEqual=" + UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate is less than or equal to DEFAULT_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.lessThanOrEqual=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate is less than or equal to SMALLER_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.lessThanOrEqual=" + SMALLER_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsLessThanSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate is less than DEFAULT_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.lessThan=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate is less than UPDATED_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.lessThan=" + UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByBirthdateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where birthdate is greater than DEFAULT_BIRTHDATE
        defaultParticipantShouldNotBeFound("birthdate.greaterThan=" + DEFAULT_BIRTHDATE);

        // Get all the participantList where birthdate is greater than SMALLER_BIRTHDATE
        defaultParticipantShouldBeFound("birthdate.greaterThan=" + SMALLER_BIRTHDATE);
    }


    @Test
    @Transactional
    public void getAllParticipantsByHandednessIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where handedness equals to DEFAULT_HANDEDNESS
        defaultParticipantShouldBeFound("handedness.equals=" + DEFAULT_HANDWRITING_MEAN);

        // Get all the participantList where handedness equals to UPDATED_HANDEDNESS
        defaultParticipantShouldNotBeFound("handedness.equals=" + UPDATED_HANDWRITING_MEAN);
    }

    @Test
    @Transactional
    public void getAllParticipantsByHandednessIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where handedness not equals to DEFAULT_HANDEDNESS
        defaultParticipantShouldNotBeFound("handedness.notEquals=" + DEFAULT_HANDWRITING_MEAN);

        // Get all the participantList where handedness not equals to UPDATED_HANDEDNESS
        defaultParticipantShouldBeFound("handedness.notEquals=" + UPDATED_HANDWRITING_MEAN);
    }

    @Test
    @Transactional
    public void getAllParticipantsByHandednessIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where handedness in DEFAULT_HANDEDNESS or UPDATED_HANDEDNESS
        defaultParticipantShouldBeFound("handedness.in=" + DEFAULT_HANDWRITING_MEAN + "," + UPDATED_HANDWRITING_MEAN);

        // Get all the participantList where handedness equals to UPDATED_HANDEDNESS
        defaultParticipantShouldNotBeFound("handedness.in=" + UPDATED_HANDWRITING_MEAN);
    }

    @Test
    @Transactional
    public void getAllParticipantsByHandednessIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where handedness is not null
        defaultParticipantShouldBeFound("handedness.specified=true");

        // Get all the participantList where handedness is null
        defaultParticipantShouldNotBeFound("handedness.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo equals to DEFAULT_ADDITIONAL_INFO
        defaultParticipantShouldBeFound("additionalInfo.equals=" + DEFAULT_ADDITIONAL_INFO);

        // Get all the participantList where additionalInfo equals to UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldNotBeFound("additionalInfo.equals=" + UPDATED_ADDITIONAL_INFO);
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo not equals to DEFAULT_ADDITIONAL_INFO
        defaultParticipantShouldNotBeFound("additionalInfo.notEquals=" + DEFAULT_ADDITIONAL_INFO);

        // Get all the participantList where additionalInfo not equals to UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldBeFound("additionalInfo.notEquals=" + UPDATED_ADDITIONAL_INFO);
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo in DEFAULT_ADDITIONAL_INFO or UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldBeFound("additionalInfo.in=" + DEFAULT_ADDITIONAL_INFO + "," + UPDATED_ADDITIONAL_INFO);

        // Get all the participantList where additionalInfo equals to UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldNotBeFound("additionalInfo.in=" + UPDATED_ADDITIONAL_INFO);
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo is not null
        defaultParticipantShouldBeFound("additionalInfo.specified=true");

        // Get all the participantList where additionalInfo is null
        defaultParticipantShouldNotBeFound("additionalInfo.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoContainsSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo contains DEFAULT_ADDITIONAL_INFO
        defaultParticipantShouldBeFound("additionalInfo.contains=" + DEFAULT_ADDITIONAL_INFO);

        // Get all the participantList where additionalInfo contains UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldNotBeFound("additionalInfo.contains=" + UPDATED_ADDITIONAL_INFO);
    }

    @Test
    @Transactional
    public void getAllParticipantsByAdditionalInfoNotContainsSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where additionalInfo does not contain DEFAULT_ADDITIONAL_INFO
        defaultParticipantShouldNotBeFound("additionalInfo.doesNotContain=" + DEFAULT_ADDITIONAL_INFO);

        // Get all the participantList where additionalInfo does not contain UPDATED_ADDITIONAL_INFO
        defaultParticipantShouldBeFound("additionalInfo.doesNotContain=" + UPDATED_ADDITIONAL_INFO);
    }


    @Test
    @Transactional
    public void getAllParticipantsByLabelsIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);
        Label labels = LabelResourceIT.createEntity(projectId);
        em.persist(labels);
        em.flush();
        participant.addLabels(labels);
        participantRepository.saveAndFlush(participant);
        Long labelsId = labels.getId();

        // Get all the participantList where labels equals to labelsId
        defaultParticipantShouldBeFound("labelsId.equals=" + labelsId);

        // Get all the participantList where labels equals to labelsId + 1
        defaultParticipantShouldNotBeFound("labelsId.equals=" + (labelsId + 1));
    }*/

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParticipantShouldBeFound(String filter) throws Exception {
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].birthdate").value(hasItem(DEFAULT_BIRTHDATE.toString())))
            .andExpect(jsonPath("$.[*].handedness").value(hasItem(DEFAULT_HANDWRITING_MEAN.toString())))
            .andExpect(jsonPath("$.[*].additionalInfo").value(hasItem(DEFAULT_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].labels").isArray())
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].createdDate").exists());

        // Check, that the count call also returns 1
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParticipantShouldNotBeFound(String filter) throws Exception {
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants/count?sort=id,desc&" + filter, projectId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingParticipant() throws Exception {
        // Get the participant
        restParticipantMockMvc.perform(get("/api/projects/{projectId}/participants/{id}", projectId, Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParticipant() throws Exception {

        Instant beforeCreateInstant = Instant.now();

        // Initialize the database
        participantRepository.saveAndFlush(participant);

        Instant afterCreateInstant = Instant.now();

        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Update the participant
        Participant updatedParticipant = participantRepository.findById(participant.getId()).get();
        // Disconnect from session so that the updates on updatedParticipant are not directly saved in db
        em.detach(updatedParticipant);
        updatedParticipant
            .name(UPDATED_NAME)
            .gender(UPDATED_GENDER)
            .birthdate(UPDATED_BIRTHDATE)
            .handedness(UPDATED_HANDWRITING_MEAN)
            .additionalInfo(UPDATED_ADDITIONAL_INFO)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        ParticipantDTO participantDTO = participantMapper.toDto(updatedParticipant);

        Instant beforeUpdateInstant = Instant.now();

        restParticipantMockMvc.perform(put("/api/projects/{projectId}/participants", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isOk());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testParticipant.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testParticipant.getBirthdate()).isEqualTo(UPDATED_BIRTHDATE);
        assertThat(testParticipant.getHandedness()).isEqualTo(UPDATED_HANDWRITING_MEAN);
        assertThat(testParticipant.getAdditionalInfo()).isEqualTo(UPDATED_ADDITIONAL_INFO);
        assertThat(testParticipant.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testParticipant.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testParticipant.getCreatedDate()).isStrictlyBetween(beforeCreateInstant, afterCreateInstant);
        assertThat(testParticipant.getLastModifiedDate()).isStrictlyBetween(beforeUpdateInstant, Instant.now());
    }

    @Test
    @Transactional
    public void updateNonExistingParticipant() throws Exception {
        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc.perform(put("/api/projects/{projectId}/participants", projectId)
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        int databaseSizeBeforeDelete = participantRepository.findAll().size();

        // Delete the participant
        restParticipantMockMvc.perform(delete("/api/projects/{projectId}/participants/{id}", projectId, participant.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void importParticipantsCsv() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_1_NAME, CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_1_GENDER.toString(), CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(CSV_PARTICIPANT_1_HANDEDNESS.toString(), CSV_PARTICIPANT_2_HANDEDNESS.toString(), CSV_PARTICIPANT_3_HANDEDNESS.toString(), CSV_PARTICIPANT_4_HANDEDNESS.toString())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_1_BIRTHDATE, CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(CSV_PARTICIPANT_1_ADDITIONAL_INFO, CSV_PARTICIPANT_2_ADDITIONAL_INFO, CSV_PARTICIPANT_3_ADDITIONAL_INFO, CSV_PARTICIPANT_4_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importParticipantsCsvNoHeader() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants-no-header.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants-no-header.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
                    .queryParam("use-header", "false")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_1_NAME, CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_1_GENDER.toString(), CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(CSV_PARTICIPANT_1_HANDEDNESS.toString(), CSV_PARTICIPANT_2_HANDEDNESS.toString(), CSV_PARTICIPANT_3_HANDEDNESS.toString(), CSV_PARTICIPANT_4_HANDEDNESS.toString())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_1_BIRTHDATE, CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(CSV_PARTICIPANT_1_ADDITIONAL_INFO, CSV_PARTICIPANT_2_ADDITIONAL_INFO, CSV_PARTICIPANT_3_ADDITIONAL_INFO, CSV_PARTICIPANT_4_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importParticipantsCsvDiffColumnOrder() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants-diff-column-order.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants-diff-column-order.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_1_NAME, CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_1_GENDER.toString(), CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(CSV_PARTICIPANT_1_HANDEDNESS.toString(), CSV_PARTICIPANT_2_HANDEDNESS.toString(), CSV_PARTICIPANT_3_HANDEDNESS.toString(), CSV_PARTICIPANT_4_HANDEDNESS.toString())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_1_BIRTHDATE, CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(CSV_PARTICIPANT_1_ADDITIONAL_INFO, CSV_PARTICIPANT_2_ADDITIONAL_INFO, CSV_PARTICIPANT_3_ADDITIONAL_INFO, CSV_PARTICIPANT_4_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importParticipantsCsvDiffSep() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants-diff-sep.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants-diff-sep.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
                    .queryParam("sep", ";")
                    .queryParam("array-sep", ",")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_1_NAME, CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_1_GENDER.toString(), CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(CSV_PARTICIPANT_1_HANDEDNESS.toString(), CSV_PARTICIPANT_2_HANDEDNESS.toString(), CSV_PARTICIPANT_3_HANDEDNESS.toString(), CSV_PARTICIPANT_4_HANDEDNESS.toString())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_1_BIRTHDATE, CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(CSV_PARTICIPANT_1_ADDITIONAL_INFO, CSV_PARTICIPANT_2_ADDITIONAL_INFO, CSV_PARTICIPANT_3_ADDITIONAL_INFO, CSV_PARTICIPANT_4_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importParticipantsCsvWrongColumns() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants-wrong-columns.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants-wrong-columns.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(4))
            .andExpect(jsonPath("$.invalid").value(0))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_1_NAME, CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_1_GENDER.toString(), CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_1_BIRTHDATE, CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue())))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(2, 1, 0, 1)));
    }

    @Test
    @Transactional
    public void importParticipantsCsvInvalidRecord() throws Exception {
        // read file
        byte[] content = TestUtil.readFileFromResourcesFolder("data/participants/participants-invalid-record.csv");
        MockMultipartFile file = new MockMultipartFile("file", "participants-invalid-record.csv", null, content);

        // Import the participants' CSV
        restParticipantMockMvc
            .perform(
                MockMvcRequestBuilders
                    .multipart("/api/projects/{projectId}/participants/import", projectId)
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.total").value(3))
            .andExpect(jsonPath("$.invalid").value(1))
            .andExpect(jsonPath("$.data.[*].name").value(containsInAnyOrder(CSV_PARTICIPANT_2_NAME, CSV_PARTICIPANT_3_NAME, CSV_PARTICIPANT_4_NAME)))
            .andExpect(jsonPath("$.data.[*].gender").value(containsInAnyOrder(CSV_PARTICIPANT_2_GENDER.toString(), CSV_PARTICIPANT_3_GENDER.toString(), CSV_PARTICIPANT_4_GENDER.toString())))
            .andExpect(jsonPath("$.data.[*].handedness").value(containsInAnyOrder(CSV_PARTICIPANT_2_HANDEDNESS.toString(), CSV_PARTICIPANT_3_HANDEDNESS.toString(), CSV_PARTICIPANT_4_HANDEDNESS.toString())))
            .andExpect(jsonPath("$.data.[*].birthdate").value(containsInAnyOrder(CSV_PARTICIPANT_2_BIRTHDATE, CSV_PARTICIPANT_3_BIRTHDATE, CSV_PARTICIPANT_4_BIRTHDATE)))
            .andExpect(jsonPath("$.data.[*].additionalInfo").value(containsInAnyOrder(CSV_PARTICIPANT_2_ADDITIONAL_INFO, CSV_PARTICIPANT_3_ADDITIONAL_INFO, CSV_PARTICIPANT_4_ADDITIONAL_INFO)))
            .andExpect(jsonPath("$.data.[*].labels.length()").value(containsInAnyOrder(1, 0, 1)));
    }
}
