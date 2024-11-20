package br.com.levva.order.service;

import br.com.levva.order.entity.Order;
import br.com.levva.order.entity.OrderItem;
import br.com.levva.order.entity.OrderStatus;
import br.com.levva.order.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RabbitMQPublisherTest {

    private RabbitMQPublisher rabbitMQPublisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rabbitMQPublisher = new RabbitMQPublisher(rabbitTemplate);
    }

    @Test
    void publishOrder_shouldSendMessageToRabbitMQ() {
        // Given
        Product product = new Product("p1", "Product 1", 100.0);
        OrderItem item = new OrderItem(product, 2, 200.0);
        Order order = new Order("12345", LocalDateTime.now(), List.of(item), OrderStatus.PENDING);

        String expectedExchange = "order.exchange";
        String expectedRoutingKey = "order.routingKey";

        // When
        rabbitMQPublisher.publishOrder(order);

        // Then
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(expectedExchange), eq(expectedRoutingKey), captor.capture());

        Object sentMessage = captor.getValue();
        assertEquals(order, sentMessage);
    }
}
