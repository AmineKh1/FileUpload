package ma.ynmo.cdn.validators;

import ma.ynmo.cdn.model.FilePlatForm;

import java.util.ArrayList;
import java.util.List;

public class FilePlatFormValidator {
    public static List<String> validate(FilePlatForm filePlatForm) {
        List<String> errors = new ArrayList<>();
        if (filePlatForm == null || filePlatForm.getName() == null) {
            errors.add("put name file");
        }
        if (filePlatForm == null || filePlatForm.getType() == null) {
            errors.add("put type file");
        }
        if (filePlatForm == null || filePlatForm.getUrl() == null) {
            errors.add("put url file");
        }
        if (filePlatForm == null || filePlatForm.getPlatform() == null) {
            errors.add("put store id");
        }
        if (filePlatForm == null || filePlatForm.getSize() > 100) {
            errors.add("size should be less than 100");
        }
        if (filePlatForm == null || filePlatForm.getPlatform() == null) {
            errors.add("put store id");
        }
        if (filePlatForm == null || (filePlatForm.getPlatform().getCurrentSize() + filePlatForm.getSize()) > 1000) {
            errors.add("sore size already reach te max");
        }
        return errors;
    }
}
