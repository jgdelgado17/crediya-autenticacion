package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.api.dto.UserResponse;
import co.com.crediya.autenticacion.model.role.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            @RouterOperation(
                    path = "/api/v1/role",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createRole",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    operation = @Operation(
                            tags = "Role",
                            operationId = "createRole",
                            description = "Creates a new role in the system",
                            summary = "Create role",
                            requestBody = @RequestBody(
                                    description = "Role request",
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = RoleRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Role created successfully",
                                            content = @Content(schema = @Schema(implementation = Role.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data",
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
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = Error.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/role"), handler::createRole)
                .andRoute(POST("/api/v1/users"), handler::createUser);
    }
}
