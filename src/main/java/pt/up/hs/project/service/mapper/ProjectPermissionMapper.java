package pt.up.hs.project.service.mapper;


import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectPermission} and its DTO {@link ProjectPermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class})
public interface ProjectPermissionMapper extends EntityMapper<ProjectPermissionDTO, ProjectPermission> {

    @Mapping(source = "project.id", target = "projectId")
    ProjectPermissionDTO toDto(ProjectPermission projectPermission);

    @Mapping(source = "projectId", target = "project")
    ProjectPermission toEntity(ProjectPermissionDTO projectPermissionDTO);

    default ProjectPermission fromId(Long id) {
        if (id == null) {
            return null;
        }
        ProjectPermission projectPermission = new ProjectPermission();
        projectPermission.setId(id);
        return projectPermission;
    }
}
