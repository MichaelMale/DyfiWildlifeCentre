package uk.co.montwt.dyfi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    /**
     * Responds to an HTTP Request for / or /index
     *
     * @return String corresponding to that request
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Responds to an HTTP Request for /about
     *
     * @return String corresponding to that request
     */
    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Responds to an HTTP Request for /admin
     *
     * @return String corresponding to that request
     */
    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }
}
