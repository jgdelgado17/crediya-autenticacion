package co.com.crediya.autenticacion.model.shared.enums;

import co.com.crediya.autenticacion.model.role.RoleEnum;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilsTest {

    @Test
    void shouldReturnCorrectEnumWhenValidAdminStringIsProvided() {
        // Arrange
        String validValue = "ADMIN";

        // Act
        RoleEnum result = EnumUtils.fromString(RoleEnum.class, validValue);

        // Assert
        assertEquals(RoleEnum.ADMIN, result);
        assertEquals("ADMIN", result.getValue());
    }

    @Test
    void shouldReturnCorrectEnumWhenValidAdvisorStringIsProvided() {
        // Arrange
        String validValue = "ADVISOR";

        // Act
        RoleEnum result = EnumUtils.fromString(RoleEnum.class, validValue);

        // Assert
        assertEquals(RoleEnum.ADVISOR, result);
        assertEquals("ADVISOR", result.getValue());
    }

    @Test
    void shouldReturnCorrectEnumWhenValidClientStringIsProvided() {
        // Arrange
        String validValue = "CLIENT";

        // Act
        RoleEnum result = EnumUtils.fromString(RoleEnum.class, validValue);

        // Assert
        assertEquals(RoleEnum.CLIENT, result);
        assertEquals("CLIENT", result.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "ADVISOR", "CLIENT"})
    void shouldReturnCorrectEnumForMultipleValidStrings(String validValue) {
        // Act & Assert
        assertDoesNotThrow(() -> {
            RoleEnum result = EnumUtils.fromString(RoleEnum.class, validValue);
            assertNotNull(result);
            assertEquals(validValue, result.getValue());
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowIllegalArgumentExceptionForNullAndEmptyStrings(String invalidValue) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EnumUtils.fromString(RoleEnum.class, invalidValue)
        );

        assertNotNull(exception);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForWhitespaceOnlyString() {
        // Arrange
        String whitespaceValue = "   ";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EnumUtils.fromString(RoleEnum.class, whitespaceValue)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("RoleEnum"));
        assertTrue(exception.getMessage().contains(whitespaceValue));
    }

    @Test
    void shouldBeCaseSensitiveForEnumValues() {
        // Arrange
        String correctCase = "ADMIN";
        String wrongCase = "admin";

        // Act & Assert
        assertDoesNotThrow(() -> EnumUtils.fromString(RoleEnum.class, correctCase));

        assertThrows(
                IllegalArgumentException.class,
                () -> EnumUtils.fromString(RoleEnum.class, wrongCase)
        );
    }

    @Test
    void shouldThrowNullPointerExceptionWhenEnumClassIsNull() {
        // Arrange
        String validValue = "ADMIN";

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> EnumUtils.fromString(null, validValue)
        );
    }

    @Test
    void shouldMaintainEnumReferenceEqualityForSameValue() {
        // Arrange
        String value = "CLIENT";

        // Act
        RoleEnum result1 = EnumUtils.fromString(RoleEnum.class, value);
        RoleEnum result2 = EnumUtils.fromString(RoleEnum.class, value);

        // Assert
        assertSame(result1, result2);
        assertEquals(RoleEnum.CLIENT, result1);
        assertEquals(RoleEnum.CLIENT, result2);
    }

    @Test
    void shouldHandleMultipleConsecutiveCallsCorrectly() {
        // Arrange
        String[] values = {"ADMIN", "ADVISOR", "CLIENT"};
        RoleEnum[] expected = {RoleEnum.ADMIN, RoleEnum.ADVISOR, RoleEnum.CLIENT};

        // Act & Assert
        for (int i = 0; i < values.length; i++) {
            RoleEnum result = EnumUtils.fromString(RoleEnum.class, values[i]);
            assertEquals(expected[i], result);
            assertEquals(values[i], result.getValue());
        }
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenInvalidStringIsProvided() {
        // Arrange
        String invalidValue = "INVALID_ROLE";
        String expectedMessage = ErrorMessages.invalidEnumValue("RoleEnum", invalidValue);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            EnumUtils.fromString(RoleEnum.class, invalidValue);
        });
        assertEquals(expectedMessage, exception.getMessage());
    }
}