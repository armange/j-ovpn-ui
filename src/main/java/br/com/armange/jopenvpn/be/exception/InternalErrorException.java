package br.com.armange.jopenvpn.be.exception;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException(final String message) {
        super(message);
    }

    public InternalErrorException(final Exception cause) {
        super(cause);
    }

    public InternalErrorException(final String message, final Exception cause) {
        super(message, cause);
    }
}
