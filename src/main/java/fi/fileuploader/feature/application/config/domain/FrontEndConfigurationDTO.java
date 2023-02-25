package fi.fileuploader.feature.application.config.domain;

import fi.fileuploader.common.DTO;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Value
@EqualsAndHashCode
@ToString
@SuperBuilder
@Jacksonized
public class FrontEndConfigurationDTO implements DTO {

    @NotNull
    String keycloakUrl;
    @NotNull
    String realm;
    @NotNull
    String clientId;
}
