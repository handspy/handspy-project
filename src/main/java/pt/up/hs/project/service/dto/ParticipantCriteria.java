package pt.up.hs.project.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import pt.up.hs.project.domain.enumeration.Gender;
import pt.up.hs.project.domain.enumeration.HandwritingMeans;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link pt.up.hs.project.domain.Participant} entity. This class is used
 * in {@link pt.up.hs.project.web.rest.ParticipantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /participants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ParticipantCriteria implements Serializable, Criteria {
    /**
     * Class for filtering Gender
     */
    public static class GenderFilter extends Filter<Gender> {

        public GenderFilter() {
        }

        public GenderFilter(GenderFilter filter) {
            super(filter);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }

    }
    /**
     * Class for filtering HandwritingMeans
     */
    public static class HandwritingMeansFilter extends Filter<HandwritingMeans> {

        public HandwritingMeansFilter() {
        }

        public HandwritingMeansFilter(HandwritingMeansFilter filter) {
            super(filter);
        }

        @Override
        public HandwritingMeansFilter copy() {
            return new HandwritingMeansFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private GenderFilter gender;

    private LocalDateFilter birthdate;

    private HandwritingMeansFilter handedness;

    private StringFilter additionalInfo;

    private LongFilter projectId;

    private LongFilter labelsId;

    public ParticipantCriteria() {
    }

    public ParticipantCriteria(ParticipantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.gender = other.gender == null ? null : other.gender.copy();
        this.birthdate = other.birthdate == null ? null : other.birthdate.copy();
        this.handedness = other.handedness == null ? null : other.handedness.copy();
        this.additionalInfo = other.additionalInfo == null ? null : other.additionalInfo.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
        this.labelsId = other.labelsId == null ? null : other.labelsId.copy();
    }

    @Override
    public ParticipantCriteria copy() {
        return new ParticipantCriteria(this);
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

    public GenderFilter getGender() {
        return gender;
    }

    public void setGender(GenderFilter gender) {
        this.gender = gender;
    }

    public LocalDateFilter getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDateFilter birthdate) {
        this.birthdate = birthdate;
    }

    public HandwritingMeansFilter getHandedness() {
        return handedness;
    }

    public void setHandedness(HandwritingMeansFilter handedness) {
        this.handedness = handedness;
    }

    public StringFilter getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(StringFilter additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public LongFilter getLabelsId() {
        return labelsId;
    }

    public void setLabelsId(LongFilter labelsId) {
        this.labelsId = labelsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ParticipantCriteria that = (ParticipantCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(birthdate, that.birthdate) &&
            Objects.equals(handedness, that.handedness) &&
            Objects.equals(additionalInfo, that.additionalInfo) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(labelsId, that.labelsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        gender,
        birthdate,
        handedness,
        additionalInfo,
        projectId,
        labelsId
        );
    }

    @Override
    public String toString() {
        return "ParticipantCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (gender != null ? "gender=" + gender + ", " : "") +
                (birthdate != null ? "birthdate=" + birthdate + ", " : "") +
                (handedness != null ? "handedness=" + handedness + ", " : "") +
                (additionalInfo != null ? "additionalInfo=" + additionalInfo + ", " : "") +
                (projectId != null ? "projectId=" + projectId + ", " : "") +
                (labelsId != null ? "labelsId=" + labelsId + ", " : "") +
            "}";
    }

}
