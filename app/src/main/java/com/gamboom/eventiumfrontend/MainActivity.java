package com.gamboom.eventiumfrontend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gamboom.eventiumfrontend.fragments.EventFragment;
import com.gamboom.eventiumfrontend.fragments.EventRegistrationFragment;
import com.gamboom.eventiumfrontend.fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                selectedFragment = new EventRegistrationFragment();
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
                    .replace(R.id.fragment_container, new UserFragment())
                    .commit();
        }
    }
}
