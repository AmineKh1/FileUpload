package ma.ynmo.cdn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.services.FileUploadServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("store")

public class StoreUploadFileController {

    private final FileUploadServices fileUploadService;
    private  final ObjectMapper objectMapper;

private final MessageChannel messageChannel;

 private  final    MessageChannel filesChannel;

    public StoreUploadFileController(FileUploadServices fileUploadService,
                                     ObjectMapper objectMapper,
                                     @Qualifier("imageToProccessChannel") MessageChannel messageChannel,
                                    @Qualifier("filesToS3Channel") MessageChannel filesChannel) {
        this.fileUploadService = fileUploadService;
        this.objectMapper = objectMapper;
        this.messageChannel = messageChannel;
        this.filesChannel = filesChannel;
    }

    // for multiple files use Flux<PartFile>
    // @see: https://vinsguru.medium.com/spring-webflux-file-upload-f6e3f3d3f5e1
    @PostMapping("{subID}/{ownerID}")
    public Mono<FileData> uploadFile(
            @RequestPart("file") Mono<FilePart> multipartFile,
            @PathVariable("subID") UUID subID,
            @PathVariable("ownerID") UUID ownerID,
            @RequestHeader("Content-Length") long contentLength,
            @RequestHeader("Content-Type") String content_type,
            @Value("${temp_dir:/tmp}") File in)
    {
        return fileUploadService.storeuploadFile(multipartFile,
                ownerID, subID,
                contentLength, in,
                messageChannel,
                filesChannel,

                objectMapper
                );
    }

}
