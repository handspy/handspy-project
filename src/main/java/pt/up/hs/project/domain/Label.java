package pt.up.hs.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The Label entity.
 *
 * @author José Carlos Paiva
 */
@Entity
@Table(name = "label")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Name of the label
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * Color of the label
     */
    @Size(max = 20)
    @Column(name = "color", length = 20)
    @ColumnDefault("white")
    private String color;

    /**
     * A label belongs to a project.
     */
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Project project;

    @Column(name = "project_id")
    @NotNull
    private Long projectId;

    @ManyToMany(mappedBy = "labels")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Participant> participants = new HashSet<>();

    @ManyToMany(mappedBy = "labels")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<Task> tasks = new HashSet<>();

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

    public Label name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public Label color(String color) {
        this.color = color;
        return this;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Project getProject() {
        return project;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Label projectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public Label participants(Set<Participant> participants) {
        setParticipants(participants);
        return this;
    }

    public Label addParticipants(Participant participant) {
        this.participants.add(participant);
        participant.getLabels().add(this);
        return this;
    }

    public Label removeParticipants(Participant participant) {
        this.participants.remove(participant);
        participant.getLabels().remove(this);
        return this;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
        if (this.participants != null) {
            for (Participant participant: this.participants) {
                participant.getLabels().add(this);
            }
        }
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public Label tasks(Set<Task> tasks) {
        setTasks(tasks);
        return this;
    }

    public Label addTasks(Task task) {
        this.tasks.add(task);
        task.getLabels().add(this);
        return this;
    }

    public Label removeTasks(Task task) {
        this.tasks.remove(task);
        task.getLabels().remove(this);
        return this;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
        if (this.tasks != null) {
            for (Task task: this.tasks) {
                task.getLabels().add(this);
            }
        }
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Label)) {
            return false;
        }
        return id != null && id.equals(((Label) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Label{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", color='" + getColor() + "'" +
            "}";
    }
}
