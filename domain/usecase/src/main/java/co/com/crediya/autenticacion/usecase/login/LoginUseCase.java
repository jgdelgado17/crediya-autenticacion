package co.com.crediya.autenticacion.usecase.login;

import co.com.crediya.autenticacion.model.securityports.LoginPort;
import co.com.crediya.autenticacion.model.securityports.PasswordEncoderPort;
import co.com.crediya.autenticacion.model.shared.exception.AuthenticationUnauthorizedException;
import co.com.crediya.autenticacion.model.shared.exception.ErrorMessages;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase implements LoginPort {
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * Validates the given user credentials.
     *
     * <p>This method first checks if a user with the given email exists. If not, it returns an error.
     * <p>If the user exists, it checks if the given passcode matches the stored passcode. If not, it returns an error.
     * <p>If the passcode matches, it returns the user.
     *
     * @param email the email of the user to validate
     * @param password the passcode to validate
     * @return a mono emitting the validated user, or an error if validation fails
     */
    @Override
    public Mono<User> validateCredentials(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationUnauthorizedException(ErrorMessages.notFoundMessage(User.class, email))))
                .flatMap(user ->
                        passwordEncoderPort.matches(password, user.getPassword())
                                .flatMap(match -> Boolean.TRUE.equals(match)
                                        ? Mono.just(user)
                                        : Mono.error(new AuthenticationUnauthorizedException("Invalid passcode")))
                );
    }
}
