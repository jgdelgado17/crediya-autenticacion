package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.RoleEnum;
import co.com.crediya.autenticacion.model.shared.enums.EnumUtils;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import reactor.core.publisher.Mono;

public class RoleValidator {
    public static Mono<Role> validate(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("roleName")));
        }

        try {
            EnumUtils.fromString(RoleEnum.class, role.getName());
        } catch (Exception e) {
            return Mono.error(e);
        }

        return Mono.just(role);
    }
}
