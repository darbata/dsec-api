package io.darbata.basecampapi;

import io.darbata.basecampapi.common.TestInitializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TestInitializer.class)
class BasecampApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
