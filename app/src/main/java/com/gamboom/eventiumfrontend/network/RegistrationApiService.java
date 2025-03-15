package com.gamboom.eventiumfrontend.network;

import com.gamboom.eventiumfrontend.model.Registration;

import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RegistrationApiService {

    @POST("event_registrations")
    Call<Registration> createEventRegistration(@Body Registration registration);

    @GET("event_registrations")
    Call<List<Registration>> getAllEventRegistrations();

    @GET("event_registrations/{id}")
    Call<Registration> getEventRegistrationById(@Path("id") UUID id);

    @PUT("event_registrations/{id}")
    Call<Registration> updateEventRegistration(@Path("id") UUID id, @Body Registration registration);

    @DELETE("event_registrations/{id}")
    Call<Void> deleteEventRegistration(@Path("id") UUID id);

}
