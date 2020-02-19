package uk.co.montwt.dyfi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MasterController {
    @RequestMapping("/")
        public String index() {
        return "index";
    }

    @RequestMapping("/about")
        public void handleAbout() {
        System.out.println("About clicked.");
    }

    @RequestMapping("/admin")
        public void handleAdmin() {
        System.out.println("Admin clicked.");
    }
}
