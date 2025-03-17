package com.gamboom.eventiumfrontend.service;

import android.annotation.SuppressLint;
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

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {
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

        // Populate eventTitles map with event IDs and their corresponding titles
        if (eventList != null) {
            for (Event event : eventList) {
                eventTitles.put(event.getEventId(), event.getTitle());
            }
        }

        // Populate userNames map with user IDs and their corresponding names
        if (userList != null) {
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());
            }
        }
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.registration_item, parent, false);
        return new RegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        Registration registration = registrationList.get(position);

        // Retrieve user name and event title from maps with null checks
        String userName = "Unknown User";
        if (registration.getUserId() != null) {
            userName = userNames.getOrDefault(registration.getUserId(), "Unknown User");
        }

        String eventTitle = "Unknown Event";
        if (registration.getEventId() != null) {
            eventTitle = eventTitles.getOrDefault(registration.getEventId(), "Unknown Event");
        }

        // Format registration date with null check
        String formattedDate = "N/A";
        if (registration.getRegistrationTime() != null) {
            formattedDate = registration.getRegistrationTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        // Set text values
        holder.eventTitleTextView.setText(eventTitle);
        holder.userNameTextView.setText(userName);
        holder.registrationTimeTextView.setText(formattedDate);

        // Log the final values being set
        Log.d("RegistrationAdapter", "User Name: " + userName);
        Log.d("RegistrationAdapter", "Event Title: " + eventTitle);
        Log.d("RegistrationAdapter", "Registration Time: " + formattedDate);
    }
    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView userNameTextView;
        TextView registrationTimeTextView;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            registrationTimeTextView = itemView.findViewById(R.id.registrationTimeTextView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Registration> newRegistrations,
                           List<Event> newEvents,
                           List<User> newUsers) {

        Log.d("RegistrationAdapter", "New Registrations: " + newRegistrations.size());
        Log.d("RegistrationAdapter", "New Events: " + (newEvents != null ? newEvents.size() : 0));
        Log.d("RegistrationAdapter", "New Users: " + (newUsers != null ? newUsers.size() : 0));

        // Update the registrations list
        registrationList.clear();
        registrationList.addAll(newRegistrations);

        // Update the eventTitles map
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
        notifyDataSetChanged();
    }

}
