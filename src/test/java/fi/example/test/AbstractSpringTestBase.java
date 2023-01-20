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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @LocalServerPort
    protected int port;

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
    }

    protected <T, S> Map<T, S> convertExceptionBodyToMap(
        final HttpClientErrorException exception
    )
        throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(exception.getResponseBodyAsString(), new TypeReference<>() {
        });
    }

}
