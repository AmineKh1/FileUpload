package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.Store;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StoreService {

    Mono<FileData> verifyFile(FileData fileData);
    Mono<Store> findStoreByOwnerID(UUID ownerId);
    Mono<Store> findStoreBySubID(UUID subID);
}
