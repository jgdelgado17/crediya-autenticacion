package co.com.crediya.autenticacion.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserRequest {
    private String name;
    private String lastName;
    private String email;
    private String documentNumber;
    private String phoneNumber;
    private Float baseSalary;
    private String roleName;
}
