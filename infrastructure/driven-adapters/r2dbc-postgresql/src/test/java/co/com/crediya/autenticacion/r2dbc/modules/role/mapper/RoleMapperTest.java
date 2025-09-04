package co.com.crediya.autenticacion.r2dbc.modules.role.mapper;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {

    private RoleMapper roleMapper;
    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        roleMapper = new RoleMapper();
        role = Role.builder()
                .id(1)
                .names("ADMIN")
                .description("Administrator role")
                .build();

        roleEntity = RoleEntity.builder()
                .id(1)
                .names("ADMIN")
                .description("Administrator role")
                .build();
    }

    @Test
    void shouldMapRoleToRoleEntity() {
        // Arrange
        // (The objects 'role' and 'roleEntity' are already created in the @BeforeEach method)

        // Act
        RoleEntity result = roleMapper.toEntity(role);

        // Assert
        assertEquals(roleEntity, result);
        assertEquals(role.getId(), result.getId());
        assertEquals(role.getNames(), result.getNames());
        assertEquals(role.getDescription(), result.getDescription());
    }

    @Test
    void shouldMapRoleEntityToRole() {
        // Arrange
        // (The objects 'role' and 'roleEntity' are already created in the @BeforeEach method)

        // Act
        Role result = roleMapper.toDomain(roleEntity);

        // Assert
        assertEquals(role.getId(), result.getId());
        assertEquals(role.getNames(), result.getNames());
        assertEquals(role.getDescription(), result.getDescription());
    }
}