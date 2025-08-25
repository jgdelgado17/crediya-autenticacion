package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.api.dto.RoleRequest;
import co.com.crediya.autenticacion.api.mapper.RoleDataMapper;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
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
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Endpoints for managing roles")
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleUseCase roleUseCase;

    @PostMapping
    @Operation(summary = "Create Role", description = "Creates a new role in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<Role>> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        log.info("Request received to create role: {}", roleRequest.getNames());
        Role role = RoleDataMapper.toRole(roleRequest);
        return roleUseCase.createRole(role)
                .map(ResponseEntity::ok)
                .doOnSuccess(r -> log.info("Role created successfully: {}", role.getNames()))
                .onErrorResume(e -> {
                    log.error("Failed to create role: {}", e.getMessage());
                    return Mono.error(e);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }
}
