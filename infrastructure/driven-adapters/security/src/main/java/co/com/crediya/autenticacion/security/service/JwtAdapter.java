package co.com.crediya.autenticacion.security.service;

import co.com.crediya.autenticacion.model.securityports.JwtPort;
import co.com.crediya.autenticacion.model.securityports.TokenInfo;
import co.com.crediya.autenticacion.model.shared.exception.AuthenticationUnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtAdapter implements JwtPort {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private String expirationTime;

    private Key key;

    /**
     * Initialize the adapter, creating the HMAC key from the provided secret.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate a JWT token from the provided email and role.
     *
     * <p>The token will contain the provided email as the subject, and the role as a claim.
     * The token will also be signed with the configured secret, and will expire after the
     * configured expiration time.
     *
     * @param email the email to include in the token
     * @param role  the role to include in the token
     * @return a Mono containing the generated JWT token
     */
    @Override
    public Mono<TokenInfo> generateToken(String email, String role) {
        long expirationSeconds = Long.parseLong(expirationTime);
        Date now = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationSeconds * 1000);

        return Mono.fromCallable(() -> Jwts.builder()
                        .claim("role", role)
                        .setSubject(email)
                        .setIssuedAt(now)
                        .setExpiration(expirationDate)
                        .signWith(key)
                        .compact()
                )
                .map(token -> TokenInfo.builder()
                        .token(token)
                        .creationDate(now)
                        .expirationDate(expirationDate)
                        .expiresIn(expirationSeconds)
                        .build());
    }

    /**
     * Validates the given JWT token.
     *
     * <p>This method will parse the token and verify its signature using the configured secret.
     * If the token is invalid, or the signature does not match, it will throw an error.
     *
     * @param token the JWT token to validate
     * @return a Mono emitting true if the token is valid, or an error if validation fails
     */
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
                    Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token);
                    return true;
                })
                .onErrorMap(ExpiredJwtException.class, e -> new AuthenticationUnauthorizedException("Token expired", e))
                .onErrorMap(JwtException.class, e -> new AuthenticationUnauthorizedException("Invalid token", e))
                .onErrorMap(IllegalArgumentException.class, e -> new AuthenticationUnauthorizedException("Invalid token", e));
    }


    /**
     * Returns the username from the given JWT token.
     *
     * <p>This method will extract the username from the token, which is the subject of the JWT.
     * If the token is invalid or the subject is not present, it will throw an error.
     *
     * @param token the JWT token to extract the username from
     * @return a Mono emitting the username from the token, or an error if extraction fails
     */
    @Override
    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromCallable(() -> getClaimFromToken(token, Claims::getSubject))
                .onErrorMap(e -> new AuthenticationUnauthorizedException("Invalid token", e));
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
