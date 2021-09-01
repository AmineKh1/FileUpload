package ma.ynmo.cdn.config.amqp;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageToS3AmqpConfiguration {
    @Bean
    Queue imageToS3Queue(@Value("${application.cdn.image_to_s3.queue:image_to_s3}")String queue)
    {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    Binding imageToS3Binding(
            @Qualifier("imageToS3Queue") Queue queue,
            @Qualifier("cdnExchange") Exchange exchange,
            @Value("${application.cdn.image_to_s3.routingKey:image_to_s3_routing_key}")String routingKey)
    {

        return BindingBuilder.bind(queue).to(exchange)
                .with(routingKey)
                .noargs();
    }
}
