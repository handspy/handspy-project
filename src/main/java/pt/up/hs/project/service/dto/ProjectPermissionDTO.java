package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pt.up.hs.project.domain.ProjectPermission} entity.
 */
@ApiModel(description = "Permissions of a user towards a project.\n\n@author José Carlos Paiva")
public class ProjectPermissionDTO implements Serializable {

    private Long id;

    /**
     * User to which this permission is assigned.
     */
    @NotNull
    @ApiModelProperty(value = "User to which this permission is assigned.", required = true)
    private String user;

    /**
     * A permission (project) refers to a project.
     */
    @ApiModelProperty(value = "A permission (project) refers to a project.")
    private Long projectId;

    /**
     * The permission of this entry.
     */
    @ApiModelProperty(value = "The permission of this entry.")
    private Integer permission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectPermissionDTO projectPermissionDTO = (ProjectPermissionDTO) o;
        if (projectPermissionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), projectPermissionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProjectPermissionDTO{" +
            "id=" + getId() +
            ", user=" + getUser() +
            ", permission=" + getPermission() +
            ", projectId=" + getProjectId() +
            "}";
    }
}
