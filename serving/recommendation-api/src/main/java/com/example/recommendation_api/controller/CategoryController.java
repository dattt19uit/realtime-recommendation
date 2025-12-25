package com.example.recommendation_api.controller;

import com.example.recommendation_api.dto.ItemView;
import com.example.recommendation_api.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final StringRedisTemplate redis;
    private final ItemService itemService;

    public CategoryController(StringRedisTemplate redis, ItemService itemService) {
        this.redis = redis;
        this.itemService = itemService;
    }

    @GetMapping("/all")
    public List<String> getAllCategories() {

        Set<String> categories = new java.util.HashSet<>();

        Set<String> keys = redis.keys("item:*");

        if (keys != null) {
            for (String key : keys) {
                Object categoryId = redis.opsForHash().get(key, "categoryid");
                if (categoryId != null) {
                    categories.add(categoryId.toString());
                }
            }
        }

        return categories.stream()
                .sorted()
                .toList();
    }

    // Lấy category gốc (parent = 0 hoặc null)
    @GetMapping("/root")
    public List<String> getRootCategories() {
        return redis.opsForSet().members("category:root")
                .stream()
                .toList();
    }

    // Lấy item theo category
    @GetMapping("/{categoryId}/items")
    public List<ItemView> getItemsByCategory(
            @PathVariable String categoryId) {

        Set<String> itemIds =
                redis.opsForSet().members("category:" + categoryId + ":items");

        if (itemIds == null) return List.of();

        return itemIds.stream()
                .limit(50)
                .map(itemService::enrichItem)
                .filter(Objects::nonNull)
                .toList();
    }
}

