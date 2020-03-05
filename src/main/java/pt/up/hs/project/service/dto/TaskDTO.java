package pt.up.hs.project.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link pt.up.hs.project.domain.Task} entity.
 */
@ApiModel(description = "The Task entity.\n\n@author José Carlos Paiva")
public class TaskDTO extends AbstractAuditingDTO {

    private Long id;

    /**
     * Name of the task
     */
    @NotNull
    @ApiModelProperty(value = "Name of the task", required = true)
    private String name;

    /**
     * Details about the task
     */
    @Size(max = 500)
    @ApiModelProperty(value = "Details about the task")
    private String description;

    /**
     * Date in which the task starts
     */
    @ApiModelProperty(value = "Date in which the task starts")
    private LocalDate startDate;

    /**
     * Date in which the task ends
     */
    @ApiModelProperty(value = "Date in which the task ends")
    private LocalDate endDate;

    /**
     * A task belongs to a project.
     */
    @ApiModelProperty(value = "A task belongs to a project.")
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        TaskDTO taskDTO = (TaskDTO) o;
        if (taskDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), taskDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", projectId=" + getProjectId() +
            "}";
    }
}
