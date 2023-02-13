package fi.fileuploader.feature.file.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.fileuploader.persistence.userinfo.UserInfo;
import fi.fileuploader.persistence.userinfo.UserInfoRepository;
import fi.fileuploader.test.AbstractSpringTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

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
        @Autowired
        private JwtDecoder jwtDecoder;

        private Jwt jwt;

        @BeforeEach
        public void beforeEach() {
            jwt = this.mockJwt(
                this.jwtDecoder,
                List.of("admin"),
                "aca777d6-ce43-4ef8-bd44-638e0d8bbdf8"
            );
        }

        @Test
        void shouldGiveExceptionIfFileEmpty() throws JsonProcessingException {
            final HttpClientErrorException exception = catchThrowableOfType(
                () -> this.importCsvString(null, jwt),
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
        void shouldGiveAccessDeniedErrorIfNotAdminUser() throws JsonProcessingException {
            jwt = this.mockJwt(
                this.jwtDecoder,
                List.of("basicuser"),
                "aca777d6-ce43-4ef8-bd44-638e0d8bbdf8"
            );

            final String fileContent = "Given name;Family name;Registration number;Email;"
                + System.getProperty("line.separator")
                + "Teppo;Testi;123456;teppo@test.fi;"
                + System.getProperty("line.separator")
                + "Test;User;44444444;test@test.fi;";

            final HttpClientErrorException exception = catchThrowableOfType(
                () -> this.importCsvString(fileContent, jwt),
                HttpClientErrorException.class
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        void shouldGiveValidationErrorIfFileIsTooBig() throws JsonProcessingException {
            StringBuilder fileContent = new StringBuilder(
                "Given name;Family name;Registration number;Email;"
                    + System.getProperty("line.separator")
            );

            for (int i = 0; i < 1000; i++) {
                fileContent.append("Test;User;")
                    .append(i)
                    .append(";test@test")
                    .append(i)
                    .append(".fi;")
                    .append(System.getProperty("line.separator"));
            }

            final HttpClientErrorException exception = catchThrowableOfType(
                () -> this.importCsvString(fileContent.toString(), jwt),
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
        void shouldAddOnlyUsersWhichAreNotFound() {
            final var dataBefore = this.userInfoRepository.findAll();

            final String fileContent = "Given name;Family name;Registration number;Email;"
                + System.getProperty("line.separator")
                + "Teppo;Testi;123456;teppo@test.fi;"
                + System.getProperty("line.separator")
                + "Test;User;44444444;test@test.fi;";

            final var response = this.importCsvString(fileContent, jwt);
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
        void shouldUpdateUsersWhichAreNotFound() {
            final var dataBefore = this.userInfoRepository.findAll();

            final String fileContent = "Given name;Family name;Registration number;Email;"
                + System.getProperty("line.separator")
                + "Teppo;Testi;123456;teppo@test.fi;"
                + System.getProperty("line.separator")
                + "Test;User;44444444;test@test.fi;";

            final var response = this.importCsvString(fileContent, jwt);
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
            final String fileContent,
            final Jwt jwt
        ) throws RestClientException {

            final var headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwt.getTokenValue());
            // TODO: Use below if using Keycloak test container
            // headers.set("Authorization", "Bearer " + this.retrieveAccessToken());
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
