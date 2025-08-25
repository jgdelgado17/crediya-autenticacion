package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserUseCase userUseCase;
    private final RoleRepository roleRepository;

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Request received to create user: {}", userRequest.getEmail());

        return roleRepository.findByName(userRequest.getRoleName())
                .doOnSuccess(role -> log.info("Found role '{}' for user creation.", userRequest.getRoleName()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Role '{}' not found.", userRequest.getRoleName());
                    return Mono.error(new IllegalArgumentException("Role not found"));
                }))
                .flatMap(role -> {
                    User userToCreate = User.builder()
                            .name(userRequest.getName())
                            .lastName(userRequest.getLastName())
                            .email(userRequest.getEmail())
                            .documentNumber(userRequest.getDocumentNumber())
                            .phoneNumber(userRequest.getPhoneNumber())
                            .baseSalary(userRequest.getBaseSalary())
                            .role(role)
                            .build();

                    return userUseCase.createUser(userToCreate)
                            .doOnSuccess(user -> log.info("User created successfully: {}", user.getEmail()))
                            .doOnError(e -> log.error("Failed to create user with email {}: {}", userRequest.getEmail(), e.getMessage()))
                            .map(ResponseEntity::ok);
                })
                .doOnError(e -> log.error("An error occurred during user creation: {}", e.getMessage()))
                .onErrorResume(Mono::error);
    }
}
