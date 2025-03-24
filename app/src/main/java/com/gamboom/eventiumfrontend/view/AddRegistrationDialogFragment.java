package com.gamboom.eventiumfrontend.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
    private TextView eventDescription, eventTime, eventLocation, noEventsMessage;
    private Button btnRegister, btnCancel;
    private List<Event> events;
    private User currentUser;
    private RegistrationFragment parentFragment;

    public void setParentFragment(RegistrationFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        currentUser = AppSession.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.dialog_add_registration, container, false);

        eventSpinner = view.findViewById(R.id.eventSpinner);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventTime = view.findViewById(R.id.eventTime);
        eventLocation = view.findViewById(R.id.eventLocation);
        btnRegister = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        noEventsMessage = view.findViewById(R.id.noEventsMessage);

        btnCancel.setOnClickListener(v -> dismiss());

        btnRegister.setOnClickListener(v -> {
            if (events == null || events.isEmpty()) {
                Toast.makeText(getActivity(), "No events to register for", Toast.LENGTH_SHORT).show();
                return;
            }

            Event selectedEvent = events.get(eventSpinner.getSelectedItemPosition());

            if (LocalDateTime.now().isAfter(selectedEvent.getEndTime())) {
                Toast.makeText(getActivity(), "This event is over", Toast.LENGTH_SHORT).show();
                return;
            }

            if (parentFragment != null) {
                parentFragment.addRegistrationToDatabase(selectedEvent.getEventId(), currentUser.getUserId());
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Parent fragment not found", Toast.LENGTH_SHORT).show();
            }
        });

        fetchEvents();
        return view;
    }

    private void fetchEvents() {
        EventRepository eventRepository = new EventRepository();
        eventRepository.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    events = new ArrayList<>();
                    LocalDateTime now = LocalDateTime.now();

                    for (Event event : response.body()) {
                        if (event.getEndTime().isAfter(now)) {
                            events.add(event);
                        }
                    }

                    if (events.isEmpty()) {
                        showNoEventsMessage();
                    } else {
                        populateEventSpinner();
                        showEventForm();
                    }
                } else {
                    showNoEventsMessage();
                    Toast.makeText(getActivity(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                showNoEventsMessage();
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

    private void showNoEventsMessage() {
        noEventsMessage.setVisibility(View.VISIBLE);
        eventSpinner.setVisibility(View.GONE);
        eventDescription.setVisibility(View.GONE);
        eventTime.setVisibility(View.GONE);
        eventLocation.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);
    }

    private void showEventForm() {
        noEventsMessage.setVisibility(View.GONE);
        eventSpinner.setVisibility(View.VISIBLE);
        eventDescription.setVisibility(View.VISIBLE);
        eventTime.setVisibility(View.VISIBLE);
        eventLocation.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
    }
}
