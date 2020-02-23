package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import pt.up.hs.project.domain.enumeration.ProjectStatus;

/**
 * A DTO for the {@link pt.up.hs.project.domain.Project} entity.
 */
@ApiModel(description = "The Project entity.\n\n@author José Carlos Paiva")
public class ProjectDTO implements Serializable {

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
     * Image of the project for visual identification
     */
    @ApiModelProperty(value = "Image of the project for visual identification")
    @Lob
    private byte[] image;

    private String imageContentType;
    /**
     * Date in which the project starts
     */
    @ApiModelProperty(value = "Date in which the project starts")
    private LocalDate startDate;

    /**
     * Date in which the project ends
     */
    @ApiModelProperty(value = "Date in which the project ends")
    private LocalDate endDate;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
            ", image='" + getImage() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", owner=" + getOwner() +
            "}";
    }
}
