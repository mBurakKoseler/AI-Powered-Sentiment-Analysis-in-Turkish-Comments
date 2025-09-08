package com.ecommerce.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.service.KeywordService;

@RestController
@RequestMapping("/api/keywords")
public class KeywordController {
    
    @Autowired
    private KeywordService keywordService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKeywords() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("categories", Map.of(
                "quality_durability", Map.of(
                    "name", keywordService.getCategoryName("quality_durability"),
                    "description", keywordService.getCategoryDescription("quality_durability"),
                    "keywords", keywordService.getKeywordsForCategory("quality_durability")
                ),
                "usage_performance", Map.of(
                    "name", keywordService.getCategoryName("usage_performance"),
                    "description", keywordService.getCategoryDescription("usage_performance"),
                    "keywords", keywordService.getKeywordsForCategory("usage_performance")
                ),
                "service_delivery", Map.of(
                    "name", keywordService.getCategoryName("service_delivery"),
                    "description", keywordService.getCategoryDescription("service_delivery"),
                    "keywords", keywordService.getKeywordsForCategory("service_delivery")
                )
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/category-names")
    public ResponseEntity<Map<String, String>> getCategoryNames() {
        try {
            return ResponseEntity.ok(keywordService.getAllCategoryNames());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
