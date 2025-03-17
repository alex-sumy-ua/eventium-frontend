package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.network.EventApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class EventRepository {

    private final EventApiService eventApiService;

    public EventRepository() {
        eventApiService = RetrofitClient.getRetrofitInstance().create(EventApiService.class);
    }

    public Call<List<Event>> getAllEvents() {
        return eventApiService.getAllEvents();
    }

    public Call<Event> getEventById(UUID id) {
        return eventApiService.getEventById(id);
    }

    public Call<Event> createEvent(Event event) {
        return eventApiService.createEvent(event);
    }

    public Call<Event> updateEvent(UUID id, Event event) {
        return eventApiService.updateEvent(id, event);
    }

    public Call<Void> deleteEvent(UUID id) {
        return eventApiService.deleteEvent(id);
    }

}
