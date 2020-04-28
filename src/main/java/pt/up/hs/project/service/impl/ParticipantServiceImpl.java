package pt.up.hs.project.service.impl;

import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.service.ParticipantService;
import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.repository.ParticipantRepository;
import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.service.importer.dto.ParticipantCsvDTO;
import pt.up.hs.project.service.importer.reader.CsvReader;
import pt.up.hs.project.service.mapper.ParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.utils.Genders;
import pt.up.hs.project.utils.HandwritingMeans;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Participant}.
 */
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final Logger log = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    private final LabelService labelService;

    public ParticipantServiceImpl(
        ParticipantRepository participantRepository,
        ParticipantMapper participantMapper,
        LabelService labelService
    ) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
        this.labelService = labelService;
    }

    /**
     * Save a participant.
     *
     * @param projectId      the ID of the project containing the participant.
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ParticipantDTO save(Long projectId, ParticipantDTO participantDTO) {
        log.debug("Request to save Participant {} in project {}", participantDTO, projectId);
        Participant participant = participantMapper.toEntity(participantDTO);
        participant.setProjectId(projectId);
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Save all participants.
     *
     * @param projectId       the ID of the project containing the participant.
     * @param participantDTOs the entities to save.
     * @return the persisted entities.
     */
    @Override
    public List<ParticipantDTO> saveAll(Long projectId, List<ParticipantDTO> participantDTOs) {
        log.debug("Request to save all Participants in project {}", projectId);
        return participantRepository
            .saveAll(
                participantDTOs.parallelStream()
                    .map(participantDTO -> {
                        Participant participant = participantMapper.toEntity(participantDTO);
                        participant.setProjectId(projectId);
                        return participant;
                    })
                    .collect(Collectors.toList())
            ).parallelStream()
            .map(participantMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Get all the participants.
     *
     * @param projectId the ID of the project containing the participants.
     * @param search the search string.
     * @param labels the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findAll(Long projectId, String search, List<Long> labels, Pageable pageable) {
        log.debug("Request to get all Participants from project {}", projectId);
        return participantRepository.findAllByProjectId(projectId, search, labels, pageable)
            .map(participantMapper::toDto);
    }

    /**
     * Get all the participants with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the participants.
     * @param search the search string.
     * @param labels the ids of the labels to filter by.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findAllWithEagerRelationships(Long projectId, String search, List<Long> labels, Pageable pageable) {
        log.debug("Request to get all Participants with eager relationships from project {}", projectId);
        return participantRepository
            .findAllWithEagerRelationships(projectId, search, labels, pageable)
            .map(participantMapper::toDto);
    }

    /**
     * Count the participants with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the tasks.
     * @param search the search string.
     * @param labels the ids of the labels to filter by.
     * @return the number of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public long count(Long projectId, String search, List<Long> labels) {
        log.debug("Request to count Participants from project {}", projectId);
        return participantRepository.count(projectId, search, labels);
    }

    /**
     * Get the "id" participant.
     *
     * @param projectId the ID of the project containing the participant.
     * @param id        the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(Long projectId, Long id) {
        log.debug("Request to get Participant {} from project {}", id, projectId);
        return participantRepository.findByProjectIdAndId(projectId, id)
            .map(participantMapper::toDto);
    }

    /**
     * Upload participants from CSV.
     *
     * @param projectId the ID of the project of the participants.
     * @param is        {@link InputStream} the file input stream.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @return {@link BulkImportResultDTO} response to CSV upload.
     */
    @Override
    public BulkImportResultDTO<ParticipantDTO> importFromCsv(
        Long projectId, InputStream is, String sep, String arraySep, boolean useHeader
    ) {
        log.debug("Request to import Participants from CSV to project {}", projectId);
        long startTime = new Date().getTime();
        CsvReader<ParticipantCsvDTO> reader = CsvReader.fromInputStream(
            ParticipantCsvDTO.class, is, sep, useHeader
        );
        List<ParticipantDTO> participantDTOs = reader.getAll().stream().map(participantCsvDTO -> {
            try {
                ParticipantDTO participantDTO = new ParticipantDTO();
                participantDTO.setProjectId(projectId);
                participantDTO.setName(participantCsvDTO.getName());
                participantDTO.setGender(Genders.fromString(participantCsvDTO.getGender()));
                participantDTO.setHandedness(HandwritingMeans.fromString(participantCsvDTO.getHandedness()));
                participantDTO.setBirthdate(participantCsvDTO.getBirthdate());
                participantDTO.setAdditionalInfo(participantCsvDTO.getAdditionalInfo());
                if (participantCsvDTO.getLabels() != null) {
                    participantDTO.setLabels(
                        Arrays.stream(participantCsvDTO.getLabels().split(arraySep))
                            .map(String::trim)
                            .filter(name -> !name.isEmpty())
                            .map(name -> labelService.createIfNameNotExists(projectId, name))
                            .collect(Collectors.toSet())
                    );
                }
                return participantDTO;
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<ParticipantDTO> savedParticipantDTOs = saveAll(projectId, participantDTOs);
        long endTime = new Date().getTime();
        return new BulkImportResultDTO<>(
            savedParticipantDTOs.size(),
            reader.getExceptionLines().length + (participantDTOs.size() - savedParticipantDTOs.size()),
            savedParticipantDTOs,
            endTime - startTime
        );
    }

    /**
     * Delete the "id" participant.
     *
     * @param projectId the ID of the project of the participant.
     * @param id        the id of the entity.
     */
    @Override
    public void delete(Long projectId, Long id) {
        log.debug("Request to delete Participant {} from project {}", id, projectId);
        participantRepository.deleteAllByProjectIdAndId(projectId, id);
    }
}
