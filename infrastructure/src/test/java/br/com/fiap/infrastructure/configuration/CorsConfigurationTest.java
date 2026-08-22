package br.com.fiap.infrastructure.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("CorsConfiguration - Unit Tests")
class CorsConfigurationTest {

    private CorsConfiguration buildConfig() {
        CorsConfiguration config = new CorsConfiguration();
        ReflectionTestUtils.setField(config, "allowedOrigins", List.of("*"));
        return config;
    }

    @Test
    @DisplayName("corsConfigurer returns a non-null WebMvcConfigurer")
    void corsConfigurer_returnsNonNull() {
        WebMvcConfigurer configurer = buildConfig().corsConfigurer();

        assertThat(configurer).isNotNull();
    }

    @Test
    @DisplayName("corsConfigurer addCorsMappings does not throw")
    void corsConfigurer_addCorsMappings_doesNotThrow() {
        WebMvcConfigurer configurer = buildConfig().corsConfigurer();

        assertThatCode(() -> configurer.addCorsMappings(new CorsRegistry()))
                .doesNotThrowAnyException();
    }
}
