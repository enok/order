package br.com.levva.order.api;

import br.com.levva.order.entity.Order;
import br.com.levva.order.entity.OrderStatus;
import br.com.levva.order.entity.ProcessedOrder;
import br.com.levva.order.service.OrderService;
import br.com.levva.order.service.RabbitMQPublisher;
import br.com.levva.order.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderController orderController;

    @Mock
    private RedisService redisService;

    @Mock
    private RabbitMQPublisher rabbitMQPublisher;

    @Mock
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(redisService, rabbitMQPublisher, orderService);
    }

    @Test
    void createOrder_shouldReturnAccepted_whenOrderIsNotDuplicate() {
        // Given
        Order order = Order.builder()
                .id("12345")
                .orderDate(LocalDateTime.now())
                .items(List.of())
                .build();
        when(redisService.isDuplicate(order.id(), 0L)).thenReturn(false); // Assuming expirationSeconds = 0

        // When
        ResponseEntity<Void> response = orderController.createOrder(order);

        // Then
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(String.format("/orders/%s", order.id()), response.getHeaders().getLocation().toString());
        verify(rabbitMQPublisher, times(1)).publishOrder(order);
    }

    @Test
    void createOrder_shouldReturnConflict_whenOrderIsDuplicate() {
        // Given
        Order order = Order.builder()
                .id("12345")
                .orderDate(LocalDateTime.now())
                .items(List.of())
                .build();
        when(redisService.isDuplicate(order.id(), 0L)).thenReturn(true); // Assuming expirationSeconds = 0

        // When
        ResponseEntity<Void> response = orderController.createOrder(order);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(rabbitMQPublisher, never()).publishOrder(order);
    }

    @Test
    void getOrder_shouldReturnOrder_whenOrderExists() {
        // Given
        String orderId = "12345";
        ProcessedOrder processedOrder = new ProcessedOrder(orderId, LocalDateTime.now(), List.of(), OrderStatus.COMPLETED, 100.0);
        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(processedOrder));

        // When
        ResponseEntity<ProcessedOrder> response = orderController.getOrder(orderId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(processedOrder, response.getBody());
    }

    @Test
    void getOrder_shouldReturnNotFound_whenOrderDoesNotExist() {
        // Given
        String orderId = "12345";
        when(orderService.getOrderById(orderId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ProcessedOrder> response = orderController.getOrder(orderId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
