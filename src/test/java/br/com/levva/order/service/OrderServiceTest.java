package br.com.levva.order.service;

import br.com.levva.order.entity.*;
import br.com.levva.order.repository.ProcessedOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private ProcessedOrderRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(repository);
    }

    @Test
    void processOrder_shouldSaveProcessedOrder() {
        // Given
        Product product1 = Product.builder()
                .id("p1")
                .name("Product 1")
                .price(100.0)
                .build();
        Product product2 = Product.builder()
                .id("p2")
                .name("Product 2")
                .price(50.0)
                .build();

        OrderItem item1 = new OrderItem(product1, 2);
        OrderItem item2 = new OrderItem(product2, 1);

        Order order = Order.builder()
                .id("12345")
                .orderDate(LocalDateTime.now())
                .items(List.of(item1, item2))
                .build();

        // When
        orderService.processOrder(order);

        // Then
        ArgumentCaptor<ProcessedOrder> captor = ArgumentCaptor.forClass(ProcessedOrder.class);
        verify(repository, times(1)).save(captor.capture());

        ProcessedOrder savedOrder = captor.getValue();
        assertEquals(order.id(), savedOrder.id());
        assertEquals(order.orderDate(), savedOrder.orderDate());
        assertEquals(OrderStatus.COMPLETED, savedOrder.status());
        assertEquals(250.0, savedOrder.total());
        assertEquals(order.items(), savedOrder.items());
    }

    @Test
    void getOrderById_shouldReturnProcessedOrder_whenOrderExists() {
        // Given
        String orderId = "12345";
        ProcessedOrder processedOrder = ProcessedOrder.builder()
                .id(orderId)
                .orderDate(LocalDateTime.now())
                .items(List.of())
                .status(OrderStatus.COMPLETED)
                .total(100.0)
                .build();

        when(repository.findById(orderId)).thenReturn(Optional.of(processedOrder));

        // When
        Optional<ProcessedOrder> result = orderService.getOrderById(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(processedOrder, result.get());
    }

    @Test
    void getOrderById_shouldReturnEmptyOptional_whenOrderDoesNotExist() {
        // Given
        String orderId = "12345";
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        // When
        Optional<ProcessedOrder> result = orderService.getOrderById(orderId);

        // Then
        assertTrue(result.isEmpty());
    }
}
