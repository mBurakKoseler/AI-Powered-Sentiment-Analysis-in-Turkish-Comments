package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating")
    List<Product> findByAverageRatingGreaterThanEqual(@Param("minRating") BigDecimal minRating);
    
    @Query("SELECT p FROM Product p WHERE p.hybridScore >= :minScore")
    List<Product> findByHybridScoreGreaterThanEqual(@Param("minScore") BigDecimal minScore);
    
    @Query("SELECT AVG(p.hybridScore) FROM Product p")
    BigDecimal getAverageHybridScore();
} 