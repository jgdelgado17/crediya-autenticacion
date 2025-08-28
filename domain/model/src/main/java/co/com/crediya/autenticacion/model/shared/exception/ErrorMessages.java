package co.com.crediya.autenticacion.model.shared.exception;

public class ErrorMessages {
    public static String notFoundMessage(Class<?> clazz, Object field) {
        return clazz.getSimpleName() + " not found : " + field;
    }

    public static String requiredField(String fieldName) {
        return fieldName + " is required";
    }

    public static String invalidEnumValue(String label, String value) {
        return "Invalid " + label + ": " + value;
    }
}
