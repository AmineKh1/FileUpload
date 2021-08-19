package ma.ynmo.cdn.services;

import reactor.core.publisher.Mono;

public interface SequenceGeneratorService {
    Mono<Long> generateNewId(String seqName);
}
