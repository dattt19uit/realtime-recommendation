package com.example.recommendation_api.controller;

import com.example.recommendation_api.dto.ItemView;
import com.example.recommendation_api.service.ItemService;
import com.example.recommendation_api.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private ItemService itemService;

    public RecommendationController(RecommendationService service) {
        this.recommendationService = service;
    }

    @GetMapping("/recommend/{userId}")
    public Map<String, Object> recommend(@PathVariable String userId) {

        // 1. Recent items
        List<String> recentItemIds =
                redis.opsForList().range("user:" + userId + ":recent", 0, 4);

        assert recentItemIds != null;
        List<ItemView> recentItems = recentItemIds.stream()
                .map(itemService::enrichItem)
                .filter(Objects::nonNull)
                .toList();

        // 2. Collect related items
        Set<String> recommendedItemIds = new LinkedHashSet<>();

        for (String itemId : recentItemIds) {
            Set<String> related =
                    recommendationService.getRelatedItems(itemId, 5);
            if (related != null) {
                recommendedItemIds.addAll(related);
            }
        }

        // 3. Remove items user already saw
        recentItemIds.forEach(recommendedItemIds::remove);

        // 4. Enrich recommended items
        List<ItemView> recommendedItems = recommendedItemIds.stream()
                .limit(10)
                .map(itemService::enrichItem)
                .filter(Objects::nonNull)
                .toList();

        // 5. Response
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("recentItems", recentItems);
        response.put("recommendedItems", recommendedItems);

        return response;
    }

}
