package ma.ynmo.cdn.controller;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.services.FileUploadServices;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("store")
@AllArgsConstructor
public class StoreUploadFile {

    private final FileUploadServices fileUploadService;


    // for multiple files use Flux<PartFile>
    // @see: https://vinsguru.medium.com/spring-webflux-file-upload-f6e3f3d3f5e1
    @PostMapping("{subID}/{ownerID}")
    public Mono<FileData> uploadFile(
            @RequestPart("file") Mono<FilePart> multipartFile,
            @PathVariable("subID") UUID subID,
            @PathVariable("ownerID") UUID ownerID)
    {
        return fileUploadService.storeuploadImage(multipartFile, ownerID, subID);
    }

}
