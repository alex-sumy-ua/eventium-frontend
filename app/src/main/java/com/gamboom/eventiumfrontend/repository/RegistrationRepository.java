package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.network.RegistrationApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;

import java.util.List;
import java.util.UUID;

import retrofit2.Callback;

public class RegistrationRepository {

    private final RegistrationApiService eventRegistrationApiService;


    public RegistrationRepository() {
        eventRegistrationApiService = RetrofitClient.getRetrofitInstance().create(RegistrationApiService.class);
    }

    public void getAllEventRegistrations(Callback<List<Registration>> callback) {
        eventRegistrationApiService.getAllEventRegistrations().enqueue(callback);
    }

    public void getEventRegistrationById(UUID id, Callback<Registration> callback) {
        eventRegistrationApiService.getEventRegistrationById(id).enqueue(callback);
    }

    public void createEventRegistration(Registration registration, Callback<Registration> callback) {
        eventRegistrationApiService.createEventRegistration(registration).enqueue(callback);
    }

    public void updateEventRegistration(UUID id, Registration registration, Callback<Registration> callback) {
        eventRegistrationApiService.updateEventRegistration(id, registration).enqueue(callback);
    }

    public void deleteEventRegistration(UUID id, Callback<Void> callback) {
        eventRegistrationApiService.deleteEventRegistration(id).enqueue(callback);
    }

}
