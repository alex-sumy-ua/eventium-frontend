package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.network.RegistrationApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class RegistrationRepository {

    private final RegistrationApiService registrationApiService;

    public RegistrationRepository() {
        registrationApiService = RetrofitClient.getRetrofitInstance().create(RegistrationApiService.class);
    }

    public Call<List<Registration>> getAllRegistrations() {
        return registrationApiService.getAllRegistrations();
    }

    public Call<Registration> getRegistrationById(UUID id) {
        return registrationApiService.getRegistrationById(id);
    }

    public Call<Registration> createRegistration(Registration registration) {
        return registrationApiService.createRegistration(registration);
    }

    public Call<Registration> updateRegistration(UUID id, Registration registration) {
        return registrationApiService.updateRegistration(id, registration);
    }

    public Call<Void> deleteRegistration(UUID id) {
        return registrationApiService.deleteRegistration(id);
    }
}
