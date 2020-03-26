package pt.up.hs.project.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import pt.up.hs.project.domain.enumeration.ProjectStatus;

/**
 * The Project entity.\n\n@author José Carlos Paiva
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Name of the project
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Details about the project
     */
    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Status of the project
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

    /**
     * Owner of the project
     */
    @NotNull
    @Column(name = "owner", nullable = false)
    private String owner;

    /**
     * Color of the project
     */
    @NotNull
    @Size(max = 20)
    @Column(name = "color", nullable = false, length = 20)
    private String color;

    @OneToMany(mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "project")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Participant> participants = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Project description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Project status(ProjectStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public Project owner(String owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getColor() {
        return color;
    }

    public Project color(String color) {
        this.color = color;
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public Project tasks(Set<Task> tasks) {
        this.tasks = tasks;
        return this;
    }

    public Project addTasks(Task task) {
        this.tasks.add(task);
        task.setProjectId(this.id);
        return this;
    }

    public Project removeTasks(Task task) {
        this.tasks.remove(task);
        task.setProjectId(null);
        return this;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public Project participants(Set<Participant> participants) {
        this.participants = participants;
        return this;
    }

    public Project addParticipants(Participant participant) {
        this.participants.add(participant);
        participant.setProjectId(this.id);
        return this;
    }

    public Project removeParticipants(Participant participant) {
        this.participants.remove(participant);
        participant.setProjectId(null);
        return this;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", owner=" + getOwner() +
            ", color='" + getColor() + "'" +
            "}";
    }
}
