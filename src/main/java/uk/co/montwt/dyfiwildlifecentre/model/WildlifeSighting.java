package uk.co.montwt.dyfiwildlifecentre.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "wildlife_sightings")
public class WildlifeSighting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "species", nullable = false, length = 160)
    private String species;

    @Column(name = "species_cy", length = 160)
    private String speciesCy;

    @Column(name = "location_name", length = 160)
    private String locationName;

    @Column(name = "notes", length = 1024)
    private String notes;

    @Column(name = "notes_cy", length = 1024)
    private String notesCy;

    @Column(name = "sighted_at", nullable = false)
    private LocalDateTime sightedAt;

    @Column(name = "recorded_by", length = 64)
    private String recordedBy;

    @PrePersist
    public void ensureTimestamp() {
        if (this.sightedAt == null) {
            this.sightedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpeciesCy() {
        return speciesCy;
    }

    public void setSpeciesCy(String speciesCy) {
        this.speciesCy = speciesCy;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotesCy() {
        return notesCy;
    }

    public void setNotesCy(String notesCy) {
        this.notesCy = notesCy;
    }

    public LocalDateTime getSightedAt() {
        return sightedAt;
    }

    public void setSightedAt(LocalDateTime sightedAt) {
        this.sightedAt = sightedAt;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }
}
