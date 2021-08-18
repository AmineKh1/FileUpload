package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.FileData;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.nio.channels.MulticastChannel;
import java.util.UUID;

public interface FileUploadServices {
    Mono<FileData> storeuploadfile(Mono<MultipartFile> file, UUID ownerID, UUID subID);
}
