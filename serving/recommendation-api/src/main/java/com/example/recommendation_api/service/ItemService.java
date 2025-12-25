package com.example.recommendation_api.service;

import com.example.recommendation_api.dto.ItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private CategoryService categoryService;

    public ItemView enrichItem(String itemId) {
        Map<Object, Object> itemData =
                redis.opsForHash().entries("item:" + itemId);

        if (itemData.isEmpty()) return null;

        String categoryId = (String) itemData.get("categoryid");
        String available = (String) itemData.get("available");

        return new ItemView(
                itemId,
                categoryId,
                categoryService.getCategoryPath(categoryId),
                available
        );
    }
}


