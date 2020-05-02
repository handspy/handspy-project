package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pt.up.hs.project.domain.enumeration.Gender;
import pt.up.hs.project.domain.enumeration.HandwritingMean;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A lightweight DTO for the {@link pt.up.hs.project.domain.Participant} entity.
 * This is used for selection boxes. Only used from server to client.
 *
 * @author José Carlos Paiva
 */
@ApiModel(description = "A subset of a Participant entity.")
public class ParticipantBasicDTO extends AbstractAuditingDTO {

    private Long id;

    /**
     * Name of the participant
     */
    @ApiModelProperty(value = "Name of the participant", required = true)
    private String name;

    /**
     * A participant belongs to a project.
     */
    @ApiModelProperty(value = "A participant belongs to a project.")
    private Long projectId;

    private Set<Long> labelIds = new HashSet<>();

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<Long> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(Set<Long> labelIds) {
        this.labelIds = labelIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParticipantBasicDTO participantDTO = (ParticipantBasicDTO) o;
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
            ", projectId=" + getProjectId() +
            "}";
    }
}
