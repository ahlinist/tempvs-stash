package club.tempvs.stash.exception;

/**
 * An exception to represent the 403 Http status.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
