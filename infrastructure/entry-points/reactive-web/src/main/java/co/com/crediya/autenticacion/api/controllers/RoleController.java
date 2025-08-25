package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
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
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleUseCase roleUseCase;

    @PostMapping
    public Mono<ResponseEntity<Role>> createRole(@RequestBody Role role) {
        log.info("Request received to create role: {}", role.getNames());
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
