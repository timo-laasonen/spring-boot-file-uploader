package fi.fileuploader.config;

import fi.fileuploader.common.AbstractFileUploadException;
import fi.fileuploader.common.ApiError;
import fi.fileuploader.common.ResponseErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;

import static fi.fileuploader.common.ResponseErrorCode.*;

/**
 * Exception handler for REST endpoints
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String RESPONSE_ERROR_HEADER_NAME = "x-response-code";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(
        final EntityNotFoundException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(DATA_NOT_FOUND);
        return this.handleExceptionInternal(
            ex,
            new ApiError(DATA_NOT_FOUND, ex.getMessage()),
            headers,
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
        final AccessDeniedException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(ACCESS_DENIED);
        return this.handleExceptionInternal(
            ex,
            new ApiError(ACCESS_DENIED, ex.getMessage()),
            headers,
            HttpStatus.FORBIDDEN,
            request
        );
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(
        final MaxUploadSizeExceededException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(BAD_REQUEST);
        return this.handleExceptionInternal(
            ex,
            new ApiError(BAD_REQUEST, ex.getMessage()),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(
        final IllegalArgumentException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(BAD_REQUEST);
        return this.handleExceptionInternal(
            ex,
            new ApiError(BAD_REQUEST, ex.getMessage()),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<Object> handleValidationException(
        final ValidationException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(BAD_REQUEST);
        return this.handleExceptionInternal(
            ex,
            new ApiError(BAD_REQUEST, ex.getMessage()),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleDataIntegrityViolationException(
        final DataIntegrityViolationException ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(DATA_INTEGRITY_VIOLATION);
        return this.handleExceptionInternal(
            ex,
            new ApiError(DATA_INTEGRITY_VIOLATION, ""),
            headers,
            DATA_INTEGRITY_VIOLATION.getStatusCode(),
            request
        );
    }

    @ExceptionHandler(value = {HttpClientErrorException.Unauthorized.class})
    public ResponseEntity<Object> handleRestUnauthorizedException(
        final HttpClientErrorException.Unauthorized ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(UNAUTHORIZED);
        return this.handleExceptionInternal(
            ex,
            new ApiError(UNAUTHORIZED, ex.getMessage()),
            headers,
            HttpStatus.UNAUTHORIZED,
            request
        );
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(
        final ConstraintViolationException ex,
        final WebRequest request
    ) {
        final List<String> errors = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage).toList();

        final HttpHeaders headers = this.getHeaders(BAD_REQUEST);
        return this.handleExceptionInternal(
            ex,
            new ApiError(BAD_REQUEST, "Validation errors occurred", errors),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAllExceptions(
        final Exception ex,
        final WebRequest request
    ) {
        final HttpHeaders headers = this.getHeaders(UNKNOWN_ERROR);
        return this.handleExceptionInternal(
            ex,
            new ApiError(INTERNAL_ERROR, ""),
            headers,
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        final List<String> errors = ex.getAllErrors().stream()
            .map(ObjectError::getDefaultMessage)
            .filter(Objects::nonNull)
            .toList();

        return this.handleExceptionInternal(
            ex,
            new ApiError(BAD_REQUEST, "Validation errors occurred", errors),
            this.getHeaders(BAD_REQUEST),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @NonNull
    protected ResponseEntity<Object> handleExceptionInternal(
        @NonNull final Exception ex,
        final Object body,
        final HttpHeaders headers,
        final HttpStatus status,
        @NonNull final WebRequest request
    ) {

        if (status.is5xxServerError()) {
            LOG.error("Handling {} due to {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        } else {
            LOG.info("Handling {} due to {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private HttpHeaders getHeaders(final ResponseErrorCode responseErrorCode) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(RESPONSE_ERROR_HEADER_NAME, responseErrorCode.name());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
