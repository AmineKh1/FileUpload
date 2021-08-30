package ma.ynmo.cdn.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidAmqpConfiguration {



    @Bean
    Queue midQueue(@Value("${application.amqp.cdn.queue:midQueue}")String queue)
    {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    Binding midBinding(
            @Qualifier("midQueue") Queue queue,
            @Qualifier("cdnExchange") Exchange exchange,
            @Value("${application.amqp.cdn.routingKey:cdnRoutingKey}")String routingKey)
    {

        return BindingBuilder.bind(queue).to(exchange)
                .with(routingKey)
                .noargs();
    }
}
