package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="FileData")
public class FileData {
    @Transient
    public static String FILE_DATA_SEQ = "fileDataSeq";


    @Id
    private Long id;
    private String baseName;

    private List<String> names = new ArrayList<>();
    private String extension;
    private String realName;
    private UUID ownerId;
    private String fileKey;
    private UUID subID;
    private String url;
    private String type;
    private FileStatus status;
    private Long size;
    private LocalDateTime uploadedAt;
    @Transient
    private boolean isImage;

    public List<String> getNames()
    {
        return this.names;
    }
}
