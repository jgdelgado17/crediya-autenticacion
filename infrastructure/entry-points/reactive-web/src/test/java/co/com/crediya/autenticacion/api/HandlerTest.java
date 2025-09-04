package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.api.mapper.UserDataMapper;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {Handler.class, RouterRest.class})
@WebFluxTest
class HandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private Validator validator;

    @MockBean
    private RoleUseCase roleUseCase;

    @MockBean
    private UserUseCase userUseCase;

    @Test
    void shouldCreateRoleSuccessfully() {
        //Arrange
        RoleRequest roleRequest = new RoleRequest("ADMIN", "Administrator role");
        Role role = RoleDataMapper.toRole(roleRequest);

        // Mock the roleUseCase to return the created role
        when(roleUseCase.createRole(any())).thenReturn(Mono.just(role));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleRequest)
                .exchange()
                // Assert
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.names").isEqualTo(role.getNames())
                .jsonPath("$.description").isEqualTo(role.getDescription());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        //Arrange
        UserRequest userRequest = UserRequest.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .roleName("ADMIN")
                .build();

        User user = UserDataMapper.toUser(userRequest);

        // Mock the userUseCase to return the created user
        when(userUseCase.createUser(any())).thenReturn(Mono.just(user));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                // Assert
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(user.getName())
                .jsonPath("$.lastName").isEqualTo(user.getLastName())
                .jsonPath("$.email").isEqualTo(user.getEmail());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFailsToCreateUser() {
        // Arrange
        UserRequest invalidRequest = UserRequest.builder()
                .name("")
                .lastName("")
                .email("")
                .roleName("ADMIN")
                .build();

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}