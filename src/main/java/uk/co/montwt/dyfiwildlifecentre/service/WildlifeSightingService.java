package uk.co.montwt.dyfiwildlifecentre.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.montwt.dyfiwildlifecentre.model.WildlifeSighting;
import uk.co.montwt.dyfiwildlifecentre.model.repository.WildlifeSightingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WildlifeSightingService {

    private final WildlifeSightingRepository sightingRepository;

    public WildlifeSightingService(WildlifeSightingRepository sightingRepository) {
        this.sightingRepository = sightingRepository;
    }

    public List<WildlifeSighting> findLatestPublic() {
        return sightingRepository.findTop20ByOrderBySightedAtDescIdDesc();
    }

    public List<WildlifeSighting> findAll() {
        return sightingRepository.findAllByOrderBySightedAtDescIdDesc();
    }

    @Transactional
    public WildlifeSighting record(WildlifeSighting sighting, String username) {
        if (sighting.getSightedAt() == null) {
            sighting.setSightedAt(LocalDateTime.now());
        }
        sighting.setRecordedBy(username);
        if (sighting.getSpecies() != null) {
            sighting.setSpecies(sighting.getSpecies().trim());
        }
        return sightingRepository.save(sighting);
    }

    @Transactional
    public void delete(Long id) {
        sightingRepository.deleteById(id);
    }
}
