package ma.ynmo.cdn.services;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.config.S3ClientConfigurarionProperties;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
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
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class UploadFileService {
    private final S3ClientConfigurarionProperties s3Properties;
    private final FileDataService fileDataService;
    private final S3AsyncClient s3Client;

    public Mono<FileData> uploadFile(FileData f, TreeMap<String,?> map) throws IOException {

//        var id  =  Long.valueOf(file.getName().split("\\.")[0]);

         return Flux.fromStream( map.entrySet().stream())
                 .flatMap(stringEntry ->
                         {
                             var n = f.getBaseName().split("\\.")[0] + "_" + stringEntry.getKey()
                                     .split("_")[1];
                             f.getNames().add(n);
                       return    Mono.fromFuture(uploadToS3(n,f,(byte[]) stringEntry.getValue()));
                         })
                 .last()
                 .flatMap(o ->
                         {
                             f.setStatus(FileStatus.COMPLETED);
                           return   fileDataService.save(f);
                         }
                 );

//
//                        .flatMap(fileData->
//                       Mono.fromFuture(uploadToS3(fileData,arr)))
//                .flatMap(putObjectResponse ->
//                {
//                    System.out.println(putObjectResponse);
//                    return fileDataService.findByID(f.getId());
//                });
    }

    public CompletableFuture<PutObjectResponse> uploadToS3(
                                                           String tag,
                                                           FileData fileData,
                                                           byte[] file) {

       var  mediaType = fileData.getType();
        Map<String, String> metadata = new HashMap<String, String>();

    return    s3Client
                .putObject(PutObjectRequest.builder()
                                .bucket(s3Properties.getBucket())
                        // the problem came out from  the content-length
                        // which is not equal to the  putObjectRequeset size
                           //      .metadata(metadata)
                                .key(fileData.getUrl()+ tag)
                                .contentType(mediaType)
                                .build(),
                        AsyncRequestBody.fromBytes(file) // the problem was here
                        );
    }


}
