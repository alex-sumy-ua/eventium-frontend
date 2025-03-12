package com.gamboom.eventiumfrontend.network;

import com.gamboom.eventiumfrontend.model.User;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApiService {

    @POST("users")
    Call<User> createUser(@Body User user);

    @GET("users")
    Call<List<User>> getAllUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") UUID id);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") UUID id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") UUID id);
}