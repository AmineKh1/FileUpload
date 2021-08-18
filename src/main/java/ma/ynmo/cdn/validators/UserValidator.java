package ma.ynmo.cdn.validators;

import ma.ynmo.cdn.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserValidator {
    public static List<String> validate(User user) {
        List<String> errors = new ArrayList<>();
        if (user == null || user.getName() == null) {
            errors.add("put a user name");
        }
        if (user == null || user.getLastName() == null) {
            errors.add("put a user Last name");
        }
        if (user == null || user.getEmail() == null){
            errors.add("put a user email");
        }
        if (user == null || user.getPsw() == null){
            errors.add("put a user password");
        }
        if (user == null || user.getPsw() == null){
            errors.add("put a user password");
        }
        return errors;
    }
}
