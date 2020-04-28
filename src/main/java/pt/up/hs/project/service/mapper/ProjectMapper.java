package pt.up.hs.project.service.mapper;


import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.ProjectDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectPermissionMapper.class})
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "removePermissions", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "removeTasks", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "removeParticipants", ignore = true)
    Project toEntity(ProjectDTO projectDTO);

    ProjectDTO toDto(Project entity);

    default Project fromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
