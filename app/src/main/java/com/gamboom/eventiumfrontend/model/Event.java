package com.gamboom.eventiumfrontend.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {

        @SerializedName("eventId")
    private UUID eventId;
        @SerializedName("title")
    private String title;
        @SerializedName("description")
    private String description;
        @SerializedName("location")
    private String location;
        @SerializedName("startTime")
    private LocalDateTime startTime;
        @SerializedName("endTime")
    private LocalDateTime endTime;
        @SerializedName("createdBy")
    private UUID createdBy;
        @SerializedName("createdAt")
    private LocalDateTime createdAt;

    public Event() {}

    public Event(UUID eventId,
                 String title,
                 String description,
                 String location,
                 LocalDateTime startTime,
                 LocalDateTime endTime,
                 UUID createdBy,
                 LocalDateTime createdAt) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}