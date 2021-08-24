package ma.ynmo.cdn.services;

import lombok.AllArgsConstructor;
import ma.ynmo.cdn.config.S3ClientConfigurarionProperties;
import ma.ynmo.cdn.model.FileData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@AllArgsConstructor
public class UploadFileService {
    private final S3ClientConfigurarionProperties s3Properties;
    private final FileDataService fileDataService;

    public Mono<FileData> uploadFile(File file)
    {
        // extract file id;

        var id  =  Long.valueOf(file.getName().split("_")[0]);
        // fetch filedata
        var length = file.length();
        // create upload configs

        // upload the file

        // update fileDate status
        //save fileData
        return null;
    }
}
