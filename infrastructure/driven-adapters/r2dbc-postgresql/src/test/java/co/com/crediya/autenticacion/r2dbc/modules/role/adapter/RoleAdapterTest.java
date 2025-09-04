package co.com.crediya.autenticacion.r2dbc.modules.role.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleAdapterTest {

    @InjectMocks
    private RoleAdapter roleAdapter;

    @Mock
    private RoleRepositoryCrud repository;

    @Mock
    private ObjectMapper mapper;

    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1).names("ADMIN").build();
        roleEntity = RoleEntity.builder().id(1).names("ADMIN").build();
    }

    @Test
    void shouldSaveRoleSuccessfully() {
        // Arrange
        when(mapper.map(role, RoleEntity.class)).thenReturn(roleEntity);
        when(repository.save(any(RoleEntity.class))).thenReturn(Mono.just(roleEntity));
        when(mapper.map(roleEntity, Role.class)).thenReturn(role);

        // Act
        Mono<Role> result = roleAdapter.save(role);

        // Assert
        StepVerifier.create(result)
                .expectNext(role)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenSaveRoleFails() {
        // Arrange
        when(mapper.map(role, RoleEntity.class)).thenReturn(roleEntity);
        when(repository.save(any(RoleEntity.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Role> result = roleAdapter.save(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Database error")
                .verify();
    }

    @Test
    void shouldReturnErrorWhenSaveRoleExists() {
        // Arrange
        when(mapper.map(role, RoleEntity.class)).thenReturn(roleEntity);
        when(repository.save(any(RoleEntity.class))).thenReturn(Mono.error(new RuntimeException("Role already exists")));

        // Act
        Mono<Role> result = roleAdapter.save(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Role already exists")
                .verify();
    }

    @Test
    void shouldFindRoleByNameSuccessfully() {
        // Arrange
        when(repository.findByNames(any(String.class))).thenReturn(Mono.just(roleEntity));
        when(mapper.map(roleEntity, Role.class)).thenReturn(role);

        // Act
        Mono<Role> result = roleAdapter.findByName("ADMIN");

        // Assert
        StepVerifier.create(result)
                .expectNext(role)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenRoleNotFoundByName() {
        // Arrange
        when(repository.findByNames(any(String.class))).thenReturn(Mono.empty());

        // Act
        Mono<Role> result = roleAdapter.findByName("NON_EXISTENT_ROLE");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}