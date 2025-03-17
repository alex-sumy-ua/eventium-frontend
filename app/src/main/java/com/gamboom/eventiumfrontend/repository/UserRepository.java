package com.gamboom.eventiumfrontend.repository;

import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.network.RetrofitClient;
import com.gamboom.eventiumfrontend.network.UserApiService;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class UserRepository {
    private final UserApiService userApiService;

    public UserRepository() {
        this.userApiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);
    }

    public Call<List<User>> getAllUsers() {
        return userApiService.getAllUsers();
    }

    public Call<User> createUser(User user) {
        return userApiService.createUser(user);
    }

    public Call<User> getUserById(UUID id) {
        return userApiService.getUserById(id);
    }

    public Call<User> updateUser(UUID id, User user) {
        return userApiService.updateUser(id, user);
    }

    public Call<Void> deleteUser(UUID id) {
        return userApiService.deleteUser(id);
    }

}