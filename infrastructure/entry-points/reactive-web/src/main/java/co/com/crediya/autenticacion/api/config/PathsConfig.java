package co.com.crediya.autenticacion.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paths")
public record PathsConfig(
        String login,
        String roles,
        String users,
        String findUserByEmail
) {
}
