package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;
import pt.up.hs.project.domain.enumeration.Gender;
import pt.up.hs.project.domain.enumeration.HandwritingMeans;

/**
 * A DTO for the {@link pt.up.hs.project.domain.Participant} entity.
 */
@ApiModel(description = "Information about a participant involved in the experiment. Participants are\norganized in groups, and part of a project.\n\n@author José Carlos Paiva")
public class ParticipantDTO implements Serializable {

    private Long id;

    /**
     * Name of the participant
     */
    @NotNull
    @ApiModelProperty(value = "Name of the participant", required = true)
    private String name;

    /**
     * Gender of the participant
     */
    @ApiModelProperty(value = "Gender of the participant")
    private Gender gender;

    /**
     * Birth date of the participant
     */
    @ApiModelProperty(value = "Birth date of the participant")
    private LocalDate birthdate;

    /**
     * Means used by participant for handwriting
     */
    @ApiModelProperty(value = "Means used by participant for handwriting")
    private HandwritingMeans handedness;

    /**
     * Additional information about the participant
     */
    @ApiModelProperty(value = "Additional information about the participant")
    private String additionalInfo;

    /**
     * Image of the participant for visual identification
     */
    @ApiModelProperty(value = "Image of the participant for visual identification")
    @Lob
    private byte[] image;

    private String imageContentType;
    /**
     * A participant belongs to a project.
     */
    @ApiModelProperty(value = "A participant belongs to a project.")

    private Long projectId;

    private Set<LabelDTO> labels = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public HandwritingMeans getHandedness() {
        return handedness;
    }

    public void setHandedness(HandwritingMeans handedness) {
        this.handedness = handedness;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<LabelDTO> getLabels() {
        return labels;
    }

    public void setLabels(Set<LabelDTO> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParticipantDTO participantDTO = (ParticipantDTO) o;
        if (participantDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), participantDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParticipantDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", gender='" + getGender() + "'" +
            ", birthdate='" + getBirthdate() + "'" +
            ", handedness='" + getHandedness() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", image='" + getImage() + "'" +
            ", projectId=" + getProjectId() +
            "}";
    }
}
