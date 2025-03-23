package com.gamboom.eventiumfrontend.service;

import com.gamboom.eventiumfrontend.model.Registration;

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

public interface RegistrationApiService {

    @POST("event-registrations")
    Call<Registration> createRegistration(@Header("Authorization") String authToken, @Body Registration registration);

    @GET("event-registrations")
    Call<List<Registration>> getAllRegistrations(@Header("Authorization") String authToken);

    @GET("event-registrations/{id}")
    Call<Registration> getRegistrationById(@Header("Authorization") String authToken, @Path("id") UUID id);

    @PUT("event-registrations/{id}")
    Call<Registration> updateRegistration(@Header("Authorization") String authToken, @Path("id") UUID id, @Body Registration registration);

    @DELETE("event-registrations/{id}")
    Call<Void> deleteRegistration(@Header("Authorization") String authToken, @Path("id") UUID id);

}
