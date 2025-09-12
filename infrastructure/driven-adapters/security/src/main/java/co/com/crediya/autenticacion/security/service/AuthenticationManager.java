package co.com.crediya.autenticacion.security.service;

import co.com.crediya.autenticacion.model.role.RoleEnum;
import co.com.crediya.autenticacion.model.shared.enums.EnumUtils;
import co.com.crediya.autenticacion.model.shared.exception.AuthenticationUnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtAdapter jwtAdapter;

    /**
     * Validates the token and creates the authentication object.
     *
     * @param authentication The authentication object containing the token to validate.
     * @return A Mono containing the authentication object.
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return jwtAdapter.validateToken(token)
                .filter(isValid -> isValid)
                .switchIfEmpty(Mono.error(new AuthenticationUnauthorizedException("Invalid token")))
                .flatMap(isValid -> Mono.fromCallable(() -> jwtAdapter.getAllClaimsFromToken(token))
                        .flatMap(claims -> {
                            String username = claims.getSubject();
                            String roleString = claims.get("role", String.class);

                            if (roleString == null || roleString.trim().isEmpty()) {
                                return Mono.error(new AuthenticationUnauthorizedException("Token contains empty role"));
                            }

                            return Mono.fromCallable(() -> EnumUtils.fromString(RoleEnum.class, roleString))
                                    .onErrorMap(e -> new AuthenticationUnauthorizedException("Token contains invalid role", e))
                                    .flatMap(validRole -> {
                                        List<SimpleGrantedAuthority> authorities = List.of(
                                                new SimpleGrantedAuthority("ROLE_" + validRole.getValue())
                                        );

                                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                                username,
                                                token,
                                                authorities
                                        ));
                                    });
                        }));
    }
}
