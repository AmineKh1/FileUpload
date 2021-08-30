package ma.ynmo.cdn.controller;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.services.FileUploadServices;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("store")
@AllArgsConstructor
public class StoreUploadFileController {

    private final FileUploadServices fileUploadService;


    // for multiple files use Flux<PartFile>
    // @see: https://vinsguru.medium.com/spring-webflux-file-upload-f6e3f3d3f5e1
    @PostMapping("{subID}/{ownerID}")
    public Mono<FileData> uploadFile(
            @RequestPart("file") Mono<FilePart> multipartFile,
            @PathVariable("subID") UUID subID,
            @PathVariable("ownerID") UUID ownerID,
            @RequestHeader("Content-Length") long contentLength,
            @RequestHeader("Content-Type") String content_type,
            @Value("${temp_dir:/tmp}") File in,
            AmqpTemplate template,
            @Value("${application.amqp.processImage.extchange:cdnExtchange}") String exchange,
            @Value("${application.amqp.cdn.queue:cdnRoutingKey}") String queue)
    {
        System.out.println(content_type);
        return fileUploadService.storeuploadImage(multipartFile,
                ownerID, subID,
                contentLength, in,
                template,
                exchange,
                queue);
    }

}
