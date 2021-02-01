package pt.up.hs.project.service;

import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.service.dto.ProjectDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link pt.up.hs.project.domain.Project}.
 */
public interface ProjectService {

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    ProjectDTO save(ProjectDTO projectDTO);

    /**
     * Get all the projects.
     *
     * @return the list of entities.
     */
    List<ProjectDTO> findAll();

    /**
     * Count the projects.
     *
     * @return the number of entities.
     */
    long count();

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectDTO> findOne(Long id);

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjectDTO> delete(Long id);
}
