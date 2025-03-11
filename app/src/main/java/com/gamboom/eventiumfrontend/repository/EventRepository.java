package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.network.EventApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {

    private final EventApiService eventApiService;


    public EventRepository() {
        eventApiService = RetrofitClient.getRetrofitInstance().create(EventApiService.class);
    }

    public void getAllEvents(Callback<List<Event>> callback) {
        eventApiService.getAllEvents().enqueue(callback);
    }

    public void getEventById(UUID id, Callback<Event> callback) {
        eventApiService.getEventById(id).enqueue(callback);
    }

    public void createEvent(Event event, Callback<Event> callback) {
        eventApiService.createEvent(event).enqueue(callback);
    }

    public void updateEvent(UUID id, Event event, Callback<Event> callback) {
        eventApiService.updateEvent(id,event).enqueue(callback);
    }

    public void deleteEvent(UUID id, Callback<Void> callback) {
        eventApiService.deleteEvent(id).enqueue(callback);
    }


}
