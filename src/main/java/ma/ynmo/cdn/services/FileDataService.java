package ma.ynmo.cdn.services;


import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.repository.FileDataRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
public interface FileDataService {
    Mono<FileData> findByID(Long id);
    Mono<FileData> findbyUrl(String url);
    Flux<FileData> findAllByStatus(FileStatus status);
    Mono<FileData> save(FileData file);
    Mono<FileData> updateStatus(FileData file,FileStatus status);
    void deleteById(Long id);
}
