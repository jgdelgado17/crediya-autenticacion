package co.com.crediya.autenticacion.model.shared.exception;

public class AuthenticationUnauthorizedException extends RuntimeException {
    public AuthenticationUnauthorizedException(String message) {
        super(message);
    }

    public AuthenticationUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
