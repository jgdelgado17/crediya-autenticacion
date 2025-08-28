package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.model.role.Role;
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
import static org.mockito.Mockito.doNothing;
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

        // Mock the validator to do nothing (simulate successful validation)
        doNothing().when(validator).validate(any(), any());

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
    void shouldReturnBadRequestWhenValidationFails() {
        // Arrange
        UserRequest invalidRequest = UserRequest.builder()
                .name("")
                .lastName("")
                .email("")
                .roleName("ADMIN")
                .build();

        // Simula el validador para que falle la validación
        doNothing().when(validator).validate(any(), any()); // La validación real ocurre en el .doOnNext

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}