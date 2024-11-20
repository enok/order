package br.com.levva.order.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record Order(
        @NotBlank(message = "Id cannot be empty.")
        String id,

        @NotNull(message = "OrderDate cannot be null.")
        LocalDateTime orderDate,

        @NotEmpty(message = "Items cannot be empty.")
        @Valid
        List<OrderItem> items,

        @NotNull(message = "OrderStatus cannot be null.")
        OrderStatus status
) implements Serializable {

    @JsonCreator
    public Order(
            @JsonProperty("id") String id,
            @JsonProperty("orderDate") LocalDateTime orderDate,
            @JsonProperty("items") List<OrderItem> items
    ) {
        this(id, orderDate, items, OrderStatus.PENDING);
    }
}
