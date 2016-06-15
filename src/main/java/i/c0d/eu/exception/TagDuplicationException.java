package i.c0d.eu.exception;

/**
 * Created by antonio on 15/06/2016.
 */
public class TagDuplicationException extends RuntimeException {
    public TagDuplicationException() {
        super();
    }

    public TagDuplicationException(String message) {
        super(message);
    }

    public TagDuplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TagDuplicationException(Throwable cause) {
        super(cause);
    }

    protected TagDuplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
