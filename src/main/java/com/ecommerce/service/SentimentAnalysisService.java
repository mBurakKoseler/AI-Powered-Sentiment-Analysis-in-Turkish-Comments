package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SentimentAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalysisService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${python.api.url:http://localhost:5000}")
    private String pythonApiUrl;

    public SentimentAnalysisService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();

        // Timeout ayarları
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 saniye
        factory.setReadTimeout(30000);    // 30 saniye
        this.restTemplate.setRequestFactory(factory);
    }

    public SentimentResult analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.warn("Boş metin gönderildi");
            return new SentimentResult("neutral", BigDecimal.ZERO, BigDecimal.ZERO);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            logger.info("Python API'ye gönderilen metin: {}", text);

            ResponseEntity<String> response = restTemplate.postForEntity(pythonApiUrl + "/predict", request, String.class);

            logger.info("Python API Raw Response: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());

                // Yeni sade yapı: root direkt sonucu içeriyor
                String label = root.get("label").asText();
                String sentiment = root.get("sentiment").asText();
                double score = root.get("score").asDouble();

                BigDecimal normalizedScore;
                if ("LABEL_1".equals(label)) {
                    normalizedScore = BigDecimal.valueOf(score);
                    logger.info("📊 Positive sentiment detected (LABEL_1), using score directly: {}", normalizedScore);
                } else if ("LABEL_2".equals(label)) {
                    normalizedScore = BigDecimal.ONE.subtract(BigDecimal.valueOf(score));
                    logger.info("📊 Negative sentiment detected (LABEL_2), converting to positive: 1 - {} = {}", score, normalizedScore);
                } else {
                    normalizedScore = new BigDecimal("0.5");
                    logger.info("📊 Neutral sentiment detected (LABEL_0), using neutral score: {}", normalizedScore);
                }

                logger.info("✅ Python API Sonucu - Label: {}, Sentiment: {}, Skor: {}, Normalize: {}",
                        label, sentiment, score, normalizedScore);

                return new SentimentResult(sentiment, BigDecimal.valueOf(score), normalizedScore);
            }

            throw new RuntimeException("Python API'den geçersiz yanıt");

        } catch (Exception e) {
            logger.error("Python API sentiment analizi hatası: {}", e.getMessage(), e);
            throw new RuntimeException("Python API sentiment analizi başarısız: " + e.getMessage(), e);
        }
    }

    public static class SentimentResult {
        private final String label;
        private final BigDecimal originalScore;
        private final BigDecimal normalizedScore;
        
        public SentimentResult(String label, BigDecimal originalScore, BigDecimal normalizedScore) {
            this.label = label;
            this.originalScore = originalScore;
            this.normalizedScore = normalizedScore;
        }

        public String getLabel() { return label; }
        public BigDecimal getOriginalScore() { return originalScore; }
        public BigDecimal getNormalizedScore() { return normalizedScore; }

        @Override
        public String toString() {
            return "Sonuç: " + label + " | Skor: " + originalScore + " | Normalize: " + normalizedScore;
        }
    }
}
