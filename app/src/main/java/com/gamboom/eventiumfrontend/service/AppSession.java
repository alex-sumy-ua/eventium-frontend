package com.gamboom.eventiumfrontend.service;

import com.gamboom.eventiumfrontend.model.User;

public class AppSession {

    private static AppSession instance;
    private User currentUser;
    private String accessToken;  // Added access token field

    private AppSession() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
