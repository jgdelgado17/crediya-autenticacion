package co.com.crediya.autenticacion.r2dbc.modules.user.repository;

import co.com.crediya.autenticacion.r2dbc.modules.user.data.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepositoryCrud extends ReactiveCrudRepository<UserEntity, Integer> {
    Mono<UserEntity> findByEmail(String email);
    Flux<UserEntity> findByEmailIn(List<String> emails);
}
