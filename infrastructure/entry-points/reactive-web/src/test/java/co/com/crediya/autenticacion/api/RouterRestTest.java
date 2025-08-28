package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private Handler handler;

    @MockBean
    private Validator validator;
    @MockBean
    private RoleUseCase roleUseCase;
    @MockBean
    private UserUseCase userUseCase;
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testRoutesAreConfigured() {
        // Arrange
        // (Este test no necesita mocks de los casos de uso, solo verifica que el
        // bean del router esté disponible)

        // Act & Assert
        // Verifica que el bean RouterRest está en el contexto de la aplicación
        // El test implícito de las rutas se hace en los tests siguientes
        WebTestClient testClient = WebTestClient.bindToApplicationContext(applicationContext).build();
        testClient.get().uri("/api/v1/role").exchange().expectStatus().isNotFound();
    }

    @Test
    void testPostToCreateRoleRoute() {
        // Arrange
        RoleRequest roleRequest = new RoleRequest("ADMIN", "Administrator");
        Role createdRole = new Role(1, "ADMIN", "Administrator");

        when(handler.createRole(any())).thenReturn(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(createdRole)
        );

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(createdRole.getId())
                .jsonPath("$.names").isEqualTo(createdRole.getNames())
                .jsonPath("$.description").isEqualTo(createdRole.getDescription());
    }

    @Test
    void testPostToCreateUserRoute() {
        // Arrange
        UserRequest userRequest = UserRequest.builder()
                .name("test")
                .lastName("test")
                .email("test@example.com")
                .roleName("ADMIN")
                .build();
        User createdUser = User.builder()
                .name("test")
                .lastName("test")
                .email("test@example.com")
                .role(new Role(1, "ADMIN", "Administrator"))
                .build();

        when(handler.createUser(any())).thenReturn(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(createdUser)
        );

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .value(user -> {
                    assertEquals(createdUser.getIdUser(), user.getIdUser());
                    assertEquals(createdUser.getName(), user.getName());
                    assertEquals(createdUser.getEmail(), user.getEmail());
                });
    }
}
