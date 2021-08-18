package ma.ynmo.cdn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.util.Collection;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="user")
public class User {
    @Id
    private Long id;
    private String name;
    private String lastName;
    @Email
    private String email;
    private String psw;


}
