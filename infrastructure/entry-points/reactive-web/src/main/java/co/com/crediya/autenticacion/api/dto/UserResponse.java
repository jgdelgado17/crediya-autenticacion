package co.com.crediya.autenticacion.api.dto;

public record UserResponse(
        Integer id,
        String name,
        String lastName,
        String email,
        String documentNumber,
        String phoneNumber,
        Float baseSalary,
        String roleName,
        Integer roleId
) {
}
