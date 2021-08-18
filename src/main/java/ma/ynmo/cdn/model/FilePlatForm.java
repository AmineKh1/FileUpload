package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="user")

public class FilePlatForm{
    @Id
    private Long id;
    private String name;
    private String url;
    private String type;
    private Long size;
    private LocalDateTime dateUploaded;
    private Platform platform;
}
