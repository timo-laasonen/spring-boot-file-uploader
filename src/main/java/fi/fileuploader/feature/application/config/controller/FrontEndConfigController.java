package fi.fileuploader.feature.application.config.controller;

import fi.fileuploader.common.FileUploadRuntimeException;
import fi.fileuploader.feature.application.config.api.FrontEndConfigApi;
import fi.fileuploader.feature.application.config.domain.FrontEndConfigurationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class FrontEndConfigController implements FrontEndConfigApi {

    @Value("${file-uploader.keycloak.urls}")
    private List<String> keycloakUrls;

    private static final Pattern keycloakPathPattern = Pattern.compile(
        "^(.+)/realms/(.+)$");

    @Override
    public List<FrontEndConfigurationDTO> frontendConfigurations() throws FileUploadRuntimeException {

        final var result = new ArrayList<FrontEndConfigurationDTO>();
        for (String keycloakUrl : keycloakUrls) {
            final Matcher matcher = keycloakPathPattern.matcher(keycloakUrl);
            if (!matcher.find()) {
                throw new FileUploadRuntimeException("Defined keycloak URL doesn't match to pattern");
            }

            final var url = matcher.group(1);
            final var realm = matcher.group(2);

            result.add(FrontEndConfigurationDTO.builder()
                .keycloakUrl(url)
                .realm(realm)
                .clientId("frontend")
                .build()
            );
        }

        return result;
    }
}
