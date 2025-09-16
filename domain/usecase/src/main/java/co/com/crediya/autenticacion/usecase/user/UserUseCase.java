package co.com.crediya.autenticacion.usecase.user;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.securityports.PasswordEncoderPort;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import co.com.crediya.autenticacion.model.shared.exception.RecordNotFoundException;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * Creates a new user. The user is first validated, then the user with the given email is searched in the database.
     * If the user exists, an exception is thrown. If the user does not exist, the role is searched in the database,
     * and if the role does not exist, an exception is thrown. If the role exists, the user's password is encoded,
     * the user is saved in the database, and the user is returned.
     *
     * @param user the user to create
     * @return a mono emitting the created user
     */
    public Mono<User> createUser(User user) {
        return ValidateUser.validateUser(user.getEmail())
                .flatMap(email ->
                        userRepository.findByEmail(user.getEmail())
                                .flatMap(existingUser ->
                                        Mono.error(new IllegalArgumentException("User with email " + user.getEmail() + " already exists")).cast(User.class)
                                )
                                .switchIfEmpty(
                                        roleRepository.findByName(user.getRole().getNames())
                                                .switchIfEmpty(Mono.error(new RecordNotFoundException(ErrorMessages.notFoundMessage(Role.class, user.getRole().getNames()))))
                                                .flatMap(role -> passwordEncoderPort.encode(user.getPassword())
                                                        .flatMap(encodedPassword -> {
                                                            user.setRole(role);
                                                            user.setPassword(encodedPassword);
                                                            return userRepository.save(user);
                                                        })
                                                )
                                )

                );
    }

    public Mono<User> findByEmail(String email) {
        return ValidateUser.validateUser(email)
                .flatMap(userRepository::findByEmail);
    }

    public Flux<User> findByEmailIn(List<String> emails) {
        return userRepository.findByEmailIn(emails);
    }
}
