package co.com.crediya.autenticacion.usecase.user;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Creates a new user, given a user with email and role.
     *
     * <p>If a user with the given email already exists, the method will return an error.
     * If the given role does not exist, the method will return an error.
     *
     * @param user the user to create
     * @return a mono emitting the created user
     */
    public Mono<User> createUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser ->
                        Mono.error(new IllegalArgumentException("User with email " + user.getEmail() + " already exists")).cast(User.class)
                )
                .switchIfEmpty(
                        roleRepository.findByName(user.getRole().getNames())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(Role.class, user.getRole().getNames()))))
                                .flatMap(role -> {
                                    user.setRole(role);
                                    return userRepository.save(user);
                                })
                );
    }
}
