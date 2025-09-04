package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class RoleValidatorTest {

    @BeforeEach
    void setUp() {
        RoleValidator roleValidator = new RoleValidator();
    }

    @Test
    void shouldReturnErrorWhenRoleNameIsNull() {
        // Arrange
        Role invalidRole = Role.builder().id(1).names(null).build();
        String expectedErrorMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(invalidRole);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenRoleNameIsEmpty() {
        // Arrange
        Role invalidRole = Role.builder().id(1).names("").build();
        String expectedErrorMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(invalidRole);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenRoleNameIsInvalid() {
        // Arrange
        Role invalidRole = Role.builder().id(1).names("INVALID_ROLE").build();
        String expectedErrorMessage = ErrorMessages.invalidEnumValue("RoleEnum", "INVALID_ROLE");

        // Act
        Mono<Role> result = RoleValidator.validate(invalidRole);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals(expectedErrorMessage))
                .verify();
    }

    @Test
    void shouldReturnValidatedRoleWhenValidAdminRoleIsProvided() {
        // Arrange
        Role validRole = Role.builder().id(1).names("ADMIN").build();

        // Act
        Mono<Role> result = RoleValidator.validate(validRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(validRole)
                .verifyComplete();
    }

    @Test
    void shouldReturnValidatedRoleWhenValidAdvisorRoleIsProvided() {
        // Arrange
        Role validRole = Role.builder().id(2).names("ADVISOR").build();

        // Act
        Mono<Role> result = RoleValidator.validate(validRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(validRole)
                .verifyComplete();
    }

    @Test
    void shouldReturnValidatedRoleWhenValidClientRoleIsProvided() {
        // Arrange
        Role validRole = Role.builder().id(3).names("CLIENT").build();

        // Act
        Mono<Role> result = RoleValidator.validate(validRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(validRole)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "ADVISOR", "CLIENT"})
    void shouldReturnValidatedRoleForAllValidRoleNames(String validRoleName) {
        // Arrange
        Role validRole = Role.builder().id(1).names(validRoleName).build();

        // Act
        Mono<Role> result = RoleValidator.validate(validRole);

        // Assert
        StepVerifier.create(result)
                .expectNext(validRole)
                .verifyComplete();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsNull() {
        // Arrange
        Role roleWithNullName = Role.builder().id(1).names(null).build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithNullName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsEmpty() {
        // Arrange
        Role roleWithEmptyName = Role.builder().id(1).names("").build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithEmptyName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowIllegalArgumentExceptionForNullAndEmptyRoleNames(String invalidRoleName) {
        // Arrange
        Role roleWithInvalidName = Role.builder().id(1).names(invalidRoleName).build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithInvalidName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsOnlyWhitespace() {
        // Arrange
        Role roleWithWhitespaceName = Role.builder().id(1).names("   ").build();
        String expectedMessage = ErrorMessages.requiredField("roleName");

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithWhitespaceName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRoleNameIsInvalid() {
        // Arrange
        Role roleWithInvalidName = Role.builder().id(1).names("INVALID_ROLE").build();
        String expectedMessage = ErrorMessages.invalidEnumValue("RoleEnum", "INVALID_ROLE");

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithInvalidName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "ADMINISTRATOR", "USER", "MANAGER", "SUPER_ADMIN", "123"})
    void shouldThrowIllegalArgumentExceptionForVariousInvalidRoleNames(String invalidRoleName) {
        // Arrange
        Role roleWithInvalidName = Role.builder().id(1).names(invalidRoleName).build();

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithInvalidName);

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldReturnSameRoleInstanceWhenValidationPasses() {
        // Arrange
        Role originalRole = Role.builder().id(5).names("ADVISOR").build();

        // Act
        Mono<Role> result = RoleValidator.validate(originalRole);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(returnedRole -> returnedRole == originalRole)
                .verifyComplete();
    }

    @Test
    void shouldValidateRoleWithDifferentIdSuccessfully() {
        // Arrange
        Role roleWithDifferentId = Role.builder().id(999).names("CLIENT").build();

        // Act
        Mono<Role> result = RoleValidator.validate(roleWithDifferentId);

        // Assert
        StepVerifier.create(result)
                .expectNext(roleWithDifferentId)
                .verifyComplete();
    }

    @Test
    void shouldHandleCaseSensitiveValidation() {
        // Arrange
        Role correctCaseRole = Role.builder().id(1).names("ADMIN").build();
        Role wrongCaseRole = Role.builder().id(2).names("admin").build();

        // Act & Assert - Correct case should pass
        StepVerifier.create(RoleValidator.validate(correctCaseRole))
                .expectNext(correctCaseRole)
                .verifyComplete();

        // Act & Assert - Wrong case should fail
        StepVerifier.create(RoleValidator.validate(wrongCaseRole))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}