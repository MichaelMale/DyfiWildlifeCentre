package uk.co.montwt.dyfiwildlifecentre.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "webcams")
public class WebcamFeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "slug", nullable = false, unique = true, length = 64)
    private String slug;

    @NotBlank
    @Column(name = "name_en", nullable = false, length = 160)
    private String nameEn;

    @Column(name = "name_cy", length = 160)
    private String nameCy;

    @Column(name = "description_en", length = 512)
    private String descriptionEn;

    @Column(name = "description_cy", length = 512)
    private String descriptionCy;

    @NotBlank
    @Column(name = "stream_url", nullable = false, length = 2048)
    private String streamUrl;

    @Column(name = "poster_url", length = 2048)
    private String posterUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameCy() {
        return nameCy;
    }

    public void setNameCy(String nameCy) {
        this.nameCy = nameCy;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionCy() {
        return descriptionCy;
    }

    public void setDescriptionCy(String descriptionCy) {
        this.descriptionCy = descriptionCy;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
