package com.example.recommendation_api.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RecommendationService {

    private final StringRedisTemplate redis;

    public RecommendationService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public List<String> getRecentItems(String userId) {
        return redis.opsForList()
                .range("user:" + userId + ":recent", 0, -1);
    }

    public Set<String> getRelatedItems(String itemId, int limit) {
        return redis.opsForZSet()
                .reverseRange("item:" + itemId + ":related", 0, limit - 1);
    }
}
