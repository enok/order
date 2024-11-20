package br.com.levva.order.service;

import br.com.levva.order.entity.Order;
import br.com.levva.order.entity.OrderItem;
import br.com.levva.order.entity.OrderStatus;
import br.com.levva.order.entity.ProcessedOrder;
import br.com.levva.order.repository.ProcessedOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static br.com.levva.order.config.RabbitMQConfig.QUEUE_NAME;

@Slf4j
@Service
public class OrderService {

    private final ProcessedOrderRepository repository;

    public OrderService(ProcessedOrderRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void processOrder(Order order) {
        log.atDebug().addArgument(order).log(() -> "Getting order: {} from queue and processing");

        double total = order.items().stream()
                .mapToDouble(OrderItem::subTotal)
                .sum();
        log.atTrace().addArgument(total).log(() -> "Total calculated: {}");

        ProcessedOrder processedOrder = new ProcessedOrder(
                order.id(),
                order.orderDate(),
                order.items(), OrderStatus.COMPLETED,
                total
        );

        log.atDebug().addArgument(processedOrder).log(() -> "Saving processedOrder: {} saved to Database");
        repository.save(processedOrder);

        log.atInfo().addArgument(order.id()).log(() -> "Order: {} processed and saved to Database");
    }

    @Cacheable(value = "orders", key = "#id")
    public Optional<ProcessedOrder> getOrderById(String id) {
        log.atInfo().addArgument(id).log(() -> "Fetching order: {} from Database...");
        return repository.findById(id);
    }
}
