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

import java.time.LocalDateTime;
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
    private final List<Event> events = new ArrayList<>(); // Initialise the list
    private final List<User> users = new ArrayList<>(); // Initialise the list

    private final Set<UUID> uniqueUserIds = new HashSet<>();
    private final Set<UUID> uniqueEventIds = new HashSet<>();

    private UUID currentUserId = UUID.fromString("3196683f-1653-4918-97fa-f85b109f42d5");

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
        eventRepository = new EventRepository(requireContext());
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

                    // Ensure currentUserId is included in the list
                    if (currentUserId != null) {
                        uniqueUserIds.add(currentUserId);
                    }

                    Log.d("RegistrationFragment", "Unique User IDs: " + uniqueUserIds);

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
        Log.d("RegistrationFragment", "Fetching users for IDs: " + userIds);

        for (UUID userId : userIds) {
            userRepository.getUserById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        users.add(user);
                        Log.d("RegistrationFragment", "User fetched: " + user.getName() + ", ID: " + user.getUserId());

                        // Check if all users have been fetched
                        if (users.size() == userIds.size()) {
                            updateAdapterIfReady();
                        }
                    } else {
                        Log.e("RegistrationFragment", "Failed to load user. Response code: " + response.code());
                        showError("Failed to load user. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.e("RegistrationFragment", "Network error: Unable to load user. Error: " + t.getMessage());
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

        // Ensure currentUser is set properly
        if (currentUserId != null) {
            User currentUser = null;
            for (User user : users) {
                if (user.getUserId().equals(currentUserId)) {
                    currentUser = user;
                    break;
                }
            }

            if (currentUser != null) {
                dialog.setCurrentUser(currentUser);
                dialog.setParentFragment(this); // Pass the parent fragment
                dialog.show(getParentFragmentManager(), "AddRegistrationDialog");
            } else {
                Toast.makeText(getContext(), "Current user not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void addRegistrationToDatabase(UUID eventId, UUID userId) {
        // Create a new Registration object with the provided data
        Registration newRegistration = new Registration();
        newRegistration.setEventId(eventId);
        newRegistration.setUserId(userId);
        newRegistration.setRegistrationTime(LocalDateTime.now());

        // Call the API method in the RegistrationRepository to create a new registration
        registrationRepository.createRegistration(newRegistration).enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Registration added successfully", Toast.LENGTH_SHORT).show();
                    fetchRegistrations(); // Refresh the list
                } else {
                    Log.e("RegistrationFragment", "Failed to add registration. Response code: " + response.code());
                    Log.e("RegistrationFragment", "Response body: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to add registration", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Registration> call, Throwable t) {
                Log.e("RegistrationFragment", "Error adding registration", t);
                Toast.makeText(getContext(), "Error adding registration", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
