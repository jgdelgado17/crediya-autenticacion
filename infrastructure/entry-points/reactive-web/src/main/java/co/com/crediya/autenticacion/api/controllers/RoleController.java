package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import lombok.RequiredArgsConstructor;
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
    private final RoleUseCase roleUseCase;

    @PostMapping
    public Mono<ResponseEntity<Role>> createRole(@RequestBody Role role) {
        return roleUseCase.createRole(role)
                .map(ResponseEntity::ok)
                .onErrorResume(Mono::error)
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().build()));
    }
}
