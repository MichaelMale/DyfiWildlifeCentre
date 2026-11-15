package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;
import uk.co.montwt.dyfiwildlifecentre.service.WildlifeSightingService;

import java.security.Principal;

@Controller
@RequestMapping("/admin/sightings")
public class AdminSightingController {

    private final WildlifeSightingService wildlifeSightingService;

    public AdminSightingController(WildlifeSightingService wildlifeSightingService) {
        this.wildlifeSightingService = wildlifeSightingService;
    }

    @GetMapping
    public String viewSightings(Model model) {
        model.addAttribute("sightings", wildlifeSightingService.findAll());
        model.addAttribute("newSighting", new WildlifeSighting());
        return "admin/sightings";
    }

    @PostMapping("/create")
    public String createSighting(@ModelAttribute WildlifeSighting sighting,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String author = principal != null ? principal.getName() : "admin";
        WildlifeSighting saved = wildlifeSightingService.record(sighting, author);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.sightingCreated");
        redirectAttributes.addFlashAttribute("flashSuccessArg", saved.getSpecies());
        return "redirect:/admin/sightings";
    }

    @PostMapping("/delete")
    public String deleteSighting(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        wildlifeSightingService.delete(id);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.sightingDeleted");
        return "redirect:/admin/sightings";
    }
}
