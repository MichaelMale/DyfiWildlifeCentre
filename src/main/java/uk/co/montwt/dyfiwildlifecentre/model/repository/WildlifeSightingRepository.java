package uk.co.montwt.dyfiwildlifecentre.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;

import java.util.List;

public interface WildlifeSightingRepository extends JpaRepository<WildlifeSighting, Long> {

    List<WildlifeSighting> findTop20ByOrderBySightedAtDescIdDesc();

    List<WildlifeSighting> findAllByOrderBySightedAtDescIdDesc();
}
