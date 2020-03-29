package pt.up.hs.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Permissions of a user towards a project.
 *
 * @author José Carlos Paiva
 */
@Entity
@IdClass(ProjectPermissionId.class)
@Table(name = "project_permission")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User to which this permission is assigned.
     */
    @Id
    @NotNull
    @Column(name = "user", nullable = false)
    private String user;

    /**
     * A permission (project) refers to a project.
     */
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Project project;

    @Id
    @Column(name = "project_id")
    @NotNull
    private Long projectId;

    /**
     * The permission of this entry.
     */
    @JoinColumn(name = "permission_name", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Permission permission;

    @Id
    @Column(name = "permission_name")
    @NotNull
    private String permissionName;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public String getUser() {
        return user;
    }

    public ProjectPermission user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectPermission projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public ProjectPermission permission(String permissionName) {
        this.permissionName = permissionName;
        return this;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermission that = (ProjectPermission) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(permissionName, that.permissionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, projectId, permissionName);
    }
}
