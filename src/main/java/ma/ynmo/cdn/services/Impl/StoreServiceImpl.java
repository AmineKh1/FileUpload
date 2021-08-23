package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.exception.EntityNotFoundException;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.Store;
import ma.ynmo.cdn.repository.StoreRepository;
import ma.ynmo.cdn.services.StoreService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    @Override
    public Mono<FileData> verifyFile(FileData fileData) {
        return findStoreBySubID(fileData.getSubID())
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                String.format("no sotre with id %s",
                                        fileData.getSubID()))))
                        .flatMap( store ->
                                checkSizeAndGenerateURl(store, fileData));
    }

    /// set url to client/ownerid/stores/subid/images/
    // set filename to RandmonUUID_originalFilename
    private Mono<FileData> checkSizeAndGenerateURl(Store store, FileData fileData)
    {
        var types = List.of(MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_PNG_VALUE);
        return Mono.just(fileData)
                .flatMap(fi->{
                    if (store.getCurrentSize() + fileData.getSize() > store.getMaxSize())
                        return Mono.error(new IllegalArgumentException("no more space"));
                    log.info(fileData.getType());
                 //   if (!types.contains(fileData.getType()))
                   //     return Mono.error(new IllegalArgumentException("invalid image Type"));
                    fileData.setUrl(String.format("clients/%s/stores/%s/images/",store.getOwnerID(),store.getSubID()));
                    fileData.setBaseName(String.format("%s_%s", UUID.randomUUID(), fileData.getBaseName()));
                    return Mono.just(fileData);
                });
    }

    @Override
    public Mono<Store> findStoreByOwnerID(UUID ownerId) {
        return storeRepository.findByOwnerID(ownerId);
    }

    @Override
    public Mono<Store> findStoreBySubID(UUID subID) {
        return storeRepository.findById(subID);
    }

    @Override
    public Mono<Store> save(Store store) {
        return storeRepository.save(store);
    }
}
