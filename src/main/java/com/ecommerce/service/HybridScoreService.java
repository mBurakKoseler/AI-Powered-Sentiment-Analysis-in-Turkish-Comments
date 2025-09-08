package com.ecommerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HybridScoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(HybridScoreService.class);
    
    // Hibrit skor hesaplama: Yıldız puanı ve sentiment puanının aritmetik ortalaması
    // Ağırlık faktörleri - eşit ağırlık (0.5 - 0.5)
    private static final double STAR_RATING_WEIGHT = 0.5; // Yıldız puanının ağırlığı
    private static final double SENTIMENT_WEIGHT = 0.5;   // Duygu analizinin ağırlığı
    
    /**
     * Tek bir yorum için hibrit puan hesaplar
     * Pozitif/Negatif duygu: Yıldız puanı ve sentiment puanının aritmetik ortalaması
     * Nötr duygu: Sadece yıldız puanı (AI emin değil, kullanıcıya güven)
     * 
     * @param starRating Kullanıcının verdiği yıldız puanı (1-5)
     * @param sentimentScore AI modelinin normalize edilmiş duygu skoru (0.0-1.0)
     * @param sentimentLabel Duygu etiketi (positive, negative, neutral)
     * @return Hibrit puan (0.0-1.0 arası)
     */
    public BigDecimal calculateHybridScore(Integer starRating, BigDecimal sentimentScore, String sentimentLabel) {
        // Null kontrolü
        if (starRating == null) {
            throw new IllegalArgumentException("Star rating cannot be null");
        }
        
        // Yıldız puanını 0-1 aralığına normalize et
        BigDecimal normalizedStarRating = normalizeStarRating(starRating);
        
        // Nötr duygu durumunda sadece yıldız puanını kullan
        if ("neutral".equalsIgnoreCase(sentimentLabel)) {
            logger.debug("Neutral sentiment detected, using only star rating: {}", normalizedStarRating);
            return normalizedStarRating;
        }
        
        // Sentiment score null kontrolü (pozitif/negatif için)
        if (sentimentScore == null) {
            logger.warn("Sentiment score is null for non-neutral sentiment, using only star rating");
            return normalizedStarRating;
        }
        
        // Pozitif/Negatif duygu durumunda hibrit puan hesapla (eşit ağırlık: 0.5 - 0.5)
        BigDecimal weightedStar = normalizedStarRating.multiply(BigDecimal.valueOf(STAR_RATING_WEIGHT));
        BigDecimal weightedSentiment = sentimentScore.multiply(BigDecimal.valueOf(SENTIMENT_WEIGHT));
        
        BigDecimal hybridScore = weightedStar.add(weightedSentiment);
        
        // 0-1 aralığında sınırla
        hybridScore = clampScore(hybridScore);
        
        logger.debug("Hibrit skor hesaplama ({}): Yıldız={}, Sentiment={}, Hibrit={}", 
                    sentimentLabel, normalizedStarRating, sentimentScore, hybridScore);
        
        return hybridScore.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Geriye uyumluluk için eski metod (sentimentLabel olmadan)
     */
    public BigDecimal calculateHybridScore(Integer starRating, BigDecimal sentimentScore) {
        return calculateHybridScore(starRating, sentimentScore, "unknown");
    }
    
    /**
     * Ürün için genel hibrit puan hesaplar (tüm yorumların ortalaması)
     * Yıldız puanı ve sentiment puanının aritmetik ortalaması
     * 
     * @param averageStarRating Ortalama yıldız puanı
     * @param averageSentimentScore Ortalama duygu skoru
     * @return Ürün hibrit puanı (0.0-1.0 arası)
     */
    public BigDecimal calculateProductHybridScore(BigDecimal averageStarRating, BigDecimal averageSentimentScore) {
        // Null kontrolü
        if (averageStarRating == null) {
            logger.warn("Average star rating is null, using neutral score");
            return BigDecimal.valueOf(0.5);
        }
        
        if (averageSentimentScore == null) {
            // Duygu analizi yapılamadıysa sadece yıldız puanını kullan
            logger.warn("Average sentiment score is null, using only star rating");
            return normalizeStarRating(averageStarRating);
        }
        
        // Yıldız puanını 0-1 aralığına normalize et
        BigDecimal normalizedStarRating = normalizeStarRating(averageStarRating);
        
        // Aritmetik ortalama hesapla (eşit ağırlık: 0.5 - 0.5)
        BigDecimal weightedStar = normalizedStarRating.multiply(BigDecimal.valueOf(STAR_RATING_WEIGHT));
        BigDecimal weightedSentiment = averageSentimentScore.multiply(BigDecimal.valueOf(SENTIMENT_WEIGHT));
        
        BigDecimal hybridScore = weightedStar.add(weightedSentiment);
        
        // 0-1 aralığında sınırla
        hybridScore = clampScore(hybridScore);
        
        logger.debug("Ürün hibrit skor hesaplama: Ortalama Yıldız={}, Ortalama Sentiment={}, Hibrit={}", 
                    normalizedStarRating, averageSentimentScore, hybridScore);
        
        return hybridScore.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Yıldız puanını 0-1 aralığına normalize eder
     * 
     * @param starRating Yıldız puanı (1-5 veya 0-5 arası)
     * @return Normalize edilmiş puan (0.0-1.0)
     */
    private BigDecimal normalizeStarRating(Number starRating) {
        double rating = starRating.doubleValue();
        
        // 1-5 aralığında sınırla
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;
        
        // 0-1 aralığına normalize et
        return BigDecimal.valueOf(rating).divide(BigDecimal.valueOf(5), 4, RoundingMode.HALF_UP);
    }
    
    /**
     * Skoru 0-1 aralığında sınırlar
     * 
     * @param score Hesaplanan skor
     * @return Sınırlanmış skor
     */
    private BigDecimal clampScore(BigDecimal score) {
        if (score.compareTo(BigDecimal.ONE) > 0) {
            return BigDecimal.ONE;
        } else if (score.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return score;
    }
    
    /**
     * Ağırlık faktörlerini dinamik olarak ayarlar
     * 
     * @param starRatingWeight Yıldız puanı ağırlığı (0.0-1.0)
     * @param sentimentWeight Duygu analizi ağırlığı (0.0-1.0)
     */
    public void setWeights(double starRatingWeight, double sentimentWeight) {
        if (starRatingWeight < 0 || starRatingWeight > 1 || sentimentWeight < 0 || sentimentWeight > 1) {
            throw new IllegalArgumentException("Weights must be between 0.0 and 1.0");
        }
        
        if (Math.abs(starRatingWeight + sentimentWeight - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 1.0");
        }
        
        // Bu değerler runtime'da değiştirilebilir
        // Şimdilik sabit değerler kullanıyoruz
        logger.info("Weight adjustment requested: star={}, sentiment={}", starRatingWeight, sentimentWeight);
    }
} 