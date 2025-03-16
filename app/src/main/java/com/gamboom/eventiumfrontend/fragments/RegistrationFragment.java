package com.gamboom.eventiumfrontend.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.EventRepository;
import com.gamboom.eventiumfrontend.repository.RegistrationRepository;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import com.gamboom.eventiumfrontend.util.RegistrationAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private RecyclerView registrationRecyclerView;
    private RegistrationAdapter registrationAdapter;
    private RegistrationRepository registrationRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    private List<Registration> registrations;
    private List<Event> events;
    private List<User> users;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repositories
        registrationRepository = new RegistrationRepository();
        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        // Setup RecyclerView
        registrationRecyclerView = view.findViewById(R.id.registrationRecyclerView);
        registrationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter with empty lists
        registrationAdapter = new RegistrationAdapter(new ArrayList<>(),
                                                      new ArrayList<>(),
                                                      new ArrayList<>());
        registrationRecyclerView.setAdapter(registrationAdapter);

        // Fetch data
        fetchData();
    }

    private void fetchData() {
        registrationRepository.getAllRegistrations().enqueue(new Callback<List<Registration>>() {
            @Override
            public void onResponse(@NonNull Call<List<Registration>> call,
                                   @NonNull Response<List<Registration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registrations = response.body();
                    fetchEvents();
                    fetchUsers();
                } else {
                    showError("Failed to load registrations. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registration>> call,
                                  @NonNull Throwable t) {
                showError("Network error: Unable to load registrations.");
            }
        });
    }

    private void fetchEvents() {
        eventRepository.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body();
                    fetchUsers();
                } else {
                    showError("Failed to load events. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call,
                                  @NonNull Throwable t) {
                showError("Network error: Unable to load events.");
            }
        });
    }

    private void fetchUsers() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call,
                                   @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();
                    updateAdapter();
                } else {
                    showError("Failed to load users. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.e("RegistrationFragment", "Network error: Unable to load users.", t);
                showError("Network error: Unable to load users.");
            }
        });
    }

    private void updateAdapter() {
        if (registrations != null && events != null && users != null) {
            registrationAdapter.updateData(registrations, events, users);
        }
    }

    private void showError(String message) {
        // Implement UI error handling (e.g., Toast, Snackbar, or Log message)
        Log.e("RegistrationFragment", message);
    }

}
