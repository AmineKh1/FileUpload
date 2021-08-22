package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.repository.FileDataRepository;
import ma.ynmo.cdn.services.FileDataService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class FileDataServiceImpl implements FileDataService {
    private final FileDataRepository fileDataRepository;
    @Override
    public Mono<FileData> findByID(Long id) {
        return fileDataRepository.findById(id);
    }

    @Override
    public Mono<FileData> findbyUrl(String url) {
        return null;
    }

    @Override
    public Flux<FileData> findAllByStatus(FileStatus status) {
        return fileDataRepository.findAllByStatus(status);
    }

    @Override
    public Mono<FileData> save(FileData file) {
        return fileDataRepository.save(file);
    }

    @Override
    public Mono<FileData> updateStatus(FileData file, FileStatus status) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
