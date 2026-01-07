package io.darbata.basecampapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.core.ApplicationModules;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class ModularityTests {
    static ApplicationModules modules = ApplicationModules.of(BasecampApiApplication.class);

    @Test
    void verifiesModularStructure() {
        System.out.println("Running modularity tests");
        modules.forEach(System.out::println);
        modules.verify();
    }
}
