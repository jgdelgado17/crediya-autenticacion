package co.com.crediya.autenticacion.api.config;

import co.com.crediya.autenticacion.api.Handler;
import co.com.crediya.autenticacion.api.RouterRest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private Handler handler;

    @MockBean
    private RouterRest routerRest;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        // Given
        when(routerRest.routerFunction(any())).thenReturn(
                RouterFunctions.route(
                        POST("/api/usecase/path"),
                        request -> ServerResponse.ok()
                                .header("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                                .header("Strict-Transport-Security", "max-age=31536000;")
                                .header("X-Content-Type-Options", "nosniff")
                                .header("Server", "")
                                .header("Cache-Control", "no-store")
                                .header("Pragma", "no-cache")
                                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                                .build()
                )
        );

        // When/Then
        webTestClient.post()
                .uri("/api/usecase/path")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}