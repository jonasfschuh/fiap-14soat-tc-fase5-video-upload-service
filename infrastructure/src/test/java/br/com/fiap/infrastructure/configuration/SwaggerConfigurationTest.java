package br.com.fiap.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("SwaggerConfiguration - Unit Tests")
class SwaggerConfigurationTest {

    private SwaggerConfiguration config;

    @BeforeEach
    void setUp() {
        config = new SwaggerConfiguration();
    }

    @Test
    @DisplayName("customOpenAPI without authServiceUrl returns local servers")
    void customOpenAPI_withoutAuthUrl_returnsLocalServers() {
        // authServiceUrl is empty string by default
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).contains("Video Upload");
        assertThat(openApi.getServers()).isNotEmpty();
    }

    @Test
    @DisplayName("customOpenAPI with authServiceUrl still uses swaggerServerUrl (not authServiceUrl)")
    void customOpenAPI_withAuthUrl_returnsAwsServer() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");
        ReflectionTestUtils.setField(config, "swaggerServerUrl", "https://api.example.com/video-upload");

        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getServers()).hasSize(1);
        assertThat(openApi.getServers().get(0).getUrl()).contains("api.example.com");
    }

    @Test
    @DisplayName("customOpenAPI has security scheme configured")
    void customOpenAPI_hasSecurityScheme() {
        OpenAPI openApi = config.customOpenAPI();

        assertThat(openApi.getComponents()).isNotNull();
        assertThat(openApi.getComponents().getSecuritySchemes()).containsKey("bearer-jwt");
    }

    @Test
    @DisplayName("authLoginServerOverride customizer does not throw when authServiceUrl is empty")
    void authLoginServerOverride_withoutUrl_doesNotThrow() {
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = config.customOpenAPI();
        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("authLoginServerOverride customizer handles null paths gracefully")
    void authLoginServerOverride_withNullPaths_doesNotThrow() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = new OpenAPI();
        openApi.setPaths(null);
        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("authLoginServerOverride customizer overrides auth login path server when URL is set")
    void authLoginServerOverride_withUrl_overridesLoginServer() {
        ReflectionTestUtils.setField(config, "authServiceUrl", "https://auth.example.com");
        var customizer = config.authLoginServerOverride();

        OpenAPI openApi = config.customOpenAPI();
        // Add the /auth/login path
        io.swagger.v3.oas.models.Paths paths = new io.swagger.v3.oas.models.Paths();
        paths.addPathItem("/auth/login", new io.swagger.v3.oas.models.PathItem());
        openApi.setPaths(paths);

        assertThatCode(() -> customizer.customise(openApi)).doesNotThrowAnyException();
    }
}
