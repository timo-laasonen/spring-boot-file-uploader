package fi.fileuploader.feature.file.validator;

import fi.fileuploader.common.AbstractFileUploadException;
import fi.fileuploader.common.ResponseErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<ValidSizeFile, MultipartFile> {

    private final DataSize maxFileUploadSize;

    @Autowired
    public FileSizeValidator(
        @Value("${file-uploader.properties.maxFileUploadSize}") final DataSize maxFileUploadSize
    ) {
        this.maxFileUploadSize = maxFileUploadSize;
    }

    @Override
    public void initialize(ValidSizeFile constraintAnnotation) {
    }

    @SneakyThrows
    @Override
    public boolean isValid(
        MultipartFile file,
        ConstraintValidatorContext constraintValidatorContext
    ) {
        if (file.isEmpty()) {
            throw new AbstractFileUploadException("file.fileEmpty") {
                @Override
                public ResponseErrorCode getResponseErrorCode() {
                    return ResponseErrorCode.BAD_REQUEST;
                }
            };
        }

        final var fileSize = DataSize.of(file.getSize(), DataUnit.BYTES);
        return maxFileUploadSize.compareTo(fileSize) >= 0;
    }
}
