package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link pt.up.hs.project.domain.Label} entity.
 */
@ApiModel(description = "The Label entity.\n\n@author José Carlos Paiva")
public class LabelDTO implements Serializable {

    private Long id;

    /**
     * Name of the label
     */
    @NotNull
    @Size(max = 50)
    @ApiModelProperty(value = "Name of the label", required = true)
    private String name;

    /**
     * Color of the label
     */
    @NotNull
    @Size(max = 20)
    @ApiModelProperty(value = "Color of the label", required = true)
    private String color;

    /**
     * A label belongs to a project.
     */
    @ApiModelProperty(value = "A label belongs to a project.")

    private Long projectId;
    /**
     * A label may have many participants.
     */
    @ApiModelProperty(value = "A label may have many participants.")
    /**
     * A group may have many participants.
     */
    @ApiModelProperty(value = "A group may have many participants.")

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
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

        LabelDTO labelDTO = (LabelDTO) o;
        if (labelDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), labelDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LabelDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", color='" + getColor() + "'" +
            ", projectId=" + getProjectId() +
            "}";
    }
}
