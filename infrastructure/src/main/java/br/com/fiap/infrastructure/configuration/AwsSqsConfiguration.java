package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.infrastructure.adapters.messaging.SqsVideoEventPublisherAdapter;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;

@Configuration
public class AwsSqsConfiguration {

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.access-key-id:test}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:test}")
    private String secretAccessKey;

    @Value("${aws.endpoint-override:#{null}}")
    private String endpointOverride;

    @Value("${aws.sqs.queues.video-uploaded:http://localhost:4566/000000000000/video-uploaded}")
    private String videoUploadedQueueUrl;

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
    public SqsClient sqsClient() {
        var builder = SqsClient.builder().region(Region.of(region));
        applyEndpoint(builder, endpointOverride);
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public S3Client s3Client() {
        var builder = S3Client.builder().region(Region.of(region));
        if (endpointOverride != null && !endpointOverride.isBlank()) {
            builder.endpointOverride(URI.create(endpointOverride))
                   .credentialsProvider(StaticCredentialsProvider.create(
                           AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                   .forcePathStyle(true);
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "true", matchIfMissing = false)
    public VideoEventPublisherPort videoEventPublisherPort(SqsClient sqsClient,
                                                            ObjectMapper objectMapper,
                                                            SqsMessageLogger sqsMessageLogger) {
        return new SqsVideoEventPublisherAdapter(sqsClient, objectMapper, sqsMessageLogger, videoUploadedQueueUrl);
    }

    @Bean
    @ConditionalOnProperty(name = "aws.sqs.enabled", havingValue = "false", matchIfMissing = true)
    public VideoEventPublisherPort noOpVideoEventPublisherPort() {
        return video -> {};
    }

    private void applyEndpoint(SqsClientBuilder builder, String endpoint) {
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                   .credentialsProvider(StaticCredentialsProvider.create(
                           AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
    }
}
