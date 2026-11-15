package uk.co.montwt.dyfiwildlifecentre.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.montwt.dyfiwildlifecentre.exception.PostcodeException;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import javax.net.ssl.HttpsURLConnection;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * DirectionsService.java - Provides free OpenStreetMap / OSRM driving directions,
 * UK postcode / Nominatim geocoding, and nearby public transport guidance from
 * Dyfi Wildlife Centre.
 */
@Service
public class DirectionsService {

    public static final double CENTRE_LAT = 52.568774;
    public static final double CENTRE_LNG = -3.918031;
    private static final double METERS_PER_MILE = 1609.344;
    private static final Pattern UK_POSTCODE_PATTERN =
            Pattern.compile("^[A-Za-z]{1,2}\\d[A-Za-z\\d]?\\s*\\d[A-Za-z]{2}$");
    private static final Pattern COORDINATE_PAIR_PATTERN =
            Pattern.compile("^\\s*(-?\\d{1,2}(?:\\.\\d+)?)\\s*,\\s*(-?\\d{1,3}(?:\\.\\d+)?)\\s*$");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @FunctionalInterface
    interface JsonHttpClient {
        JsonNode get(String url) throws IOException;
    }

    private final PointOfInterestRepository pointOfInterestRepository;
    private JsonHttpClient httpClient = DirectionsService::fetchJson;

