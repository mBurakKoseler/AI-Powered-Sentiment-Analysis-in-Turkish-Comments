package com.ecommerce.dto;
//API’nden dış dünyaya döneceğin ürün bilgisini temsil eden bir DTO (Data Transfer Object)
import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private BigDecimal averageRating;
    private BigDecimal hybridScore;
    private Integer totalReviews;
    private BigDecimal shippingScore;
    private BigDecimal qualityScore;
    private BigDecimal performanceScore;
    private List<ReviewResponse> reviews;

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

    public List<ReviewResponse> getReviews() {
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

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
} 