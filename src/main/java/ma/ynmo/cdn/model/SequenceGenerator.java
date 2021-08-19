package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "DBSequences")
@Data
@AllArgsConstructor
public class SequenceGenerator {
    @Id
    private String name;
    private Long seq;

    public void increment() {
        this.seq++;
    }
}
