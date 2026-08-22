package br.com.fiap.infrastructure.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JpaConfiguration - Unit Tests")
class JpaConfigurationTest {

    @Test
    @DisplayName("JpaConfiguration can be instantiated")
    void jpaConfiguration_canBeInstantiated() {
        JpaConfiguration config = new JpaConfiguration();

        assertThat(config).isNotNull();
    }
}
