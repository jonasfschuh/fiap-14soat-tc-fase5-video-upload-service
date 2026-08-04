package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.model.Video;
import br.com.fiap.domain.ports.out.VideoEventPublisherPort;
import br.com.fiap.infrastructure.adapters.messaging.SqsVideoEventPublisherAdapter;
import br.com.fiap.infrastructure.logging.SqsMessageLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("AwsSqsConfiguration - Unit Tests")
class AwsSqsConfigurationTest {

    private AwsSqsConfiguration config;

    @BeforeEach
    void setUp() throws Exception {
        config = new AwsSqsConfiguration();
        setField("region", "us-east-1");
        setField("accessKeyId", "test");
        setField("secretAccessKey", "test");
        setField("videoUploadedQueueUrl", "http://localhost:4566/000000000000/video-uploaded");
    }

    @Test
    @DisplayName("noOpVideoEventPublisherPort returns a bean that does nothing")
    void noOpVideoEventPublisherPort_doesNothing() {
        VideoEventPublisherPort noOp = config.noOpVideoEventPublisherPort();

        assertThat(noOp).isNotNull();
        Video video = Video.create("u", "f.mp4", 1L, "video/mp4");
        assertThatCode(() -> noOp.publishVideoUploaded(video)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("sqsClient with endpoint override builds using static credentials")
    void sqsClient_withEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", "http://localhost:4566");

        SqsClient client = config.sqsClient();
        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    @DisplayName("sqsClient without endpoint override builds using default credentials")
    void sqsClient_withoutEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", null);

        SqsClient client = config.sqsClient();
        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    @DisplayName("videoEventPublisherPort returns SqsVideoEventPublisherAdapter")
    void videoEventPublisherPort_returnsSqsAdapter() throws Exception {
        setField("endpointOverride", "http://localhost:4566");
        SqsClient sqsClient = config.sqsClient();
        ObjectMapper mapper = new ObjectMapper();
        SqsMessageLogger logger = new SqsMessageLogger(mapper);

        VideoEventPublisherPort port = config.videoEventPublisherPort(sqsClient, mapper, logger);

        assertThat(port).isNotNull().isInstanceOf(SqsVideoEventPublisherAdapter.class);
        sqsClient.close();
    }

    @Test
    @DisplayName("s3Client with endpoint override uses static credentials")
    void s3Client_withEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", "http://localhost:4566");

        S3Client s3Client = config.s3Client();
        assertThat(s3Client).isNotNull();
        s3Client.close();
    }

    @Test
    @DisplayName("s3Client without endpoint override uses default credentials")
    void s3Client_withoutEndpointOverride_buildsSuccessfully() throws Exception {
        setField("endpointOverride", null);

        S3Client s3Client = config.s3Client();
        assertThat(s3Client).isNotNull();
        s3Client.close();
    }

    private void setField(String name, Object value) throws Exception {
        Field f = AwsSqsConfiguration.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(config, value);
    }
}
