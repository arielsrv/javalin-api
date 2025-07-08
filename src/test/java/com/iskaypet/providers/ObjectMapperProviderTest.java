package com.iskaypet.providers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ObjectMapperProviderTest {

    @Test
    void provides_configured_object_mapper() {
        ObjectMapperProvider provider = new ObjectMapperProvider();
        ObjectMapper om = provider.get();
        assertThat(om).isNotNull();
        assertThat(om.getSerializationConfig().getPropertyNamingStrategy()).isNotNull();
    }

    @Test
    void serializes_snake_case() throws Exception {
        ObjectMapper om = new ObjectMapperProvider().get();
        class User {

            public final String userId = "x";
        }
        String json = om.writeValueAsString(new User());
        assertThat(json).contains("user_id");
    }
}
