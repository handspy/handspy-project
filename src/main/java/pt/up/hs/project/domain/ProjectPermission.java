package pt.up.hs.project.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Permissions of a user towards a project.
 *
 * @author José Carlos Paiva
 */
@Entity
@Table(name = "project_permission")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ProjectPermissionId id = new ProjectPermissionId();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public ProjectPermissionId getId() {
        return id;
    }

    public ProjectPermission id(ProjectPermissionId id) {
        this.id = id;
        return this;
    }

    public void setId(ProjectPermissionId id) {
        this.id = id;
    }

    public String getUser() {
        return id.getUser();
    }

    public ProjectPermission user(String user) {
        id.setUser(user);
        return this;
    }

    public void setUser(String user) {
        this.id.setUser(user);
    }

    public Project getProject() {
        return id.getProject();
    }

    public ProjectPermission project(Project project) {
        id.setProject(project);
        return this;
    }

    public void setProject(Project project) {
        id.setProject(project);
    }

    public Permission getPermission() {
        return id.getPermission();
    }

    public ProjectPermission permission(Permission permission) {
        id.setPermission(permission);
        return this;
    }

    public void setPermission(Permission permission) {
        id.setPermission(permission);
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermission that = (ProjectPermission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectPermission{" +
            "id=" + id +
        '}';
    }
}
