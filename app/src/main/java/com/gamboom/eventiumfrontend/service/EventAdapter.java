package com.gamboom.eventiumfrontend.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.util.Log;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Map<UUID, String> userNames;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EventAdapter(List<Event> eventList, List<User> userList) {
        this.eventList = eventList != null ? eventList : new ArrayList<>(); // Prevent NullPointerException
        this.userNames = new HashMap<>();

        if (userList != null) { // Prevent NullPointerException
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());  // Ensure these getters exist
            }
        }
        Log.d("EventAdapter", "List of users: " + userList);
        Log.d("EventAdapter", "List of names: " + userNames);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.location.setText(event.getLocation());

        // Format createdAt with DateTimeFormatter and handle null value
        holder.startTime.setText(event.getStartTime() != null
                ? event.getStartTime().format(FORMATTER)
                : "N/A");
        holder.endTime.setText(event.getEndTime() != null
                ? event.getEndTime().format(FORMATTER)
                : "N/A");
        holder.createdAt.setText(event.getCreatedAt() != null
                ? event.getCreatedAt().format(FORMATTER)
                : "N/A");

        // Log the createdBy UUID value
        Log.d("EventAdapter", "CreatedBy UUID: " + event.getCreatedBy());

        // Check if createdBy UUID exists in the map and fetch the name
        if (event.getCreatedBy() != null && userNames.containsKey(event.getCreatedBy())) {
            String creatorName = userNames.get(event.getCreatedBy());
            holder.createdBy.setText(creatorName);
        } else {
            // If no user is found in the map, set "Unknown"
            holder.createdBy.setText("Unknown");
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView location;
        TextView startTime;
        TextView endTime;
        TextView createdAt;
        TextView createdBy;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            location = itemView.findViewById(R.id.eventLocation);
            startTime = itemView.findViewById(R.id.eventStartTime);
            endTime = itemView.findViewById(R.id.eventEndTime);
            createdAt = itemView.findViewById(R.id.eventCreatedAt);
            createdBy = itemView.findViewById(R.id.eventCreatedBy);
        }
    }

    public void updateData(List<Event> newEvents, List<User> userList) {
        // Update the events list
        eventList.clear();
        eventList.addAll(newEvents != null ? newEvents : new ArrayList<>());

        // Update the userNames map
        userNames.clear();
        if (userList != null) {
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());
            }
        }

        // Notify the adapter of changes
        notifyDataSetChanged();
    }
}