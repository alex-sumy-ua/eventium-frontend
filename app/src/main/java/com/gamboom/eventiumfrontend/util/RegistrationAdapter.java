package com.gamboom.eventiumfrontend.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.util.Log;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.EventRegistrationViewHolder> {
    private final List<Registration> registrationList;
    private final Map<UUID, String> eventTitles;
    private final Map<UUID, String> userNames;

    public RegistrationAdapter(List<Registration> registrationList,
                               List<Event> eventList,
                               List<User> userList) {
        this.registrationList = registrationList != null
                                   ? registrationList
                                   : new ArrayList<>(); // Prevent NullPointerException
        this.eventTitles = new HashMap<>();
        this.userNames = new HashMap<>();

        if (eventList != null) { // Prevent NullPointerException
            for (Event event : eventList) {
                eventTitles.put(event.getEventId(), event.getTitle());  // Ensure these getters exist
            }
        }
        Log.d("EventRegistrationAdapter", "List of events: " + eventList);

        if (userList != null) { // Prevent NullPointerException
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());  // Ensure these getters exist
            }
        }
        Log.d("EventRegistrationAdapter", "List of users: " + userList);

    }

    @NonNull
    @Override
    public EventRegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registration_item, parent, false);
        return new EventRegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRegistrationViewHolder holder, int position) {
        Registration registration = registrationList.get(position);

        // Log the eventId & userId UUID values
        Log.d("EventRegistrationAdapter", "EventId UUID: " + registration.getEvent().getEventId());
        Log.d("EventRegistrationAdapter", "UserId UUID: " + registration.getUser().getUserId());

        // Check if eventId UUID exists in the map and fetch the title
        if (registration.getEvent() != null &&
            eventTitles.containsKey(registration.getEvent().getEventId())) {
            String eventTitle = eventTitles.get(registration.getEvent().getEventId());
            holder.event.setText(eventTitle);
        } else {
            // If no event is found in the map, set "Unknown"
            holder.event.setText("Unknown");
        }

        // Check if userId UUID exists in the map and fetch the name
        if (registration.getUser() != null &&
            userNames.containsKey(registration.getUser().getUserId())) {
            String userName = userNames.get(registration.getUser().getUserId());
            holder.user.setText(userName);
        } else {
            // If no user is found in the map, set "Unknown"
            holder.user.setText("Unknown");
        }

        // Format createdAt with DateTimeFormatter and handle null value
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // Null check for LocalDateTime fields
        holder.registrationTime.setText(registration.getRegistrationTime() != null
                ? registration.getRegistrationTime().format(formatter)
                : "N/A");
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    static class EventRegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView event, user, registrationTime;

        public EventRegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.registrationEventTitle);
            user = itemView.findViewById(R.id.userName);
            registrationTime = itemView.findViewById(R.id.registrationTime);
        }
    }

    public void updateData(List<Registration> newRegistrations,
                           List<Event> newEvents,
                           List<User> newUsers) {

        // Update the registrations list
        int previousSize = registrationList.size();
        registrationList.clear();
        registrationList.addAll(newRegistrations);

        // Update the events list
        eventTitles.clear();
        if (newEvents != null) {
            for (Event event : newEvents) {
                eventTitles.put(event.getEventId(), event.getTitle());
            }
        }

        // Update the userNames map
        userNames.clear();
        if (newUsers != null) {
            for (User user : newUsers) {
                userNames.put(user.getUserId(), user.getName());
            }
        }

        // Notify the adapter of changes
        notifyItemRangeInserted(previousSize, newRegistrations.size());
    }

}