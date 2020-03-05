package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import pt.up.hs.project.domain.enumeration.ProjectStatus;

/**
 * A DTO for the {@link pt.up.hs.project.domain.Project} entity.
 */
@ApiModel(description = "The Project entity.\n\n@author José Carlos Paiva")
public class ProjectDTO extends AbstractAuditingDTO {

    private Long id;

    /**
     * Name of the project
     */
    @NotNull
    @ApiModelProperty(value = "Name of the project", required = true)
    private String name;

    /**
     * Details about the project
     */
    @Size(max = 500)
    @ApiModelProperty(value = "Details about the project")
    private String description;

    /**
     * Status of the project
     */
    @NotNull
    @ApiModelProperty(value = "Status of the project", required = true)
    private ProjectStatus status;

    /**
     * Owner of the project
     */
    @NotNull
    @ApiModelProperty(value = "Owner of the project", required = true)
    private Long owner;

    @NotNull
    private String color;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectDTO projectDTO = (ProjectDTO) o;
        if (projectDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), projectDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", owner=" + getOwner() +
            ", color='" + getColor() + "'" +
            "}";
    }
}
