package uk.co.montwt.dyfiwildlifecentre.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.montwt.dyfiwildlifecentre.model.PointOfInterest;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Imports and synchronizes Welsh railway stations and NaPTAN bus stops from CSV
 * uploads or bundled classpath datasets, upserting by transport code.
 */
@Service
public class TransportImportService {

    public static final String BUNDLED_STATIONS_CSV = "data/wales-railway-stations.csv";
    public static final String BUNDLED_BUS_STOPS_CSV = "data/dyfi-bus-stops.csv";

    private final PointOfInterestRepository pointOfInterestRepository;

    public TransportImportService(PointOfInterestRepository pointOfInterestRepository) {
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    public ImportSummary syncBundledStations() throws IOException {
        try (InputStream is = new ClassPathResource(BUNDLED_STATIONS_CSV).getInputStream()) {
            return importRailwayStations(is);
        }
    }

    public ImportSummary syncBundledBusStops() throws IOException {
        try (InputStream is = new ClassPathResource(BUNDLED_BUS_STOPS_CSV).getInputStream()) {
            return importBusStops(is);
        }
    }

    @Transactional
    public ImportSummary importRailwayStations(InputStream inputStream) throws IOException {
        List<Map<String, String>> rows = readCsvRows(inputStream);
        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        for (Map<String, String> row : rows) {
            String code = firstNonBlank(row, "station_code", "crscode", "crs", "code");
            String name = firstNonBlank(row, "station_name", "name");
            Double lat = parseDoubleOrNull(firstNonBlank(row, "latitude", "lat"));
            Double lng = parseDoubleOrNull(firstNonBlank(row, "longitude", "lon", "lng"));

            if (code == null || name == null || !isValidCoordinate(lat, lng)) {
                skipped++;
                continue;
            }

            String normalizedCode = code.toUpperCase(Locale.ROOT);
            PointOfInterest existing = pointOfInterestRepository.findByStationCode(normalizedCode).orElse(null);
            boolean isNew = existing == null;
            PointOfInterest poi = isNew ? new PointOfInterest() : existing;

            poi.setStationCode(normalizedCode);
            poi.setBusStopCode(null);
            poi.setName(name);
            if (isNew || poi.getDescription() == null || poi.getDescription().isBlank()) {
                poi.setDescription("Welsh railway station (" + normalizedCode + ")");
            }
            if (isNew || poi.getDescriptionCy() == null || poi.getDescriptionCy().isBlank()) {
                poi.setDescriptionCy("Gorsaf reilffordd yng Nghymru (" + normalizedCode + ")");
            }
            poi.setLatitude(lat);
            poi.setLongitude(lng);
            poi.setPostcode("");
            poi.setCategory("transport");
            if (poi.getImageUrl() == null || poi.getImageUrl().isBlank()) {
                poi.setImageUrl("/images/thomas-train.jpg");
            }
            poi.setDistanceFromCentre(poi.calculateDistanceFromCentre());
            pointOfInterestRepository.save(poi);

            if (isNew) {
                inserted++;
            } else {
                updated++;
            }
        }
        return new ImportSummary("stations", inserted, updated, skipped);
    }

    @Transactional
    public ImportSummary importBusStops(InputStream inputStream) throws IOException {
        List<Map<String, String>> rows = readCsvRows(inputStream);
        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        for (Map<String, String> row : rows) {
            String status = firstNonBlank(row, "status");
            if (status != null && "inactive".equalsIgnoreCase(status)) {
                skipped++;
                continue;
            }

            String atco = firstNonBlank(row, "atcocode", "bus_stop_code", "code");
            String naptan = firstNonBlank(row, "naptancode");
            String commonName = firstNonBlank(row, "commonname", "name");
            String locality = firstNonBlank(row, "localityname", "town");
            String bearing = firstNonBlank(row, "bearing");
            String street = firstNonBlank(row, "street");
            String indicator = firstNonBlank(row, "indicator");
            Double lat = parseDoubleOrNull(firstNonBlank(row, "latitude", "lat"));
            Double lng = parseDoubleOrNull(firstNonBlank(row, "longitude", "lon", "lng"));

            if (atco == null || commonName == null || !isValidCoordinate(lat, lng)) {
                skipped++;
                continue;
            }

            String displayName = formatBusStopName(commonName, locality, bearing);
            String descriptionEn = buildBusStopDescription(atco, naptan, indicator, street, locality, bearing);
            String descriptionCy = buildBusStopDescriptionCy(atco, naptan, street, locality);

            PointOfInterest existing = pointOfInterestRepository.findByBusStopCode(atco).orElse(null);
            boolean isNew = existing == null;
            PointOfInterest poi = isNew ? new PointOfInterest() : existing;

            poi.setBusStopCode(atco);
            poi.setStationCode(null);
            poi.setName(displayName);
            poi.setDescription(descriptionEn);
            if (isNew || poi.getDescriptionCy() == null || poi.getDescriptionCy().isBlank()) {
                poi.setDescriptionCy(descriptionCy);
            }
            poi.setLatitude(lat);
            poi.setLongitude(lng);
            poi.setPostcode("");
            poi.setCategory("transport");
            if (poi.getImageUrl() == null || poi.getImageUrl().isBlank()) {
                poi.setImageUrl("/images/magic-school-bus.jpg");
            }
            poi.setDistanceFromCentre(poi.calculateDistanceFromCentre());
            pointOfInterestRepository.save(poi);

            if (isNew) {
                inserted++;
            } else {
                updated++;
            }
        }
        return new ImportSummary("busStops", inserted, updated, skipped);
    }

    private static String formatBusStopName(String commonName, String locality, String bearing) {
        StringBuilder sb = new StringBuilder(commonName);
        if (locality != null && !commonName.toLowerCase(Locale.ROOT).contains(locality.toLowerCase(Locale.ROOT))) {
            sb.append(", ").append(locality);
        }
        if (bearing != null) {
            sb.append(" (").append(bearing).append(")");
        }
        return sb.toString();
    }

    private static String buildBusStopDescription(String atco, String naptan, String indicator,
                                                  String street, String locality, String bearing) {
        String codeLabel = naptan != null ? atco + " / " + naptan : atco;
        List<String> parts = new ArrayList<>();
        if (indicator != null) parts.add(indicator);
        if (street != null) parts.add(street);
        if (locality != null) parts.add(locality);
        if (bearing != null) parts.add("bearing " + bearing);
        String detail = parts.isEmpty() ? "" : " — " + String.join(", ", parts);
        String raw = "Bus stop (" + codeLabel + ")" + detail;
        return raw.length() > 250 ? raw.substring(0, 250) : raw;
    }

    private static String buildBusStopDescriptionCy(String atco, String naptan, String street, String locality) {
        String codeLabel = naptan != null ? atco + " / " + naptan : atco;
        List<String> parts = new ArrayList<>();
        if (street != null) parts.add(street);
        if (locality != null) parts.add(locality);
        String detail = parts.isEmpty() ? "" : " — " + String.join(", ", parts);
        String raw = "Arosfan bws (" + codeLabel + ")" + detail;
        return raw.length() > 250 ? raw.substring(0, 250) : raw;
    }

    private static List<Map<String, String>> readCsvRows(InputStream inputStream) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return rows;
            }
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            List<String> headers = parseCsvLine(headerLine).stream()
                    .map(h -> h.trim().toLowerCase(Locale.ROOT))
                    .toList();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> values = parseCsvLine(line);
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    String val = i < values.size() ? values.get(i).trim() : "";
                    row.put(headers.get(i), val);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    private static String firstNonBlank(Map<String, String> row, String... keys) {
        for (String key : keys) {
            String val = row.get(key);
            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }
        return null;
    }

    private static Double parseDoubleOrNull(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isValidCoordinate(Double lat, Double lng) {
        return lat != null && lng != null
                && lat > -90.0 && lat < 90.0 && lat != 0.0
                && lng > -180.0 && lng < 180.0 && lng != 0.0;
    }

    public record ImportSummary(String dataset, int inserted, int updated, int skipped) {
    }
}
