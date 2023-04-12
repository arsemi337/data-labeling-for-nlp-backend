package it.nlp.backend.config;

import it.nlp.backend.utils.TimeSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class TimeConfig {
    @Bean
    TimeSupplier timeSupplier() {
        return LocalDateTime::now;
    }
}
