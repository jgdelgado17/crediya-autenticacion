package co.com.crediya.autenticacion.r2dbc.modules.user.mapper;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.r2dbc.modules.user.data.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .idUser(user.getIdUser())
                .names(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .documentNumber(user.getDocumentNumber())
                .phoneNumber(user.getPhoneNumber())
                .baseSalary(user.getBaseSalary())
                .idRole(user.getRole().getId())
                .passcode(user.getPassword())
                .build();
    }

    public User toModel(UserEntity userEntity, Role role) {
        return User.builder()
                .idUser(userEntity.getIdUser())
                .name(userEntity.getNames())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .documentNumber(userEntity.getDocumentNumber())
                .phoneNumber(userEntity.getPhoneNumber())
                .baseSalary(userEntity.getBaseSalary())
                .role(role)
                .password(userEntity.getPasscode())
                .build();
    }
}
