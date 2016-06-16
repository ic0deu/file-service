package i.c0d.eu.exception;

/**
 * Created by antonio on 15/06/2016.
 */
public class ExtensionReplacementException extends RuntimeException {
    public ExtensionReplacementException() {
    }

    public ExtensionReplacementException(String message) {
        super(message);
    }

    public ExtensionReplacementException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionReplacementException(Throwable cause) {
        super(cause);
    }

    public ExtensionReplacementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