    @Autowired
    public DirectionsService(PointOfInterestRepository pointOfInterestRepository) {
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    void setHttpClient(JsonHttpClient httpClient) {
        this.httpClient = httpClient != null ? httpClient : DirectionsService::fetchJson;
    }

    void resetHttpClient() {
        this.httpClient = DirectionsService::fetchJson;
    }

    /**
     * Calculates driving or public transport directions from Dyfi Wildlife Centre to a visitor destination.
     *
     * @param destination UK postcode, town, or address.
     * @param mode        {@code "driving"} or {@code "transit"}.
     * @return Structured directions payload.
     */
    public DirectionsResult getDirections(String destination, String mode) {
        return getDirections(destination, mode, "fromCentre");
    }

    /**
     * Calculates two-way driving or public transport directions either to or from Dyfi Wildlife Centre.
     *
     * @param locationQuery UK postcode, town, address, or "lat, lng" coordinate pair.
     * @param mode          {@code "driving"} or {@code "transit"}.
     * @param direction     {@code "toCentre"} or {@code "fromCentre"}.
     * @return Structured directions payload.
     */
    public DirectionsResult getDirections(String locationQuery, String mode, String direction) {
        if (locationQuery == null || locationQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a postcode, town, or address.");
        }

        String normalizedMode = "transit".equalsIgnoreCase(mode) ? "transit" : "driving";
        boolean toCentre = "toCentre".equalsIgnoreCase(direction);
        String normalizedDirection = toCentre ? "toCentre" : "fromCentre";
        String trimmedQuery = locationQuery.trim();

        ResolvedLocation resolved = resolveDestination(trimmedQuery);
        List<TransitHub> nearestHubs = findNearestTransitHubs();

        double startLat = toCentre ? resolved.lat() : CENTRE_LAT;
        double startLng = toCentre ? resolved.lng() : CENTRE_LNG;
        double endLat = toCentre ? CENTRE_LAT : resolved.lat();
        double endLng = toCentre ? CENTRE_LNG : resolved.lng();

        String originLabel = toCentre ? resolved.label() : "Dyfi Wildlife Centre";
        String destLabel = toCentre ? "Dyfi Wildlife Centre" : resolved.label();

        String osmDirectionsUrl = String.format(
                Locale.ROOT,
                "https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=%.6f%%2C%.6f%%3B%.6f%%2C%.6f",
                startLat, startLng, endLat, endLng);

        if ("transit".equals(normalizedMode)) {
            double directMiles = haversineMiles(startLat, startLng, endLat, endLng);
            List<double[]> fallbackCoords = List.of(
                    new double[]{startLat, startLng},
                    new double[]{endLat, endLng}
            );
            List<String> transitSteps = buildTransitSteps(resolved, nearestHubs, directMiles, toCentre);
            return new DirectionsResult(
                    trimmedQuery,
                    destLabel,
                    endLat,
                    endLng,
                    "transit",
                    roundOneDecimal(directMiles),
                    Math.max(5L, Math.round(directMiles * 2.0)),
                    true,
                    fallbackCoords,
                    transitSteps,
                    nearestHubs,
                    osmDirectionsUrl,
                    "https://www.traveline.cymru/",
                    normalizedDirection,
                    originLabel,
                    resolved.lat(),
                    resolved.lng(),
                    resolved.label()
            );
        }

        return fetchDrivingRoute(trimmedQuery, resolved, normalizedDirection, originLabel, destLabel,
                startLat, startLng, endLat, endLng, nearestHubs, osmDirectionsUrl);
    }

    private DirectionsResult fetchDrivingRoute(String query,
                                               ResolvedLocation resolved,
                                               String direction,
                                               String originLabel,
                                               String destLabel,
                                               double startLat,
                                               double startLng,
                                               double endLat,
                                               double endLng,
                                               List<TransitHub> nearestHubs,
                                               String osmDirectionsUrl) {
        String osrmUrl = String.format(
                Locale.ROOT,
                "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson&steps=true",
                startLng, startLat, endLng, endLat);

        try {
            JsonNode root = httpClient.get(osrmUrl);
            if (root != null && "Ok".equalsIgnoreCase(root.path("code").asText())
                    && root.path("routes").isArray() && !root.path("routes").isEmpty()) {
                JsonNode route = root.path("routes").get(0);
                double miles = roundOneDecimal(route.path("distance").asDouble(0) / METERS_PER_MILE);
                long minutes = Math.max(1L, Math.round(route.path("duration").asDouble(0) / 60.0));

                List<double[]> coords = new ArrayList<>();
                JsonNode rawCoords = route.path("geometry").path("coordinates");
                if (rawCoords.isArray()) {
                    for (JsonNode pt : rawCoords) {
                        if (pt.isArray() && pt.size() >= 2) {
                            coords.add(new double[]{pt.get(1).asDouble(), pt.get(0).asDouble()});
                        }
                    }
                }
                if (coords.isEmpty()) {
                    coords.add(new double[]{startLat, startLng});
                    coords.add(new double[]{endLat, endLng});
                }

                List<String> steps = parseOsrmSteps(route);
                return new DirectionsResult(
                        query,
                        destLabel,
                        endLat,
                        endLng,
                        "driving",
                        miles,
                        minutes,
                        false,
                        coords,
                        steps,
                        nearestHubs,
                        osmDirectionsUrl,
                        "https://www.traveline.cymru/",
                        direction,
                        originLabel,
                        resolved.lat(),
                        resolved.lng(),
                        resolved.label()
                );
            }
        } catch (IOException ignored) {
            // Fall through to Haversine estimate below when external OSRM is unreachable.
        }

        double directMiles = roundOneDecimal(haversineMiles(startLat, startLng, endLat, endLng));
        List<String> fallbackSteps = "toCentre".equals(direction)
                ? List.of(
                        String.format(Locale.ROOT, "Head from %s toward Machynlleth and the A487.", resolved.label()),
                        String.format(Locale.ROOT, "Follow signs for Dyfi Wildlife Centre / Cors Dyfi (approx. %.1f miles direct).", directMiles)
                )
                : List.of(
                        "Leave Dyfi Wildlife Centre (Cors Dyfi) heading onto the A487.",
                        String.format(Locale.ROOT, "Follow local roads toward %s (approx. %.1f miles direct).", resolved.label(), directMiles)
                );
        return new DirectionsResult(
                query,
                destLabel,
                endLat,
                endLng,
                "driving",
                directMiles,
                Math.max(2L, Math.round(directMiles * 1.5)),
                true,
                List.of(new double[]{startLat, startLng}, new double[]{endLat, endLng}),
                fallbackSteps,
                nearestHubs,
                osmDirectionsUrl,
                "https://www.traveline.cymru/",
                direction,
                originLabel,
                resolved.lat(),
                resolved.lng(),
                resolved.label()
        );
    }

    private static List<String> parseOsrmSteps(JsonNode route) {
        List<String> steps = new ArrayList<>();
        JsonNode legs = route.path("legs");
        if (!legs.isArray()) {
            return steps;
        }
        for (JsonNode leg : legs) {
            JsonNode legSteps = leg.path("steps");
            if (!legSteps.isArray()) {
                continue;
            }
            for (JsonNode step : legSteps) {
                String formatted = formatOsrmStep(step);
                if (!formatted.isEmpty()) {
                    steps.add(formatted);
                }
            }
        }
        return steps;
    }

    private static String formatOsrmStep(JsonNode step) {
        JsonNode maneuver = step.path("maneuver");
        String type = maneuver.path("type").asText("");
        String modifier = maneuver.path("modifier").asText("");
        String road = step.path("name").asText("");
        double distMeters = step.path("distance").asDouble(0);

        String action;
        if ("arrive".equals(type)) {
            return "Arrive at destination";
        } else if ("depart".equals(type)) {
            action = modifier.isEmpty() ? "Depart" : "Head " + modifier;
        } else if ("roundabout".equals(type) || "rotary".equals(type)) {
            int exit = maneuver.path("exit").asInt(0);
            action = exit > 0 ? "At roundabout take exit " + exit : "Enter roundabout";
        } else if (!modifier.isEmpty()) {
            action = capitalize(type.replace('_', ' ')) + " " + modifier;
        } else if (!type.isEmpty()) {
            action = capitalize(type.replace('_', ' '));
        } else {
            action = "Continue";
        }

        String roadPart = road.isEmpty() ? "" : " onto " + road;
        String distPart = distMeters >= 160
                ? String.format(Locale.ROOT, " (%.1f mi)", distMeters / METERS_PER_MILE)
                : String.format(Locale.ROOT, " (%d m)", Math.round(distMeters));
        return action + roadPart + distPart;
    }

    private static List<String> buildTransitSteps(ResolvedLocation resolved,
                                                  List<TransitHub> hubs,
                                                  double directMiles,
                                                  boolean toCentre) {
        List<String> steps = new ArrayList<>();
        if (!hubs.isEmpty()) {
            TransitHub primary = hubs.get(0);
            steps.add(String.format(
                    Locale.ROOT,
                    "Nearest public transport point: %s (%.2f miles from Dyfi Wildlife Centre).",
                    primary.name(), primary.distanceMiles()));
            if (hubs.size() > 1) {
                TransitHub secondary = hubs.get(1);
                steps.add(String.format(
                        Locale.ROOT,
                        "Alternative station/stop: %s (%.2f miles from centre).",
                        secondary.name(), secondary.distanceMiles()));
            }
        } else {
            steps.add("No public transport stops are currently recorded nearby; nearest rail link is Dovey Junction / Machynlleth.");
        }
        String actionText = toCentre
                ? String.format(Locale.ROOT,
                        "Check live Cambrian Line rail and TrawsCymru / local bus departures from %s toward Dyfi Wildlife Centre (%.1f miles straight-line distance) via Traveline Cymru.",
                        resolved.label(), directMiles)
                : String.format(Locale.ROOT,
                        "Check live Cambrian Line rail and TrawsCymru / local bus departures toward %s (%.1f miles straight-line distance) via Traveline Cymru.",
                        resolved.label(), directMiles);
        steps.add(actionText);
        return steps;
    }

    private List<TransitHub> findNearestTransitHubs() {
        return pointOfInterestRepository.findAll().stream()
                .filter(poi -> "transport".equalsIgnoreCase(poi.getCategory()))
                .sorted(Comparator.comparingDouble(PointOfInterest::calculateDistanceFromCentre))
                .limit(5)
                .map(poi -> new TransitHub(
                        poi.getId(),
                        poi.getName(),
                        poi.getNameCy(),
                        poi.getLatitude(),
                        poi.getLongitude(),
                        poi.calculateDistanceFromCentre()
                ))
                .toList();
    }

    private ResolvedLocation resolveDestination(String query) {
        var coordMatch = COORDINATE_PAIR_PATTERN.matcher(query);
        if (coordMatch.matches()) {
            double lat = Double.parseDouble(coordMatch.group(1));
            double lng = Double.parseDouble(coordMatch.group(2));
            if (lat >= -90.0 && lat <= 90.0 && lng >= -180.0 && lng <= 180.0) {
                return new ResolvedLocation(lat, lng, String.format(Locale.ROOT, "%.5f, %.5f", lat, lng));
            }
        }

        if (UK_POSTCODE_PATTERN.matcher(query).matches()) {
            try {
                Point2D.Double coords = PostcodeService.getCoordinatesFromPostcode(query);
                return new ResolvedLocation(coords.getX(), coords.getY(), query.toUpperCase(Locale.ROOT));
            } catch (IOException | PostcodeException ignored) {
                // Fall back to Nominatim if postcodes.io fails or is unavailable.
            }
        }

        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            JsonNode gbResults = httpClient.get(
                    "https://nominatim.openstreetmap.org/search?format=json&limit=1&countrycodes=gb&q=" + encoded);
            ResolvedLocation parsed = parseNominatimFirst(gbResults);
            if (parsed != null) {
                return parsed;
            }
            JsonNode globalResults = httpClient.get(
                    "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + encoded);
            parsed = parseNominatimFirst(globalResults);
            if (parsed != null) {
                return parsed;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to look up destination '" + query + "'.");
        }

        throw new IllegalArgumentException("Could not find destination: " + query);
    }

    private static ResolvedLocation parseNominatimFirst(JsonNode results) {
        if (results != null && results.isArray() && !results.isEmpty()) {
            JsonNode first = results.get(0);
            if (first.hasNonNull("lat") && first.hasNonNull("lon")) {
                double lat = Double.parseDouble(first.get("lat").asText());
                double lon = Double.parseDouble(first.get("lon").asText());
                String label = first.path("display_name").asText("Destination");
                return new ResolvedLocation(lat, lon, label);
            }
        }
        return null;
    }

    private static JsonNode fetchJson(String urlString) throws IOException {
        URL url = URI.create(urlString).toURL();
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestProperty("User-Agent", "DyfiWildlifeCentre/1.0 (https://github.com/MichaelMale/DyfiWildlifeCentre)");
        try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return OBJECT_MAPPER.readTree(response.toString());
        } finally {
            con.disconnect();
        }
    }

    private static double haversineMiles(double lat1, double lng1, double lat2, double lng2) {
        final double earthRadiusMiles = 3958.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLng / 2), 2) * Math.cos(rLat1) * Math.cos(rLat2);
        return earthRadiusMiles * 2 * Math.asin(Math.sqrt(a));
    }

    private static double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private record ResolvedLocation(double lat, double lng, String label) {
    }

    public record TransitHub(
            long id,
            String name,
            String nameCy,
            double latitude,
            double longitude,
            double distanceMiles
    ) {
    }

    public record DirectionsResult(
            String destinationQuery,
            String destinationLabel,
            double destinationLat,
            double destinationLng,
            String mode,
            double distanceMiles,
            long durationMinutes,
            boolean estimated,
            List<double[]> coordinates,
            List<String> steps,
            List<TransitHub> nearestTransitHubs,
            String externalDirectionsUrl,
            String transitPlannerUrl,
            String direction,
            String originLabel,
            double remoteLat,
            double remoteLng,
            String remoteLabel
    ) {
        public DirectionsResult(
                String destinationQuery,
                String destinationLabel,
                double destinationLat,
                double destinationLng,
                String mode,
                double distanceMiles,
                long durationMinutes,
                boolean estimated,
                List<double[]> coordinates,
                List<String> steps,
                List<TransitHub> nearestTransitHubs,
                String externalDirectionsUrl,
                String transitPlannerUrl
        ) {
            this(destinationQuery, destinationLabel, destinationLat, destinationLng,
                    mode, distanceMiles, durationMinutes, estimated, coordinates,
                    steps, nearestTransitHubs, externalDirectionsUrl, transitPlannerUrl,
                    "fromCentre", "Dyfi Wildlife Centre", destinationLat, destinationLng, destinationLabel);
        }
    }
}
