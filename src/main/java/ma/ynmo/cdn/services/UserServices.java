package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserServices {
    Mono<User> save(User obj);
    Mono<User> findById(Long id);
    Mono<User> findByEmail(String email);
    Flux<User> findAll();
    void delete(Long id);
}
