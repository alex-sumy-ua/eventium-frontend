package com.gamboom.eventiumfrontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.service.LoginActivity;
import com.gamboom.eventiumfrontend.view.EventFragment;
import com.gamboom.eventiumfrontend.view.RegistrationFragment;
import com.gamboom.eventiumfrontend.view.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gamboom.eventiumfrontend.service.AppSession;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is authenticated
        User currentUser = AppSession.getInstance().getCurrentUser(); // Pass context
        if (currentUser == null) {
            // Redirect to LoginActivity if the user is not authenticated
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_users) {
                selectedFragment = new UserFragment();
            } else if (item.getItemId() == R.id.nav_events) {
                selectedFragment = new EventFragment();
            } else if (item.getItemId() == R.id.nav_event_registrations) {
                selectedFragment = new RegistrationFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment if none is already loaded
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EventFragment())
                    .commit();
        }
    }

}
