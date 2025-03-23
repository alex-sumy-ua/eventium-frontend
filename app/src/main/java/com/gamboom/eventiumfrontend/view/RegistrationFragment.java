package com.gamboom.eventiumfrontend.view;

import android.app.AlertDialog;
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
import com.gamboom.eventiumfrontend.service.AppSession;
import com.gamboom.eventiumfrontend.service.RegistrationAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.*;

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
    private final List<Event> events = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    private final Set<UUID> uniqueUserIds = new HashSet<>();
    private final Set<UUID> uniqueEventIds = new HashSet<>();

    private User currentUser;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currentUser = AppSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("EventFragment", "Current user ID: " + currentUser.getUserId());
        } else {
            Log.e("EventFragment", "No current user found in AppSession");
        }

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

        // Initialize Adapter with click listener
        registrationAdapter = new RegistrationAdapter(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                this::onRegistrationClicked
        );

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

                    events.clear();
                    users.clear();
                    uniqueEventIds.clear();
                    uniqueUserIds.clear();

                    for (Registration registration : registrations) {
                        if (registration.getEventId() != null) {
                            uniqueEventIds.add(registration.getEventId());
                        }
                        if (registration.getUserId() != null) {
                            uniqueUserIds.add(registration.getUserId());
                        }
                    }

                    if (currentUser.getUserId() != null) {
                        uniqueUserIds.add(currentUser.getUserId());
                    }

                    fetchEventsByIds(new ArrayList<>(uniqueEventIds));
                    fetchUsersByIds(new ArrayList<>(uniqueUserIds));
                } else {
                    showError("Failed to load registrations. Response code: " + response.code());
                    Log.e("RegistrationFragment", "Network error: Unable to load registrations.");

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registration>> call,
                                  @NonNull Throwable t) {
                showError("API: Unable to load registrations.");
                Log.e("RegistrationFragment", "Network error: Unable to load registrations.", t);
            }
        });
    }

    private void fetchEventsByIds(List<UUID> eventIds) {
        for (UUID eventId : eventIds) {
            eventRepository.getEventById(eventId).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        events.add(response.body());
                        if (events.size() == eventIds.size()) {
                            updateAdapterIfReady();
                        }
                    } else {
                        showError("Failed to load event. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                    showError("Network error: Unable to load event. Error: " + t.getMessage());
                }
            });
        }
    }

    private void fetchUsersByIds(List<UUID> userIds) {
        for (UUID userId : userIds) {
            userRepository.getUserById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        users.add(response.body());
                        if (users.size() == userIds.size()) {
                            updateAdapterIfReady();
                        }
                    } else {
                        showError("Failed to load user. Response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
        if (registrations != null && !events.isEmpty() && !users.isEmpty()) {
            registrationAdapter.updateData(registrations, events, users);
        } else {
            showError("Incomplete data: Cannot update adapter.");
        }
    }

    private void showError(String message) {
        Log.e("RegistrationFragment", message);
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void openAddRegistrationDialog() {
        AddRegistrationDialogFragment dialog = new AddRegistrationDialogFragment();

        if (currentUser != null) {
            for (User user : users) {
                if (user.getUserId().equals(currentUser.getUserId())) {
                    dialog.setCurrentUser(user);
                    dialog.setParentFragment(this);
                    dialog.show(getParentFragmentManager(), "AddRegistrationDialog");
                    return;
                }
            }
            Toast.makeText(getContext(), "Current user not found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void addRegistrationToDatabase(UUID eventId, UUID userId) {
        Registration newRegistration = new Registration();
        newRegistration.setEventId(eventId);
        newRegistration.setUserId(userId);
        newRegistration.setRegistrationTime(LocalDateTime.now());

        registrationRepository.createRegistration(newRegistration).enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(@NonNull Call<Registration> call, @NonNull Response<Registration> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Registration added successfully", Toast.LENGTH_SHORT).show();
                    fetchRegistrations(); // Refresh the list
                } else {
                    showError("Failed to add registration. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Registration> call, @NonNull Throwable t) {
                showError("Error adding registration: " + t.getMessage());
            }
        });
    }

    private void onRegistrationClicked(Registration registration) {
        String userName = "Unknown User";
        String eventTitle = "Unknown Event";

        for (User user : users) {
            if (user.getUserId().equals(registration.getUserId())) {
                userName = user.getName();
                break;
            }
        }

        for (Event event : events) {
            if (event.getEventId().equals(registration.getEventId())) {
                eventTitle = event.getTitle();
                break;
            }
        }

        Toast.makeText(getContext(),
                "Registration clicked:\nUser: " + userName + "\nEvent: " + eventTitle,
                Toast.LENGTH_LONG).show();
    }

    private void showDeleteConfirmationDialog(String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", null)
                .show();
    }

}
