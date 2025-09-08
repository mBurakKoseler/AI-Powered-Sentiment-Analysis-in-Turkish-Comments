package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private Long productId;
    private String comment;
    private Integer starRating;
    private BigDecimal sentimentScore;
    private String sentimentLabel;
    private BigDecimal hybridScore;
    private String category;
    private LocalDateTime createdAt;

    // Getter methods
    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getComment() {
        return comment;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public BigDecimal getSentimentScore() {
        return sentimentScore;
    }

    public String getSentimentLabel() {
        return sentimentLabel;
    }

    public BigDecimal getHybridScore() {
        return hybridScore;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public void setSentimentScore(BigDecimal sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public void setSentimentLabel(String sentimentLabel) {
        this.sentimentLabel = sentimentLabel;
    }

    public void setHybridScore(BigDecimal hybridScore) {
        this.hybridScore = hybridScore;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 