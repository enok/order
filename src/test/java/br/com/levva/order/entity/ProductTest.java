package br.com.levva.order.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenProductIsValid() {
        // Given
        Product validProduct = new Product("p1", "Product 1", 100.0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(validProduct);

        // Then
        assertEquals(0, violations.size(), "There should be no validation errors for a valid product.");
    }

    @Test
    void shouldFailValidation_whenIdIsBlank() {
        // Given
        Product invalidProduct = new Product(" ", "Product 1", 100.0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Id cannot be empty.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenNameIsBlank() {
        // Given
        Product invalidProduct = new Product("p1", " ", 100.0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Name cannot be empty.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenPriceIsNull() {
        // Given
        Product invalidProduct = new Product("p1", "Product 1", null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Price cannot be null.", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidation_whenPriceIsNotPositive() {
        // Given
        Product invalidProduct = new Product("p1", "Product 1", -100.0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(invalidProduct);

        // Then
        assertEquals(1, violations.size());
        assertEquals("Price must be bigger than 0.", violations.iterator().next().getMessage());
    }
}
