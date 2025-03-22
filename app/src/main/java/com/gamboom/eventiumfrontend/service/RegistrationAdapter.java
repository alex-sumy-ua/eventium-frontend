package com.gamboom.eventiumfrontend.service;

import android.annotation.SuppressLint;
import android.util.Log;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    private final List<Registration> registrationList;
    private final Map<UUID, String> eventTitles;
    private final Map<UUID, String> userNames;

    private OnRegistrationClickListener listener;

    // Optional click listener interface
    public interface OnRegistrationClickListener {
        void onRegistrationClick(Registration registration);
    }

    // Constructor with click listener
    public RegistrationAdapter(List<Registration> registrationList,
                               List<Event> eventList,
                               List<User> userList,
                               OnRegistrationClickListener listener) {
        this(registrationList, eventList, userList);
        this.listener = listener;
    }

    // Constructor without click listener
    public RegistrationAdapter(List<Registration> registrationList,
                               List<Event> eventList,
                               List<User> userList) {
        this.registrationList = registrationList != null ? registrationList : new ArrayList<>();
        this.eventTitles = new HashMap<>();
        this.userNames = new HashMap<>();
        this.listener = null;

        if (eventList != null) {
            for (Event event : eventList) {
                eventTitles.put(event.getEventId(), event.getTitle());
            }
        }

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

        String userName = userNames.getOrDefault(registration.getUserId(), "Unknown User");
        String eventTitle = eventTitles.getOrDefault(registration.getEventId(), "Unknown Event");
        String formattedDate = formatDateTime(registration.getRegistrationTime());

        holder.eventTitleTextView.setText(eventTitle);
        holder.userNameTextView.setText(userName);
        holder.registrationTimeTextView.setText(formattedDate);

        Log.d("RegistrationAdapter", "User: " + userName + ", Event: " + eventTitle + ", Time: " + formattedDate);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onRegistrationClick(registration));
        }
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    // Reusable date formatter
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
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

        Log.d("RegistrationAdapter", "Updating data...");

        registrationList.clear();
        registrationList.addAll(newRegistrations != null ? newRegistrations : new ArrayList<>());

        eventTitles.clear();
        if (newEvents != null) {
            for (Event event : newEvents) {
                eventTitles.put(event.getEventId(), event.getTitle());
            }
        }

        userNames.clear();
        if (newUsers != null) {
            for (User user : newUsers) {
                userNames.put(user.getUserId(), user.getName());
            }
        }

        notifyDataSetChanged();
    }

}
