package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.api.mapper.UserDataMapper;
import co.com.crediya.autenticacion.model.role.Role;
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
                .flatMap(userRequest -> {
                    var userToCreate = UserDataMapper.toUser(userRequest);
                    var role = Role.builder().names(userRequest.getRoleName()).build();
                    userToCreate.setRole(role);
                    return userUseCase.createUser(userToCreate);
                })
                .map(UserDataMapper::toUserResponse)
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse))
                .doOnSuccess(user -> log.info("User created successfully"))
                .doOnError(e -> log.error("Failed to create user: {}", e.getMessage()));
    }
}
