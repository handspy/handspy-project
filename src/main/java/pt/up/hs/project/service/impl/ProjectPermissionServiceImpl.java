package pt.up.hs.project.service.impl;

import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.repository.ProjectPermissionRepository;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;
import pt.up.hs.project.service.mapper.ProjectPermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ProjectPermission}.
 */
@Service
@Transactional
public class ProjectPermissionServiceImpl implements ProjectPermissionService {

    private final Logger log = LoggerFactory.getLogger(ProjectPermissionServiceImpl.class);

    private final ProjectPermissionRepository projectPermissionRepository;

    private final ProjectPermissionMapper projectPermissionMapper;

    public ProjectPermissionServiceImpl(ProjectPermissionRepository projectPermissionRepository, ProjectPermissionMapper projectPermissionMapper) {
        this.projectPermissionRepository = projectPermissionRepository;
        this.projectPermissionMapper = projectPermissionMapper;
    }

    /**
     * Save a projectPermission.
     *
     * @param projectPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ProjectPermissionDTO save(ProjectPermissionDTO projectPermissionDTO) {
        log.debug("Request to save ProjectPermission : {}", projectPermissionDTO);
        ProjectPermission projectPermission = projectPermissionMapper.toEntity(projectPermissionDTO);
        projectPermission = projectPermissionRepository.save(projectPermission);
        return projectPermissionMapper.toDto(projectPermission);
    }

    /**
     * Get all the projectPermissions.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectPermissionDTO> findAll() {
        log.debug("Request to get all ProjectPermissions");
        return projectPermissionRepository.findAll().stream()
            .map(projectPermissionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one projectPermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectPermissionDTO> findOne(Long id) {
        log.debug("Request to get ProjectPermission : {}", id);
        return projectPermissionRepository.findById(id)
            .map(projectPermissionMapper::toDto);
    }

    /**
     * Delete the projectPermission by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectPermission : {}", id);
        projectPermissionRepository.deleteById(id);
    }
}
