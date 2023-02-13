package fi.fileuploader.feature.userinfo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fi.fileuploader.common.PagedResponseDTO;
import fi.fileuploader.feature.userinfo.domain.UserInfoDTO;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class UserControllerIT {

    private static final String API_URL = "/api/user-info";

    @Nested
    class FindingUsers extends AbstractSpringTestBase {

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
        @Sql(
            scripts = {
                "/test-data/userInfos/severalUserInfos.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        @Sql(statements = {"DELETE FROM user_info;"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        void shouldFindAllUsersInOnePage() {

            final List<UserInfo> users = this.userInfoRepository.findAll();
            assertThat(users).hasSizeGreaterThanOrEqualTo(7);

            final var headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.jwt.getTokenValue());

            final var requestParam = "?page=0&size=10";

            final ResponseEntity<PagedResponseDTO<UserInfoDTO>> response =
                this.testRestTemplate.exchange(
                    this.getBaseUrl() + API_URL + "/users" + requestParam,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
                );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            final PagedResponseDTO<UserInfoDTO> page = response.getBody();
            assertThat(page).isNotNull();
            assertThat(page.getTotalCount()).isGreaterThanOrEqualTo(7);
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.getPage()).isEqualTo(0);
            assertThat(page.getContent()).hasSize(7);
        }

        @Test
        @Sql(
            scripts = {
                "/test-data/userInfos/severalUserInfos.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        @Sql(statements = {"DELETE FROM user_info;"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        void shouldFindAccessLogsFirstPage() {

            final List<UserInfo> users = this.userInfoRepository.findAll();
            assertThat(users).hasSizeGreaterThanOrEqualTo(7);

            final var headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.jwt.getTokenValue());

            final var requestParam = "?page=0&size=5";

            final ResponseEntity<PagedResponseDTO<UserInfoDTO>> response =
                this.testRestTemplate.exchange(
                    this.getBaseUrl() + API_URL + "/users" + requestParam,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
                );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            final PagedResponseDTO<UserInfoDTO> page = response.getBody();
            assertThat(page).isNotNull();
            assertThat(page.getTotalCount()).isGreaterThanOrEqualTo(7);
            assertThat(page.getSize()).isEqualTo(5);
            assertThat(page.getPage()).isEqualTo(0);
            assertThat(page.getContent()).hasSize(5);

            assertThat(page.getContent()).extracting(UserInfoDTO::getName)
                .containsExactlyInAnyOrder(
                    "Teppo Testi",
                    "Test User1",
                    "Test User2",
                    "Test User3",
                    "Test User4"
                );
        }

        @Test
        @Sql(
            scripts = {
                "/test-data/userInfos/severalUserInfos.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        @Sql(statements = {"DELETE FROM user_info;"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            config = @SqlConfig(separator = ScriptUtils.EOF_STATEMENT_SEPARATOR))
        void shouldFindAccessLogsLastPage() {

            final List<UserInfo> users = this.userInfoRepository.findAll();
            assertThat(users).hasSizeGreaterThanOrEqualTo(7);

            final var headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + this.jwt.getTokenValue());

            final var requestParam = "?page=1&size=5";

            final ResponseEntity<PagedResponseDTO<UserInfoDTO>> response =
                this.testRestTemplate.exchange(
                    this.getBaseUrl() + API_URL + "/users" + requestParam,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
                );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            final PagedResponseDTO<UserInfoDTO> page = response.getBody();
            assertThat(page).isNotNull();
            assertThat(page.getTotalCount()).isGreaterThanOrEqualTo(7);
            assertThat(page.getSize()).isEqualTo(5);
            assertThat(page.getPage()).isEqualTo(1);
            assertThat(page.getContent()).hasSize(2);

            assertThat(page.getContent()).extracting(UserInfoDTO::getName)
                .containsExactlyInAnyOrder(
                    "Test User5",
                    "Test User6"
                );
        }
    }

}
