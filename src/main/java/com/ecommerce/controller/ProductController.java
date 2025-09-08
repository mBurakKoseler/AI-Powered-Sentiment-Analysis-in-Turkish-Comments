//uygulamanın ürünlerle ilgili tüm REST API uç noktalarını yöneten controller
package com.ecommerce.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductResponse> responses = products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            return ResponseEntity.ok(convertToResponse(product));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable("category") String category) {
        try {
            List<Product> products = productRepository.findByCategory(category);
            List<ProductResponse> responses = products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(convertToResponse(savedProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            Product product = productRepository.findById(productId)
                .orElse(null);
            
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Cascade delete will automatically delete related reviews
            productRepository.delete(product);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log the error instead of printing stack trace
            System.err.println("Error deleting product: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setAverageRating(product.getAverageRating());
        response.setHybridScore(product.getHybridScore());
        response.setTotalReviews(product.getTotalReviews());
        response.setShippingScore(product.getShippingScore());
        response.setQualityScore(product.getQualityScore());
        response.setPerformanceScore(product.getPerformanceScore());
        
        // Reviews'ları da ekle
        if (product.getReviews() != null) {
            response.setReviews(product.getReviews().stream()
                .map(review -> {
                    ReviewResponse reviewResponse = new ReviewResponse();
                    reviewResponse.setId(review.getId());
                    reviewResponse.setProductId(review.getProduct().getId());
                    reviewResponse.setComment(review.getComment());
                    reviewResponse.setStarRating(review.getStarRating());
                    reviewResponse.setSentimentScore(review.getSentimentScore());
                    reviewResponse.setSentimentLabel(review.getSentimentLabel());
                    reviewResponse.setHybridScore(review.getHybridScore());
                    reviewResponse.setCategory(review.getCategory());
                    reviewResponse.setCreatedAt(review.getCreatedAt());
                    return reviewResponse;
                })
                .collect(Collectors.toList()));
        }
        
        return response;
    }
} 