package it.nlp.backend.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("general")
@Getter
@Setter
public class GeneralProperties {
    private Boolean securityTurnedOn;
    private String tfServingModelsBasePath;
    private String tfServingHost;
    private String tfServingPort;
}
