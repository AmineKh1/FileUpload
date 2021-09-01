package ma.ynmo.cdn.config.amqp;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilesToS3AmqpConfiguration {



    @Bean
    Queue FilesToS3Queue(@Value("${application.cdn.files_to_s3.queue:files_to_s3}")String queue)
    {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    Binding FilesToS3Binding(
            @Qualifier("FilesToS3Queue") Queue queue,
            @Qualifier("cdnExchange") Exchange exchange,
            @Value("${application.cdn.files_to_s3.routingKey:files_to_s3_routing_key}")String routingKey)
    {

        return BindingBuilder.bind(queue).to(exchange)
                .with(routingKey)
                .noargs();
    }
}
