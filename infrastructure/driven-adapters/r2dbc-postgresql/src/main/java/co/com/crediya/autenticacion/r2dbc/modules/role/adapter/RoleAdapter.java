package co.com.crediya.autenticacion.r2dbc.modules.role.adapter;

import co.com.crediya.autenticacion.model.role.Role;
import co.com.crediya.autenticacion.model.role.gateways.RoleRepository;
import co.com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.autenticacion.r2dbc.modules.role.data.RoleEntity;
import co.com.crediya.autenticacion.r2dbc.modules.role.repository.RoleRepositoryCrud;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleAdapter
        extends ReactiveAdapterOperations<Role, RoleEntity, Integer, RoleRepositoryCrud>
        implements RoleRepository {

    private static final Logger log = LoggerFactory.getLogger(RoleAdapter.class);

    public RoleAdapter(RoleRepositoryCrud repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
        this.repository = repository;
    }

    @Override
    public Mono<Role> save(Role role) {
        log.info("Saving role entity: {}", role.getNames());
        return super.save(role)
                .doOnSuccess(r -> log.info("Role entity saved successfully: {}", r.getNames()))
                .doOnError(e -> log.error("Error saving role entity: {}", e.getMessage()));
    }

    @Override
    public Mono<Role> findByName(String name) {
        log.info("Finding role entity by name: {}", name);
        return repository.findByNames(name)
                .map(super::toEntity)
                .doOnSuccess(r -> {
                    if (r != null) {
                        log.info("Role entity found: {}", r.getNames());
                    } else {
                        log.info("No role entity found with name: {}", name);
                    }
                })
                .doOnError(e -> log.error("Error finding role entity by name: {}", e.getMessage()));
    }
}
