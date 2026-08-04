package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.infrastructure.adapters.storage.LocalFileStorageAdapter;
import br.com.fiap.infrastructure.adapters.storage.S3StorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class VideoStorageBeanConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public VideoStoragePort localFileStoragePort(
            @Value("${app.storage.local.path:./uploads}") String basePath) {
        return new LocalFileStorageAdapter(basePath);
    }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public VideoStoragePort s3StoragePort(S3Client s3Client,
                                           @Value("${app.storage.s3.bucket:fiap-video-uploads}") String bucket) {
        return new S3StorageAdapter(s3Client, bucket);
    }
}
