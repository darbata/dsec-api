package io.darbata.basecampapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BasecampApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasecampApiApplication.class, args);
    }

}
