package ma.ynmo.cdn.config.integration;

import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.UploadFileService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
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
    IntegrationFlow files(UnZipTransformer unZipTransformer,
                          FileDataService fileDataService,
                          ConnectionFactory connectionFactory,
                          UploadFileService uploadFileService,
                          @Value("${application.cdn.image_to_s3.queue:image_to_s3}") String queue
    ) {

        GenericTransformer<Message<File>, Message<File>> messageGenericTransformer = ( source) -> {
            try {
                log.info("here i start");
                   FileData fileData= (FileData) source.getHeaders().get("fileData");
                  return  fileDataService.findByID(fileData.getId())
                          .flatMap(fd->{
                              if (!fd.getStatus().equals(FileStatus.PENDING))
                                return  Mono.empty();
                              fd.setStatus(FileStatus.PROCESSING);
                              return fileDataService.save(fd);
                          })
                          .switchIfEmpty(Mono.error(new IllegalStateException("file allready in processing")))
                          .flatMap(fd-> Mono.just(MessageBuilder.withPayload(source.getPayload())
                                    .setHeader("fileData", fd)
                                  .build()))
                          .flux().blockFirst();

            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        };


        return IntegrationFlows
//                .from(Files.inboundAdapter(in).autoCreateDirectory(true)
//                                .preventDuplicates(true),
//                        poller -> poller.poller(pm -> pm.fixedRate(1000)))
                .from(Amqp.inboundAdapter(connectionFactory, queue))
                .transform(unZipTransformer)
               // .transform(messageGenericTransformer)
              //  .transform(zipTransformer)
                //.channel(this.FileChannel())
                .split()
                .handle(this.mmessageHandler(uploadFileService)) // send to aws
                .get();
    }

    MessageHandler mmessageHandler(UploadFileService uploadFileService)
    {
        return  new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                    try {
                        System.out.println(message);
                        uploadFileService.uploadFile((FileData) message.getHeaders().get("fileData"),(File)message.getPayload())
                                .subscribe(System.out::println);// send event through fileStatus channel
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        };
    }
    @Bean
    MessageChannel imageToProccessChannel()
    {
        return MessageChannels.publishSubscribe().get();
    }


}
