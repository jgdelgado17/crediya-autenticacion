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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserAdapter.class);
    private final UserRepositoryCrud userRepository;
    private final UserMapper userMapper;
    private final RoleRepositoryCrud roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Saves a user entity.
     *
     * @param user the user to save
     * @return a mono emitting the saved user
     */
    @Override
    @Transactional
    public Mono<User> save(User user) {
        log.info("Saving user entity for email: {}", user.getEmail());
        return userRepository.save(userMapper.toEntity(user))
                .flatMap(this::buildUserModel)
                .doOnSuccess(u -> log.info("User entity saved successfully with ID: {}", u.getIdUser()))
                .doOnError(e -> log.error("Failed to save user entity for email {}: {}", user.getEmail(), e.getMessage()));
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to find
     * @return a mono emitting the found user, or empty if not found
     */
    @Override
    public Mono<User> findByEmail(String email) {
        log.info("Finding user entity by email: {}", email);
        return userRepository.findByEmail(email)
                .flatMap(this::buildUserModel)
                .doOnError(e -> log.error("Failed to find user by email {}: {}", email, e.getMessage()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found for email: {}", email);
                    return Mono.empty();
                }));
    }

    private Mono<User> buildUserModel(UserEntity userEntity) {
        log.debug("Building user model for user entity with ID: {}", userEntity.getIdUser());
        return roleRepository.findById(userEntity.getIdRole())
                .switchIfEmpty(Mono.defer(() -> {
                    String errorMessage = ErrorMessages.notFoundMessage(Role.class, userEntity.getIdRole());
                    log.error("Role not found for user ID {}: {}", userEntity.getIdUser(), errorMessage);
                    return Mono.error(new IllegalArgumentException(errorMessage));
                }))
                .doOnSuccess(roleEntity -> log.debug("Role entity found for user ID {}: {}", userEntity.getIdUser(), roleEntity.getNames()))
                .map(roleMapper::toDomain)
                .map(role -> userMapper.toModel(userEntity, role))
                .doOnSuccess(user -> log.debug("User model built successfully for user ID: {}", user.getIdUser()))
                .doOnError(e -> log.error("Failed to build user model for user ID {}: {}", userEntity.getIdUser(), e.getMessage()));
    }
}
