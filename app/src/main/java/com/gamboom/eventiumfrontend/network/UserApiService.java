package com.gamboom.eventiumfrontend.network;

import com.gamboom.eventiumfrontend.model.User;

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

public interface UserApiService {

    @POST("users")
    Call<User> createUser(@Header("Authorization") String authToken, @Body User user);

    @GET("users")
    Call<List<User>> getAllUsers(@Header("Authorization") String authToken);

    @GET("users/{id}")
    Call<User> getUserById(@Header("Authorization") String authToken, @Path("id") UUID id);

    @GET("users/by-email/{email}")
    Call<User> getUserByEmail(@Header("Authorization") String authToken, @Path("email") String email);

    @PUT("users/{id}")
    Call<User> updateUser(@Header("Authorization") String authToken, @Path("id") UUID id, @Body User user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Header("Authorization") String authToken, @Path("id") UUID id);

}