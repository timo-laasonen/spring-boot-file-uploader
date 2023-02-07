package fi.fileuploader;

import fi.fileuploader.test.AbstractSpringTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FileUploaderApplicationIT extends AbstractSpringTestBase {

    @Test
    void shouldLoadTestContext() {
    }

    @Test
    void shouldBeHealthy() {
        final var headers = new HttpHeaders();

        final ResponseEntity<String> response = this.testRestTemplate.exchange(
            this.getBaseUrl() + "/actuator/health",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {
            }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
