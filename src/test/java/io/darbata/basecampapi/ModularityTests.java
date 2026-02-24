package io.darbata.basecampapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.core.ApplicationModules;

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
