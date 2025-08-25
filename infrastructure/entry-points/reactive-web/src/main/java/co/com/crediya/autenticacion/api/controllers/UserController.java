package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.dto.UserResponse;
import co.com.crediya.autenticacion.api.mapper.UserDataMapper;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "Endpoints for managing users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserUseCase userUseCase;
    private final RoleRepository roleRepository;

    @PostMapping
    @Operation(summary = "Create User", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Request received to create user: {}", userRequest.getEmail());

        return roleRepository.findByName(userRequest.getRoleName())
                .doOnSuccess(role -> log.info("Found role '{}' for user creation.", userRequest.getRoleName()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Role '{}' not found.", userRequest.getRoleName());
                    return Mono.error(new IllegalArgumentException("Role not found"));
                }))
                .flatMap(role -> {
                    User userToCreate = UserDataMapper.toUser(userRequest);
                    userToCreate.setRole(role);

                    return userUseCase.createUser(userToCreate)
                            .doOnSuccess(user -> log.info("User created successfully: {}", user.getEmail()))
                            .doOnError(e -> log.error("Failed to create user with email {}: {}", userRequest.getEmail(), e.getMessage()))
                            .map(UserDataMapper::toUserResponse)
                            .map(ResponseEntity::ok);
                })
                .doOnError(e -> log.error("An error occurred during user creation: {}", e.getMessage()))
                .onErrorResume(Mono::error);
    }
}
