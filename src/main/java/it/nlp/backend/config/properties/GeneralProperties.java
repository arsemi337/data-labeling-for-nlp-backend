package it.nlp.backend.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("general")
@Getter
@Setter
public class GeneralProperties {
    private String modelsAppDestinationPath;
    private Boolean securityTurnedOn;
    private String tfServingConfigFileName;
    private String tfServingHost;
    private String tfServingPort;
    private Long ytVideosMaxResults;
    private Long ytCommentsMaxResults;
    private Long ytChannelVideosMaxResults;
}
