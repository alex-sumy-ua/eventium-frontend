package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.network.RegistrationApiService;
import com.gamboom.eventiumfrontend.network.RetrofitClient;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class RegistrationRepository {

    private final RegistrationApiService registrationApiService;
    private final String authToken;

    public RegistrationRepository() {
        this.authToken = AppSession.getInstance().getAccessToken();
        registrationApiService = RetrofitClient.getRetrofitInstance().create(RegistrationApiService.class);
    }

    public Call<List<Registration>> getAllRegistrations() {
        return registrationApiService.getAllRegistrations(authToken);
    }

    public Call<Registration> getRegistrationById(UUID id) {
        return registrationApiService.getRegistrationById(authToken, id);
    }

    public Call<Registration> createRegistration(Registration registration) {
        return registrationApiService.createRegistration(authToken, registration);
    }

    public Call<Registration> updateRegistration(UUID id, Registration registration) {
        return registrationApiService.updateRegistration(authToken, id, registration);
    }

    public Call<Void> deleteRegistration(UUID id) {
        return registrationApiService.deleteRegistration(authToken, id);
    }
}
