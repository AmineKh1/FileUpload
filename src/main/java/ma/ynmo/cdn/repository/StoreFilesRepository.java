package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.StoreFiles;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StoreFilesRepository extends ReactiveMongoRepository<StoreFiles, Long> {
}
