package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.network.RetrofitClient;
import com.gamboom.eventiumfrontend.network.UserApiService;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class UserRepository {

    private final UserApiService userApiService;
    private final String authToken;

    public UserRepository() {
        this.authToken = AppSession.getInstance().getAccessToken();
        this.userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);
    }

    public Call<List<User>> getAllUsers() {
        return userApiService.getAllUsers("Bearer " + authToken);
    }

    public Call<User> createUser(User user) {
        return userApiService.createUser("Bearer " + authToken, user);
    }

    public Call<User> getUserById(UUID id) {
        return userApiService.getUserById("Bearer " + authToken, id);
    }

    public Call<User> updateUser(UUID id, User user) {
        return userApiService.updateUser("Bearer " + authToken, id, user);
    }

    public Call<Void> deleteUser(UUID id) {
        return userApiService.deleteUser("Bearer " + authToken, id);
    }

}