package ma.ynmo.cdn.services.Impl;

import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.exception.EntityNotFoundException;
import ma.ynmo.cdn.exception.ErrorCodes;
import ma.ynmo.cdn.exception.InvalidEntityException;
import ma.ynmo.cdn.model.Platform;
import ma.ynmo.cdn.repository.PlatFormFilesRepository;
import ma.ynmo.cdn.services.PlatFormFilesServices;
import ma.ynmo.cdn.validators.PlatFormFilesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlatFormFilesServiceImpl implements PlatFormFilesServices {
    private PlatFormFilesRepository platFormFilesRepository;
    @Autowired
    public PlatFormFilesServiceImpl(PlatFormFilesRepository platFormFilesRepository) {

        this.platFormFilesRepository = platFormFilesRepository;
    }
    @Override
    public Mono<Platform> save(Platform obj) {
//        List<String> errors= PlatFormFilesValidator.validate(obj);
//
//        if(!errors.isEmpty()) {
//            log.error("PLAT FORM IS NOT VALID", obj);
//            throw new InvalidEntityException("Plat Form is not valid", ErrorCodes.PLATFORM_NOT_VALID,errors);
//        }
        return platFormFilesRepository.save(obj);
    }

    @Override
    public Mono<Platform> findById(Long id) {
        if(id==null) {
            log.error("PLAT FORM ID IS NULL");
            return null;
        }
        Mono<Platform> platForm= platFormFilesRepository.findById(id);
        return Optional.of(platForm).orElseThrow(() -> new EntityNotFoundException("No user id ="+id+"exist in DB",ErrorCodes.PLATFORM_NOT_FOUND));
    }

    @Override
    public Flux<Platform> findAll() {
        return platFormFilesRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if(id==null) {
            log.error("PLAT FORM IS NULL");
            return;
        }
        platFormFilesRepository.deleteById(id);
    }
}
