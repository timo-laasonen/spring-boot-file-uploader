package fi.example.feature.file.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.example.persistence.userinfo.UserInfo;
import fi.example.persistence.userinfo.UserInfoRepository;
import fi.example.test.AbstractSpringTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class FileControllerIT {

    private static final String API_URL = "/api/files";

    @Nested
    class ImportingFromCSV extends AbstractSpringTestBase {

        @Autowired
        private UserInfoRepository userInfoRepository;

        @Test
        void shouldGiveExceptionIfFileEmpty() throws JsonProcessingException {
            final HttpClientErrorException exception = catchThrowableOfType(
                () -> this.importCsvString(null),
                HttpClientErrorException.class
            );
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            final Map<String, List<String>> jsonMap = this.convertExceptionBodyToMap(exception);

            final var errors = jsonMap.get("errors");
            assertThat(errors).isInstanceOfAny(ArrayList.class);
            assertThat(errors).containsExactlyInAnyOrder(
                "file.fileEmpty"
            );

        }

        @Test
        void shouldGiveValidationErrorIfFileIsTooBig() throws JsonProcessingException {
            StringBuilder fileContent = new StringBuilder(
                "Given name;Family name;Registration number;"
                    + System.getProperty("line.separator")
            );

            for (int i = 0; i < 1000; i++) {
                fileContent.append("Test;User;")
                    .append(System.getProperty("line.separator"));
            }

            final HttpClientErrorException exception = catchThrowableOfType(
                () -> this.importCsvString(fileContent.toString()),
                HttpClientErrorException.class
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

            final Map<String, List<String>> jsonMap = this.convertExceptionBodyToMap(exception);

            final var errors = jsonMap.get("errors");
            assertThat(errors).isInstanceOfAny(ArrayList.class);
            assertThat(errors).containsExactlyInAnyOrder(
                "file.allowedFileSizeExceeded"
            );

        }

        @Test
        @Sql(scripts = {"/test-data/userInfos/userInfos.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        @Sql(
            statements = {
                """
                DELETE FROM user_info;
                """
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        void shouldAddOnlyUsersWhichAreNotFound() throws URISyntaxException {
            final var dataBefore = this.userInfoRepository.findAll();

            final String fileContent = "Given name;Family name;Registration number;"
                + System.getProperty("line.separator")
                + "Teppo;Testi;123456;"
                + System.getProperty("line.separator")
                + "Test;User;44444444;";

            final var response = this.importCsvString(fileContent);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            final var addedUser = this.userInfoRepository.findAll().stream()
                .filter(userInfo -> !dataBefore.stream()
                    .map(UserInfo::getId)
                    .toList()
                    .contains(userInfo.getId()))
                .findFirst();

            assertThat(addedUser).isPresent();
            assertThat(addedUser.get().getFirstName()).isEqualTo("Test");
            assertThat(addedUser.get().getLastName()).isEqualTo("User");
            assertThat(addedUser.get().getRegistrationNumber())
                .isEqualTo("44444444");

        }

        @Test
        @Sql(scripts = {"/test-data/userInfos/userInfos.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        @Sql(
            statements = {
                """
                DELETE FROM user_info;
                """
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        void shouldUpdateUsersWhichAreNotFound() throws URISyntaxException {
            final var dataBefore = this.userInfoRepository.findAll();

            final String fileContent = "Given name;Family name;Registration number;"
                + System.getProperty("line.separator")
                + "Teppo;Testi;123456;"
                + System.getProperty("line.separator")
                + "Test;User;44444444;";

            final var response = this.importCsvString(fileContent);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            final var addedUser = this.userInfoRepository.findAll().stream()
                .filter(userInfo -> !dataBefore.stream()
                    .map(UserInfo::getId)
                    .toList()
                    .contains(userInfo.getId()))
                .findFirst();

            assertThat(addedUser).isPresent();
            assertThat(addedUser.get().getFirstName()).isEqualTo("Test");
            assertThat(addedUser.get().getLastName()).isEqualTo("User");
            assertThat(addedUser.get().getRegistrationNumber())
                .isEqualTo("44444444");

        }

        private ResponseEntity<?> importCsvString(
            final String fileContent
        ) throws RestClientException, URISyntaxException {

            final var headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.retrieveAccessToken());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            final String fileName = "test_csv.csv";

            final HttpHeaders parts = new HttpHeaders();
            parts.setContentType(MediaType.TEXT_PLAIN);
            final ByteArrayResource byteArrayResource = new ByteArrayResource(
                Optional.ofNullable(fileContent).map(String::getBytes)
                    .orElseGet(() -> new byte[0])
            ) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            final HttpEntity<ByteArrayResource> partsEntity = new HttpEntity<>(
                byteArrayResource,
                parts
            );

            final MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("file", partsEntity);

            return this.testRestTemplate.exchange(
                this.getBaseUrl() + API_URL + "/upload-users",
                HttpMethod.POST,
                new HttpEntity<>(requestMap, headers),
                new ParameterizedTypeReference<>() {
                }
            );
        }
    }

}
