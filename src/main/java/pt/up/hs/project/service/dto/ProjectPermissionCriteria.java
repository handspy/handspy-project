package pt.up.hs.project.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link pt.up.hs.project.domain.ProjectPermission} entity. This class is used
 * in {@link pt.up.hs.project.web.rest.ProjectPermissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-permissions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectPermissionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter user;

    private IntegerFilter permission;

    private LongFilter projectId;

    public ProjectPermissionCriteria() {
    }

    public ProjectPermissionCriteria(ProjectPermissionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.user = other.user == null ? null : other.user.copy();
        this.permission = other.permission == null ? null : other.permission.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
    }

    @Override
    public ProjectPermissionCriteria copy() {
        return new ProjectPermissionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getUser() {
        return user;
    }

    public void setUser(LongFilter user) {
        this.user = user;
    }

    public IntegerFilter getPermission() {
        return permission;
    }

    public void setPermission(IntegerFilter permission) {
        this.permission = permission;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
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
        final ProjectPermissionCriteria that = (ProjectPermissionCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(user, that.user) &&
            Objects.equals(permission, that.permission) &&
            Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        user,
        permission,
        projectId
        );
    }

    @Override
    public String toString() {
        return "ProjectPermissionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (user != null ? "user=" + user + ", " : "") +
                (permission != null ? "permission=" + permission + ", " : "") +
                (projectId != null ? "projectId=" + projectId + ", " : "") +
            "}";
    }

}
