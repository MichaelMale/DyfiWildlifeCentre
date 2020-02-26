package uk.co.montwt.dyfi.model;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PageRepository {
    private static final List<String> pageList = Arrays.asList(
            "Home",
            "About",
            "Admin Panel"
    );
}
