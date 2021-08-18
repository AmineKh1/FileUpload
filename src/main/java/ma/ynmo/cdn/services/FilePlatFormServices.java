package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.FilePlatForm;

import java.util.List;

public interface FilePlatFormServices {
    FilePlatForm save(FilePlatForm obj);
    FilePlatForm findById(Long id);
    FilePlatForm findByPlatFormFiles(Long id);
    List<FilePlatForm> findAll();
    void delete(Long id);
}
