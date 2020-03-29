package pt.up.hs.project.service;

import pt.up.hs.project.service.dto.LabelDTO;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.Label}.
 */
public interface LabelService {

    /**
     * Save a label.
     *
     * @param projectId ID of the project containing the label.
     * @param labelDTO the entity to save.
     * @return the persisted entity.
     */
    LabelDTO save(Long projectId, LabelDTO labelDTO);

    /**
     * Create a label with name "name" if one does not exist yet.
     *
     * @param projectId ID of the project containing the label.
     * @param name the name of the label.
     * @return the entity with name "name".
     */
    LabelDTO createIfNameNotExists(Long projectId, String name);

    /**
     * Get all the labels.
     *
     * @param projectId ID of the project containing the labels.
     * @return the list of entities.
     */
    List<LabelDTO> findAll(Long projectId);

    /**
     * Count the labels.
     *
     * @param projectId ID of the project containing the labels.
     * @return the number of entities.
     */
    long count(Long projectId);

    /**
     * Get the "id" label.
     *
     * @param projectId ID of the project containing the label.
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LabelDTO> findOne(Long projectId, Long id);

    /**
     * Get the label with "name".
     *
     * @param projectId ID of the project containing the label.
     * @param name the name of the entity.
     * @return the entity.
     */
    Optional<LabelDTO> findOneByName(Long projectId, String name);

    /**
     * Delete the "id" label.
     *
     * @param projectId ID of the project containing the label.
     * @param id the id of the entity.
     */
    void delete(Long projectId, Long id);
}
