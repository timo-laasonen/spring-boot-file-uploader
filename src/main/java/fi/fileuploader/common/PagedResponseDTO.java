package fi.fileuploader.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@EqualsAndHashCode
@ToString
@SuperBuilder
@Jacksonized
public class PagedResponseDTO<T extends DTO> implements DTO {

    int page;
    int size;
    Long totalCount;
    List<T> content;

}
