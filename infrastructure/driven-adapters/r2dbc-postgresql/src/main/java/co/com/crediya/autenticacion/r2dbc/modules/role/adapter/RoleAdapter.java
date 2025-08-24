package co.com.crediya.autenticacion.r2dbc.modules.role.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.r2dbc.modules.role.mapper.RoleMapper;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleAdapter implements RoleRepository {

    private final RoleRepositoryCrud repository;
    private final RoleMapper mapper;

    @Override
    public Mono<Role> save(Role role) {
        return repository.save(mapper.toEntity(role))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Role> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }
}
