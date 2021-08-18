package ma.ynmo.cdn.services.Impl;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.services.FileUploadServices;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class FileUploadServiceImpl implements FileUploadServices {



    @Override
    public Mono<FileData> storeuploadfile(Mono<MultipartFile> file, UUID ownerID, UUID subID) {
        return file.map(
                file-> {
                }
        );
    }
    public Mono<FileData>
}
