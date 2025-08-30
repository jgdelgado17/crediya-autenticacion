package co.com.crediya.autenticacion.api.mapper;

import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.dto.UserResponse;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;

public class UserDataMapper {
    public static User toUser(UserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .documentNumber(userRequest.getDocumentNumber())
                .phoneNumber(userRequest.getPhoneNumber())
                .baseSalary(userRequest.getBaseSalary())
                .role(Role.builder().names(userRequest.getRoleName()).build())
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getIdUser(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getDocumentNumber(),
                user.getPhoneNumber(),
                user.getBaseSalary(),
                user.getRole() != null ? user.getRole().getNames() : ""
        );
    }
}
