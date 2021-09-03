package ma.ynmo.cdn.config.amqp;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageToProccessAmqpConfig {

    @Bean
    Exchange cdnExchange(@Value("${application.amqp.extchange:cdnExchange}") String exchange)
    {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Queue imageToProcessQueue(@Value("${application.cdn.image_to_process.queue:image_to_process}")String queue)
    {
        return QueueBuilder.durable(queue).build();
    }
    @Bean
    Binding imageToProcessBinding(
            @Qualifier("imageToProcessQueue") Queue queue,
            @Qualifier("cdnExchange") Exchange exchange,
            @Value("${application.cdn.image_to_process.routingKey:image_to_process_routing_key}")String routingKey)
    {

        return BindingBuilder.bind(queue).to(exchange)
                .with(routingKey)
                .noargs();
    }

}
