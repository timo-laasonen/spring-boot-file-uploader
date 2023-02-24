package fi.fileuploader.feature.auditlog.domain;

import fi.fileuploader.common.DTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Value
@EqualsAndHashCode
@ToString
@Builder
public class AuditLogData implements Serializable {

    @NotNull
    String entityName;
    @NotNull
    String id;
    @NotNull
    String auditEvent;
    @Singular
    List<AuditLogFieldData> properties;
}
