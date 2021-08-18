package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.Platform;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlatFormFilesServices {
    Mono<Platform> save(Platform obj);
    Mono<Platform> findById(Long id);
    Flux<Platform> findAll();
    void delete(Long id);
}
