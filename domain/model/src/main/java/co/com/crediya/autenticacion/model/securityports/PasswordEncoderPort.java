package co.com.crediya.autenticacion.model.securityports;

import reactor.core.publisher.Mono;

public interface PasswordEncoderPort {
    Mono<String> encode(String rawPassword);
    Mono<Boolean> matches(String rawPassword, String encodedPassword);
}
