package pt.up.hs.project.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.*; // for static metamodels
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.service.dto.LabelCriteria;
import pt.up.hs.project.service.dto.LabelDTO;
import pt.up.hs.project.service.mapper.LabelMapper;

/**
 * Service for executing complex queries for {@link Label} entities in the database.
 * The main input is a {@link LabelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LabelDTO} or a {@link Page} of {@link LabelDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LabelQueryService extends QueryService<Label> {

    private final Logger log = LoggerFactory.getLogger(LabelQueryService.class);

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    public LabelQueryService(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    /**
     * Return a {@link List} of {@link LabelDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LabelDTO> findByCriteria(Long projectId, LabelCriteria criteria) {
        log.debug("find by criteria {} in project {}", criteria, projectId);
        final Specification<Label> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return labelMapper.toDto(labelRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LabelDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LabelDTO> findByCriteria(Long projectId, LabelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {} in project {}", criteria, page, projectId);
        final Specification<Label> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return labelRepository.findAll(specification, page)
            .map(labelMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param projectId ID of the container project.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Long projectId, LabelCriteria criteria) {
        log.debug("count by criteria {} in project {}", criteria, projectId);
        final Specification<Label> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return labelRepository.count(specification);
    }

    /**
     * Function to convert {@link LabelCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Label> createSpecification(LabelCriteria criteria) {
        Specification<Label> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Label_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Label_.name));
            }
            if (criteria.getColor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getColor(), Label_.color));
            }
            if (criteria.getParticipantsId() != null) {
                specification = specification.and(buildSpecification(criteria.getParticipantsId(),
                    root -> root.join(Label_.participants, JoinType.LEFT).get(Participant_.id)));
            }
            if (criteria.getTasksId() != null) {
                specification = specification.and(buildSpecification(criteria.getTasksId(),
                    root -> root.join(Label_.tasks, JoinType.LEFT).get(Task_.id)));
            }
        }
        return specification;
    }
}
