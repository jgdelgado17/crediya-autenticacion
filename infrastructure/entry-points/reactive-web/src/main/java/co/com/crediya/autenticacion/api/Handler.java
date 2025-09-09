package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.LoginRequest;
import co.com.crediya.autenticacion.api.dto.LoginResponse;
import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.api.mapper.UserDataMapper;
import co.com.crediya.autenticacion.model.securityports.JwtPort;
import co.com.crediya.autenticacion.model.shared.exception.RecordNotFoundException;
import co.com.crediya.autenticacion.usecase.login.LoginUseCase;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final Validator validator;
    private final RoleUseCase roleUseCase;
    private final UserUseCase userUseCase;
    private final LoginUseCase loginUseCase;
    private final JwtPort jwtPort;

    public Mono<ServerResponse> createRole(ServerRequest request) {
        log.info("Request received to create role");
        return request.bodyToMono(RoleRequest.class)
                .doOnNext(roleRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(roleRequest, "roleRequest");
                    validator.validate(roleRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .map(RoleDataMapper::toRole)
                .flatMap(roleUseCase::createRole)
                .flatMap(role -> ServerResponse.ok().bodyValue(role))
                .doOnSuccess(r -> log.info("Role created successfully"))
                .doOnError(e -> log.error("Failed to create role: {}", e.getMessage()));
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        log.info("Request received to create user");
        return request.bodyToMono(UserRequest.class)
                .doOnSuccess(userRequest -> log.info("User request received for: {}", userRequest.getEmail()))
                .doOnNext(userRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userRequest, "userRequest");
                    validator.validate(userRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .map(UserDataMapper::toUser)
                .flatMap(userUseCase::createUser)
                .map(UserDataMapper::toUserResponse)
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse))
                .doOnSuccess(user -> log.info("User created successfully"))
                .doOnError(e -> log.error("Failed to create user: {}", e.getMessage()));
    }

    public Mono<ServerResponse> findUserByEmail(ServerRequest request) {
        String email = request.pathVariable("email");
        log.info("Request received to find user by email: {}", email);
        return userUseCase.findByEmail(email)
                .map(UserDataMapper::toUserResponse)
                .switchIfEmpty(Mono.error(new RecordNotFoundException("User not found")))
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse))
                .doOnSuccess(user -> log.info("User retrieved successfully"))
                .doOnError(e -> log.error("Failed to retrieve user: {}", e.getMessage()));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        log.info("Request received to login");
        return request.bodyToMono(LoginRequest.class)
                .doOnSuccess(loginRequest -> log.info("Login request received for: {}", loginRequest.getEmail()))
                .doOnNext(loginRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(loginRequest, "loginRequest");
                    validator.validate(loginRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .flatMap(dto -> loginUseCase.validateCredentials(dto.getEmail(), dto.getPassword()))
                .flatMap(user -> jwtPort.generateToken(user.getEmail(), user.getRole().getNames())
                        .map(token -> new LoginResponse(user, token)))
                .flatMap(loginResponse -> ServerResponse.ok().bodyValue(loginResponse))
                .doOnSuccess(loginResponse -> log.info("Login successful"))
                .doOnError(e -> log.error("Failed to login: {}", e.getMessage()));
    }
}
