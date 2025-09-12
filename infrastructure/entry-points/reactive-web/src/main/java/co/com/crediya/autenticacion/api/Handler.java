package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.*;
import co.com.crediya.autenticacion.api.exceptionHandler.GlobalErrorWebExceptionHandler;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.api.mapper.UserDataMapper;
import co.com.crediya.autenticacion.model.securityports.JwtPort;
import co.com.crediya.autenticacion.usecase.login.LoginUseCase;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.util.Collections;
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

    @Operation(
            summary = "Create user",
            description = "Creates a new user in the system",
            tags = "User",
            operationId = "createUser",
            requestBody = @RequestBody(
                    description = "User request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
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

    @Operation(
            summary = "Find user by email",
            description = "Finds a user by email in the system",
            tags = "User",
            operationId = "findUserByEmail",
            parameters = {
                    @Parameter(
                            name = "email",
                            in = ParameterIn.PATH,
                            description = "User's email to search for",
                            required = true,
                            example = "user@example.com"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = GlobalErrorWebExceptionHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = GlobalErrorWebExceptionHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = GlobalErrorWebExceptionHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = GlobalErrorWebExceptionHandler.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = GlobalErrorWebExceptionHandler.class))
                    )
            }
    )
    public Mono<ServerResponse> findUserByEmail(ServerRequest request) {
        String email = request.pathVariable("email");
        log.info("Request received to find user by email: {}", email);
        return userUseCase.findByEmail(email)
                .map(UserDataMapper::toUserResponse)
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSuccess(user -> log.info("User retrieved successfully"))
                .doOnError(e -> log.error("Failed to retrieve user: {}", e.getMessage()));
    }

    public Mono<ServerResponse> findUserByEmailIn(ServerRequest request) {
        log.info("Request received to find users by emails");

        List<String> emails = request.queryParam("emails")
                .map(s -> List.of(s.split(",")))
                .orElse(Collections.emptyList());

        if (emails.isEmpty()) {
            log.warn("No emails provided in the query parameter.");
            return ServerResponse.ok().bodyValue(Collections.emptyList());
        }

        return userUseCase.findByEmailIn(emails)
                .map(UserDataMapper::toUserResponse)
                .collectList()
                .flatMap(userResponses -> {
                    if (userResponses.isEmpty()) {
                        log.info("No users found for the given emails.");
                        return ServerResponse.ok().bodyValue(Collections.emptyList());
                    }
                    return ServerResponse.ok().bodyValue(userResponses);
                })
                .doOnSuccess(serverResponse -> log.info("Users retrieved successfully"))
                .doOnError(e -> log.error("Failed to retrieve users: {}", e.getMessage()));
    }

    @Operation(
            summary = "Login user",
            description = "Logs in a user",
            tags = "User",
            operationId = "login",
            requestBody = @RequestBody(
                    description = "Login request",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in successfully",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
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
