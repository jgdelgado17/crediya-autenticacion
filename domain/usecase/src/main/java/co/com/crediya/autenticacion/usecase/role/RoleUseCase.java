package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase {
    private final RoleRepository roleRepository;

    public Mono<Role> createRole(Role role) {
        return RoleValidator.validate(role)
                .flatMap(validRole ->
                        roleRepository.findByName(validRole.getName())
                                .flatMap(existingRole ->
                                        Mono.error(new IllegalArgumentException("Role already exists")).cast(Role.class)
                                )
                                .switchIfEmpty(roleRepository.save(validRole))
                );
    }

}
