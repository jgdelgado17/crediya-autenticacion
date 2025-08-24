package co.com.crediya.autenticacion.r2dbc.modules.user.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
import co.com.crediya.autenticacion.r2dbc.modules.role.mapper.RoleMapper;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import co.com.crediya.autenticacion.r2dbc.modules.user.data.UserEntity;
import co.com.crediya.autenticacion.r2dbc.modules.user.mapper.UserMapper;
import co.com.crediya.autenticacion.r2dbc.modules.user.repository.UserRepositoryCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {
    private final UserRepositoryCrud userRepository;
    private final UserMapper userMapper;
    private final RoleRepositoryCrud roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(userMapper.toEntity(user))
                .flatMap(this::buildUserModel);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(this::buildUserModel);
    }

    private Mono<User> buildUserModel(UserEntity userEntity) {
        return roleRepository.findById(userEntity.getIdRole())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(Role.class, userEntity.getIdRole()))))
                .map(roleMapper::toDomain)
                .map(user -> userMapper.toModel(userEntity, user));
    }
}
