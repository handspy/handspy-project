package pt.up.hs.project.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectPermissionId implements Serializable {

    /**
     * A permission belongs to a user.
     */
    @NotNull
    @Column(name = "hs_user", nullable = false)
    private String user;

    /**
     * A permission refers to a project.
     */
    @JoinColumn(name = "project_id", nullable = false)
    @MapsId("project_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project project;

    /**
     * The permission of this entry.
     */
    @JoinColumn(name = "permission_name", referencedColumnName = "name", nullable = false)
    @MapsId("permission_name")
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Permission permission;

    public ProjectPermissionId() {
    }

    public ProjectPermissionId(@NotNull String user, Project project, Permission permission) {
        this.user = user;
        this.project = project;
        this.permission = permission;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermissionId that = (ProjectPermissionId) o;
        return Objects.equals(getUser(), that.getUser()) &&
            Objects.equals(getProject(), that.getProject()) &&
            Objects.equals(getPermission(), that.getPermission());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getProject(), getPermission());
    }

    @Override
    public String toString() {
        return "ProjectPermissionId{" +
            "user='" + user + '\'' +
            ", project=" + project +
            ", permission=" + permission +
        '}';
    }
}
