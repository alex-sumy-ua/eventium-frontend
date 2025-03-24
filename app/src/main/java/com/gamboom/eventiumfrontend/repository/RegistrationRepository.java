package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.service.RegistrationApiService;
import com.gamboom.eventiumfrontend.service.RetrofitClient;
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
        return registrationApiService.getAllRegistrations("Bearer " + authToken);
    }

    public Call<Registration> getRegistrationById(UUID id) {
        return registrationApiService.getRegistrationById("Bearer " + authToken, id);
    }

    public Call<Registration> createRegistration(Registration registration) {
        return registrationApiService.createRegistration("Bearer " + authToken, registration);
    }

    public Call<Registration> updateRegistration(UUID id, Registration registration) {
        return registrationApiService.updateRegistration("Bearer " + authToken, id, registration);
    }

    public Call<Void> deleteRegistration(UUID id) {
        return registrationApiService.deleteRegistration("Bearer " + authToken, id);
    }

}
