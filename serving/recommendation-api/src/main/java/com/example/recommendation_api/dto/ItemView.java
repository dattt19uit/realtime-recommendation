package com.example.recommendation_api.dto;

import java.util.List;

public class ItemView {
    private String itemId;
    private String categoryId;
    private List<String> categoryPath;
    private String available;

    public ItemView(String itemId, String categoryId, List<String> categoryPath, String available) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.categoryPath = categoryPath;
        this.available = available;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(List<String> categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
