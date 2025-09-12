package co.com.crediya.autenticacion.model.user.gateways;

import co.com.crediya.autenticacion.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> save(User user);

    Mono<User> findByEmail(String email);

    Flux<User> findByEmailIn(List<String> emails);
}
