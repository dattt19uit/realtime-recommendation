package com.example.recommendation_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private StringRedisTemplate redis;

    public List<String> getCategoryPath(String categoryId) {
        List<String> path = new ArrayList<>();

        String current = categoryId;
        while (current != null && !current.isEmpty()) {
            path.add(0, current); // add to head
            current = (String) redis.opsForHash()
                    .get("category:" + current, "parent");
        }
        return path;
    }
}

