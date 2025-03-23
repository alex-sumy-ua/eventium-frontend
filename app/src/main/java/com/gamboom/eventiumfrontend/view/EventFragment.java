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
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.EventRepository;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import com.gamboom.eventiumfrontend.service.AppSession;
import com.gamboom.eventiumfrontend.service.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment {

    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    private List<Event> events;
    private List<User> users;

    private User currentUser;

    public EventFragment() {
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

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> openAddEventDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize repositories
        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        // Setup RecyclerView
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter with empty lists
        eventAdapter = new EventAdapter(new ArrayList<>(), new ArrayList<>());
        eventRecyclerView.setAdapter(eventAdapter);

        // Fetch events and users
        fetchEvents();
    }

    private void fetchEvents() {
        eventRepository.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call,
                                   @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body();
                    Log.d("EventFragment", "Events loaded: " + events.size());
                    fetchUsers();
                } else {
                    Log.e("EventFragment", "Failed to load events. HTTP " + response.code());

                    if (response.errorBody() != null) {
                        try {
                            String error = response.errorBody().string();
                            Log.e("EventFragment", "Raw error body: " + error);
                        } catch (Exception e) {
                            Log.e("EventFragment", "Error parsing errorBody", e);
                        }
                    }

                    showError("Failed to load events. Try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showError("API: Unable to load events.");
                Log.e("EventFragment", "❌ Exception while loading events", t);
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
                    Log.d("EventFragment", "Users loaded successfully: " + users.size());
                    eventAdapter.updateData(events, users);
                } else {

                    if (response.errorBody() != null) {
                        try {
                            String error = response.errorBody().string();
                            Log.e("EventFragment", "Raw error response: " + error);
                        } catch (Exception e) {
                            Log.e("EventFragment", "Error reading errorBody", e);
                        }
                    }

                    showError("Failed to load users.");
                    Log.e("EventFragment", "Failed to load users. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call,
                                  @NonNull Throwable t) {
                showError("API: Unable to load users.");
                Log.e("EventFragment", "Network error: Unable to load users.", t);
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e("EventFragment", message);
    }

    private void openAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment();
        dialog.setParentFragment(this); // Pass the parent fragment
        dialog.show(getParentFragmentManager(), "AddEventDialog");
    }

    public void addEventToDatabase(String eventTitle,
                                   String eventDescription,
                                   String eventLocation,
                                   LocalDateTime eventStartTime,
                                   LocalDateTime eventEndTime) {
        if (currentUser == null) {
            showError("Error: No current user found.");
            Log.e("EventFragment", "Attempted to create event with null currentUserId.");
            return;
        }

        // Create a new Event object with the provided data
        Event newEvent = new Event();
        newEvent.setTitle(eventTitle);
        newEvent.setDescription(eventDescription);
        newEvent.setLocation(eventLocation);
        newEvent.setStartTime(eventStartTime);
        newEvent.setEndTime(eventEndTime);
        newEvent.setCreatedBy(currentUser.getUserId());
        newEvent.setCreatedAt(LocalDateTime.now());

        // Call the API method in the EventRepository to create a new event
        eventRepository.createEvent(newEvent).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call,
                                   @NonNull Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                    Log.d("EventFragment", "Event created successfully: " + response.body().getTitle());
                    fetchEvents(); // Refresh the event list
                } else {
                    showError("Failed to add event. Response code: " + response.code());
                    Log.e("EventFragment", "Failed to add event. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call,
                                  @NonNull Throwable t) {
                showError("Error adding event.");
                Log.e("EventFragment", "Error adding event", t);
            }
        });
    }
}
