package co.com.crediya.autenticacion.r2dbc.modules.role.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleAdapter
        extends ReactiveAdapterOperations<Role, RoleEntity, Integer, RoleRepositoryCrud>
        implements RoleRepository {

    public RoleAdapter(RoleRepositoryCrud repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
        this.repository = repository;
    }

    @Override
    public Mono<Role> save(Role role) {
        return super.save(role);
    }

    @Override
    public Mono<Role> findByName(String name) {
        return repository.findByNames(name)
                .map(super::toEntity);
    }
}
