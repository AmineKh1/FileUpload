package ma.ynmo.cdn.services;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.config.S3ClientConfigurarionProperties;
import ma.ynmo.cdn.model.FileData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@AllArgsConstructor
public class UploadFileService {
    private final S3ClientConfigurarionProperties s3Properties;
    private final FileDataService fileDataService;
    private final S3AsyncClient s3Client;

    public Mono<FileData> uploadFile(File file) throws IOException {

        var id  =  Long.valueOf(file.getName().split("\\.")[0]);
        return fileDataService.findByID(id)
                        .flatMap(fileData->
                       Mono.fromFuture(uploadToS3(fileData,file)))
                .flatMap(putObjectResponse ->
                {
                    System.out.println(putObjectResponse);
                    return fileDataService.findByID(id);
                });
    }

    public CompletableFuture<PutObjectResponse> uploadToS3(
                                                           FileData fileData,
                                                           File file) {

       var  mediaType = fileData.getType();
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("filename", file.getName());
        metadata.put("path", fileData.getUrl());
    return    s3Client
                .putObject(PutObjectRequest.builder()
                                .bucket(s3Properties.getBucket())
                        // the problem came out from  the content-length
                        // which is not equal to the  putObjectRequeset size
                                 .metadata(metadata)
                                .key(fileData.getUrl()+ file.getName())
                                .contentType(mediaType)
                                .build(),
                        file.toPath() // the problem was here
                        );
    }


}
