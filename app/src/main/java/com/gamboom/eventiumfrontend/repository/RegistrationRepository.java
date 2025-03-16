//package com.gamboom.eventiumfrontend.repository;
//
//import com.gamboom.eventiumfrontend.model.Registration;
//import com.gamboom.eventiumfrontend.network.RegistrationApiService;
//import com.gamboom.eventiumfrontend.network.RetrofitClient;
//
//import java.util.List;
//import java.util.UUID;
//
//import retrofit2.Callback;
//
//public class RegistrationRepository {
//
//    private final RegistrationApiService registrationApiService;
//
//
//    public RegistrationRepository() {
//        registrationApiService = RetrofitClient.getRetrofitInstance().create(RegistrationApiService.class);
//    }
//
//    public void getAllRegistrations(Callback<List<Registration>> callback) {
//        registrationApiService.getAllRegistrations().enqueue(callback);
//    }
//
//    public void getRegistrationById(UUID id, Callback<Registration> callback) {
//        registrationApiService.getRegistrationById(id).enqueue(callback);
//    }
//
//    public void createRegistration(Registration registration, Callback<Registration> callback) {
//        registrationApiService.createRegistration(registration).enqueue(callback);
//    }
//
//    public void updateRegistration(UUID id, Registration registration, Callback<Registration> callback) {
//        registrationApiService.updateRegistration(id, registration).enqueue(callback);
//    }
//
//    public void deleteRegistration(UUID id, Callback<Void> callback) {
//        registrationApiService.deleteRegistration(id).enqueue(callback);
//    }
//
//}
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
