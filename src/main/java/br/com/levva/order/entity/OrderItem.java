package br.com.levva.order.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record OrderItem(
        @NotNull(message = "Product cannot be null.")
        @Valid
        Product product,

        @NotNull(message = "Quantity cannot be null.")
        @Min(value = 1, message = "Quantity must be at least 1.")
        Integer quantity,

        @NotNull(message = "SubTotal cannot be null.")
        @Positive(message = "SubTotal must be bigger than 0.")
        Double subTotal
) implements Serializable {

    @JsonCreator
    public OrderItem(
        @JsonProperty("product") Product product,
        @JsonProperty("quantity") Integer quantity
    ) {
        this(product, quantity, product.price() * quantity);
    }

//    @JsonProperty("subTotal")
//    public Double calculateSubTotal() {
//        return product.price() * quantity;
//    }
}
