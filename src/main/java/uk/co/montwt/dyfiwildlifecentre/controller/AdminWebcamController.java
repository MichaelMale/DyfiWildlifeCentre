package uk.co.montwt.dyfiwildlifecentre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.montwt.dyfiwildlifecentre.model.WebcamFeed;
import uk.co.montwt.dyfiwildlifecentre.service.WebcamService;

@Controller
@RequestMapping("/admin/webcams")
public class AdminWebcamController {

    private final WebcamService webcamService;

    public AdminWebcamController(WebcamService webcamService) {
        this.webcamService = webcamService;
    }

    @GetMapping
    public String viewWebcams(Model model) {
        model.addAttribute("webcams", webcamService.findAll());
        model.addAttribute("newWebcam", new WebcamFeed());
        return "admin/webcams";
    }

    @PostMapping("/save")
    public String saveWebcam(@ModelAttribute WebcamFeed webcam, RedirectAttributes redirectAttributes) {
        WebcamFeed saved = webcamService.saveOrUpdate(webcam);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.webcamSaved");
        redirectAttributes.addFlashAttribute("flashSuccessArg", saved.getNameEn());
        return "redirect:/admin/webcams";
    }

    @PostMapping("/toggle")
    public String toggleWebcam(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        webcamService.toggleActive(id);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.webcamUpdated");
        return "redirect:/admin/webcams";
    }

    @PostMapping("/delete")
    public String deleteWebcam(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        webcamService.delete(id);
        redirectAttributes.addFlashAttribute("flashSuccessKey", "admin.flash.webcamDeleted");
        return "redirect:/admin/webcams";
    }
}
