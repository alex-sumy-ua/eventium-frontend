package com.gamboom.eventiumfrontend.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class Registration {

    @SerializedName("event_registration_id")
    private UUID eventRegistrationId;

    @SerializedName("eventId")
    private UUID eventId; // Only the event ID is provided

    @SerializedName("userId")
    private UUID userId; // Only the user ID is provided

    @SerializedName("registrationDate")
    private LocalDateTime registrationTime;

    public Registration() {
    }

    public Registration(UUID eventRegistrationId,
                        UUID eventId,
                        UUID userId,
                        LocalDateTime registrationTime) {
        this.eventRegistrationId = eventRegistrationId;
        this.eventId = eventId;
        this.userId = userId;
        this.registrationTime = registrationTime;
    }

    public UUID getEventRegistrationId() {
        return eventRegistrationId;
    }

    public void setEventRegistrationId(UUID eventRegistrationId) {
        this.eventRegistrationId = eventRegistrationId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

}
