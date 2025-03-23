package com.gamboom.eventiumfrontend.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gamboom.eventiumfrontend.MainActivity;
import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView messageTextView;
    private String authToken;  // Store the token temporarily

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LoginActivity", "LoginActivity created");
        setContentView(R.layout.activity_login);

        // Initialize views
        Button githubLoginButton = findViewById(R.id.githubLoginButton);
        messageTextView = findViewById(R.id.messageTextView);

        // Handle GitHub login button click
        githubLoginButton.setOnClickListener(v -> {
            Log.d("LoginActivity", "GitHub login button clicked");
            startGitHubLogin();
        });

        // Handle deep link from GitHub OAuth callback
        handleOAuthCallback(getIntent());
    }

    private void startGitHubLogin() {
        Log.d("LoginActivity", "Redirecting to GitHub OAuth endpoint");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://10.0.2.2:8080/oauth2/authorization/github"));
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleOAuthCallback(intent);
    }

    @SuppressLint("SetTextI18n")
    private void handleOAuthCallback(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            Log.d("LoginActivity", "Received URI: " + uri.toString());

            if ("eventium".equals(uri.getScheme()) && "auth".equals(uri.getHost())) {
                String email = uri.getQueryParameter("email");
                authToken = uri.getQueryParameter("token");

                if (email != null && authToken != null && !authToken.equals("null")) {
                    authToken = authToken.trim();

                    // Save token immediately
                    AppSession.getInstance().setAccessToken(authToken);

                    Log.d("LoginActivity", "Email received: " + email);
                    Log.d("LoginActivity", "Token received: " + authToken);

                    Log.d("LoginActivity", "Saved token: " + AppSession.getInstance().getAccessToken());

                    // Fetch user and proceed
                    fetchUserByEmail(email);
                } else {
                    Log.e("LoginActivity", "Missing email or token in URI");
                    messageTextView.setText("Authentication failed. Please try again.");
                    messageTextView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("LoginActivity", "Invalid deep link: " + uri.toString());
            }
        } else {
            Log.e("LoginActivity", "No URI received in intent");
        }
    }

    private void fetchUserByEmail(String email) {
        UserApiService apiService = RetrofitClient.getRetrofitInstance().create(UserApiService.class);
        apiService.getUserByEmail(authToken, email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User currentUser = response.body();
                    if (currentUser != null) {
                        // Save user to session
                        AppSession.getInstance().setCurrentUser(currentUser);

                        // Navigate to MainActivity
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to fetch user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
