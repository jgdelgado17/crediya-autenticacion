package co.com.crediya.autenticacion.api.mapper;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.model.role.Role;

public class RoleDataMapper {
    public static Role toRole(RoleRequest roleRequest) {
        return Role.builder()
                .names(roleRequest.getNames())
                .description(roleRequest.getDescription())
                .build();
    }
}
