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
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FileUploadServiceImpl implements FileUploadServices {

    private  final FileDataService fileDAtaServiceImpl;
    private  final SequenceGeneratorService sequenceGeneratorService;
    private final StoreService storeService;

    // this workFlow disgned to handle images
    // you should make another one for files
    //
    @Override
    public Mono<FileData> storeuploadImage(Mono<FilePart> file,
                                           UUID ownerID,
                                           UUID subID) {
        return file
                .flatMap(mfile ->
                        FileUploadServiceImpl
                                .createFile(mfile, ownerID, subID,
                                        storeService,sequenceGeneratorService))
                .doOnSuccess(fileData -> FileUploadServiceImpl.zipAndTransaferToInputDir(fileData,file));
    }

    // zip fileData and file then write files to spring integration input Dir
    // then the image proccessing service will extract the zip file
    // and put files inside this zip f
    public static void zipAndTransaferToInputDir(FileData fileData, Mono<MultipartFile> file) {
       //    file.subscribe(f -> zipFile(fileData, f ) );
    }



    public static Mono<FileData>  createFile(FilePart file,
                                             UUID ownerID,
                                             UUID subID,
                                             StoreService storeService,
                                             SequenceGeneratorService sequenceGeneratorService )
    {
        return sequenceGeneratorService.generateNewId(FileData.FILE_DATA_SEQ)
                .flatMap(id ->FileUploadServiceImpl.createNewFileData(id,file, ownerID, subID))
                .flatMap(storeService::verifyFile);
    }

    private static Mono<FileData> createNewFileData(
                                            Long id,
                                             FilePart file,
                                             UUID ownerID,
                                             UUID subID)
    {
        // generate file url later
        return Mono.just(new FileData(id,file.filename(),ownerID,
                subID,
                null,
                file.headers().getContentType().getType(),FileStatus.PENDING,
                file.headers().getContentLength(), LocalDateTime.now()));
    }
}
