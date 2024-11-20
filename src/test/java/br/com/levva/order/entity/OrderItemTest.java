package br.com.levva.order.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenOrderItemIsValid() {
        // Given
        Product validProduct = new Product("p1", "Product 1", 100.0);
        OrderItem validOrderItem = new OrderItem(validProduct, 2);

        // When
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(validOrderItem);

        // Then
        assertEquals(0, violations.size(), "There should be no validation errors for a valid OrderItem.");
    }

    @Test
    void shouldFailValidation_whenProductIsNull() {
        // Given
        OrderItem invalidOrderItem = new OrderItem(null, 2, 1.0);

        // When
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(invalidOrderItem);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Product cannot be null.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenQuantityIsNull() {
        // Given
        Product validProduct = new Product("p1", "Product 1", 100.0);
        OrderItem invalidOrderItem = new OrderItem(validProduct, null, 1.0);

        // When
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(invalidOrderItem);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Quantity cannot be null.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenQuantityIsLessThanOne() {
        // Given
        Product validProduct = new Product("p1", "Product 1", 100.0);
        OrderItem invalidOrderItem = new OrderItem(validProduct, 0);

        // When
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(invalidOrderItem);
        List<String> violationList = violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();

        // Then
        assertEquals(2, violationList.size());

        assertEquals("Quantity must be at least 1.", violationList.get(0));
        assertEquals("SubTotal must be bigger than 0.", violationList.get(1));
    }

    @Test
    void shouldFailValidation_whenSubTotalIsNegative() {
        // Given
        Product validProduct = new Product("p1", "Product 1", -100.0); // Negative price will create a negative subtotal
        OrderItem invalidOrderItem = new OrderItem(validProduct, 2);

        // When
        Set<ConstraintViolation<OrderItem>> violations = validator.validate(invalidOrderItem);
        List<String> violationList = violations.stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .toList();

        // Then
        assertEquals(2, violationList.size());
        assertEquals("Price must be bigger than 0.", violationList.get(0));
        assertEquals("SubTotal must be bigger than 0.", violationList.get(1));
    }
}
