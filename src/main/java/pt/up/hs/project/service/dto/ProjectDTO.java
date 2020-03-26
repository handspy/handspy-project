package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pt.up.hs.project.domain.enumeration.ProjectStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

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
     * Color of the project
     */
    @NotNull
    @Size(max = 20)
    @ApiModelProperty(value = "Color of the project")
    private String color;

    /**
     * Owner of the project
     */
    @ApiModelProperty(value = "Owner of the project", required = true)
    private String owner;


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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
