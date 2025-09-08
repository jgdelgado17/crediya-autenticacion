package co.com.crediya.autenticacion.model.securityports;

import co.com.crediya.autenticacion.model.user.User;
import reactor.core.publisher.Mono;

public interface LoginPort {
    Mono<User> validateCredentials(String email, String password);
}
