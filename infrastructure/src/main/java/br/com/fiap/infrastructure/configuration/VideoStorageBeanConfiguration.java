package br.com.fiap.infrastructure.configuration;

import br.com.fiap.domain.ports.out.VideoStoragePort;
import br.com.fiap.infrastructure.adapters.storage.LocalFileStorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoStorageBeanConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public VideoStoragePort localFileStoragePort(
            @Value("${app.storage.local.path:./uploads}") String basePath) {
        return new LocalFileStorageAdapter(basePath);
    }
}
