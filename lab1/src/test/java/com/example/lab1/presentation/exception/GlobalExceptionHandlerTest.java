package com.example.lab1.presentation.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest_WithFieldErrors() {
        request.setRequestURI("/api/v1/products");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createProductRequest");
        bindingResult.addError(new FieldError("createProductRequest", "name", "must not be blank"));
        bindingResult.addError(new FieldError("createProductRequest", "price", "must be greater than 0"));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(URI.create("https://cosmo-cats.market/errors/validation"), body.type());
        assertEquals("Validation failed", body.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals("Field 'name' must not be blank. Field 'price' must be greater than 0.", body.detail());
        assertEquals(URI.create("/api/v1/products"), body.instance());
    }

    @Test
    void handleProductNotFound_ShouldReturnNotFound_WithErrorDetails() {
        UUID productId = UUID.randomUUID();
        request.setRequestURI("/api/v1/products/" + productId);
        ProductNotFoundException exception = new ProductNotFoundException(productId);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleProductNotFound(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(URI.create("https://cosmo-cats.market/errors/not-found"), body.type());
        assertEquals("Product not found", body.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.status());
        assertEquals(exception.getMessage(), body.detail());
        assertEquals(URI.create("/api/v1/products/" + productId), body.instance());
    }

    @Test
    void handleCategoryNotFound_ShouldReturnNotFound_WithErrorDetails() {
        UUID categoryId = UUID.randomUUID();
        request.setRequestURI("/api/v1/categories/" + categoryId);
        CategoryNotFoundException exception = new CategoryNotFoundException(categoryId);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCategoryNotFound(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(URI.create("https://cosmo-cats.market/errors/not-found"), body.type());
        assertEquals("Category not found", body.title());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.status());
        assertEquals(exception.getMessage(), body.detail());
        assertEquals(URI.create("/api/v1/categories/" + categoryId), body.instance());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest_WithErrorDetails() {
        request.setRequestURI("/api/v1/products");
        IllegalArgumentException exception = new IllegalArgumentException("Page index must not be less than zero");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(URI.create("https://cosmo-cats.market/errors/validation"), body.type());
        assertEquals("Validation failed", body.title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.status());
        assertEquals("Page index must not be less than zero", body.detail());
        assertEquals(URI.create("/api/v1/products"), body.instance());
    }

    @Test
    void handleUnexpectedException_ShouldReturnInternalServerError() {
        request.setRequestURI("/api/v1/unexpected");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpectedException(request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(URI.create("https://cosmo-cats.market/errors/internal"), body.type());
        assertEquals("Internal server error", body.title());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.status());
        assertEquals("An unexpected error has occurred.", body.detail());
        assertEquals(URI.create("/api/v1/unexpected"), body.instance());
    }
}
