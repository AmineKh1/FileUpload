package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.FileData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface FileDataRepository extends ReactiveCrudRepository<FileData, Long > {
}
