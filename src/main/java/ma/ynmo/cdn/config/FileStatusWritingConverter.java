package ma.ynmo.cdn.config;

import ma.ynmo.cdn.model.FileStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class FileStatusWritingConverter implements Converter<FileStatus, String> {
    @Override
    public String convert(FileStatus fileStatus) {
        return fileStatus.getName();
    }
}
