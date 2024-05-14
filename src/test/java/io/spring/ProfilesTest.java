package io.spring;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ProfilesTest {
    @Nested
    @ActiveProfiles(value = "development")
    public class ActiveDevProfileTest {

        @Value("${server.port}")
        private Integer port;

        @Test
        void testProperties() {
            assertThat(port).isEqualTo(7070);
        }
    }

    @Nested
    @ActiveProfiles(value = "stage")
    public class ActiveStageProfileTest {

        @Value("${server.port}")
        private Integer port;

        @Test
        void testProperties() {
            assertThat(port).isEqualTo(4040);
        }
    }

    @Nested
    @ActiveProfiles(value = "production")
    public class ActiveProductionProfileTest {

        @Value("${server.port}")
        private Integer port;

        @Test
        void testProperties() {
            assertThat(port).isEqualTo(8080);
        }
    }
}
