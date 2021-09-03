package ma.ynmo.cdn.services.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.internal.util.Mimetype;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

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
    public Mono<FileData> storeuploadFile(Mono<FilePart> file,
                                          UUID ownerID,
                                          UUID subID,
                                          long contentLength,
                                          File in,
                                          MessageChannel imageChannel,
                                          MessageChannel filesChannel,
                                          ObjectMapper objectMapper
                                           ){
        return file
                .flatMap(mfile ->
                        FileUploadServiceImpl
                                .createFile(mfile, ownerID, subID,
                                        storeService,
                                        sequenceGeneratorService,
                                        contentLength,
                                        in,
                                        imageChannel,
                                        filesChannel,
                                        objectMapper))
                .flatMap(fileDataService::save);
    }

   // rename file with file data id
    public static void TransaferToInputDir(FileData fileData, FilePart file,
                                           File in,
                                           MessageChannel imageChannel,
                                           MessageChannel filesChannel,
                                           ObjectMapper objectMapper
                                          ) {
        if (fileData.getStatus().equals(FileStatus.INVALID))
                return;
        if (fileData.getExtension().equals("webp") || fileData.getExtension().equals("gif") )
            fileData.setImage(false);
        var filename = fileData.getId() +

                ( !fileData.isImage()
                ? "_"+ fileData.getRealName() :
                "")+
                "." +
                fileData.getExtension();
        var channel = fileData.isImage() ? imageChannel: filesChannel;

      File to = new File(in.getAbsolutePath() +"/" + filename);
      file.transferTo(to)
              .doOnEach(unused -> {
               channel
                       .send(
                       MessageBuilder
                       .withPayload(to)
                               .setHeader("fileData",convertToJson(objectMapper, fileData)
                                    ).build());

      }).subscribe();
    }

    public static String convertToJson(ObjectMapper objectMapper, FileData fileData )
    {
        try
        {
            return    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( fileData);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Mono<FileData>  createFile(FilePart file,
                                             UUID ownerID,
                                             UUID subID,
                                             StoreService storeService,
                                             SequenceGeneratorService sequenceGeneratorService ,
                                             long contentLength,
                                             File in,
                                             MessageChannel imageChannel,
                                             MessageChannel filesChannel,
                                             ObjectMapper objectMapper
                                            )
    {
        return sequenceGeneratorService.generateNewId(FileData.FILE_DATA_SEQ)
                .flatMap(id ->FileUploadServiceImpl
                        .createNewFileData(id,file, ownerID, contentLength,subID))
                .flatMap(storeService::verifyFile)
                .doOnSuccess(fileData -> FileUploadServiceImpl.TransaferToInputDir(fileData,
                        file,
                        in,
                        imageChannel,
                        filesChannel, objectMapper));
    }

    private static Mono<FileData> createNewFileData(
                                            Long id,
                                             FilePart file,
                                             UUID ownerID,
                                             long contentLength,
                                             UUID subID)
    {
        var n  = file.filename().split("\\.");

        // generate file url later
        if (!file.filename().contains("."))
                throw new IllegalArgumentException("Invalid file");
        return Mono.just(new FileData(id,
                        file.filename(),
                        List.of(),
                n[n.length - 1],
                        file.filename().substring(0, file.filename().lastIndexOf('.')),
                ownerID,
                null,
                subID,
                null,
                 null,
                FileStatus.PENDING,
                contentLength,
                LocalDateTime.now(),false))
                .map(FileUploadServiceImpl::getMediaType);
    }

    private static FileData getMediaType(FileData fileData) {
        var image = List.of("jpeg", "jpg", "gif", "png", "webp");
        var files = List.of("svg", "json", "pdf", "html","css", "js","zip", "rar","tar");
        var filesMediaValues = Map.of("svg","image/svg+xml",
                "json", MediaType.APPLICATION_JSON_VALUE,
                "pdf", MediaType.APPLICATION_PDF_VALUE,
                "html", MediaType.TEXT_HTML_VALUE,
                "css", "text/css",
                "js", "text/javascript",
                "zip","application/zip",
                "rar", "application/vnd.rar",
                "tar","application/x-tar");
        var imageMediaValues = Map.of("jpeg",MediaType.IMAGE_JPEG_VALUE,
                    "jpg", "image/jpeg",
                    "gif", MediaType.IMAGE_GIF_VALUE,
                    "png", MediaType.IMAGE_PNG_VALUE,
                    "webp","image/webp");


        if (image.contains(fileData.getExtension()))
        {
            fileData.setImage(true);
            fileData.setType(imageMediaValues.get(fileData.getExtension()));
        }
        else  if (files.contains(fileData.getExtension()))
        {
            fileData.setImage(false);
            fileData.setType(filesMediaValues.get(fileData.getExtension()));
        }
      else
          fileData.setStatus(FileStatus.INVALID);
        return fileData;
    }

}
