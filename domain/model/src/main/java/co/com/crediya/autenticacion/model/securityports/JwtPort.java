package co.com.crediya.autenticacion.model.securityports;

import reactor.core.publisher.Mono;

public interface JwtPort {
    Mono<TokenInfo> generateToken(String email, String role);
    Mono<Boolean> validateToken(String token);
    Mono<String> getUsernameFromToken(String token);
}
