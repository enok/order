package br.com.levva.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public record ProcessedOrder(@Id String id,
                             LocalDateTime orderDate,
                             OrderStatus status,
                             List<OrderItem> items,
                             double total) implements Serializable {
}
