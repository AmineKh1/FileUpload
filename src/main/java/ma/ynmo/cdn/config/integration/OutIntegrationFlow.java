package ma.ynmo.cdn.config.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ynmo.cdn.model.FileData;
import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.UploadFileService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;
import java.util.zip.Deflater;

import static ma.ynmo.cdn.config.integration.ImageToS3Integration.mmessageHandler;

@Configuration
public class OutIntegrationFlow {
    @Bean
    public UnZipTransformer unZipTransformer() {
        UnZipTransformer unZipTransformer = new UnZipTransformer();
        unZipTransformer.setZipResultType(ZipResultType.BYTE_ARRAY);
        unZipTransformer.setWorkDirectory(new File("/tmp"));
        unZipTransformer.setDeleteFiles(true);
        return unZipTransformer;
    }
    @Bean
    IntegrationFlow fileToS3ChannelIntegration(AmqpTemplate amqpTemplate,
                                       ZipTransformer zipTransformer,
                                       @Value("${application.cdn.files_to_s3.routingKey:files_to_s3_routing_key}") String routingKey,
                                       @Value("${application.amqp.extchange:cdnExchange}") String exchange,
                                       @Qualifier("filesToS3Channel") MessageChannel filesChannel
                                       )
    {
        return IntegrationFlows.from(filesChannel)
                .transform(zipTransformer)
                .handle(
                        Amqp.outboundAdapter(amqpTemplate)
                                .exchangeName(exchange)
                                .routingKey(routingKey)
                )
                .get();
    }

    @Bean
    IntegrationFlow outputFile(
                               UnZipTransformer unZipTransformer,
                               FileDataService fileDataService,
                               ConnectionFactory connectionFactory,
                               UploadFileService uploadFileService,
                               ObjectMapper objectMapper,
                               AmqpTemplate amqpTemplate,
           @Value("${application.cdn.files_to_s3.queue:files_to_s3}") String queue) {

//        GenericTransformer<File, Message<File>> transformer = (File source) -> {
//            try {
//                return fileDataService.findByID(Long.valueOf(source.getName().split("_")[0]))
//                        .switchIfEmpty(Mono.error(new IllegalArgumentException("no such file")))
//                        .flatMap(fileData ->
//                        {
//                            fileData.setStatus(FileStatus.UPLOADING);
//                            return fileDataService.save(fileData);
//                        })
//                        .flatMap(fileData -> Mono.just(
//                                MessageBuilder.withPayload(source)
//                                        .setHeader(FileHeaders.FILENAME,  fileData.getBaseName()) //source.getAbsoluteFile().getName() )
//
//                                        // set custom  varialble for aws integration
//                                        .setHeader("awsUrl", fileData.getUrl())
//                                        .setHeader("realFileName", fileData.getBaseName())
//                                        .build())).flux().blockFirst();
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//                return null;
//            }
//        };


        return IntegrationFlows
                .from(Amqp
                        .inboundAdapter(connectionFactory, queue))
//                .transform(File.class, transformer)
              .transform(unZipTransformer)
                .split()
                .handle(mmessageHandler(uploadFileService, objectMapper, amqpTemplate))
                // we gonna change this transformer or delete it it just for testing
              //  .transform(File.class, fileStringGenericTransformer)
                // we gonna add aws handler here
//                .handle(Files.outboundAdapter(fin)
//                        .autoCreateDirectory(true)
//                        .fileNameGenerator(
//                                message ->{
//                                    System.out.println(message);
//                                  //  return UUID.randomUUID() + ".txt";
//                                   return message.getHeaders().get(FileHeaders.FILENAME).toString().split("\\.")[0] + ".txt";
//                                }
//                        ))
                .get();

        // here is an example of ftp implimentation but we want to keep headers so

//                .handle(Ftp.outboundAdapter(ftpSessionFactory)
//                        .remoteDirectory(remoteDirectory)
//                        .fileNameGenerator(message -> {
//                            Object o = message.getHeaders().get(FileHeaders.FILENAME);
//                            String fileName = String.class.cast(o);
//                            return fileName.split("\\.")[0]+".txt";
//                        })).get();
    }

    @Bean
    MessageChannel filesToS3Channel()
    {
      return   MessageChannels.publishSubscribe().get();
    }
}
