package ma.ynmo.cdn.config;

import ma.ynmo.cdn.model.FileStatus;
import ma.ynmo.cdn.services.FileDataService;
import ma.ynmo.cdn.services.UploadFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ImageBanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;
import org.springframework.integration.zip.transformer.ZipTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;
import java.util.zip.Deflater;

@Configuration
public class OutIntegrationFlow {
    @Bean
    public UnZipTransformer unZipTransformer() {
        UnZipTransformer unZipTransformer = new UnZipTransformer();
        unZipTransformer.setExpectSingleResult(true);
        unZipTransformer.setZipResultType(ZipResultType.FILE);
        unZipTransformer.setWorkDirectory(new File("/tmp"));
        unZipTransformer.setDeleteFiles(true);
        return unZipTransformer;
    }
    @Bean
    IntegrationFlow outputFile(@Value("${input-directory:${HOME}/Desktop/out}") File out,
                               @Value("${input-directory:${HOME}/Desktop/final}") File fin,
                               UnZipTransformer unZipTransformer, FileDataService fileDataService,
                               UploadFileService uploadFileService, Environment environment) {
        // this transoform is just for testing it tranfers image to string
        GenericTransformer<File, Message<String>> fileStringGenericTransformer = (File source) -> {

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream printStream = new PrintStream(baos)) {
                ImageBanner imageBanner = new ImageBanner(new FileSystemResource(source));
                imageBanner.printBanner(environment, getClass(), printStream);


                return MessageBuilder.withPayload(baos.toString())
                        .setHeader(FileHeaders.FILENAME, source.getAbsoluteFile().getName())
                        .build();
            } catch (IOException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }
            return null;
        };
        GenericTransformer<File, Message<File>> transformer = (File source) -> {
            try {
                return fileDataService.findByID(Long.valueOf(source.getName().split("_")[0]))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("no such file")))
                        .flatMap(fileData ->
                        {
                            fileData.setStatus(FileStatus.UPLOADING);
                            return fileDataService.save(fileData);
                        })
                        .flatMap(fileData -> Mono.just(
                                MessageBuilder.withPayload(source)
                                        .setHeader(FileHeaders.FILENAME,  fileData.getBaseName()) //source.getAbsoluteFile().getName() )

                                        // set custom  varialble for aws integration
                                        .setHeader("awsUrl", fileData.getUrl())
                                        .setHeader("realFileName", fileData.getBaseName())
                                        .build())).flux().blockFirst();
            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        };


        return IntegrationFlows
                .from(Files.inboundAdapter(out)
                                .autoCreateDirectory(true)
                        .preventDuplicates(true),
                        poller -> poller.poller(pm -> pm.fixedRate(1000)))
              .transform(File.class, transformer)
                .transform(unZipTransformer)
                .split()
//                .handle(message -> {
//                    uploadFileService.uploadFile((File)message.getPayload(),null)
//                            .subscribe();// send event through fileStatus channel
//                })
                // we gonna change this transformer or delete it it just for testing
                .transform(File.class, fileStringGenericTransformer)
                // we gonna add aws handler here
                .handle(Files.outboundAdapter(fin)
                        .autoCreateDirectory(true)
                        .fileNameGenerator(
                                message ->{
                                    System.out.println(message);
                                  //  return UUID.randomUUID() + ".txt";
                                   return message.getHeaders().get(FileHeaders.FILENAME).toString().split("\\.")[0] + ".txt";
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
