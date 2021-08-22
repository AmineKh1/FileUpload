package ma.ynmo.cdn;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class CdnApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdnApplication.class, args);
	}

	@Bean
	CommandLineRunner run(FileDataService fileDataService)
	{
		return args -> {
			fileDataService.save(new FileData(0L,"sdf", UUID.randomUUID(), UUID.randomUUID(),
					"sdf", "url", FileStatus.CANCLED ,1L, LocalDateTime.now())).subscribe();

			fileDataService.findAllByStatus(FileStatus.PENDING)
					.subscribe(System.out::println);
		};
	}
}
