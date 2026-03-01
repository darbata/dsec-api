package io.darbata.basecampapi.common;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class TestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.load();
        Map<String, Object> dotEnvProps = new HashMap<>();
        dotenv.entries().forEach(entry -> dotEnvProps.put(entry.getKey(), entry.getValue()));

        applicationContext.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("dotenvProperties", dotEnvProps)
        );
    }
}
