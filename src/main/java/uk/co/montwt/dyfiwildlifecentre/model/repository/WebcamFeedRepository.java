package uk.co.montwt.dyfiwildlifecentre.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.montwt.dyfiwildlifecentre.model.WebcamFeed;

import java.util.List;
import java.util.Optional;

public interface WebcamFeedRepository extends JpaRepository<WebcamFeed, Long> {

    List<WebcamFeed> findByActiveTrueOrderBySortOrderAscIdAsc();

    List<WebcamFeed> findAllByOrderBySortOrderAscIdAsc();

    Optional<WebcamFeed> findBySlug(String slug);
}
