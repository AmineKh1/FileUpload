package ma.ynmo.cdn.config.integration;

import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.Impl.FileUploadServiceImpl;
import ma.ynmo.cdn.services.UploadFileService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.transformer.MessageTransformingHandler;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.TreeMap;
import java.util.zip.Deflater;

@Configuration
@Slf4j
public class ImageToS3Integration {
    @Bean
//    @Transformer(inputChannel = "input", outputChannel = "output")
    public ZipTransformer zipTransformer() {
        ZipTransformer zipTransformer = new ZipTransformer();
        zipTransformer.setCompressionLevel(Deflater.BEST_COMPRESSION);
        zipTransformer.setZipResultType(ZipResultType.BYTE_ARRAY);
        zipTransformer.setDeleteFiles(true);
        return zipTransformer;
    }


    @Bean
    IntegrationFlow channelIntegration(AmqpTemplate amqpTemplate,
                                       ZipTransformer zipTransformer,
                                       @Value("${application.cdn.image_to_process.routingKey:image_to_process_routing_key}") String routingKey,
                                       @Value("${application.amqp.extchange:cdnExchange}") String exchange)
    {
        return IntegrationFlows.from(this.imageToProccessChannel())
                .transform(zipTransformer)
                .handle(
                     Amqp.outboundAdapter(amqpTemplate)
                             .exchangeName(exchange)
                             .routingKey(routingKey)
             )
                .get();
    }


    // to test workflow just move an image to in directory but change the name to saved file id.png
    // it will be always 0 in testing
    // cp ./elb.png ./in/0.png
    @Bean
    IntegrationFlow files(@Qualifier("unZipTransformer") UnZipTransformer unZipTransformer,
                          FileDataService fileDataService,
                          ConnectionFactory connectionFactory,
                          UploadFileService uploadFileService,
                          @Value("${application.cdn.image_to_s3.queue:image_to_s3}") String queue,
                          ObjectMapper objectMapper,
                          AmqpTemplate amqpTemplate
    ) {



        return IntegrationFlows

                .from(Amqp.inboundAdapter(connectionFactory, queue))
                .transform(unZipTransformer)
                .split()
                .handle(mmessageHandler(uploadFileService, objectMapper, amqpTemplate)) // send to aws
                .get();
    }


    public static MessageHandler mmessageHandler(UploadFileService uploadFileService, ObjectMapper objectMapper, AmqpTemplate amqpTemplate)
    {
        return message -> {


            try {
                uploadFileService.uploadFile(objectMapper
                                .readValue(Objects.requireNonNull(message.getHeaders()
                                        .get("fileData")).toString(), FileData.class),
                                ((TreeMap<String, Byte[]>)  message.getPayload()))
                        .subscribe(fileData -> amqpTemplate.send("cdnTopicExchange",fileData.getSubID().toString(),
                                new Message(FileUploadServiceImpl.convertToJson(objectMapper,fileData)
                                        .getBytes(StandardCharsets.UTF_8))));
            } catch (IOException e) {
                e.printStackTrace();
            }

      // send event through fileStatus channel

        };
    }

    @Bean
    MessageChannel imageToProccessChannel()
    {
        return MessageChannels.publishSubscribe().get();
    }


}
