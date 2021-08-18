package ma.ynmo.cdn.validators;

import ma.ynmo.cdn.model.Rules;

import java.util.ArrayList;
import java.util.List;

public class RulesValidator {
    public static List<String> validate(Rules rules) {
        List<String> errors = new ArrayList<>();
        if (rules == null || rules.getUser() == null) {
            errors.add("put a user id");
        }
        if (rules == null || rules.getRuleName() == null) {
            errors.add("put a Role name");
        }
        return errors;
    }
}
