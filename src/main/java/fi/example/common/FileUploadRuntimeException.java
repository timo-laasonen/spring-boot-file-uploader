package fi.example.common;

import java.io.Serial;

/**
 *
 */
public class FileUploadRuntimeException extends Exception {

    @Serial
    private static final long serialVersionUID = -7584120851447607247L;

    public FileUploadRuntimeException() {
    }

    public FileUploadRuntimeException(final String message) {
        super(message);
    }

    public FileUploadRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FileUploadRuntimeException(final Throwable cause) {
        super(cause);
    }

    public FileUploadRuntimeException(
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
