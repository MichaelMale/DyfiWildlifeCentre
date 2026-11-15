package uk.co.montwt.dyfiwildlifecentre.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * WebcamProperties.java - Holds configurable Osprey & reserve webcam feed definitions.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class WebcamProperties {

    private List<WebcamFeed> webcams = defaultFeeds();

    public List<WebcamFeed> getWebcams() {
        return webcams;
    }

    public void setWebcams(List<WebcamFeed> webcams) {
        this.webcams = (webcams == null || webcams.isEmpty()) ? defaultFeeds() : webcams;
    }

    private static List<WebcamFeed> defaultFeeds() {
        List<WebcamFeed> defaults = new ArrayList<>();
        defaults.add(new WebcamFeed(
                "nest-cam",
                "Osprey Nest Camera",
                "Camera Nyth Gwalch y Pysgod",
                "Live view of the main osprey nesting platform at Cors Dyfi.",
                "Golygfa fyw o brif lwyfan nythu gwalch y pysgod yng Nghors Dyfi.",
                "https://www.youtube.com/embed/YnKZJ3SF5WM?rel=0",
                "/images/osprey.jpeg"));
        return defaults;
    }

    public static class WebcamFeed {
        private String id;
        private String nameEn;
        private String nameCy;
        private String descriptionEn;
        private String descriptionCy;
        private String streamUrl;
        private String posterUrl;

        public WebcamFeed() {
        }

        public WebcamFeed(String id, String nameEn, String nameCy,
                          String descriptionEn, String descriptionCy,
                          String streamUrl, String posterUrl) {
            this.id = id;
            this.nameEn = nameEn;
            this.nameCy = nameCy;
            this.descriptionEn = descriptionEn;
            this.descriptionCy = descriptionCy;
            this.streamUrl = streamUrl;
            this.posterUrl = posterUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
    }
}
