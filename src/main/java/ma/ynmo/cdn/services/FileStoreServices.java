package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.FileData;

import java.util.List;

public interface FileStoreServices {
    FileData save(FileData obj);
    FileData findById(Long id);
    FileData findByStoreFiles(Long id);
    List<FileData> findAll();
    void delete(Long id);
}
