package fi.fileuploader.feature.auditlog.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.io.Serializable;

@Value
@EqualsAndHashCode
@ToString
@Builder
public class AuditLogFieldData implements Serializable {

    @NotNull
    String fieldName;
    String previousValue;
    String currentValue;

}
