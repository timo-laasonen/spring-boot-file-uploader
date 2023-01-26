package fi.fileuploader.common;


import java.util.List;

public class ApiError {
    private ResponseErrorCode status;
    private String message;

    private List<String> errors;

    public ApiError(ResponseErrorCode status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(final ResponseErrorCode status, final String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public ResponseErrorCode getStatus() {
        return this.status;
    }

    public void setStatus(final ResponseErrorCode status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
