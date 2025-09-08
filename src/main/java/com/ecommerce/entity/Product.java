package com.ecommerce.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    private String category;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;
    
    @Column(name = "hybrid_score", precision = 3, scale = 2)
    private BigDecimal hybridScore;
    
    @Column(name = "total_reviews")
    private Integer totalReviews;
    
    @Column(name = "shipping_score", precision = 3, scale = 2)
    private BigDecimal shippingScore;
    
    @Column(name = "quality_score", precision = 3, scale = 2)
    private BigDecimal qualityScore;
    
    @Column(name = "performance_score", precision = 3, scale = 2)
    private BigDecimal performanceScore;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Constructors
    public Product() {
    }

    public Product(Long id, String name, String description, BigDecimal price, String category, 
                   String imageUrl, BigDecimal averageRating, BigDecimal hybridScore, 
                   Integer totalReviews, BigDecimal shippingScore, BigDecimal qualityScore, 
                   BigDecimal performanceScore, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
        this.hybridScore = hybridScore;
        this.totalReviews = totalReviews;
        this.shippingScore = shippingScore;
        this.qualityScore = qualityScore;
        this.performanceScore = performanceScore;
        this.reviews = reviews;
    }

    // Getter methods
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public BigDecimal getHybridScore() {
        return hybridScore;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public BigDecimal getShippingScore() {
        return shippingScore;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public void setHybridScore(BigDecimal hybridScore) {
        this.hybridScore = hybridScore;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public void setShippingScore(BigDecimal shippingScore) {
        this.shippingScore = shippingScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }

    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
} 