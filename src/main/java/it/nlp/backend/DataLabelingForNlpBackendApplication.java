package it.nlp.backend;

import it.nlp.backend.migration.service.MigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DataLabelingForNlpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataLabelingForNlpBackendApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner(MigrationService migrationService) {
//		return args -> migrationService.checkAssignmentsNumber();
//	}
}