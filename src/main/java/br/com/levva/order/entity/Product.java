package br.com.levva.order.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record Product(
        @NotBlank(message = "Id cannot be empty.")
        @JsonProperty("id")
        String id,

        @NotBlank(message = "Name cannot be empty.")
        @JsonProperty("name")
        String name,

        @NotNull(message = "Price cannot be null.")
        @Positive(message = "Price must be bigger than 0.")
        @JsonProperty("price")
        Double price) implements Serializable {
}
