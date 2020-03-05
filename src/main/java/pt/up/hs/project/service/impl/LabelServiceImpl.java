package pt.up.hs.project.service.impl;

import pt.up.hs.project.service.LabelService;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.mapper.LabelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Label}.
 */
@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    private final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    /**
     * Save a label.
     *
     * @param projectId ID of the project containing the label.
     * @param labelDTO  the entity to save.
     * @return the persisted entity.
     */
    @Override
    public LabelDTO save(Long projectId, LabelDTO labelDTO) {
        log.debug("Request to save Label {} from project {}", labelDTO, projectId);
        Label label = labelMapper.toEntity(labelDTO);
        label.setProjectId(projectId);
        label = labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    /**
     * Create a label with name "name" if one does not exist yet.
     *
     * @param projectId ID of the project containing the label.
     * @param name the name of the label.
     * @return the entity with name "name".
     */
    @Override
    public LabelDTO createIfNameNotExists(Long projectId, String name) {
        log.debug("Request to create Label with name {} in project {} if not exists", name, projectId);
        Optional<LabelDTO> optionalLabelDTO = findOneByName(projectId, name);
        if (optionalLabelDTO.isPresent()) {
            return optionalLabelDTO.get();
        }
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setName(name);
        return save(projectId, labelDTO);
    }

    /**
     * Get all the labels.
     *
     * @param projectId ID of the project containing the labels.
     * @param pageable  the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LabelDTO> findAll(Long projectId, Pageable pageable) {
        log.debug("Request to get all Labels from project {}", projectId);
        return labelRepository.findAllByProjectId(projectId, pageable)
            .map(labelMapper::toDto);
    }

    /**
     * Get the "id" label.
     *
     * @param projectId ID of the project containing the label.
     * @param id        the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabelDTO> findOne(Long projectId, Long id) {
        log.debug("Request to get Label {} from project {}", id, projectId);
        return labelRepository.findByProjectIdAndId(projectId, id)
            .map(labelMapper::toDto);
    }

    /**
     * Get the label with "name".
     *
     * @param projectId ID of the project containing the label.
     * @param name      the name of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabelDTO> findOneByName(Long projectId, String name) {
        log.debug("Request to get Label with name {} from project {}", name, projectId);
        return labelRepository.findByProjectIdAndName(projectId, name)
            .map(labelMapper::toDto);
    }

    /**
     * Delete the "id" label.
     *
     * @param projectId ID of the project containing the label.
     * @param id        the id of the entity.
     */
    @Override
    public void delete(Long projectId, Long id) {
        log.debug("Request to delete Label {} from project {}", id, projectId);
        labelRepository.deleteAllByProjectIdAndId(projectId, id);
    }
}
