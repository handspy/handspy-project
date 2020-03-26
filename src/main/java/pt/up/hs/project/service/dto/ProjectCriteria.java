package pt.up.hs.project.service.dto;

import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import pt.up.hs.project.domain.enumeration.ProjectStatus;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link pt.up.hs.project.domain.Project} entity. This class is used
 * in {@link pt.up.hs.project.web.rest.ProjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /projects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectCriteria implements Serializable, Criteria {
    /**
     * Class for filtering ProjectStatus
     */
    public static class ProjectStatusFilter extends Filter<ProjectStatus> {

        public ProjectStatusFilter() {
        }

        public ProjectStatusFilter(ProjectStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProjectStatusFilter copy() {
            return new ProjectStatusFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private ProjectStatusFilter status;

    private StringFilter owner;

    private StringFilter color;

    private LongFilter tasksId;

    private LongFilter participantsId;

    public ProjectCriteria() {
    }

    public ProjectCriteria(ProjectCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.owner = other.owner == null ? null : other.owner.copy();
        this.color = other.color == null ? null : other.color.copy();
        this.tasksId = other.tasksId == null ? null : other.tasksId.copy();
        this.participantsId = other.participantsId == null ? null : other.participantsId.copy();
    }

    @Override
    public ProjectCriteria copy() {
        return new ProjectCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ProjectStatusFilter getStatus() {
        return status;
    }

    public void setStatus(ProjectStatusFilter status) {
        this.status = status;
    }

    public StringFilter getOwner() {
        return owner;
    }

    public void setOwner(StringFilter owner) {
        this.owner = owner;
    }

    public StringFilter getColor() {
        return color;
    }

    public void setColor(StringFilter color) {
        this.color = color;
    }

    public LongFilter getTasksId() {
        return tasksId;
    }

    public void setTasksId(LongFilter tasksId) {
        this.tasksId = tasksId;
    }

    public LongFilter getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(LongFilter participantsId) {
        this.participantsId = participantsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProjectCriteria that = (ProjectCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(status, that.status) &&
            Objects.equals(owner, that.owner) &&
            Objects.equals(color, that.color) &&
            Objects.equals(tasksId, that.tasksId) &&
            Objects.equals(participantsId, that.participantsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        description,
        status,
        owner,
        color,
        tasksId,
        participantsId
        );
    }

    @Override
    public String toString() {
        return "ProjectCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (owner != null ? "owner=" + owner + ", " : "") +
                (color != null ? "color=" + color + ", " : "") +
                (tasksId != null ? "tasksId=" + tasksId + ", " : "") +
                (participantsId != null ? "participantsId=" + participantsId + ", " : "") +
            "}";
    }

}
