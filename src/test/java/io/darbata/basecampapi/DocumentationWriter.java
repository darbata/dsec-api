package io.darbata.basecampapi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class DocumentationWriter {
    @Test
    @Disabled("enable or run manually, don't run this be default")
    void writeDocumentation() {
        final ApplicationModules modules = ApplicationModules.of(BasecampApiApplication.class);
        new Documenter(modules).writeDocumentation();
    }
}
