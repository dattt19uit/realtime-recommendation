package com.example.recommendation_api.controller;

import com.example.recommendation_api.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/recommend/{userId}")
    public Map<String, Object> recommend(@PathVariable String userId) {

        List<String> recentItems = service.getRecentItems(userId);
        Set<String> recommendations = new LinkedHashSet<>();

        if (recentItems != null) {
            for (String item : recentItems) {
                Set<String> related = service.getRelatedItems(item, 5);
                if (related != null) {
                    recommendations.addAll(related);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("recentItems", recentItems);
        response.put("recommendations", recommendations);

        return response;
    }
}
