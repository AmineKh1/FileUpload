package ma.ynmo.cdn.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessImageAmqpConfiguration {

    @Bean
    Exchange cdnExchange(@Value("${application.amqp.processImage.extchange:cdnExtchange}") String exchange)
    {

        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Queue proccessQueue(@Value("${application.amqp.processImage.queue:proccessImageQueue}")String queue)
    {
        return QueueBuilder.durable(queue).build();
    }
    @Bean
    Binding binding(
            @Qualifier("proccessQueue") Queue queue,
            @Qualifier("cdnExchange") Exchange exchange,
            @Value("${application.amqp.processImage.routingKey:proccessImageRoutingKey}")String routingKey)
    {

        return BindingBuilder.bind(queue).to(exchange)
                .with(routingKey)
                .noargs();
    }

}
