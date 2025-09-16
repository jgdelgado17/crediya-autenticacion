package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.config.PathsConfig;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final PathsConfig pathsConfig;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createUser",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    }
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "login",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    }
            ),
            @RouterOperation(
                    path = "/api/v1/users/{email}",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "findUserByEmail",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    }
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(pathsConfig.users()), handler::createUser)
                //.andRoute(POST(pathsConfig.roles()), handler::createRole)
                .andRoute(GET(pathsConfig.findUserByEmail()), handler::findUserByEmail)
                .andRoute(GET(pathsConfig.users()), handler::findUserByEmailIn)
                .andRoute(POST(pathsConfig.login()), handler::login);
    }
}
