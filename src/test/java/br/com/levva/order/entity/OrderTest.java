package br.com.levva.order.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenOrderIsValid() {
        // Given
        OrderItem validItem = new OrderItem(
                new Product("p1", "Product 1", 100.0),
                2,
                200.0
        );

        Order validOrder = new Order(
                "12345",
                LocalDateTime.now(),
                List.of(validItem),
                OrderStatus.PENDING
        );

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(validOrder);

        // Then
        assertEquals(0, violations.size(), "There should be no validation errors for a valid order.");
    }

    @Test
    void shouldFailValidation_whenIdIsBlank() {
        // Given
        Order invalidOrder = new Order(
                " ",
                LocalDateTime.now(),
                List.of(new OrderItem(
                        new Product("p1", "Product 1", 100.0),
                        2,
                        200.0
                )),
                OrderStatus.PENDING
        );

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(invalidOrder);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Id cannot be empty.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenOrderDateIsNull() {
        // Given
        Order invalidOrder = new Order(
                "12345",
                null,
                List.of(new OrderItem(
                        new Product("p1", "Product 1", 100.0),
                        2,
                        200.0
                )),
                OrderStatus.PENDING
        );

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(invalidOrder);

        // Then
        assertEquals(1, violations.size());
        assertEquals("OrderDate cannot be null.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenItemsAreEmpty() {
        // Given
        Order invalidOrder = new Order(
                "12345",
                LocalDateTime.now(),
                List.of(),
                OrderStatus.PENDING
        );

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(invalidOrder);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Items cannot be empty.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenStatusIsNull() {
        // Given
        Order invalidOrder = new Order(
                "12345",
                LocalDateTime.now(),
                List.of(new OrderItem(
                        new Product("p1", "Product 1", 100.0),
                        2,
                        200.0
                )),
                null
        );

        // When
        Set<ConstraintViolation<Order>> violations = validator.validate(invalidOrder);

        // Then
        assertEquals(1, violations.size());
        assertEquals("OrderStatus cannot be null.", violations.iterator().next().getMessage());
    }
}
