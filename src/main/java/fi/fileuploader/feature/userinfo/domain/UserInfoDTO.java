package fi.fileuploader.feature.userinfo.domain;

import fi.fileuploader.common.DTO;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@EqualsAndHashCode
@ToString
@SuperBuilder
@Jacksonized
public class UserInfoDTO implements DTO {

    String firstName;
    String lastName;
    String username;
    String registrationNumber;
    UUID id;
}
