package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.SequenceGenerator;
import ma.ynmo.cdn.repository.SequenceGeneratorRepository;
import ma.ynmo.cdn.services.SequenceGeneratorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {
    private SequenceGeneratorRepository sequenceGeneratorRepository;


    @Override
    public Mono<Long> generateNewId(String seqName) {
            return sequenceGeneratorRepository.findById(seqName)
                    .switchIfEmpty(
                            sequenceGeneratorRepository
                                    .save(
                                            new SequenceGenerator(seqName, 0L)))

                    .flatMap(
                            dbSequence -> {
                                dbSequence.increment();
                                return  sequenceGeneratorRepository.save(dbSequence);
                            }

                    ).flatMap(dbSequence -> Mono.just(dbSequence.getSeq() -1));

    }
}
