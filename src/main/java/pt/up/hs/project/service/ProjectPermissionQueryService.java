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

import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.domain.*; // for static metamodels
import pt.up.hs.project.repository.ProjectPermissionRepository;
import pt.up.hs.project.service.dto.ProjectPermissionCriteria;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;
import pt.up.hs.project.service.mapper.ProjectPermissionMapper;

/**
 * Service for executing complex queries for {@link ProjectPermission} entities in the database.
 * The main input is a {@link ProjectPermissionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjectPermissionDTO} or a {@link Page} of {@link ProjectPermissionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectPermissionQueryService extends QueryService<ProjectPermission> {

    private final Logger log = LoggerFactory.getLogger(ProjectPermissionQueryService.class);

    private final ProjectPermissionRepository projectPermissionRepository;

    private final ProjectPermissionMapper projectPermissionMapper;

    public ProjectPermissionQueryService(ProjectPermissionRepository projectPermissionRepository, ProjectPermissionMapper projectPermissionMapper) {
        this.projectPermissionRepository = projectPermissionRepository;
        this.projectPermissionMapper = projectPermissionMapper;
    }

    /**
     * Return a {@link List} of {@link ProjectPermissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjectPermissionDTO> findByCriteria(ProjectPermissionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProjectPermission> specification = createSpecification(criteria);
        return projectPermissionMapper.toDto(projectPermissionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjectPermissionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectPermissionDTO> findByCriteria(ProjectPermissionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProjectPermission> specification = createSpecification(criteria);
        return projectPermissionRepository.findAll(specification, page)
            .map(projectPermissionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectPermissionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProjectPermission> specification = createSpecification(criteria);
        return projectPermissionRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectPermissionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProjectPermission> createSpecification(ProjectPermissionCriteria criteria) {
        Specification<ProjectPermission> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProjectPermission_.id));
            }
            if (criteria.getUser() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUser(), ProjectPermission_.user));
            }
            if (criteria.getPermission() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPermission(), ProjectPermission_.permission));
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(buildSpecification(criteria.getProjectId(),
                    root -> root.join(ProjectPermission_.project, JoinType.LEFT).get(Project_.id)));
            }
        }
        return specification;
    }
}
