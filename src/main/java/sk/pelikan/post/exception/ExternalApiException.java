package sk.pelikan.post.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExternalApiException(String message) {
        super(message);
    }
}
