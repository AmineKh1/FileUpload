package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.FileUploadServices;
import ma.ynmo.cdn.services.SequenceGeneratorService;
import ma.ynmo.cdn.services.StoreService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadServices {

    private  final FileDataService fileDataService;
    private  final SequenceGeneratorService sequenceGeneratorService;
    private final StoreService storeService;

    // this workFlow disgned to handle images
    // you should make another one for files
    //
    @Override
    public Mono<FileData> storeuploadImage(Mono<FilePart> file,
                                           UUID ownerID,
                                           UUID subID,
                                           long contentLength,
                                           File in,
                                           AmqpTemplate template,
                                           String exchange,
                                           String queue){
        return file
                .flatMap(mfile ->
                        FileUploadServiceImpl
                                .createFile(mfile, ownerID, subID,
                                        storeService,
                                        sequenceGeneratorService,
                                        contentLength,
                                        in,
                                        template,
                                        exchange,
                                        queue))
                .flatMap(fileDataService::save);
    }

   // rename file with file data id
    public static void TransaferToInputDir(FileData fileData, FilePart file,
                                           File in,
                                           AmqpTemplate template,
                                           String exchange,
                                           String queue) {
        var filename = new StringBuilder();
        filename.append(fileData.getId());
        filename.append(".");
       var n =   fileData.getBaseName().split("\\.");
      filename.append(n[n.length - 1]);
      File to = new File(in.getAbsolutePath() +"/" + filename);
      file.transferTo(to)
              .doOnEach(unused -> {
               log.info("before Sending");
               template.send(
                       exchange,
                       "cdnRoutingKey",
                        new Message(to, Map.of("fileData", fileData))
               );

      }).subscribe();
    }



    public static Mono<FileData>  createFile(FilePart file,
                                             UUID ownerID,
                                             UUID subID,
                                             StoreService storeService,
                                             SequenceGeneratorService sequenceGeneratorService ,
                                             long contentLength,
                                             File in,
                                             AmqpTemplate template,
                                             String exchange,
                                             String routingKey)
    {
        return sequenceGeneratorService.generateNewId(FileData.FILE_DATA_SEQ)
                .flatMap(id ->FileUploadServiceImpl
                        .createNewFileData(id,file, ownerID, contentLength,subID))
                .flatMap(storeService::verifyFile)
                .doOnSuccess(fileData -> FileUploadServiceImpl.TransaferToInputDir(fileData,
                        file,
                        in,
                        template,
                        exchange,
                        routingKey));
    }

    private static Mono<FileData> createNewFileData(
                                            Long id,
                                             FilePart file,
                                             UUID ownerID,
                                             long contentLength,
                                             UUID subID)
    {
        // generate file url later
        return Mono.just(new FileData(id, file.filename(),ownerID,
                null,
                subID,
                null,
                Objects.requireNonNull(file.headers().getContentType()).getType(),
                FileStatus.PENDING,
                contentLength,
                LocalDateTime.now()));
    }

}
