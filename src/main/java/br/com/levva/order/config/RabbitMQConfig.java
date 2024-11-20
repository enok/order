package br.com.levva.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String QUEUE_NAME = "order.queue";
    public static final String ROUTING_KEY = "order.routingKey";

    @Bean
    public DirectExchange exchange() {
        log.atTrace().addArgument(EXCHANGE_NAME).log(() -> "Configuring exchange: {}");
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        log.atTrace().addArgument(QUEUE_NAME).log(() -> "Configuring queue: {}");
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding biding(Queue queue, DirectExchange exchange) {
        log.atTrace().addArgument(queue).addArgument(exchange).log(() -> "Configuring biding for queue: {} and exchange: {}");
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        // Configure pacotes confiáveis para evitar o erro de segurança
        javaTypeMapper.setTrustedPackages("br.com.levva.order.entity"); // Adicione outros pacotes, se necessário

        converter.setJavaTypeMapper(javaTypeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
