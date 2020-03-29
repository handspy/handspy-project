package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for bulk adding {@link pt.up.hs.project.domain.ProjectPermission}
 * entities of the same user and project.
 *
 * @author José Carlos Paiva
 */
@ApiModel(description = "Permissions of a user towards a project.")
public class BulkProjectPermissionDTO implements Serializable {

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
    @ApiModelProperty(value = "A permission (project) refers to a project.")
    private Long projectId;

    /**
     * The permission of this entry.
     */
    @ApiModelProperty(value = "The permissions of this user in this project.")
    private List<String> permissions;

    public BulkProjectPermissionDTO() {
    }

    public BulkProjectPermissionDTO(@NotNull String user, @NotNull Long projectId, List<String> permissions) {
        this.user = user;
        this.projectId = projectId;
        this.permissions = permissions;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
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
        BulkProjectPermissionDTO that = (BulkProjectPermissionDTO) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, projectId);
    }

    @Override
    public String toString() {
        return "ProjectPermissionDTO{" +
            ", user=" + getUser() +
            ", projectId=" + getProjectId() +
            ", permissions=" + getPermissions() +
            "}";
    }
}
