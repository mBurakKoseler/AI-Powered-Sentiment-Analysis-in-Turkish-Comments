package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.ReviewRequest;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;

@Service
public class ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;
    
    @Autowired
    private HybridScoreService hybridScoreService;
    
    @Autowired
    private KeywordService keywordService;
    
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        // Validation
        if (request == null) {
            throw new IllegalArgumentException("Review request cannot be null");
        }
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be null or empty");
        }
        if (request.getStarRating() == null || request.getStarRating() < 1 || request.getStarRating() > 5) {
            throw new IllegalArgumentException("Star rating must be between 1 and 5");
        }
        
        logger.info("Creating review for product ID: {}", request.getProductId());
        
        // Ürünü bul
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));
        
        // Önce yorumu temel bilgilerle kaydet
        Review review = new Review();
        review.setProduct(product);
        review.setComment(request.getComment().trim());
        review.setStarRating(request.getStarRating());
        
        // Geçici sentiment değerleri (yıldız puanına göre)
        BigDecimal tempSentimentScore = BigDecimal.valueOf(request.getStarRating()).divide(BigDecimal.valueOf(5), 2, BigDecimal.ROUND_HALF_UP);
        review.setSentimentScore(tempSentimentScore);
        review.setSentimentLabel("neutral");
        
        // Geçici hibrit puan
        BigDecimal tempHybridScore = hybridScoreService.calculateHybridScore(request.getStarRating(), tempSentimentScore);
        review.setHybridScore(tempHybridScore);
        
        // Kategori belirle
        String category = determineCategory(request.getComment());
        review.setCategory(category);
        
        // Önce kaydet
        Review savedReview = reviewRepository.save(review);
        logger.info("Review created successfully with ID: {}", savedReview.getId());
        
        // Şimdi sentiment analysis yap (arka planda - Python API'ye gönder)
        try {
            logger.info("Sending comment to Python API for sentiment analysis: {}", request.getComment());
            SentimentAnalysisService.SentimentResult sentimentResult = sentimentAnalysisService.analyzeSentiment(request.getComment());
            
            // Sentiment sonuçlarını al
            BigDecimal sentimentScore = sentimentResult.getNormalizedScore();
            String sentimentLabel = sentimentResult.getLabel();
            BigDecimal originalScore = sentimentResult.getOriginalScore();
            
            logger.info("📊 Sentiment Analysis Results:");
            logger.info("   - Label: {}", sentimentLabel);
            logger.info("   - Original Score: {}", originalScore);
            logger.info("   - Normalized Sentiment Score: {}", sentimentScore);
            
            // Sentiment bilgilerini güncelle (artık değiştirmiyoruz)
            savedReview.setSentimentScore(sentimentScore);
            savedReview.setSentimentLabel(sentimentLabel);
            
            // Hibrit puanı yeni sistem ile hesapla (nötr duygu = sadece yıldız puanı)
            BigDecimal hybridScore = hybridScoreService.calculateHybridScore(request.getStarRating(), sentimentScore, sentimentLabel);
            savedReview.setHybridScore(hybridScore);
            
            logger.info("🎯 Hybrid Score Calculation:");
            logger.info("   - Star Rating: {} (normalized: {})", request.getStarRating(), 
                       BigDecimal.valueOf(request.getStarRating()).divide(BigDecimal.valueOf(5), 2, BigDecimal.ROUND_HALF_UP));
            logger.info("   - Sentiment Score: {} (label: {})", sentimentScore, sentimentLabel);
            logger.info("   - Final Hybrid Score: {}", hybridScore);
            
            // Güncellenmiş review'ı kaydet
            Review updatedReview = reviewRepository.save(savedReview);
            logger.info("✅ Python API sentiment analysis completed and saved to database:");
            logger.info("   - Review ID: {}", updatedReview.getId());
            logger.info("   - Sentiment Label: {}", updatedReview.getSentimentLabel());
            logger.info("   - Sentiment Score: {}", updatedReview.getSentimentScore());
            logger.info("   - Hybrid Score: {}", updatedReview.getHybridScore());
            
        } catch (Exception e) {
            logger.error("❌ Python API sentiment analysis failed, but review was saved: {}", e.getMessage());
            // Sentiment analysis başarısız olsa bile yorum kaydedildi - kullanıcı deneyimi kesintisiz
        }
        
        // Ürün puanlarını güncelle
        updateProductScores(product.getId());
        
        return convertToResponse(savedReview);
    }
    
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        logger.debug("Fetching reviews for product ID: {}", productId);
        
        return reviewRepository.findByProductId(productId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public List<ReviewResponse> getReviewsBySentimentLabel(String sentimentLabel) {
        if (sentimentLabel == null || sentimentLabel.trim().isEmpty()) {
            throw new IllegalArgumentException("Sentiment label cannot be null or empty");
        }
        
        logger.debug("Fetching reviews with sentiment label: {}", sentimentLabel);
        
        return reviewRepository.findBySentimentLabel(sentimentLabel.trim())
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public ReviewResponse getReviewById(Long reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        
        logger.debug("Fetching review with ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));
        return convertToResponse(review);
    }
    
    @Transactional
    public void deleteReview(Long reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        
        logger.info("Deleting review with ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));
        
        // Ürün ID'sini al (silmeden önce)
        Long productId = review.getProduct().getId();
        
        // Yorumu sil
        reviewRepository.delete(review);
        logger.info("Review deleted successfully with ID: {}", reviewId);
        
        // Ürün puanlarını güncelle
        updateProductScores(productId);
    }
    
    @Transactional
    public void updateProductScores(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        logger.debug("Updating product scores for product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        // Yorum sayısını kontrol et
        Long reviewCount = reviewRepository.getReviewCountByProductId(productId);
        product.setTotalReviews(reviewCount.intValue());
        logger.debug("Total reviews count: {}", reviewCount);
        
        // Eğer hiç yorum yoksa tüm puanları sıfırla
        if (reviewCount == 0) {
            product.setAverageRating(BigDecimal.ZERO);
            product.setHybridScore(BigDecimal.ZERO);
            product.setShippingScore(BigDecimal.ZERO);
            product.setQualityScore(BigDecimal.ZERO);
            product.setPerformanceScore(BigDecimal.ZERO);
            logger.info("No reviews found, resetting all scores to zero for product ID: {}", productId);
        } else {
            // Ortalama yıldız puanı
            BigDecimal avgStarRating = reviewRepository.getAverageStarRatingByProductId(productId);
            if (avgStarRating != null) {
                product.setAverageRating(avgStarRating);
                logger.debug("Updated average star rating: {}", avgStarRating);
            }
            
            // Ortalama duygu skoru
            BigDecimal avgSentimentScore = reviewRepository.getAverageSentimentScoreByProductId(productId);
            
            // Hibrit puan hesapla (ürün seviyesinde eşit ağırlık kullan)
            BigDecimal hybridScore = hybridScoreService.calculateProductHybridScore(avgStarRating, avgSentimentScore);
            product.setHybridScore(hybridScore);
            logger.debug("Updated hybrid score: {}", hybridScore);
            
            // Kategori bazlı puanları hesapla
            calculateCategoryScores(product);
        }
        
        productRepository.save(product);
        logger.info("Product scores updated successfully for product ID: {}", productId);
    }
    
    private void calculateCategoryScores(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        
        // Yeni kategori bazlı puanları hesapla
        BigDecimal qualityDurabilityScore = calculateCategoryScore(reviews, "quality_durability");
        BigDecimal usagePerformanceScore = calculateCategoryScore(reviews, "usage_performance");
        BigDecimal serviceDeliveryScore = calculateCategoryScore(reviews, "service_delivery");
        
        // Eski alanları yeni kategorilerle güncelle (geriye uyumluluk için)
        product.setShippingScore(serviceDeliveryScore);  // Hizmet & Teslimat
        product.setQualityScore(qualityDurabilityScore); // Kalite & Dayanıklılık
        product.setPerformanceScore(usagePerformanceScore); // Kullanım & Performans
        
        logger.debug("Category scores - Quality & Durability: {}, Usage & Performance: {}, Service & Delivery: {}", 
                   qualityDurabilityScore, usagePerformanceScore, serviceDeliveryScore);
    }
    
    private BigDecimal calculateCategoryScore(List<Review> reviews, String category) {
        // Birden fazla kategoriye ait yorumları da dahil et
        List<Review> categoryReviews = reviews.stream()
            .filter(r -> {
                String reviewCategory = r.getCategory();
                if (reviewCategory == null) return false;
                
                // Tam eşleşme veya virgülle ayrılmış kategorilerde eşleşme
                return reviewCategory.equals(category) || 
                       reviewCategory.contains(category + ",") || 
                       reviewCategory.contains("," + category) ||
                       reviewCategory.contains("," + category + ",");
            })
            .collect(Collectors.toList());
        
        if (categoryReviews.isEmpty()) {
            return BigDecimal.ZERO; // Kategori için yorum yoksa 0 döndür
        }
        
        BigDecimal totalScore = categoryReviews.stream()
            .map(Review::getHybridScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageScore = totalScore.divide(BigDecimal.valueOf(categoryReviews.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        logger.debug("Category '{}' score calculation: {} reviews, total score: {}, average: {}", 
                   category, categoryReviews.size(), totalScore, averageScore);
        
        return averageScore;
    }
    
    private String determineCategory(String comment) {
        // Yeni kategori sistemi: Birden fazla kategoriye ait olabilir
        List<String> matchedCategories = new ArrayList<>();
        
        // 1. Kalite & Dayanıklılık
        if (keywordService.containsKeyword("quality_durability", comment)) {
            matchedCategories.add("quality_durability");
        }
        
        // 2. Kullanım & Performans  
        if (keywordService.containsKeyword("usage_performance", comment)) {
            matchedCategories.add("usage_performance");
        }
        
        // 3. Hizmet & Teslimat (Müşteri Deneyimi)
        if (keywordService.containsKeyword("service_delivery", comment)) {
            matchedCategories.add("service_delivery");
        }
        
        // Eğer hiç kategori eşleşmezse genel kategori
        if (matchedCategories.isEmpty()) {
            return "general";
        }
        
        // Birden fazla kategori eşleşirse, virgülle ayırarak döndür
        return String.join(",", matchedCategories);
    }
    
    
    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProduct().getId());
        response.setComment(review.getComment());
        response.setStarRating(review.getStarRating());
        response.setSentimentScore(review.getSentimentScore());
        response.setSentimentLabel(review.getSentimentLabel());
        response.setHybridScore(review.getHybridScore());
        response.setCategory(review.getCategory());
        response.setCreatedAt(review.getCreatedAt());
        
        logger.debug("📋 Converting Review to Response:");
        logger.debug("   - ID: {}", response.getId());
        logger.debug("   - Sentiment Label: {}", response.getSentimentLabel());
        logger.debug("   - Sentiment Score: {}", response.getSentimentScore());
        logger.debug("   - Hybrid Score: {}", response.getHybridScore());
        
        return response;
    }
} 