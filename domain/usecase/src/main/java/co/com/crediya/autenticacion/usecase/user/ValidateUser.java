package co.com.crediya.autenticacion.usecase.user;

import reactor.core.publisher.Mono;

public class ValidateUser {
    public static Mono<String> validateUser(String email) {
        if (email == null || email.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Email is required"));
        }
        return Mono.just(email);
    }
}
