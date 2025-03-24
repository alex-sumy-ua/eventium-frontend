package com.gamboom.eventiumfrontend.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.model.Role;
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

public class RegistrationFragment extends Fragment implements RegistrationAdapter.OnRegistrationActionListener {

    private RecyclerView registrationRecyclerView;
    private RegistrationAdapter registrationAdapter;
    private FloatingActionButton fabAdd;
    private TextView emptyMessage;

    private RegistrationRepository registrationRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    private final List<Registration> registrations = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    private final Set<UUID> eventIdsToFetch = new HashSet<>();
    private final Set<UUID> userIdsToFetch = new HashSet<>();

    private int fetchedEventsCount = 0;
    private int fetchedUsersCount = 0;

    private User currentUser;

    public RegistrationFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currentUser = AppSession.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        fabAdd = view.findViewById(R.id.fab_add);
        registrationRecyclerView = view.findViewById(R.id.registrationRecyclerView);
        emptyMessage = view.findViewById(R.id.emptyMessage);

        registrationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        registrationRepository = new RegistrationRepository();
        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        registrationAdapter = new RegistrationAdapter(
                registrations,
                events,
                users,
                this
        );
        registrationRecyclerView.setAdapter(registrationAdapter);

        fabAdd.setOnClickListener(v -> openAddRegistrationDialog());

        fetchRegistrations();

        return view;
    }

    private void fetchRegistrations() {
        registrationRepository.getAllRegistrations().enqueue(new Callback<List<Registration>>() {
            @Override
            public void onResponse(@NonNull Call<List<Registration>> call,
                                   @NonNull Response<List<Registration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registrations.clear();
                    registrations.addAll(response.body());
                    registrations.sort(Comparator.comparing(Registration::getRegistrationTime).reversed());

                    eventIdsToFetch.clear();
                    userIdsToFetch.clear();

                    for (Registration registration : registrations) {
                        if (registration.getEventId() != null)
                            eventIdsToFetch.add(registration.getEventId());
                        if (registration.getUserId() != null)
                            userIdsToFetch.add(registration.getUserId());
                        Log.d("RegistrationDebug", "Registration - UserID: " + registration.getUserId() + ", EventID: " + registration.getEventId());
                    }

                    if (currentUser != null) {
                        userIdsToFetch.add(currentUser.getUserId());
                    }

                    Log.d("RegistrationFragment", "Registrations loaded: " + registrations.size());

                    fetchEvents();
                    fetchUsers();
                } else {
                    showError("Failed to load registrations.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Registration>> call, @NonNull Throwable t) {
                showError("Error fetching registrations.");
            }
        });
    }

    private void fetchEvents() {
        events.clear();
        fetchedEventsCount = 0;

        if (eventIdsToFetch.isEmpty()) {
            updateAdapterIfReady();
            return;
        }

        for (UUID id : eventIdsToFetch) {
            eventRepository.getEventById(id).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        events.add(response.body());
                    }
                    checkIfAllEventsFetched();
                }

                @Override
                public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                    checkIfAllEventsFetched();
                    showError("Error fetching event.");
                }

                private void checkIfAllEventsFetched() {
                    fetchedEventsCount++;
                    Log.d("RegistrationFragment", "Fetched event count: " + fetchedEventsCount + "/" + eventIdsToFetch.size());
                    if (fetchedEventsCount == eventIdsToFetch.size()) {
                        Log.d("RegistrationFragment", "All events fetched: " + events.size());
                        updateAdapterIfReady();
                    }
                }
            });
        }
    }

    private void fetchUsers() {
        users.clear();
        fetchedUsersCount = 0;

        if (userIdsToFetch.isEmpty()) {
            updateAdapterIfReady();
            return;
        }

        for (UUID id : userIdsToFetch) {
            userRepository.getUserById(id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        users.add(response.body());
                    }
                    checkIfAllUsersFetched();
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    checkIfAllUsersFetched();
                    showError("Error fetching user.");
                }

                private void checkIfAllUsersFetched() {
                    fetchedUsersCount++;
                    Log.d("RegistrationFragment", "Fetched user count: " + fetchedUsersCount + "/" + userIdsToFetch.size());
                    if (fetchedUsersCount == userIdsToFetch.size()) {
                        Log.d("RegistrationFragment", "All users fetched: " + users.size());
                        updateAdapterIfReady();
                    }
                }
            });
        }
    }

    private void updateAdapterIfReady() {
        Log.d("RegistrationFragment", "Check adapter readiness: " + fetchedEventsCount + "/" + eventIdsToFetch.size() + " events, " + fetchedUsersCount + "/" + userIdsToFetch.size() + " users");
        if (fetchedEventsCount == eventIdsToFetch.size() && fetchedUsersCount == userIdsToFetch.size()) {
            Log.d("RegistrationFragment", "Updating adapter with " + registrations.size() + " registrations, " + events.size() + " events, " + users.size() + " users");
            registrationAdapter.updateData(registrations, events, users);
            boolean hasFutureEvents = events.stream().anyMatch(e -> e.getEndTime().isAfter(LocalDateTime.now()));
            fabAdd.setEnabled(hasFutureEvents);

            if (registrations.isEmpty()) {
                emptyMessage.setVisibility(View.VISIBLE);
                registrationRecyclerView.setVisibility(View.GONE);
            } else {
                emptyMessage.setVisibility(View.GONE);
                registrationRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void openAddRegistrationDialog() {
        AddRegistrationDialogFragment dialog = new AddRegistrationDialogFragment();
        dialog.setParentFragment(this);
        dialog.show(getParentFragmentManager(), "AddRegistrationDialog");
    }

    public void addRegistrationToDatabase(UUID eventId, UUID userId) {
        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setUserId(userId);
        registration.setRegistrationTime(LocalDateTime.now());

        registrationRepository.createRegistration(registration).enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(@NonNull Call<Registration> call, @NonNull Response<Registration> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    fetchRegistrations();
                } else {
                    showError("Failed to register.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Registration> call, @NonNull Throwable t) {
                showError("Error creating registration");
            }
        });
    }

    @Override
    public void onDeleteRegistration(Registration registration) {
        if (currentUser == null) return;

        boolean isSelf = registration.getUserId().equals(currentUser.getUserId());
        boolean isStaff = currentUser.getRole() == Role.STAFF;

        if (isSelf || isStaff) {
            showDeleteConfirmationDialog("Delete this registration?", () -> deleteRegistration(registration));
        } else {
            Toast.makeText(getContext(), "Only for your own registration", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRegistration(Registration registration) {
        registrationRepository.deleteRegistration(registration.getEventRegistrationId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration deleted", Toast.LENGTH_SHORT).show();
                    fetchRegistrations();
                } else {
                    showError("Failed to delete registration");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showError("Error deleting registration");
            }
        });
    }

    private void showDeleteConfirmationDialog(String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showError(String message) {
        Log.e("RegistrationFragment", message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}