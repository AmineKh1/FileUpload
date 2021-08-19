package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="user")
public class FileData {
    @Transient
    public static String FILE_DATA_SEQ = "fileDataSeq";

    @Id
    private Long id;
    private String name;
    private UUID ownerId;
    private UUID subID;
    private String url;
    private String type;
    private FileStatus status;
    private Long size;
    private LocalDateTime uploadedAt;

}
