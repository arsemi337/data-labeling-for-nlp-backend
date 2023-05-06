package it.nlp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DataLabelingForNlpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataLabelingForNlpBackendApplication.class, args);
	}
}