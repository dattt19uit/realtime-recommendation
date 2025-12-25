package com.example.flink.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEvent {

    @JsonProperty("event_time")
    private long eventTime;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("item_id")
    private String itemId;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("transaction_id")
    private String transactionId;

}
