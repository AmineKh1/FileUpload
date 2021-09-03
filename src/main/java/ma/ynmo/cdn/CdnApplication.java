package ma.ynmo.cdn;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.model.Store;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.StoreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class CdnApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdnApplication.class, args);
	}

	@Bean
	CommandLineRunner run(FileDataService fileDataService, StoreService storeService)
	{
		return args -> {
			storeService.save(new Store(UUID.fromString("3227cacd-ee25-4160-9cd1-f3e286a56c15"),
					UUID.fromString("3227cacd-ee25-4160-9cd1-f3e286a56c14"),0L,
					111123123212030L)).subscribe(System.out::println);
//			fileDataService.save(new FileData(0L,"img.png", UUID.randomUUID(), UUID.randomUUID(),
//					"sdf", "png", FileStatus.PENDING ,1L, LocalDateTime.now())).subscribe();
//
//			fileDataService.findAllByStatus(FileStatus.PENDING)
//					.subscribe(System.out::println);
		};
	}
}
