package ma.ynmo.cdn.config;

import ma.ynmo.cdn.services.FileDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.Deflater;

@Configuration
public class MidIntegrationFlow {
    @Bean
//    @Transformer(inputChannel = "input", outputChannel = "output")
    public ZipTransformer zipTransformer() {
        ZipTransformer zipTransformer = new ZipTransformer();
        zipTransformer.setCompressionLevel(Deflater.BEST_COMPRESSION);
        zipTransformer.setZipResultType(ZipResultType.BYTE_ARRAY);
        return zipTransformer;
    }

    // to test workflow just move an image to in directory but change the name to saved file id.png
    // it will be always 0 in testing
    // cp ./elb.png ./in/0.png
    @Bean
    IntegrationFlow files(@Value("${input-directory:${HOME}/Desktop/in}") File in ,
                          @Value("${input-directory:${HOME}/Desktop/out}") File out,
                          ZipTransformer zipTransformer, FileDataService fileDataService) {

        GenericTransformer<File, Message<File>> messageGenericTransformer = (File source) -> {
            try {
                return fileDataService.findByID(Long.valueOf(source.getName().split("\\.")[0]))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("no such file")))
                        .flatMap(fileData -> Mono.just(
                                MessageBuilder.withPayload(source)
                                       // .setHeader(FileHeaders.FILENAME, fileData.getBaseName())

                                        .setHeader(FileHeaders.FILENAME, fileData.getBaseName())
                                        .setHeader("realFileName", source.getAbsoluteFile().getName())
                                        .build())).flux().blockFirst();
            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        };


        return IntegrationFlows
                .from(Files.inboundAdapter(in).autoCreateDirectory(true).preventDuplicates(true),
                        poller -> poller.poller(pm -> pm.fixedRate(1000)))
             .transform(File.class, messageGenericTransformer)
             .transform(zipTransformer)
                // we gonna change this one to kafka  handler to send zipped file to other service
                .handle(Files.outboundAdapter(out)
                        .autoCreateDirectory(true)
                        .fileNameGenerator(
                        message ->{
                            System.out.println(message);
                            return( (String)message.getHeaders().get("realFileName")).split("\\.")[0]
                                    +"_" +
                                    ( (String)message.getHeaders().get(FileHeaders.FILENAME)).split("\\.")[0] +
                            ".zip";
                        }
                        ))
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
}
