package co.com.crediya.autenticacion.r2dbc.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostgresqlConnectionPropertiesTest {

    @Test
    void shouldLoadPropertiesSuccessfully() {
        //Arrange && Act
        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties(
                "localhost",
                5432,
                "myDatabase",
                "public",
                "dbUser",
                "dbPassword"
        );

        //Assert
        assertNotNull(properties);
        assertEquals("localhost", properties.host());
        assertEquals(5432, properties.port());
        assertEquals("myDatabase", properties.database());
        assertEquals("public", properties.schema());
        assertEquals("dbUser", properties.username());
        assertEquals("dbPassword", properties.password());
    }
}