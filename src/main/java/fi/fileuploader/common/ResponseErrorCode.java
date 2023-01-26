package fi.fileuploader.common;

import org.springframework.http.HttpStatus;

/**
 *
 */
public enum ResponseErrorCode {

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpStatus;

    ResponseErrorCode(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatusCode() {
        return this.httpStatus;
    }
}
