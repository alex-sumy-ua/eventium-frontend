package com.gamboom.eventiumfrontend.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.EventRepository;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRegistrationDialogFragment extends DialogFragment {

    private Spinner eventSpinner;
    private TextView eventDescription, eventTime, eventLocation;
    private Button btnRegister, btnCancel;
    private List<Event> events;
    private User currentUser; // Current logged-in user
    private RegistrationFragment parentFragment; // Reference to the parent fragment

    // Add method to set parent fragment
    public void setParentFragment(RegistrationFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the dialog's width and height
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT  // Height
            );
        }
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_update_registration, container, false);

        eventSpinner = view.findViewById(R.id.eventSpinner);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventTime = view.findViewById(R.id.eventTime);
        eventLocation = view.findViewById(R.id.eventLocation);
        btnRegister = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        // Fetch events and populate the spinner
        fetchEvents();

        btnCancel.setOnClickListener(v -> dismiss());

        btnRegister.setOnClickListener(v -> {
            Event selectedEvent = events.get(eventSpinner.getSelectedItemPosition());

            // Check if the event has already passed
            if (LocalDateTime.now().isAfter(selectedEvent.getStartTime())) {
                Toast.makeText(getActivity(), "This event has already passed", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register the user for the event
            if (parentFragment != null) {
                parentFragment.addRegistrationToDatabase(
                        selectedEvent.getEventId(), // Pass the eventId directly
                        currentUser.getUserId()
                );
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Parent fragment is not set", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchEvents() {
        EventRepository eventRepository = new EventRepository();
        String token = AppSession.getInstance().getAccessToken();

        eventRepository.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter events to show only future events
                    events = new ArrayList<>();
                    LocalDateTime now = LocalDateTime.now();
                    for (Event event : response.body()) {
                        if (event.getStartTime().isAfter(now)) {
                            events.add(event);
                        }
                    }

                    if (events.isEmpty()) {
                        Toast.makeText(getActivity(), "No upcoming events found", Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog if there are no future events
                    } else {
                        populateEventSpinner();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateEventSpinner() {
        List<String> eventTitles = new ArrayList<>();
        for (Event event : events) {
            eventTitles.add(event.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, eventTitles);
        eventSpinner.setAdapter(adapter);

        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = events.get(position);
                eventDescription.setText(selectedEvent.getDescription());
                eventTime.setText(selectedEvent.getStartTime().toString());
                eventLocation.setText(selectedEvent.getLocation());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Add method to set currentUser
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}