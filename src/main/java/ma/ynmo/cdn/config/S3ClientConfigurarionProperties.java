package ma.ynmo.cdn.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class S3ClientConfigurarionProperties {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey;
    @Value("${cloud.aws.credentials.bucket}")
    private String bucket;

}
