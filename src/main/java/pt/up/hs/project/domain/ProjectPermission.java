package pt.up.hs.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * Permissions of a user towards a project.\n\n@author José Carlos Paiva
 */
@Entity
@Table(name = "project_permission")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * User to which this permission is assigned.
     */
    @NotNull
    @Column(name = "hs_user", nullable = false)
    private Long user;

    /**
     * The permission of this entry.
     */
    @Column(name = "permission")
    private Integer permission;

    /**
     * A permission (project) refers to a project.
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("projectPermissions")
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public ProjectPermission user(Long user) {
        this.user = user;
        return this;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Integer getPermission() {
        return permission;
    }

    public ProjectPermission permission(Integer permission) {
        this.permission = permission;
        return this;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public Project getProject() {
        return project;
    }

    public ProjectPermission project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectPermission)) {
            return false;
        }
        return id != null && id.equals(((ProjectPermission) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ProjectPermission{" +
            "id=" + getId() +
            ", user=" + getUser() +
            ", permission=" + getPermission() +
            "}";
    }
}
