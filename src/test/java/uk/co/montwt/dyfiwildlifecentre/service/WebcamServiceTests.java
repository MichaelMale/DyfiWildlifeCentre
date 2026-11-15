package uk.co.montwt.dyfiwildlifecentre.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.montwt.dyfiwildlifecentre.config.WebcamProperties;
import uk.co.montwt.dyfiwildlifecentre.model.WebcamFeed;
import uk.co.montwt.dyfiwildlifecentre.model.repository.WebcamFeedRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebcamServiceTests {

    @Mock
    private WebcamFeedRepository repository;

    private WebcamService webcamService;

    @BeforeEach
    void setup() {
        webcamService = new WebcamService(repository, new WebcamProperties());
    }

    @Test
    void fallsBackToDefaultPropertiesWhenDatabaseTableEmpty() {
        when(repository.findByActiveTrueOrderBySortOrderAscIdAsc()).thenReturn(List.of());
        var feeds = webcamService.getActivePublicFeeds();
        Assertions.assertEquals(1, feeds.size());
        Assertions.assertEquals("nest-cam", feeds.get(0).getId());
    }

    @Test
    void normalizesYouTubeWatchAndShortUrlsToEmbedOnSave() {
        when(repository.findBySlug("estuary-cam")).thenReturn(Optional.empty());
        when(repository.save(any(WebcamFeed.class))).thenAnswer(inv -> inv.getArgument(0));

        WebcamFeed incoming = new WebcamFeed();
        incoming.setSlug("Estuary Cam");
        incoming.setNameEn("Estuary View");
        incoming.setStreamUrl("https://www.youtube.com/watch?v=YnKZJ3SF5WM");
        incoming.setActive(true);

        webcamService.saveOrUpdate(incoming);

        ArgumentCaptor<WebcamFeed> captor = ArgumentCaptor.forClass(WebcamFeed.class);
        verify(repository).save(captor.capture());
        Assertions.assertEquals("estuary-cam", captor.getValue().getSlug());
        Assertions.assertEquals("https://www.youtube.com/embed/YnKZJ3SF5WM?rel=0", captor.getValue().getStreamUrl());
        Assertions.assertEquals("/images/osprey.jpeg", captor.getValue().getPosterUrl());
    }
}
