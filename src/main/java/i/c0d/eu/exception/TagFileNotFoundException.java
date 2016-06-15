package i.c0d.eu.exception;

/**
 * Created by antonio on 15/06/2016.
 */
public class TagFileNotFoundException extends RuntimeException {
    public TagFileNotFoundException() {
        super();
    }

    public TagFileNotFoundException(String message) {
        super(message);
    }

    public TagFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TagFileNotFoundException(Throwable cause) {
        super(cause);
    }

    protected TagFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
