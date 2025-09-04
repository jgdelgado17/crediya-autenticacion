package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.RoleEnum;
import co.com.crediya.autenticacion.model.shared.enums.EnumUtils;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import reactor.core.publisher.Mono;

public class RoleValidator {

    /**
     * Validates the given role.
     *
     * <p>The role name must not be null or empty, and must be a valid RoleEnum value.
     *
     * @param role the role to validate
     * @return a mono emitting the validated role, or an error if validation fails
     */
    public static Mono<Role> validate(Role role) {
        if (role.getNames() == null || role.getNames().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("roleName")));
        }

        try {
            EnumUtils.fromString(RoleEnum.class, role.getNames());
        } catch (Exception e) {
            return Mono.error(e);
        }

        return Mono.just(role);
    }
}
