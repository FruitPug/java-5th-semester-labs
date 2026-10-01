package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.application.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> save(@Valid @RequestBody CreateProductRequest productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @Valid @RequestBody UpdateProductRequest productDto,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(productService.updateProduct(productDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
