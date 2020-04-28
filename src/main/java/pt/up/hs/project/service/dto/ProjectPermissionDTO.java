package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pt.up.hs.project.domain.ProjectPermission} entity.
 *
 * @author José Carlos Paiva
 */
@ApiModel(description = "Permission of a user towards a project.")
public class ProjectPermissionDTO implements Serializable {

    /**
     * User to which this permission is assigned.
     */
    @NotNull
    @ApiModelProperty(value = "User to which this permission is assigned.", required = true)
    private String user;

    /**
     * A permission (project) refers to a project.
     */
    @NotNull
    @ApiModelProperty(value = "A permission (project) refers to a project.", required = true)
    private Long projectId;

    /**
     * The permission of this entry.
     */
    @ApiModelProperty(value = "A permission of this user in this project.", required = true)
    private String permissionName;



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermissionDTO that = (ProjectPermissionDTO) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(permissionName, that.permissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, projectId, permissionName);
    }

    @Override
    public String toString() {
        return "ProjectPermissionDTO{" +
            ", user=" + getUser() +
            ", projectId=" + getProjectId() +
            ", permissionName=" + getPermissionName() +
            "}";
    }
}
