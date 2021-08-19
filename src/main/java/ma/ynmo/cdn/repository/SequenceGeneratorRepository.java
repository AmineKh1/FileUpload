package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.SequenceGenerator;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SequenceGeneratorRepository extends ReactiveCrudRepository<SequenceGenerator, String> {
}
