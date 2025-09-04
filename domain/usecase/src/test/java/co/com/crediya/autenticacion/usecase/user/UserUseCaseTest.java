package co.com.crediya.autenticacion.usecase.user;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import co.com.crediya.autenticacion.model.shared.exception.RecordNotFoundException;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @InjectMocks
    private UserUseCase userUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private User user;
    private Role adminRole;
    private Role advisorRole;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder().id(1).names("ADMIN").build();
        advisorRole = Role.builder().id(2).names("ADVISOR").build();
        clientRole = Role.builder().id(3).names("CLIENT").build();

        user = User.builder()
                .idUser(1)
                .email("test@example.com")
                .name("John")
                .lastName("Doe")
                .role(adminRole)
                .build();
    }

    @Test
    void shouldCreateUserSuccessfullyWhenUserDoesNotExistAndRoleExists(){
        //Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName(adminRole.getNames())).thenReturn(Mono.just(adminRole));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        //Act
        Mono<User> result = userUseCase.createUser(user);

        //Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).save(user);
        verify(roleRepository, times(1)).findByName(adminRole.getNames());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    void shouldCreateClientUserSuccessfullyWhenUserDoesNotExistAndRoleExists() {
        // Arrange
        User clientUser = User.builder()
                .idUser(3)
                .email("client@example.com")
                .name("Bob")
                .lastName("Johnson")
                .role(clientRole)
                .build();

        when(userRepository.findByEmail("client@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(Mono.just(clientRole));
        when(userRepository.save(clientUser)).thenReturn(Mono.just(clientUser));

        // Act
        Mono<User> result = userUseCase.createUser(clientUser);

        // Assert
        StepVerifier.create(result)
                .expectNext(clientUser)
                .verifyComplete();

        verify(userRepository).findByEmail("client@example.com");
        verify(roleRepository).findByName("CLIENT");
        verify(userRepository).save(clientUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "ADVISOR", "CLIENT"})
    void shouldCreateUserSuccessfullyForAllValidRoles(String roleName) {
        // Arrange
        Role role = Role.builder().id(1).names(roleName).build();
        User userWithRole = User.builder()
                .idUser(1)
                .email("user@example.com")
                .name("Test")
                .lastName("User")
                .role(role)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName(roleName)).thenReturn(Mono.just(role));
        when(userRepository.save(userWithRole)).thenReturn(Mono.just(userWithRole));

        // Act
        Mono<User> result = userUseCase.createUser(userWithRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(userWithRole)
                .verifyComplete();

        verify(userRepository).findByEmail("user@example.com");
        verify(roleRepository).findByName(roleName);
        verify(userRepository).save(userWithRole);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleDoesNotExist() {
        // Arrange
        String expectedMessage = ErrorMessages.notFoundMessage(Role.class, "ADMIN");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.empty());

        // Act
        Mono<User> result = userUseCase.createUser(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RecordNotFoundException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(userRepository).findByEmail("test@example.com");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAdvisorRoleDoesNotExist() {
        // Arrange
        User advisorUser = User.builder()
                .idUser(2)
                .email("advisor@example.com")
                .name("Jane")
                .lastName("Smith")
                .role(advisorRole)
                .build();

        String expectedMessage = ErrorMessages.notFoundMessage(Role.class, "ADVISOR");
        when(userRepository.findByEmail("advisor@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADVISOR")).thenReturn(Mono.empty());

        // Act
        Mono<User> result = userUseCase.createUser(advisorUser);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RecordNotFoundException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();

        verify(userRepository).findByEmail("advisor@example.com");
        verify(roleRepository).findByName("ADVISOR");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnErrorWhenRoleRepositoryFindByNameFails() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.error(new RuntimeException("Role database error")));

        // Act
        Mono<User> result = userUseCase.createUser(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Role database error")
                .verify();

        verify(userRepository).findByEmail("test@example.com");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnErrorWhenUserRepositorySaveFails() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.just(adminRole));
        when(userRepository.save(user)).thenReturn(Mono.error(new RuntimeException("Save error")));

        // Act
        Mono<User> result = userUseCase.createUser(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Save error")
                .verify();

        verify(userRepository).findByEmail("test@example.com");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    void shouldSetRoleOnUserBeforeSaving() {
        // Arrange
        Role fetchedRole = Role.builder().id(10).names("ADMIN").build(); // Different instance
        User userWithOriginalRole = User.builder()
                .idUser(1)
                .email("test@example.com")
                .name("John")
                .lastName("Doe")
                .role(adminRole) // Original role
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.just(fetchedRole));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithOriginalRole));

        // Act
        Mono<User> result = userUseCase.createUser(userWithOriginalRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(userWithOriginalRole)
                .verifyComplete();

        // Verify that the role was set to the fetched role before saving
        verify(userRepository).save(userWithOriginalRole);
        // The user should have the fetched role set
        assert userWithOriginalRole.getRole() == fetchedRole;
    }

    @Test
    void shouldReturnSameUserInstanceAfterSuccessfulCreation() {
        // Arrange
        User originalUser = User.builder()
                .idUser(5)
                .email("original@example.com")
                .name("Original")
                .lastName("User")
                .role(clientRole)
                .build();

        when(userRepository.findByEmail("original@example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(Mono.just(clientRole));
        when(userRepository.save(originalUser)).thenReturn(Mono.just(originalUser));

        // Act
        Mono<User> result = userUseCase.createUser(originalUser);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(returnedUser -> returnedUser == originalUser)
                .verifyComplete();

        verify(userRepository).findByEmail("original@example.com");
        verify(roleRepository).findByName("CLIENT");
        verify(userRepository).save(originalUser);
    }

    @Test
    void shouldHandleUserWithComplexEmailFormat() {
        // Arrange
        User userWithComplexEmail = User.builder()
                .idUser(1)
                .email("user.name+tag@subdomain.example.com")
                .name("Complex")
                .lastName("Email")
                .role(adminRole)
                .build();

        when(userRepository.findByEmail("user.name+tag@subdomain.example.com")).thenReturn(Mono.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Mono.just(adminRole));
        when(userRepository.save(userWithComplexEmail)).thenReturn(Mono.just(userWithComplexEmail));

        // Act
        Mono<User> result = userUseCase.createUser(userWithComplexEmail);

        // Assert
        StepVerifier.create(result)
                .expectNext(userWithComplexEmail)
                .verifyComplete();

        verify(userRepository).findByEmail("user.name+tag@subdomain.example.com");
        verify(roleRepository).findByName("ADMIN");
        verify(userRepository).save(userWithComplexEmail);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        // Make sure roleRepository.findByName() returns a proper Mono, even if empty
        when(roleRepository.findByName(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<User> result = userUseCase.createUser(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("User with email test@example.com already exists")
                )
                .verify();
    }
}