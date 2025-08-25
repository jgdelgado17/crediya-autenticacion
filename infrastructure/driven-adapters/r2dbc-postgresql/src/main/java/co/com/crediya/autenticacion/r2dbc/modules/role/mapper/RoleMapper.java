package co.com.crediya.autenticacion.r2dbc.modules.role.mapper;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleEntity toEntity(Role role) {
        return RoleEntity.builder()
                .id(role.getId())
                .names(role.getNames())
                .build();
    }

    public Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .names(entity.getNames())
                .build();
    }
}
