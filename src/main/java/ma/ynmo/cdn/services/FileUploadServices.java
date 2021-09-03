package ma.ynmo.cdn.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ynmo.cdn.model.FileData;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.channels.MulticastChannel;
import java.util.UUID;

public interface FileUploadServices {
    Mono<FileData> storeuploadFile(Mono<FilePart> file, UUID ownerID,
                                   UUID subID, long contentLength,
                                   File in,   MessageChannel imageChannel,
                                   MessageChannel filesChannel,
                                   ObjectMapper objectMapper
                                 );
}
