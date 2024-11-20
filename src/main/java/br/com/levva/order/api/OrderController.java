package br.com.levva.order.api;

import br.com.levva.order.entity.Order;
import br.com.levva.order.entity.ProcessedOrder;
import br.com.levva.order.service.OrderService;
import br.com.levva.order.service.RabbitMQPublisher;
import br.com.levva.order.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RedisService redisService;
    private final RabbitMQPublisher rabbitMQPublisher;
    private final OrderService orderService;

    @Value("${order.expiration-seconds}")
    private long expirationSeconds;

    public OrderController(RedisService redisService, RabbitMQPublisher rabbitMQPublisher, OrderService orderService) {
        this.redisService = redisService;
        this.rabbitMQPublisher = rabbitMQPublisher;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody Order order) {
        log.atDebug().addArgument(order).log(() -> "Creating order: {}");

        if (redisService.isDuplicate(order.id(), expirationSeconds)) {
            log.atWarn().addArgument(order).log(() -> "Order: {} is DUPLICATED");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(null);
        }

        rabbitMQPublisher.publishOrder(order);

        URI location = URI.create(String.format("/orders/%s", order.id()));
        log.atTrace().addArgument(location).log(() -> "Location: {}");

        return ResponseEntity.accepted().header("Location", location.toString()).build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ProcessedOrder> getOrder(@PathVariable String orderId) {
        log.atDebug().addArgument(orderId).log(() -> "Getting order by id: {}");

        Optional<ProcessedOrder> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}