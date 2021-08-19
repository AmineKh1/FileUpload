package ma.ynmo.cdn.services.Impl;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.FileUploadServices;
import ma.ynmo.cdn.services.SequenceGeneratorService;
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

    @Override
    public Mono<FileData> storeuploadfile(Mono<MultipartFile> file,
                                          UUID ownerID,
                                          UUID subID) {
        return file
                .flatMap(mfile ->
                        FileUploadServiceImpl
                                .createFile(mfile, ownerID, subID,sequenceGeneratorService));
    }


    public static Mono<FileData>  createFile(MultipartFile file,
                                             UUID ownerID,
                                             UUID subID,
                                             SequenceGeneratorService sequenceGeneratorService )
    {
        return sequenceGeneratorService.generateNewId(FileData.FILE_DATA_SEQ)
                .flatMap(id->FileUploadServiceImpl.createNewFileData(id,file, ownerID, subID))
                .map();
    }
    private static Mono<FileData> createNewFileData(
                                            Long id,
                                             MultipartFile file,
                                             UUID ownerID,
                                             UUID subID)
    {
        // generate file url later
        return Mono.just(new FileData(id,file.getName(), subID, null,
                file.getContentType(),FileStatus.PENDING,
                file.getSize(), LocalDateTime.now()));
    }
}
