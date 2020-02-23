package pt.up.hs.project.service;

import pt.up.hs.project.service.dto.LabelDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.Label}.
 */
public interface LabelService {

    /**
     * Save a label.
     *
     * @param labelDTO the entity to save.
     * @return the persisted entity.
     */
    LabelDTO save(LabelDTO labelDTO);

    /**
     * Get all the labels.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LabelDTO> findAll(Pageable pageable);

    /**
     * Get the "id" label.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LabelDTO> findOne(Long id);

    /**
     * Delete the "id" label.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
