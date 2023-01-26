package fi.fileuploader.test;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
@EnableConfigurationProperties(TestingProperties.class)
public class TestConfig {

    // TODO: disable this if using Keycloak test container
    @MockBean
    protected JwtDecoder jwtDecoder;
}
