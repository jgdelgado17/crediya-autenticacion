package co.com.crediya.autenticacion.r2dbc.modules.user.mapper;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.r2dbc.modules.user.data.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    private UserMapper userMapper;
    private User user;
    private UserEntity userEntity;

    private Role role;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        role = Role.builder().id(1).names("ADMIN").description("Administrator role").build();
        user = User.builder()
                .idUser(1)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("123456789")
                .baseSalary(1000.0f)
                .role(role)
                .build();

        userEntity = UserEntity.builder()
                .idUser(1)
                .names("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("123456789")
                .baseSalary(1000.0f)
                .idRole(1)
                .build();
    }

    @Test
    void shouldConvertUserToUserEntity() {
        //Arrange
        //(The objects 'user' and 'userEntity' are already created in the @BeforeEach method)

        //Act
        UserEntity result = userMapper.toEntity(user);

        //Assert
        assertEquals(userEntity, result);
        assertEquals(user.getIdUser(), result.getIdUser());
        assertEquals(user.getName(), result.getNames());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getBaseSalary(), result.getBaseSalary());
        assertEquals(user.getRole().getId(), result.getIdRole());
    }

    @Test
    void shouldConvertUserEntityToUser() {
        //Arrange
        //(The objects 'user' and 'userEntity' are already created in the @BeforeEach method)

        //Act
        User result = userMapper.toModel(userEntity, role);

        //Assert
        assertEquals(user.getIdUser(), result.getIdUser());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getBaseSalary(), result.getBaseSalary());
        assertEquals(user.getRole().getId(), result.getRole().getId());
        assertEquals(user.getRole().getNames(), result.getRole().getNames());
        assertEquals(user.getRole().getDescription(), result.getRole().getDescription());
    }
}