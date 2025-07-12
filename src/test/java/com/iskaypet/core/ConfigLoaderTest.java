package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigLoaderTest {

    private String originalEnv;

    @BeforeEach
    void setup() {
        originalEnv = System.getenv("ENV");
    }

    @AfterEach
    void cleanup() {
        // No hay forma estándar de restaurar ENV en Java puro, pero los tests no la modifican realmente
    }

    @Test
    void load_returns_properties_for_local() {
        Properties props = ConfigLoader.load();
        assertThat(props).isNotNull();
        assertThat(props.stringPropertyNames()).isNotEmpty();
    }

    @Test
    void load_throws_if_file_missing() {
        // Simular ENV no existente
        String oldEnv = System.getenv("ENV");
        try {
            System.setProperty("ENV", "notfound");
            assertThatThrownBy(ConfigLoader::load).isInstanceOf(RuntimeException.class);
        } finally {
            if (oldEnv != null) System.setProperty("ENV", oldEnv);
        }
    }
} 