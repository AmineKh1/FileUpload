package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.FilePlatForm;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FilePlatFormRepository extends ReactiveMongoRepository<FilePlatForm, Long> {
}
