package co.com.crediya.autenticacion.model.shared.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ErrorMessagesTest {

    @BeforeEach
    void setUp() {
        ErrorMessages errorMessages = new ErrorMessages();
    }

    @Test
    void shouldReturnNotFoundMessage() {
        // Arrange
        Class<?> clazz = String.class;
        Object field = "testField";

        // Act
        String result = ErrorMessages.notFoundMessage(clazz, field);

        // Assert
        assert result.equals("String not found : testField");
    }

    @Test
    void shouldReturnRequiredFieldMessage() {
        // Arrange
        String fieldName = "username";

        // Act
        String result = ErrorMessages.requiredField(fieldName);

        // Assert
        assert result.equals("username is required");
    }

    @Test
    void shouldReturnInvalidEnumValueMessage() {
        // Arrange
        String label = "RoleEnum";
        String value = "INVALID";

        // Act
        String result = ErrorMessages.invalidEnumValue(label, value);

        // Assert
        assert result.equals("Invalid RoleEnum: INVALID");
    }

}