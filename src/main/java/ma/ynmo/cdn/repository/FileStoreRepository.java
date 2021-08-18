package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.FileData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FileStoreRepository extends ReactiveMongoRepository<FileData, Long> {
}
