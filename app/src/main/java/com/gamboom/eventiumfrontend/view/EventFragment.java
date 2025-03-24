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
import com.gamboom.eventiumfrontend.model.Role;
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

    public EventFragment() {}

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
        if (currentUser != null && currentUser.getRole() == Role.STAFF) {
            fabAdd.setOnClickListener(v -> openAddEventDialog());
        } else {
            fabAdd.setOnClickListener(v ->
                    Toast.makeText(getContext(), "For STAFF ONLY",
                            Toast.LENGTH_SHORT).show()
            );
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventAdapter = new EventAdapter(new ArrayList<>()
                , new ArrayList<>(), currentUser, new EventAdapter.OnEventActionListener() {
            @Override
            public void onEdit(Event event) {
                if (currentUser.getRole() == Role.STAFF) {
                    openEditEventDialog(event);
                } else {
                    Toast.makeText(getContext(), "Only STAFF can edit events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDelete(Event event) {
                if (currentUser.getRole() == Role.STAFF) {
                    showDeleteConfirmationDialog("Are you sure you want to delete this event?",
                            () -> deleteEvent(event));
                } else {
                    Toast.makeText(getContext(), "Only STAFF can delete events", Toast.LENGTH_SHORT).show();
                }
            }
        });
        eventRecyclerView.setAdapter(eventAdapter);

        fetchEvents();
    }

    private void fetchEvents() {
        eventRepository.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call,
                                   @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = response.body();
                    fetchUsers();
                } else {
                    showError("Failed to load events. Try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showError("API: Unable to load events.");
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
                    eventAdapter.updateData(events, users);
                } else {
                    showError("Failed to load users.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                showError("API: Unable to load users.");
            }
        });
    }

    private void deleteEvent(Event event) {
        eventRepository.deleteEvent(event.getEventId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    fetchEvents();
                } else {
                    showError("Failed to delete event");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showError("Error deleting event");
            }
        });
    }

    private void openAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment();
        dialog.setParentFragment(this);
        dialog.show(getParentFragmentManager(), "AddEventDialog");
    }

    private void openEditEventDialog(Event event) {
        AddEventDialogFragment dialog = new AddEventDialogFragment();
        dialog.setParentFragment(this);
        dialog.setEditingEvent(event);
        dialog.show(getParentFragmentManager(), "EditEventDialog");
    }

    public void addEventToDatabase(String eventTitle,
                                   String eventDescription,
                                   String eventLocation,
                                   LocalDateTime eventStartTime,
                                   LocalDateTime eventEndTime) {
        if (currentUser == null) {
            showError("Error: No current user found.");
            return;
        }

        Event newEvent = new Event();
        newEvent.setTitle(eventTitle);
        newEvent.setDescription(eventDescription);
        newEvent.setLocation(eventLocation);
        newEvent.setStartTime(eventStartTime);
        newEvent.setEndTime(eventEndTime);
        newEvent.setCreatedBy(currentUser.getUserId());
        newEvent.setCreatedAt(LocalDateTime.now());

        eventRepository.createEvent(newEvent).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                    fetchEvents();
                } else {
                    showError("Failed to add event. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                showError("Error adding event.");
            }
        });
    }

    public void updateEventInDatabase(Event event) {
        eventRepository.updateEvent(event.getEventId(), event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                    fetchEvents();
                } else {
                    showError("Failed to update event.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                showError("Error updating event.");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e("EventFragment", message);
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
