package br.com.levva.order.service;

import br.com.levva.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrder(Order order) {
        log.atInfo().addArgument(order).log(() -> "Publishing order: {} to Queue");

        rabbitTemplate.convertAndSend("order.exchange", "order.routingKey", order);
    }
}
