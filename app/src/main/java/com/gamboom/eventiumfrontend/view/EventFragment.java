package com.gamboom.eventiumfrontend.view;

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
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.EventRepository;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import com.gamboom.eventiumfrontend.service.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    public EventFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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
        eventAdapter = new EventAdapter(new ArrayList<>(),
                                        new ArrayList<>());
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
                    eventAdapter.updateData(events, users);
                } else {
                    Log.e("EventFragment", "Failed to load users. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("EventFragment", "Network error: Unable to load users.", t);
                showError("Network error: Unable to load users.");
            }
        });
    }

    private void showError(String message) {
        // Implement UI error handling (e.g., Toast, Snackbar, or Log message)
        Log.e("EventFragment", message);
    }

    private void openAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment();
        dialog.show(getParentFragmentManager(), "AddEventDialog");
    }

}
