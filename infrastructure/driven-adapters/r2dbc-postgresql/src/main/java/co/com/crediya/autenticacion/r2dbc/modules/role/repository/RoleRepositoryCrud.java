package co.com.crediya.autenticacion.r2dbc.modules.role.repository;

import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleRepositoryCrud extends ReactiveCrudRepository<RoleEntity, Integer> {
    Mono<RoleEntity> findByName(String name);
}
