package co.com.crediya.autenticacion.usecase.user;

import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;

    /**
     * Create a new user, given a {@link User} object.
     *
     * If a user with the same email already exists, an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param user The user to create.
     * @return A {@link Mono} that emits the created user.
     */
    public Mono<User> createUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .switchIfEmpty(userRepository.save(user))
                .flatMap(existingUser ->
                        Mono.error(new IllegalArgumentException("User with email " + user.getEmail() + " already exists")).cast(User.class)
                );
    }
}
