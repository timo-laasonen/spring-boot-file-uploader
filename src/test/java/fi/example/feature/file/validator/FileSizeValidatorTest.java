package fi.example.feature.file.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;

class FileSizeValidatorTest {

    private FileSizeValidator validator;

    @BeforeEach
    public void beforeEach() {

        this.validator = new FileSizeValidator(
            DataSize.of(1, DataUnit.KILOBYTES)
        );

        this.validator.initialize(mock(ValidSizeFile.class));
    }

    @Test
    void shouldThrowExceptionWhenFileEmpty() {
        final var file = new MockMultipartFile(
            "data",
            "file.txt",
            "text/plain",
            new byte[0]
        );

        final Exception exception = catchThrowableOfType(
            () -> this.validator.isValid(
                file,
                mock(ConstraintValidatorContext.class)
            ),
            Exception.class
        );

        assertThat(exception.getMessage()).isEqualTo("file.fileEmpty");
    }

    @Test
    void shouldGiveErrorWhenFileTooBig() {
        // file is 2KB
        final byte[] bytes = new byte[1024 * 2];
        final var file = new MockMultipartFile(
            "data",
            "file.txt",
            "text/plain",
            bytes
        );

        final boolean result = this.validator.isValid(
            file,
            mock(ConstraintValidatorContext.class)
        );

        assertThat(result).isFalse();
    }

    @Test
    void shouldNotGiveErrorWhenFileIsUnderLimit() {
        this.validator = new FileSizeValidator(
            DataSize.of(1, DataUnit.MEGABYTES)
        );

        // file is 512KB
        final byte[] bytes = new byte[1024 * 512];
        final var file = new MockMultipartFile(
            "data",
            "file.txt",
            "text/plain",
            bytes
        );

        final boolean result = this.validator.isValid(
            file,
            mock(ConstraintValidatorContext.class)
        );

        assertThat(result).isTrue();
    }
}
