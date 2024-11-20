package br.com.levva.order.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Document(collection = "orders")
public record ProcessedOrder(@Id String id,
                             LocalDateTime orderDate,
                             List<OrderItem> items,
                             OrderStatus status,
                             double total) implements Serializable {
}
