package pt.up.hs.project.domain;

import java.io.Serializable;
import java.util.Objects;

public class ProjectPermissionId implements Serializable {

    private String user;
    private Long projectId;
    private String permissionName;

    public ProjectPermissionId() {
    }

    public ProjectPermissionId(String user, Long projectId, String permissionName) {
        this.user = user;
        this.projectId = projectId;
        this.permissionName = permissionName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermissionId that = (ProjectPermissionId) o;
        return user.equals(that.user) &&
            projectId.equals(that.projectId) &&
            permissionName.equals(that.permissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, projectId, permissionName);
    }
}
