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

import pt.up.hs.project.domain.Task;
import pt.up.hs.project.domain.*; // for static metamodels
import pt.up.hs.project.repository.TaskRepository;
import pt.up.hs.project.service.dto.TaskCriteria;
import pt.up.hs.project.service.dto.TaskDTO;
import pt.up.hs.project.service.mapper.TaskMapper;

/**
 * Service for executing complex queries for {@link Task} entities in the database.
 * The main input is a {@link TaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TaskDTO} or a {@link Page} of {@link TaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskQueryService extends QueryService<Task> {

    private final Logger log = LoggerFactory.getLogger(TaskQueryService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskQueryService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Return a {@link List} of {@link TaskDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> findByCriteria(Long projectId, TaskCriteria criteria) {
        log.debug("find by criteria {} in project {}", criteria, projectId);
        final Specification<Task> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return null; // taskMapper.toDto(taskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TaskDTO} which matches the criteria from the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @param page      The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> findByCriteria(Long projectId, TaskCriteria criteria, Pageable page) {
        log.debug("find by criteria {}, page {} in project {}", criteria, page, projectId);
        final Specification<Task> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return null/*taskRepository.findAll(specification, page)
            .map(taskMapper::toDto)*/;
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param projectId ID of the container project.
     * @param criteria  The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(Long projectId, TaskCriteria criteria) {
        log.debug("count by criteria {} in project {}", criteria, projectId);
        final Specification<Task> specification = createSpecification(criteria)
            .and(equalsSpecification(root -> root.get("projectId"), projectId));
        return 0L; // taskRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Task> createSpecification(TaskCriteria criteria) {
        Specification<Task> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Task_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Task_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Task_.description));
            }
            if (criteria.getStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartDate(), Task_.startDate));
            }
            if (criteria.getEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndDate(), Task_.endDate));
            }
            if (criteria.getLabelsId() != null) {
                specification = specification.and(buildSpecification(criteria.getLabelsId(),
                    root -> root.join(Task_.labels, JoinType.LEFT).get(Label_.id)));
            }
        }
        return specification;
    }
}
