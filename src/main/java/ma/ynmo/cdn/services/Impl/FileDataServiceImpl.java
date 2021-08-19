package ma.ynmo.cdn.services.Impl;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FileDataServiceImpl implements FileDataService {
    @Override
    public Mono<FileData> findByID(Long id) {
        return null;
    }

    @Override
    public Mono<FileData> findbyUrl(String url) {
        return null;
    }

    @Override
    public Flux<FileData> findAllByStatus(FileStatus status) {
        return null;
    }

    @Override
    public Mono<FileData> save(FileData file) {
        return null;
    }

    @Override
    public Mono<FileData> updateStatus(FileData file, FileStatus status) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
