//Bu controller, kullanıcıların ürünlere yorum yapmasını, yorumları görmesini ve silmesini sağlar.
package com.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        try {
            System.out.println("Received review request - Product: " + request.getProductId() +
                             ", Stars: " + request.getStarRating() + 
                             ", Comment: " + request.getComment());
            
            ReviewResponse response = reviewService.createReview(request);
            
            System.out.println("Review created successfully!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Review creation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable Long productId) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/sentiment/{sentimentLabel}")
    public ResponseEntity<List<ReviewResponse>> getReviewsBySentimentLabel(@PathVariable String sentimentLabel) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsBySentimentLabel(sentimentLabel);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        try {
            ReviewResponse review = reviewService.getReviewById(reviewId);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 