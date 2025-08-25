package co.com.crediya.autenticacion.api.controllers;

import co.com.crediya.autenticacion.api.dto.UserRequest;
import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.model.user.User;
import co.com.crediya.autenticacion.usecase.role.RoleUseCase;
import co.com.crediya.autenticacion.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
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
    private final UserUseCase userUseCase;
    private final RoleRepository roleRepository;

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody UserRequest userRequest) {
        return roleRepository.findByName(userRequest.getRoleName())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
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
                            .map(ResponseEntity::ok)
                            .onErrorResume(Mono::error)
                            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
                });
    }
}
