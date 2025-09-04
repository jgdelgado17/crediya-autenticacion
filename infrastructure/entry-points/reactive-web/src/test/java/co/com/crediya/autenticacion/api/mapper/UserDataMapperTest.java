package co.com.crediya.autenticacion.api.mapper;

import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.dto.UserResponse;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserDataMapperTest {

    private UserRequest userRequest;

    private User user;

    @BeforeEach
    void setUp() {

        userRequest = UserRequest.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("12345678")
                .phoneNumber("+57300123456")
                .baseSalary(2500000.0f)
                .roleName("ADMIN")
                .build();

        user = User.builder()
                .idUser(1)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("12345678")
                .phoneNumber("+57300123456")
                .baseSalary(2500000.0f)
                .role(Role.builder().names("ADMIN").build())
                .build();
    }

    @Test
    void shouldMapUserRequestToUser() {
        //Arrange
        //(The object userRequest is already created in the setUp method)

        //Act
        User result = UserDataMapper.toUser(userRequest);

        //Assert
        assertNotNull(result);
        assertEquals(userRequest.getName(), result.getName());
        assertEquals(userRequest.getLastName(), result.getLastName());
        assertEquals(userRequest.getEmail(), result.getEmail());
        assertEquals(userRequest.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(userRequest.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(userRequest.getBaseSalary(), result.getBaseSalary());
        assertEquals(userRequest.getRoleName(), result.getRole().getNames());
    }

    @Test
    void shouldMapUserToUserResponse() {
        //Arrange
        //(The object user is already created in the setUp method)

        //Act
        UserResponse result = UserDataMapper.toUserResponse(user);

        //Assert
        assertNotNull(result);
        assertEquals(user.getIdUser(), result.id());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getLastName(), result.lastName());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getDocumentNumber(), result.documentNumber());
        assertEquals(user.getPhoneNumber(), result.phoneNumber());
        assertEquals(user.getBaseSalary(), result.baseSalary());
        assertEquals(user.getRole().getNames(), result.roleName());
    }

    @Test
    void shouldMapUserToUserResponseWhenRoleIsNull() {
        //Arrange
        User user = User.builder()
                .idUser(1)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("12345678")
                .phoneNumber("+57300123456")
                .baseSalary(2500000.0f)
                .role(null)
                .build();

        //Act
        UserResponse result = UserDataMapper.toUserResponse(user);

        //Assert
        assertNotNull(result);
        assertEquals(user.getIdUser(), result.id());
        assertEquals(user.getName(), result.name());
        assertEquals(user.getLastName(), result.lastName());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getDocumentNumber(), result.documentNumber());
        assertEquals(user.getPhoneNumber(), result.phoneNumber());
        assertEquals(user.getBaseSalary(), result.baseSalary());
        assertEquals("", result.roleName());
    }
}