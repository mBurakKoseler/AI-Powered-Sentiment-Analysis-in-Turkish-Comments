package com.ecommerce.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false, length = 2000)
    private String comment;
    
    @Column(name = "star_rating", nullable = false)
    private Integer starRating;
    
    @Column(name = "sentiment_score", precision = 3, scale = 2)
    private BigDecimal sentimentScore;
    
    @Column(name = "sentiment_label", length = 20)
    private String sentimentLabel;
    
    @Column(name = "hybrid_score", precision = 3, scale = 2)
    private BigDecimal hybridScore;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Review() {
    }

    public Review(Long id, Product product, String comment, Integer starRating, 
                  BigDecimal sentimentScore, String sentimentLabel, BigDecimal hybridScore, 
                  String category, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.comment = comment;
        this.starRating = starRating;
        this.sentimentScore = sentimentScore;
        this.sentimentLabel = sentimentLabel;
        this.hybridScore = hybridScore;
        this.category = category;
        this.createdAt = createdAt;
    }

    // Getter methods
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
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

    public void setProduct(Product product) {
        this.product = product;
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