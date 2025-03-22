package com.gamboom.eventiumfrontend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("userId")
    private UUID userId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private Role role;

    @SerializedName("createdAt")
    private LocalDateTime createdAt;

    @SerializedName("token") // ✅ Added token field
    private String token;

    public User() {
    }

    public User(UUID userId,
                String name,
                String email,
                String password,
                Role role,
                LocalDateTime createdAt,
                String token) { // ✅ Updated constructor
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getToken() { // ✅ Getter for token
        return token;
    }

    public void setToken(String token) { // ✅ Setter for token
        this.token = token;
    }
}
