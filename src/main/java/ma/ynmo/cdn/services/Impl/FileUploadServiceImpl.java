package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.FileUploadServices;
import ma.ynmo.cdn.services.SequenceGeneratorService;
import ma.ynmo.cdn.services.StoreService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
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
                                           File in) {
        return file
                .flatMap(mfile ->
                        FileUploadServiceImpl
                                .createFile(mfile, ownerID, subID,
                                        storeService,
                                        sequenceGeneratorService,
                                        contentLength,
                                        in))
                .flatMap(fileDataService::save)
                ;
    }

   // rename file with file data id
    public static void TransaferToInputDir(FileData fileData, FilePart file, File in) {
        var filename = new StringBuilder();
        filename.append(fileData.getId());
        filename.append(".");
       var n =   fileData.getBaseName().split("\\.");
      filename.append(n[n.length - 1]);
      file.transferTo(new File(in.getAbsolutePath() +"/" + filename)).subscribe();
    }



    public static Mono<FileData>  createFile(FilePart file,
                                             UUID ownerID,
                                             UUID subID,
                                             StoreService storeService,
                                             SequenceGeneratorService sequenceGeneratorService ,
                                             long contentLength,
                                             File in)
    {
        return sequenceGeneratorService.generateNewId(FileData.FILE_DATA_SEQ)
                .flatMap(id ->FileUploadServiceImpl
                        .createNewFileData(id,file, ownerID, contentLength,subID))
                .flatMap(storeService::verifyFile)
                .doOnSuccess(fileData -> FileUploadServiceImpl.TransaferToInputDir(fileData,file,in ));
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
                subID,
                null,
                Objects.requireNonNull(file.headers().getContentType()).getType(),
                FileStatus.PENDING,
                contentLength,
                LocalDateTime.now()));
    }

}
