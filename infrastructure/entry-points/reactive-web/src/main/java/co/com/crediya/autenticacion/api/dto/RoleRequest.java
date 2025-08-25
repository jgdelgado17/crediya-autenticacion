package co.com.crediya.autenticacion.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleRequest {
    @NotBlank(message = "Names is mandatory")
    private String names;
    private String description;
}
