package co.com.crediya.autenticacion.model.role.gateways;

import co.com.crediya.autenticacion.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> save(Role role);
    Mono<Role> findByName(String name);
}
