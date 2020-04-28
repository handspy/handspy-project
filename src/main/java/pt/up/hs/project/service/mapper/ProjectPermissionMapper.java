package pt.up.hs.project.service.mapper;

import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectPermission} and its DTO {@link ProjectPermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class})
public interface ProjectPermissionMapper extends EntityMapper<ProjectPermissionDTO, ProjectPermission> {

    @Mapping(source = "id.user", target = "user")
    @Mapping(source = "id.project.id", target = "projectId")
    @Mapping(source = "id.permission.name", target = "permissionName")
    ProjectPermissionDTO toDto(ProjectPermission projectPermission);

    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(source = "user", target = "id.user")
    @Mapping(source = "projectId", target = "id.project.id")
    @Mapping(source = "permissionName", target = "id.permission.name")
    ProjectPermission toEntity(ProjectPermissionDTO projectPermissionDTO);

    default ProjectPermission fromId(ProjectPermissionId id) {
        if (id == null) {
            return null;
        }
        ProjectPermission projectPermission = new ProjectPermission();
        projectPermission.setId(id);
        return projectPermission;
    }
}
