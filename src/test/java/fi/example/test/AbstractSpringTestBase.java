package fi.example.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.example.test.container.TestDBContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @LocalServerPort
    protected int port;

    // TODO: Enable below if using Keycloak test container
    // Keycloak is run in Docker and tests uses real user from there defined in realm-export.json
    /*
    private static final KeycloakContainer keycloak;
    static {
        keycloak = new KeycloakContainer().withRealmImportFile("keycloak/realm-export.json");
        keycloak.start();
    }
    */

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

        // TODO: Enable below if using Keycloak test container
        /* registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "realms/fileupload-api");
         */
    }

    protected <T, S> Map<T, S> convertExceptionBodyToMap(
        final HttpClientErrorException exception
    )
        throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(exception.getResponseBodyAsString(), new TypeReference<>() {
        });
    }

    protected Jwt mockJwt(final JwtDecoder jwtDecoder,
                        final List<String> roles,
                        final String subject) {
        final Jwt jwt = Jwt
            .withTokenValue("accessToken")
            .header("alg", "none")
            .claim("scope", "openId")
            .claim("roles", roles)
            .subject(subject)
            .build();

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        return jwt;
    }

    // TODO: Enable below if using Keycloak test container
    // Keycloak is run in Docker and tests uses real user from there
    /*
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
    */
}
