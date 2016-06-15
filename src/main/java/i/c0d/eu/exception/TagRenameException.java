package i.c0d.eu.exception;

/**
 * Created by antonio on 15/06/2016.
 */
public class TagRenameException extends RuntimeException {
    public TagRenameException() {
    }

    public TagRenameException(String message) {
        super(message);
    }

    public TagRenameException(String message, Throwable cause) {
        super(message, cause);
    }

    public TagRenameException(Throwable cause) {
        super(cause);
    }

    public TagRenameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
