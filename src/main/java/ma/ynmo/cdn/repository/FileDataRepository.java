package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface FileDataRepository extends ReactiveCrudRepository<FileData, Long > {
    Flux<FileData> findAllByStatus(FileStatus status);

}
