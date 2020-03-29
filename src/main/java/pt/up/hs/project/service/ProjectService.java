package pt.up.hs.project.service;

import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.service.dto.ProjectDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectDTO> findAll(Pageable pageable);

    /**
     * Get all the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjectDTO> findAll(String search, List<ProjectStatus> statuses, Pageable pageable);

    /**
     * Count the projects.
     *
     * @param search the search string.
     * @param statuses the statuses to include.
     * @return the list of entities.
     */
    long count(String search, List<ProjectStatus> statuses);

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
     */
    void delete(Long id);
}
