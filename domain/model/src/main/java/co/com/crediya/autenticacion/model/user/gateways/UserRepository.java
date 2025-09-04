package co.com.crediya.autenticacion.model.user.gateways;

import co.com.crediya.autenticacion.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);

    Mono<User> findByEmail(String email);
}
