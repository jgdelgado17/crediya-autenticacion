package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseTest {

    @InjectMocks
    private RoleUseCase roleUseCase;

    @Mock
    private RoleRepository roleRepository;

    private Role role;
    private Role existingRole;
    private Role invalidRole;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1).names("ADMIN").build();
        existingRole = Role.builder().id(2).names("ADMIN").build();
        invalidRole = Role.builder().id(3).names("INVALID_ROLE").build();
    }

    @Test
    void shouldCreateRoleSuccessfullyWhenRoleDoesNotExist() {
        // Arrange
        when(roleRepository.findByName(role.getNames())).thenReturn(Mono.empty());
        when(roleRepository.save(role)).thenReturn(Mono.just(role));

        // Act
        Mono<Role> result = roleUseCase.createRole(role);

        // Assert
        StepVerifier.create(result)
                .expectNext(role)
                .expectComplete()
                .verify();

        verify(roleRepository).findByName(role.getNames());
        verify(roleRepository).save(role);
    }

    @Test
    void shouldCreateAdvisorRoleSuccessfullyWhenRoleDoesNotExist() {
        // Arrange
        Role advisorRole = Role.builder().id(2).names("ADVISOR").build();
        when(roleRepository.findByName("ADVISOR")).thenReturn(Mono.empty());
        when(roleRepository.save(advisorRole)).thenReturn(Mono.just(advisorRole));

        // Act
        Mono<Role> result = roleUseCase.createRole(advisorRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(advisorRole)
                .verifyComplete();

        verify(roleRepository).findByName("ADVISOR");
        verify(roleRepository).save(advisorRole);
    }

    @Test
    void shouldCreateClientRoleSuccessfullyWhenRoleDoesNotExist() {
        // Arrange
        Role clientRole = Role.builder().id(3).names("CLIENT").build();
        when(roleRepository.findByName("CLIENT")).thenReturn(Mono.empty());
        when(roleRepository.save(clientRole)).thenReturn(Mono.just(clientRole));

        // Act
        Mono<Role> result = roleUseCase.createRole(clientRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(clientRole)
                .verifyComplete();

        verify(roleRepository).findByName("CLIENT");
        verify(roleRepository).save(clientRole);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "ADVISOR", "CLIENT"})
    void shouldCreateRoleSuccessfullyForAllValidRoleNames(String roleName) {
        // Arrange
        Role validRole = Role.builder().id(1).names(roleName).build();
        when(roleRepository.findByName(roleName)).thenReturn(Mono.empty());
        when(roleRepository.save(validRole)).thenReturn(Mono.just(validRole));

        // Act
        Mono<Role> result = roleUseCase.createRole(validRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(validRole)
                .verifyComplete();

        verify(roleRepository).findByName(roleName);
        verify(roleRepository).save(validRole);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleAlreadyExists() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.just(existingRole));

        // Act
        Mono<Role> result = roleUseCase.createRole(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
                    assertThat(throwable).hasMessage("Role already exists");
                })
                .verify();

        verify(roleRepository).findByName("ADMIN");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsNull() {
        // Arrange
        Role roleWithNullName = Role.builder().id(1).names(null).build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = roleUseCase.createRole(roleWithNullName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(roleRepository, never()).findByName(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsEmpty() {
        // Arrange
        Role roleWithEmptyName = Role.builder().id(1).names("").build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = roleUseCase.createRole(roleWithEmptyName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(roleRepository, never()).findByName(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsOnlyWhitespace() {
        // Arrange
        Role roleWithWhitespaceName = Role.builder().id(1).names("   ").build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = roleUseCase.createRole(roleWithWhitespaceName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(roleRepository, never()).findByName(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsInvalid() {
        // Arrange
        String expectedMessage = ErrorMessages.invalidEnumValue("RoleEnum", "INVALID_ROLE");

        // Act
        Mono<Role> result = roleUseCase.createRole(invalidRole);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(roleRepository, never()).findByName(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldReturnErrorWhenRepositoryFindByNameFails() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<Role> result = roleUseCase.createRole(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Database error")
                .verify();

        verify(roleRepository).findByName("ADMIN");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldReturnErrorWhenRepositorySaveFails() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.empty());
        when(roleRepository.save(role)).thenReturn(Mono.error(new RuntimeException("Save error")));

        // Act
        Mono<Role> result = roleUseCase.createRole(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Save error")
                .verify();

        verify(roleRepository).findByName("ADMIN");
        verify(roleRepository).save(role);
    }

    @Test
    void shouldReturnSameRoleInstanceWhenCreatedSuccessfully() {
        // Arrange
        Role originalRole = Role.builder().id(5).names("CLIENT").build();
        when(roleRepository.findByName("CLIENT")).thenReturn(Mono.empty());
        when(roleRepository.save(originalRole)).thenReturn(Mono.just(originalRole));

        // Act
        Mono<Role> result = roleUseCase.createRole(originalRole);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(returnedRole -> returnedRole == originalRole)
                .verifyComplete();

        verify(roleRepository).findByName("CLIENT");
        verify(roleRepository).save(originalRole);
    }

    @Test
    void shouldNotCallSaveWhenValidationFailsWithCaseSensitivity() {
        // Arrange
        Role roleWithWrongCase = Role.builder().id(1).names("admin").build();

        // Act
        Mono<Role> result = roleUseCase.createRole(roleWithWrongCase);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(roleRepository, never()).findByName(anyString());
        verify(roleRepository, never()).save(any(Role.class));
    }
}