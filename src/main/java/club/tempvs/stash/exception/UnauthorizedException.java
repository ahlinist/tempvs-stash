package club.tempvs.stash.exception;

/**
 * An exception to represent the 401 Http status.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
