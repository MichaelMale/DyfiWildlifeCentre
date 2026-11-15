package uk.co.montwt.dyfiwildlifecentre.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.montwt.dyfiwildlifecentre.config.WebcamProperties;
import uk.co.montwt.dyfiwildlifecentre.model.WebcamFeed;
import uk.co.montwt.dyfiwildlifecentre.model.repository.WebcamFeedRepository;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages database-backed reserve webcam streams, falling back to configured
 * defaults if the table has not been populated yet.
 */
@Service
public class WebcamService {

    private static final Pattern YOUTUBE_WATCH_V = Pattern.compile("[?&]v=([^&]+)");

    private final WebcamFeedRepository webcamFeedRepository;
    private final WebcamProperties webcamProperties;

    public WebcamService(WebcamFeedRepository webcamFeedRepository, WebcamProperties webcamProperties) {
        this.webcamFeedRepository = webcamFeedRepository;
        this.webcamProperties = webcamProperties;
    }

    public List<WebcamProperties.WebcamFeed> getActivePublicFeeds() {
        List<WebcamFeed> stored = webcamFeedRepository.findByActiveTrueOrderBySortOrderAscIdAsc();
        if (stored.isEmpty()) {
            return webcamProperties.getWebcams();
        }
        return stored.stream()
                .map(feed -> new WebcamProperties.WebcamFeed(
                        feed.getSlug(),
                        feed.getNameEn(),
                        feed.getNameCy(),
                        feed.getDescriptionEn(),
                        feed.getDescriptionCy(),
                        feed.getStreamUrl(),
                        feed.getPosterUrl()
                ))
                .toList();
    }

    public List<WebcamFeed> findAll() {
        return webcamFeedRepository.findAllByOrderBySortOrderAscIdAsc();
    }

    @Transactional
    public WebcamFeed saveOrUpdate(WebcamFeed incoming) {
        WebcamFeed entity = incoming.getId() != null
                ? webcamFeedRepository.findById(incoming.getId()).orElse(new WebcamFeed())
                : new WebcamFeed();

        String rawSlug = incoming.getSlug();
        if (rawSlug == null || rawSlug.isBlank()) {
            rawSlug = sanitizeSlug(incoming.getNameEn());
        } else {
            rawSlug = sanitizeSlug(rawSlug);
        }

        // Ensure uniqueness for newly inserted records when slug already exists
        if (entity.getId() == null) {
            Optional<WebcamFeed> conflict = webcamFeedRepository.findBySlug(rawSlug);
            if (conflict.isPresent()) {
                rawSlug = rawSlug + "-" + System.currentTimeMillis() % 10000;
            }
        }

        entity.setSlug(rawSlug);
        entity.setNameEn(incoming.getNameEn().trim());
        entity.setNameCy(trimToNull(incoming.getNameCy()));
        entity.setDescriptionEn(trimToNull(incoming.getDescriptionEn()));
        entity.setDescriptionCy(trimToNull(incoming.getDescriptionCy()));
        entity.setStreamUrl(normalizeStreamUrl(incoming.getStreamUrl()));
        String poster = trimToNull(incoming.getPosterUrl());
        entity.setPosterUrl(poster != null ? poster : "/images/osprey.jpeg");
        entity.setActive(incoming.isActive());
        entity.setSortOrder(incoming.getSortOrder());
        return webcamFeedRepository.save(entity);
    }

    @Transactional
    public void toggleActive(Long id) {
        webcamFeedRepository.findById(id).ifPresent(feed -> {
            feed.setActive(!feed.isActive());
            webcamFeedRepository.save(feed);
        });
    }

    @Transactional
    public void delete(Long id) {
        webcamFeedRepository.deleteById(id);
    }

    static String normalizeStreamUrl(String rawUrl) {
        if (rawUrl == null) {
            return "";
        }
        String trimmed = rawUrl.trim();
        try {
            URI uri = URI.create(trimmed);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT).replaceFirst("^www\\.", "");
            String path = uri.getPath() == null ? "" : uri.getPath();
            String videoId = null;

            if ("youtu.be".equals(host)) {
                String seg = path.replaceFirst("^/", "");
                int slash = seg.indexOf('/');
                videoId = slash > 0 ? seg.substring(0, slash) : seg;
            } else if ("youtube.com".equals(host) || "youtube-nocookie.com".equals(host)) {
                if ("/watch".equals(path) && uri.getRawQuery() != null) {
                    Matcher m = YOUTUBE_WATCH_V.matcher("?" + uri.getRawQuery());
                    if (m.find()) {
                        videoId = m.group(1);
                    }
                } else if (path.startsWith("/live/") || path.startsWith("/embed/")) {
                    String[] parts = path.split("/");
                    if (parts.length >= 3 && !parts[2].isBlank()) {
                        videoId = parts[2];
                    }
                }
            }
            if (videoId != null && !videoId.isBlank()) {
                return "https://www.youtube.com/embed/" + videoId + "?rel=0";
            }
        } catch (IllegalArgumentException ignored) {
            // Keep trimmed value when not a full URI (e.g. relative /videos/... path).
        }
        return trimmed;
    }

    private static String sanitizeSlug(String value) {
        if (value == null || value.isBlank()) {
            return "cam-" + System.currentTimeMillis() % 100000;
        }
        String clean = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return clean.isEmpty() ? "cam-" + System.currentTimeMillis() % 100000 : (clean.length() > 60 ? clean.substring(0, 60) : clean);
    }

    private static String trimToNull(String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        return val.trim();
    }
}
