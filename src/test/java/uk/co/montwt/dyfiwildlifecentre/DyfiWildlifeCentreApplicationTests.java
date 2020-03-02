package uk.co.montwt.dyfiwildlifecentre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DyfiWildlifeCentreApplicationTests {

    @Autowired
    private DyfiWildlifeCentreApplication application;

    @Test
    void contextLoads() throws Exception {
        assertThat(application).isNotNull();
    }

}
