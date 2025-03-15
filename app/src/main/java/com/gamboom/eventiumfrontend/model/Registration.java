package com.gamboom.eventiumfrontend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Registration {

//    @SerializedName("event_registration_id")
    private UUID eventRegistrationId;
//    @SerializedName("user")
    private User user;
//    @SerializedName("event")
    private Event event;
//    @SerializedName("registration_time")
    private LocalDateTime registrationTime;

    public Registration() {
    }

    public Registration(UUID eventRegistrationId, User user, Event event, LocalDateTime registrationTime) {
        this.eventRegistrationId = eventRegistrationId;
        this.user = user;
        this.event = event;
        this.registrationTime = registrationTime;
    }

    public UUID getEventRegistrationId() {
        return eventRegistrationId;
    }

    public void setEventRegistrationId(UUID eventRegistrationId) {
        this.eventRegistrationId = eventRegistrationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
}
