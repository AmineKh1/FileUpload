package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.Platform;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlatFormFilesRepository extends ReactiveMongoRepository<Platform, Long> {
}
