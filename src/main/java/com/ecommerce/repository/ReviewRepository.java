package com.ecommerce.repository;

import com.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProductId(Long productId);
    
    @Query("SELECT AVG(r.starRating) FROM Review r WHERE r.product.id = :productId")
    BigDecimal getAverageStarRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT AVG(r.sentimentScore) FROM Review r WHERE r.product.id = :productId")
    BigDecimal getAverageSentimentScoreByProductId(@Param("productId") Long productId);
    
    @Query("SELECT AVG(r.hybridScore) FROM Review r WHERE r.product.id = :productId")
    BigDecimal getAverageHybridScoreByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long getReviewCountByProductId(@Param("productId") Long productId);
    
    List<Review> findBySentimentLabel(String sentimentLabel);

    @Query("SELECT r FROM Review r WHERE r.hybridScore >= :minScore")
    List<Review> findByHybridScoreGreaterThanEqual(@Param("minScore") BigDecimal minScore);
} 