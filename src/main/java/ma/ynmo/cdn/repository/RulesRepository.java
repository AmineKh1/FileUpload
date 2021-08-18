package ma.ynmo.cdn.repository;

import ma.ynmo.cdn.model.Rules;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RulesRepository extends ReactiveMongoRepository<Rules, Long> {
}
