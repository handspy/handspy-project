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

import pt.up.hs.project.domain.Participant;
import pt.up.hs.project.domain.*; // for static metamodels
import pt.up.hs.project.repository.ParticipantRepository;
import pt.up.hs.project.service.dto.ParticipantCriteria;
import pt.up.hs.project.service.dto.ParticipantDTO;
import pt.up.hs.project.service.mapper.ParticipantMapper;

/**
 * Service for executing complex queries for {@link Participant} entities in the database.
 * The main input is a {@link ParticipantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ParticipantDTO} or a {@link Page} of {@link ParticipantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantQueryService extends QueryService<Participant> {

    private final Logger log = LoggerFactory.getLogger(ParticipantQueryService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantQueryService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Return a {@link List} of {@link ParticipantDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findByCriteria(Long projectId, ParticipantCriteria criteria) {
        log.debug("find by criteria {} in project {}", criteria, projectId);
        final Specification<Participant> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return null; // participantMapper.toDto(participantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ParticipantDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @param page      The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findByCriteria(Long projectId, ParticipantCriteria criteria, Pageable page) {
        log.debug("find by criteria {}, page {} in project {}", criteria, page, projectId);
        final Specification<Participant> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return null; /*participantRepository.findAll(specification, page)
            .map(participantMapper::toDto);*/
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Long projectId, ParticipantCriteria criteria) {
        log.debug("count by criteria {} in project {}", criteria, projectId);
        final Specification<Participant> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return 0L; // participantRepository.count(specification);
    }

    /**
     * Function to convert {@link ParticipantCriteria} to a {@link Specification}
     *
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Participant> createSpecification(ParticipantCriteria criteria) {
        Specification<Participant> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Participant_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Participant_.name));
            }
            if (criteria.getGender() != null) {
                specification = specification.and(buildSpecification(criteria.getGender(), Participant_.gender));
            }
            if (criteria.getBirthdate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthdate(), Participant_.birthdate));
            }
            if (criteria.getHandedness() != null) {
                specification = specification.and(buildSpecification(criteria.getHandedness(), Participant_.handedness));
            }
            if (criteria.getAdditionalInfo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdditionalInfo(), Participant_.additionalInfo));
            }
            if (criteria.getLabelsId() != null) {
                specification = specification.and(buildSpecification(criteria.getLabelsId(),
                    root -> root.join(Participant_.labels, JoinType.LEFT).get(Label_.id)));
            }
        }
        return specification;
    }
}
