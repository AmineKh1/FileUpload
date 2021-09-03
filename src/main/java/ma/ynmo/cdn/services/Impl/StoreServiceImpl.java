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
        System.out.println(fileData);
        return findStoreBySubID(fileData.getSubID())
                .switchIfEmpty(
                        Mono.error(new EntityNotFoundException(
                                String.format("no sotre with id %s",
                                        fileData.getSubID()))))
                .flatMap(store -> {
                    store.setCurrentSize(store.getCurrentSize() + fileData.getSize());
                    return storeRepository.save(store);
                })
                        .flatMap( store ->
                                checkSizeAndGenerateURl(store, fileData));
    }

    /// set url to client/ownerid/stores/subid/images/
    // set filename to RandmonUUID_originalFilename
    private Mono<FileData> checkSizeAndGenerateURl(Store store, FileData fileData)
    {

        return Mono.just(fileData)
                .flatMap(fi->{
                    if (store.getCurrentSize() > store.getMaxSize()) {
                        store.setCurrentSize(store.getCurrentSize() - fi.getSize());
                        storeRepository.save(store).subscribe(System.out::println);
                        return Mono.error(new IllegalArgumentException("no more space"));
                    }
                        log.info(fileData.getType());
                 //   if (!types.contains(fileData.getType()))
                   //     return Mono.error(new IllegalArgumentException("invalid image Type"));
                    if (fi.isImage())
                        fi.setUrl(String.format("clients/%s/stores/%s/images/", store.getOwnerID(), store.getSubID()));
                     else
                        fi.setUrl(String.format("clients/%s/stores/%s/files/", store.getOwnerID(), store.getSubID()));


                    fi.setBaseName(String.format("%s.%s", UUID.randomUUID(),fi.getExtension()));
                    return Mono.just(fi);
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
