package com.gamboom.eventiumfrontend.repository;

import android.content.Context;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.network.EventApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class EventRepository {

    private final EventApiService eventApiService;
    private final String authToken;


    public EventRepository() {
        this.authToken = AppSession.getInstance().getAccessToken();
        eventApiService = RetrofitClient.getRetrofitInstance().create(EventApiService.class);
    }

    public Call<List<Event>> getAllEvents() {
        return eventApiService.getAllEvents("Bearer " + authToken);
    }

    public Call<Event> getEventById(UUID id) {
        return eventApiService.getEventById("Bearer " + authToken, id);
    }

    public Call<Event> createEvent(Event event) {
        return eventApiService.createEvent("Bearer " + authToken, event);
    }

    public Call<Event> updateEvent(UUID id, Event event) {
        return eventApiService.updateEvent("Bearer " + authToken, id, event);
    }

    public Call<Void> deleteEvent(UUID id) {
        return eventApiService.deleteEvent("Bearer " + authToken, id);
    }

}

