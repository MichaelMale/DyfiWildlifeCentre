package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;
import uk.co.montwt.dyfiwildlifecentre.service.TransportImportService;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/admin/import")
public class AdminImportController {

    private final TransportImportService transportImportService;
    private final PointOfInterestRepository pointOfInterestRepository;

    public AdminImportController(TransportImportService transportImportService,
                                 PointOfInterestRepository pointOfInterestRepository) {
        this.transportImportService = transportImportService;
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @GetMapping
    public String viewImportPage(Model model) {
        model.addAttribute("stationCount", pointOfInterestRepository.countByStationCodeIsNotNull());
        model.addAttribute("busStopCount", pointOfInterestRepository.countByBusStopCodeIsNotNull());
        return "admin/import";
    }

    @PostMapping("/stations")
    public String importStations(@RequestParam(value = "file", required = false) MultipartFile file,
                                 RedirectAttributes redirectAttributes) throws IOException {
        TransportImportService.ImportSummary summary;
        if (file != null && !file.isEmpty()) {
            try (InputStream is = file.getInputStream()) {
                summary = transportImportService.importRailwayStations(is);
            }
        } else {
            summary = transportImportService.syncBundledStations();
        }
        addSummaryFlash(redirectAttributes, summary);
        return "redirect:/admin/import";
    }

    @PostMapping("/bus-stops")
    public String importBusStops(@RequestParam(value = "file", required = false) MultipartFile file,
                                 RedirectAttributes redirectAttributes) throws IOException {
        TransportImportService.ImportSummary summary;
        if (file != null && !file.isEmpty()) {
            try (InputStream is = file.getInputStream()) {
                summary = transportImportService.importBusStops(is);
            }
        } else {
            summary = transportImportService.syncBundledBusStops();
        }
        addSummaryFlash(redirectAttributes, summary);
        return "redirect:/admin/import";
    }

    private static void addSummaryFlash(RedirectAttributes redirectAttributes,
                                        TransportImportService.ImportSummary summary) {
        String formatted = summary.inserted() + " inserted, " + summary.updated() + " updated, " + summary.skipped() + " skipped";
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.transportSynced");
        redirectAttributes.addFlashAttribute("flashSuccessArg", formatted);
    }
}
