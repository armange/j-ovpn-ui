package br.com.armange.jopenvpn.be.exception;

public class UncheckedDBusException extends RuntimeException {
    public UncheckedDBusException(final String message, final Throwable e) {
        super(message, e);
    }
}
