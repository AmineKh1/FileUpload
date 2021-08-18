package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="user")
public class FileData {
    @Id
    private Long id;

    private String name;
    private UUID subID; // ila kant stor subid = store.subid or if platform aykon subid=platform.ownerid
    private String url;
    private String type;
    private FileStatus status;
    private Long size;
    private LocalDateTime dateUploaded;

}
