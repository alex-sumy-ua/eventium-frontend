package com.gamboom.eventiumfrontend.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    @SerializedName("event_id")
    private UUID eventId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("location")
    private String location;

    @SerializedName("start_time")
    private LocalDateTime startTime;

    @SerializedName("end_time")
    private LocalDateTime endTime;

    @SerializedName("created_by")
    private UUID createdBy;

    @SerializedName("created_at")
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
