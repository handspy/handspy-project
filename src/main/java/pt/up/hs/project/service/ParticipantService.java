package pt.up.hs.project.service;

import pt.up.hs.project.service.dto.BulkImportResultDTO;
import pt.up.hs.project.service.dto.ParticipantDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.Participant}.
 */
public interface ParticipantService {

    /**
     * Save a participant.
     *
     * @param projectId      the ID of the project containing the participant.
     * @param participantDTO the entity to save.
     * @return the persisted entity.
     */
    ParticipantDTO save(Long projectId, ParticipantDTO participantDTO);

    /**
     * Save all participants.
     *
     * @param projectId       the ID of the project containing the participant.
     * @param participantDTOs the entities to save.
     * @return the persisted entities.
     */
    List<ParticipantDTO> saveAll(Long projectId, List<ParticipantDTO> participantDTOs);

    /**
     * Get all the participants.
     *
     * @param projectId the ID of the project containing the participants.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    Page<ParticipantDTO> findAll(Long projectId, Pageable pageable);

    /**
     * Get all the participants with eager load of many-to-many relationships.
     *
     * @param projectId the ID of the project containing the participants.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    Page<ParticipantDTO> findAllWithEagerRelationships(Long projectId, Pageable pageable);

    /**
     * Get the "id" participant.
     *
     * @param projectId the ID of the project containing the participant.
     * @param id        the id of the entity.
     * @return the entity.
     */
    Optional<ParticipantDTO> findOne(Long projectId, Long id);

    /**
     * Upload participants from CSV.
     *
     * @param projectId the ID of the project containing the participants.
     * @param is        {@link InputStream} the file input stream.
     * @param sep       column separator.
     * @param arraySep  separator for array elements.
     * @param useHeader does the CSV include an header?
     * @return {@link BulkImportResultDTO} response to CSV upload.
     */
    BulkImportResultDTO<ParticipantDTO> importFromCsv(
        Long projectId, InputStream is, String sep, String arraySep, boolean useHeader);

    /**
     * Delete the "id" participant.
     *
     * @param projectId the ID of the project containing the participant.
     * @param id        the id of the entity.
     */
    void delete(Long projectId, Long id);
}
