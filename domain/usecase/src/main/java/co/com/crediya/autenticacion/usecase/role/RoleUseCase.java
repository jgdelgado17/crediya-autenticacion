package co.com.crediya.autenticacion.usecase.role;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase {
    private final RoleRepository roleRepository;

    /**
     * Creates a new role, given a role with name.
     *
     * <p>If a role with the given name already exists, the method will return an error.
     *
     * @param role the role to create
     * @return a mono emitting the created role
     */
    public Mono<Role> createRole(Role role) {
        return RoleValidator.validate(role)
                .flatMap(validRole ->
                        roleRepository.findByName(validRole.getNames())
                                .flatMap(existingRole ->
                                        Mono.error(new IllegalArgumentException("Role already exists")).cast(Role.class)
                                )
                                .switchIfEmpty(roleRepository.save(validRole))
                );
    }

}
