package com.gamboom.eventiumfrontend.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.gamboom.eventiumfrontend.service.RegistrationAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private final List<Event> events = new ArrayList<>(); // Initialize the list
    private final List<User> users = new ArrayList<>(); // Initialize the list

    private final Set<UUID> uniqueUserIds = new HashSet<>();
    private final Set<UUID> uniqueEventIds = new HashSet<>();

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> openAddRegistrationDialog());

        return view;
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

        // Fetch registrations, events, and users
        fetchRegistrations();
    }

    private void fetchRegistrations() {
        registrationRepository.getAllRegistrations().enqueue(new Callback<List<Registration>>() {
            @Override
            public void onResponse(@NonNull Call<List<Registration>> call,
                                   @NonNull Response<List<Registration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registrations = response.body();
                    Log.d("RegistrationFragment", "Registrations fetched: " + registrations.size());

                    // Clear existing data
                    events.clear();
                    users.clear();
                    uniqueEventIds.clear();
                    uniqueUserIds.clear();

                    // Extract unique event IDs and user IDs
                    for (Registration registration : registrations) {
                        if (registration.getEventId() != null) {
                            uniqueEventIds.add(registration.getEventId());
                        }
                        if (registration.getUserId() != null) {
                            uniqueUserIds.add(registration.getUserId());
                        }
                    }

                    // Fetch events and users
                    fetchEventsByIds(new ArrayList<>(uniqueEventIds));
                    fetchUsersByIds(new ArrayList<>(uniqueUserIds));
                } else {
                    showError("Failed to load registrations. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registration>> call,
                                  @NonNull Throwable t) {
                showError("Network error: Unable to load registrations. Error: " + t.getMessage());
            }
        });
    }

    private void fetchEventsByIds(List<UUID> eventIds) {
        for (UUID eventId : eventIds) {
            eventRepository.getEventById(eventId).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call,
                                       @NonNull Response<Event> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        events.add(response.body());
                        Log.d("RegistrationFragment", "Event fetched: " + response.body().getTitle());

                        // Check if all events have been fetched
                        if (events.size() == eventIds.size()) {
                            updateAdapterIfReady();
                        }
                    } else {
                        showError("Failed to load event. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Event> call,
                                      @NonNull Throwable t) {
                    showError("Network error: Unable to load event. Error: " + t.getMessage());
                }
            });
        }
    }

    private void fetchUsersByIds(List<UUID> userIds) {
        for (UUID userId : userIds) {
            userRepository.getUserById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call,
                                       @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        users.add(response.body());
                        Log.d("RegistrationFragment", "User fetched: " + response.body().getName());

                        // Check if all users have been fetched
                        if (users.size() == userIds.size()) {
                            updateAdapterIfReady();
                        }
                    } else {
                        showError("Failed to load user. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call,
                                      @NonNull Throwable t) {
                    showError("Network error: Unable to load user. Error: " + t.getMessage());
                }
            });
        }
    }

    private void updateAdapterIfReady() {
        if (events.size() == uniqueEventIds.size() && users.size() == uniqueUserIds.size()) {
            updateAdapter();
        }
    }

    private void updateAdapter() {
        Log.d("RegistrationFragment", "Updating adapter with registrations: " + registrations.size());
        Log.d("RegistrationFragment", "Events: " + events.size());
        Log.d("RegistrationFragment", "Users: " + users.size());

        // Ensure all data is available before updating the adapter
        if (registrations != null && !events.isEmpty() && !users.isEmpty()) {
            registrationAdapter.updateData(registrations, events, users);
        } else {
            showError("Incomplete data: Cannot update adapter.");
        }
    }

    private void showError(String message) {
        Log.e("RegistrationFragment", message);
        // Display a Toast or Snackbar to the user
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void openAddRegistrationDialog() {
        AddRegistrationDialogFragment dialog = new AddRegistrationDialogFragment();
        dialog.show(getParentFragmentManager(), "AddRegistrationDialog");
    }

}