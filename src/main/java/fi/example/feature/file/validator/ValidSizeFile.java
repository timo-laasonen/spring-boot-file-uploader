package fi.example.feature.file.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileSizeValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSizeFile {

    String message() default
        "file.allowedFileSizeExceeded";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
