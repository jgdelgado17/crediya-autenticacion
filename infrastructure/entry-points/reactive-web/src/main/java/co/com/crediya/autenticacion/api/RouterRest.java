package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.LoginRequest;
import co.com.crediya.autenticacion.api.dto.LoginResponse;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createUser",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    operation = @Operation(
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
                                            responseCode = "201",
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
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "login",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    operation = @Operation(
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
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/users"), handler::createUser)
                //.andRoute(POST("/api/v1/role"), handler::createRole)
                //.andRoute(GET("/api/v1/users/{email}"), handler::findUserByEmail)
                .andRoute(POST("/api/v1/login"), handler::login);
    }
}
