package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.Store;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StoreRepository extends ReactiveCrudRepository<Store, UUID> {
    Mono<Store> findByOwnerID(UUID ownerId);
}
