package ma.ynmo.cdn.validators;

import ma.ynmo.cdn.model.FileData;

import java.util.ArrayList;
import java.util.List;

public class FileStoreValidator {
    public static List<String> validate(FileData fileStore) {
        List<String> errors = new ArrayList<>();
        if (fileStore == null || fileStore.getName() == null) {
            errors.add("put name file");
        }
        if (fileStore == null || fileStore.getType() == null) {
            errors.add("put type file");
        }
        if (fileStore == null || fileStore.getUrl() == null) {
            errors.add("put url file");
        }
        if (fileStore == null || fileStore.getStore() == null) {
            errors.add("put store id");
        }
        if (fileStore == null || fileStore.getSize() > 100) {
            errors.add("size should be less than 100");
        }
        if (fileStore == null || fileStore.getStore() == null) {
            errors.add("put store id");
        }
        if (fileStore == null || (fileStore.getStore().getCurrentSize() + fileStore.getSize()) > 1000) {
            errors.add("store size already reach te max");
        }
        return errors;
    }
}
