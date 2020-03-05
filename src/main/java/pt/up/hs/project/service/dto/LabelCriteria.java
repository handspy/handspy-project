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
 * Criteria class for the {@link pt.up.hs.project.domain.Label} entity. This class is used
 * in {@link pt.up.hs.project.web.rest.LabelResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /labels?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LabelCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter color;

    private LongFilter participantsId;

    private LongFilter tasksId;

    public LabelCriteria() {
    }

    public LabelCriteria(LabelCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.color = other.color == null ? null : other.color.copy();
        this.participantsId = other.participantsId == null ? null : other.participantsId.copy();
        this.tasksId = other.tasksId == null ? null : other.tasksId.copy();
    }

    @Override
    public LabelCriteria copy() {
        return new LabelCriteria(this);
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

    public StringFilter getColor() {
        return color;
    }

    public void setColor(StringFilter color) {
        this.color = color;
    }

    public LongFilter getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(LongFilter participantsId) {
        this.participantsId = participantsId;
    }

    public LongFilter getTasksId() {
        return tasksId;
    }

    public void setTasksId(LongFilter tasksId) {
        this.tasksId = tasksId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LabelCriteria that = (LabelCriteria) o;
        return
            Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(color, that.color) &&
                Objects.equals(participantsId, that.participantsId) &&
                Objects.equals(tasksId, that.tasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            color,
            participantsId,
            tasksId
        );
    }

    @Override
    public String toString() {
        return "LabelCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (color != null ? "color=" + color + ", " : "") +
            (participantsId != null ? "participantsId=" + participantsId + ", " : "") +
            (tasksId != null ? "tasksId=" + tasksId + ", " : "") +
            "}";
    }

}
