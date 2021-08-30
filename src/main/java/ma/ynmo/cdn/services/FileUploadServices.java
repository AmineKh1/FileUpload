package ma.ynmo.cdn.services;

import ma.ynmo.cdn.model.FileData;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.channels.MulticastChannel;
import java.util.UUID;

public interface FileUploadServices {
    Mono<FileData> storeuploadImage(Mono<FilePart> file, UUID ownerID,
                                    UUID subID,long contentLength,
                                    File in, AmqpTemplate template,
                                    String exchange, String queue);
}
