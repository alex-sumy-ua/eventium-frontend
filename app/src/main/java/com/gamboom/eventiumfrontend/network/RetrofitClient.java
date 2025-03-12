package com.gamboom.eventiumfrontend.network;

import com.gamboom.eventiumfrontend.util.LocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
//    private static final String BASE_URL = "http://localhost:8080/api/";
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient with timeout and logging
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Custom Gson instance for LocalDateTime
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                    .create();

            // Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }



}
