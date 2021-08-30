package ma.ynmo.cdn.config;

import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
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
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.zip.Deflater;

@Configuration
public class MidIntegrationFlow {
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
                                       @Value("${application.amqp.processImage.routingKey:processImageRoutingKey}") String routingKey,
                                       @Value("${application.amqp.processImage.extchange:cdnExtchange}") String exchange)
    {
        return IntegrationFlows.from(this.FileChannel()).
             handle(
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
    IntegrationFlow files(ZipTransformer zipTransformer,
                          FileDataService fileDataService,
                          ConnectionFactory connectionFactory,
                          @Value("${application.amqp.cdn.queue}") String queue
    ) {

        GenericTransformer<Message<File>, Message<File>> messageGenericTransformer = ( source) -> {
            try {
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
                .from(Amqp.inboundAdapter(connectionFactory,queue))
             .transform(messageGenericTransformer)
             .transform(zipTransformer)
                .channel(this.FileChannel())
                .handle(System.out::println)
                .get();
    }

    @Bean
    MessageChannel FileChannel()
    {
        return MessageChannels.publishSubscribe().get();
    }
    @Bean
    MessageChannel midChannel()
    {
        return MessageChannels.publishSubscribe().get();
    }
}
