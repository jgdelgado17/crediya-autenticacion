package co.com.crediya.autenticacion.model.shared.exception;

public class ErrorMessages {
    public static String notFoundMessage(Class<?> clazz, Object id) {
        return clazz.getSimpleName() + " not found with ID: " + id;
    }

    public static String requiredField(String fieldName) {
        return fieldName + " is required";
    }

    public static String invalidEnumValue(String label, String value) {
        return "Invalid " + label + ": " + value;
    }
}
