package co.com.crediya.autenticacion.r2dbc.config;

// TODO: Load properties from the application.yaml file or from secrets manager
// import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @ConfigurationProperties(prefix = "adapters.r2dbc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostgresqlConnectionProperties {

    private String database;
    private String schema;
    private String username;
    private String password;
    private String host;
    private Integer port;

}