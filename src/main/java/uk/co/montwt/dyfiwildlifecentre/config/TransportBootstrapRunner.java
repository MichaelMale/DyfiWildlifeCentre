package uk.co.montwt.dyfiwildlifecentre.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.montwt.dyfiwildlifecentre.model.repository.PointOfInterestRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.Role;
import uk.co.montwt.dyfiwildlifecentre.security.model.User;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.RoleRepository;
import uk.co.montwt.dyfiwildlifecentre.security.model.repository.UserRepository;
import uk.co.montwt.dyfiwildlifecentre.security.service.UserService;
import uk.co.montwt.dyfiwildlifecentre.service.TransportImportService;

/**
 * Seeds transport POIs from bundled datasets and optionally creates an initial
 * administrator account when booting against a fresh database (for example, Docker Compose).
 */
@Component
@ConditionalOnProperty(name = "app.bootstrap.transport-on-empty", havingValue = "true", matchIfMissing = true)
public class TransportBootstrapRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportBootstrapRunner.class);

    private final PointOfInterestRepository pointOfInterestRepository;
    private final TransportImportService transportImportService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final String bootstrapAdminPassword;

    public TransportBootstrapRunner(PointOfInterestRepository pointOfInterestRepository,
                                    TransportImportService transportImportService,
                                    UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    UserService userService,
                                    @Value("${app.bootstrap.admin-password:}") String bootstrapAdminPassword) {
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.transportImportService = transportImportService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.bootstrapAdminPassword = bootstrapAdminPassword;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (pointOfInterestRepository.countByStationCodeIsNotNull() == 0) {
            var summary = transportImportService.syncBundledStations();
            LOGGER.info("Bootstrapped Welsh railway stations: inserted={}, updated={}, skipped={}",
                    summary.inserted(), summary.updated(), summary.skipped());
        }
        if (pointOfInterestRepository.countByBusStopCodeIsNotNull() == 0) {
            var summary = transportImportService.syncBundledBusStops();
            LOGGER.info("Bootstrapped Dyfi bus stops: inserted={}, updated={}, skipped={}",
                    summary.inserted(), summary.updated(), summary.skipped());
        }
        if (userRepository.count() == 0 && bootstrapAdminPassword != null && !bootstrapAdminPassword.isBlank()) {
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }
            User admin = new User();
            admin.setUsername("dyfi_admin");
            admin.setPassword(bootstrapAdminPassword.trim());
            userService.save(admin);
            LOGGER.info("Bootstrapped initial administrator user 'dyfi_admin'.");
        }
    }
}
