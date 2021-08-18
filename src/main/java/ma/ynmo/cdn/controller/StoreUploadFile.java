package ma.ynmo.cdn.controller;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.services.FileUploadServices;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("store")
@AllArgsConstructor
public class StoreUploadFile {

    private final FileUploadServices fileUploadService;
    @PostMapping("{subID}/{ownerID}")
    public Mono<FileData> uploadFile(
            @RequestPart("file") Mono<MultipartFile> multipartFile,
            @PathVariable("subID") UUID subID,
            @PathVariable("ownerID") UUID ownerID)
    {
        return fileUploadService.storeuploadfile(multipartFile, ownerID, subID);
    }

}
