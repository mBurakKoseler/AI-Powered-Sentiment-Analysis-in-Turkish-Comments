package com.ecommerce.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class KeywordService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeywordService.class);
    
    private final Map<String, List<String>> categoryKeywords = new HashMap<>();
    private final Map<String, String> categoryNames = new HashMap<>();
    private final Map<String, String> categoryDescriptions = new HashMap<>();
    
    @PostConstruct
    public void loadKeywords() {
        try {
            ClassPathResource resource = new ClassPathResource("static/keywords.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(resource.getInputStream());
            
            JsonNode categories = root.get("categories");
            
            categories.fieldNames().forEachRemaining(categoryKey -> {
                JsonNode category = categories.get(categoryKey);
                
                // Kategori adı ve açıklaması
                categoryNames.put(categoryKey, category.get("name").asText());
                categoryDescriptions.put(categoryKey, category.get("description").asText());
                
                // Anahtar kelimeler
                List<String> keywords = new ArrayList<>();
                JsonNode keywordsNode = category.get("keywords");
                keywordsNode.forEach(keyword -> keywords.add(keyword.asText()));
                
                categoryKeywords.put(categoryKey, keywords);
                
                logger.info("Loaded {} keywords for category: {}", keywords.size(), categoryKey);
            });
            
            logger.info("Successfully loaded keywords for {} categories", categoryKeywords.size());
            
        } catch (IOException e) {
            logger.error("Failed to load keywords from JSON file", e);
            // Fallback: Varsayılan kelimeler
            loadDefaultKeywords();
        }
    }
    
    private void loadDefaultKeywords() {
        logger.warn("Loading default keywords as fallback");
        
        // Kalite & Dayanıklılık
        categoryKeywords.put("quality_durability", Arrays.asList(
            "kalite", "malzeme", "sağlam", "dayanıklı", "bozuk", "kırık"
        ));
        categoryNames.put("quality_durability", "Kalite & Dayanıklılık");
        categoryDescriptions.put("quality_durability", "Ürün malzemesi, işçiliği, uzun ömürlülüğü");
        
        // Kullanım & Performans
        categoryKeywords.put("usage_performance", Arrays.asList(
            "performans", "çalışıyor", "hız", "kolay", "rahat", "mükemmel"
        ));
        categoryNames.put("usage_performance", "Kullanım & Performans");
        categoryDescriptions.put("usage_performance", "Ürünün işlevselliği, vaat edilen işi ne kadar iyi yaptığı");
        
        // Hizmet & Teslimat
        categoryKeywords.put("service_delivery", Arrays.asList(
            "kargo", "teslimat", "paket", "hızlı", "yavaş", "destek"
        ));
        categoryNames.put("service_delivery", "Hizmet & Teslimat");
        categoryDescriptions.put("service_delivery", "Kargo hızı, paketleme kalitesi, müşteri desteği");
    }
    
    public List<String> getKeywordsForCategory(String category) {
        return categoryKeywords.getOrDefault(category, new ArrayList<>());
    }
    
    public String getCategoryName(String category) {
        return categoryNames.getOrDefault(category, category);
    }
    
    public String getCategoryDescription(String category) {
        return categoryDescriptions.getOrDefault(category, "");
    }
    
    public Map<String, List<String>> getAllCategoryKeywords() {
        return new HashMap<>(categoryKeywords);
    }
    
    public Map<String, String> getAllCategoryNames() {
        return new HashMap<>(categoryNames);
    }
    
    public Map<String, String> getAllCategoryDescriptions() {
        return new HashMap<>(categoryDescriptions);
    }
    
    public boolean containsKeyword(String category, String comment) {
        List<String> keywords = getKeywordsForCategory(category);
        String lowerComment = comment.toLowerCase();
        
        return keywords.stream().anyMatch(lowerComment::contains);
    }
}
