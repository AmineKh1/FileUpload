package ma.ynmo.cdn.config;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.StringUtils;

import java.time.Duration;

@Configurable
@EnableAutoConfiguration
public class StorageConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(S3ClientConfigurarionProperties s3props) {
        if (StringUtils.isBlank(s3props.getAccessKeyId())) {
            return DefaultCredentialsProvider.create();
        } else {
            return () -> {
                return AwsBasicCredentials.create(
                        s3props.getAccessKeyId(),
                        s3props.getSecretAccessKey());
            };
        }
    }
    @Bean
    public S3AsyncClient s3client(S3ClientConfigurarionProperties s3props,
                                  AwsCredentialsProvider credentialsProvider) {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
        S3AsyncClientBuilder b = S3AsyncClient.builder().httpClient(httpClient)
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(serviceConfiguration);

//        if (s3props.getEndpoint() != null) {
//            b = b.endpointOverride(s3props.getEndpoint());
//        }
        return b.build();
    }
}