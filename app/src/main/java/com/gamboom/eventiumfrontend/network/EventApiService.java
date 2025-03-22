package com.gamboom.eventiumfrontend.network;

import com.gamboom.eventiumfrontend.model.Event;

import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventApiService {

    @POST("events")
    Call<Event> createEvent(@Body Event event);

    @GET("events")
    Call<List<Event>> getAllEvents(@Header("Authorization") String authToken);

    @GET("events/{id}")
    Call<Event> getEventById(@Path("id") UUID id);

    @PUT("events/{id}")
    Call<Event> updateEvent(@Path("id") UUID id, @Body Event event);

    @DELETE("events/{id}")
    Call<Void> deleteEvent(@Path("id") UUID id);

}
