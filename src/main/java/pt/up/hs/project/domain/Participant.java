package pt.up.hs.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import pt.up.hs.project.domain.enumeration.Gender;

import pt.up.hs.project.domain.enumeration.HandwritingMeans;

/**
 * Information about a participant involved in the experiment. Participants are\norganized in groups, and part of a project.\n\n@author José Carlos Paiva
 */
@Entity
@Table(name = "participant")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Participant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Name of the participant
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Gender of the participant
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    /**
     * Birth date of the participant
     */
    @Column(name = "birthdate")
    private LocalDate birthdate;

    /**
     * Means used by participant for handwriting
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "handedness")
    private HandwritingMeans handedness;

    /**
     * Additional information about the participant
     */
    @Column(name = "additional_info")
    private String additionalInfo;

    /**
     * Image of the participant for visual identification
     */
    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    /**
     * A participant belongs to a project.
     */
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("participants")
    private Project project;

    /**
     * A participant may be associated with several labels.
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "participant_labels",
               joinColumns = @JoinColumn(name = "participant_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "labels_id", referencedColumnName = "id"))
    private Set<Label> labels = new HashSet<>();

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

    public Participant name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public Participant gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Participant birthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public HandwritingMeans getHandedness() {
        return handedness;
    }

    public Participant handedness(HandwritingMeans handedness) {
        this.handedness = handedness;
        return this;
    }

    public void setHandedness(HandwritingMeans handedness) {
        this.handedness = handedness;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public Participant additionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
        return this;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public byte[] getImage() {
        return image;
    }

    public Participant image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public Participant imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Project getProject() {
        return project;
    }

    public Participant project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public Participant labels(Set<Label> labels) {
        this.labels = labels;
        return this;
    }

    public Participant addLabels(Label label) {
        this.labels.add(label);
        label.getParticipants().add(this);
        return this;
    }

    public Participant removeLabels(Label label) {
        this.labels.remove(label);
        label.getParticipants().remove(this);
        return this;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Participant)) {
            return false;
        }
        return id != null && id.equals(((Participant) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Participant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", gender='" + getGender() + "'" +
            ", birthdate='" + getBirthdate() + "'" +
            ", handedness='" + getHandedness() + "'" +
            ", additionalInfo='" + getAdditionalInfo() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            "}";
    }
}
