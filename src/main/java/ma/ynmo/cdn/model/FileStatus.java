package ma.ynmo.cdn.model;

import java.nio.file.Files;
import java.util.stream.Stream;

public enum FileStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    VERIFIYING("verifiying"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCLED("cancled"),
    INVALID("invalid"),
    DELETED("deleted"),
    UPLOADING("uploading");
    String name;

    FileStatus(String name)
    {
        this.name =name;
    }
    public String getName()
    {
        return this.name;
    }
    public static  FileStatus fromName(String name)
    {
        return Stream.of(FileStatus.values())
                .filter(fs -> fs.name.equals(name))
                .findFirst()
                .orElseThrow(()->
                        new IllegalArgumentException("no such fileStatus")
                );
    }

}
