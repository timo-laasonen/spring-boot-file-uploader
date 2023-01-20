package fi.example.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import fi.example.test.container.TestDBContainer;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlConfig(
    separator = ScriptUtils.EOF_STATEMENT_SEPARATOR
)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "testing")
@Import(TestConfig.class)
@Testcontainers
public abstract class AbstractSpringTestBase {

    @Container
    public static final TestDBContainer DB_CONTAINER = TestDBContainer.getInstance();

    protected RestTemplate testRestTemplate;

    private static final KeycloakContainer keycloak;

    @LocalServerPort
    protected int port;

    static {
        keycloak = new KeycloakContainer().withRealmImportFile("keycloak/realm-export.json");
        keycloak.start();
    }

    protected AbstractSpringTestBase() {
        this.testRestTemplate = new RestTemplate();
    }

    protected String getBaseUrl() {
        return "http://localhost:" + this.port;
    }

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "file-uploader-application-test");
        registry.add("spring.datasource.password", DB_CONTAINER::getPassword);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "realms/fileupload-api");
    }

    protected <T, S> Map<T, S> convertExceptionBodyToMap(
        final HttpClientErrorException exception
    )
        throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(exception.getResponseBodyAsString(), new TypeReference<>() {
        });
    }

    protected String retrieveAccessToken() throws URISyntaxException {
        final URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl()
            + "realms/fileupload-api/protocol/openid-connect/token")
            .build();

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("webapp"));
        formData.put("username", Collections.singletonList("fileupload-test-user"));
        formData.put("password", Collections.singletonList("secret123"));

        final ResponseEntity<String> response = this.testRestTemplate.exchange(
            authorizationURI,
            HttpMethod.POST,
            new HttpEntity<>(formData, headers),
            new ParameterizedTypeReference<>() {
            }
        );

        final var jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(response.getBody())
            .get("access_token")
            .toString();
    }
}
