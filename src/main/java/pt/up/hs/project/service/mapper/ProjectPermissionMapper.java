package pt.up.hs.project.service.mapper;

import pt.up.hs.project.domain.*;
import pt.up.hs.project.service.dto.ProjectPermissionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectPermission} and its DTO {@link ProjectPermissionDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class})
public interface ProjectPermissionMapper extends EntityMapper<ProjectPermissionDTO, ProjectPermission> {

    ProjectPermissionDTO toDto(ProjectPermission projectPermission);

    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "project", ignore = true)
    ProjectPermission toEntity(ProjectPermissionDTO projectPermissionDTO);

    default ProjectPermission fromId(ProjectPermissionId id) {
        if (id == null) {
            return null;
        }
        ProjectPermission projectPermission = new ProjectPermission();
        projectPermission.setUser(id.getUser());
        projectPermission.setProjectId(id.getProjectId());
        projectPermission.setPermissionName(id.getPermissionName());
        return projectPermission;
    }
}
