package co.com.crediya.autenticacion.r2dbc.modules.user.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import co.com.crediya.autenticacion.r2dbc.modules.role.mapper.RoleMapper;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import co.com.crediya.autenticacion.r2dbc.modules.user.data.UserEntity;
import co.com.crediya.autenticacion.r2dbc.modules.user.mapper.UserMapper;
import co.com.crediya.autenticacion.r2dbc.modules.user.repository.UserRepositoryCrud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdapterTest {

    @InjectMocks
    private UserAdapter userAdapter;

    @Mock
    private UserRepositoryCrud userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepositoryCrud roleRepository;

    @Mock
    private RoleMapper roleMapper;

    private User user;
    private UserEntity userEntity;
    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        // Arrange: Preparing the test environment

        role = Role.builder().id(1).names("ADMIN").description("Administrator role").build();
        roleEntity = RoleEntity.builder().id(1).names("ADMIN").description("Administrator role").build();

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
    void shouldSaveUserSuccessfully() {
        // Arrange
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(anyInt())).thenReturn(Mono.just(roleEntity));
        when(roleMapper.toDomain(roleEntity)).thenReturn(role);
        when(userMapper.toModel(userEntity, role)).thenReturn(user);

        // Act
        Mono<User> result = userAdapter.save(user);

        // Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldFailToSaveUserIfRoleIsNotFound() {
        // Arrange
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Act
        Mono<User> result = userAdapter.save(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Role not found"))
                .verify();

        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(userEntity));
        when(roleRepository.findById(anyInt())).thenReturn(Mono.just(roleEntity));
        when(roleMapper.toDomain(roleEntity)).thenReturn(role);
        when(userMapper.toModel(userEntity, role)).thenReturn(user);

        // Act
        Mono<User> result = userAdapter.findByEmail("john.doe@example.com");

        // Assert
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void shouldReturnEmptyMonoIfUserIsNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<User> result = userAdapter.findByEmail("john.doe@example.com");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository).findByEmail("john.doe@example.com");
    }
}