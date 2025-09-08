package com.ecommerce.dto;
//veri alımı ve doğrulaması
import jakarta.validation.constraints.*;

public class ReviewRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;
    
    @NotBlank(message = "Comment cannot be blank")
    @Size(min = 10, max = 2000, message = "Comment must be between 10 and 2000 characters")
    private String comment;
    
    @NotNull(message = "Star rating cannot be null")
    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must be at most 5")
    private Integer starRating;

    // Getter methods
    public Long getProductId() {
        return productId;
    }

    public String getComment() {
        return comment;
    }

    public Integer getStarRating() {
        return starRating;
    }

    // Setter methods
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }
} 