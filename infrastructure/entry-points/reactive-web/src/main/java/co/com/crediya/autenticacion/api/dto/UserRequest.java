package co.com.crediya.autenticacion.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
    private String documentNumber;
    private String phoneNumber;
    @NotNull(message = "Base salary is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base salary must be greater than zero")
    @DecimalMax(value = "15000000.0", message = "Base salary must be less than or equal to 15,000,000")
    private Float baseSalary;
    @NotBlank(message = "Role name is mandatory")
    private String roleName;
    @NotBlank(message = "Password is required")
    private String password;
}
