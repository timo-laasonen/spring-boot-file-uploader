package fi.example.common;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;

/**
 * Abstract exception class to include response error code with
 **/
public abstract class AbstractFileUploadException extends FileUploadRuntimeException {
    @Serial
    private static final long serialVersionUID = 1911735748988856486L;

    public AbstractFileUploadException() {
    }

    public AbstractFileUploadException(final String message) {
        super(message);
    }

    public AbstractFileUploadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AbstractFileUploadException(final Throwable cause) {
        super(cause);
    }

    public AbstractFileUploadException(
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @NotNull
    public abstract ResponseErrorCode getResponseErrorCode();
}
