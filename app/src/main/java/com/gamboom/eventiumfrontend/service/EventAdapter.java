package com.gamboom.eventiumfrontend.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Map<UUID, String> userNames;
    private final OnEventActionListener actionListener;
    private final Role currentUserRole;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public interface OnEventActionListener {
        void onEdit(Event event);
        void onDelete(Event event);
    }

    public EventAdapter(List<Event> eventList, List<User> userList, User currentUser, OnEventActionListener actionListener) {
        this.eventList = eventList != null ? eventList : new ArrayList<>();
        this.userNames = new HashMap<>();
        this.currentUserRole = currentUser != null ? currentUser.getRole() : null;
        this.actionListener = actionListener;

        if (userList != null) {
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());
            }
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());
        holder.location.setText(event.getLocation());
        holder.startTime.setText(event.getStartTime() != null ? event.getStartTime().format(FORMATTER) : "N/A");
        holder.endTime.setText(event.getEndTime() != null ? event.getEndTime().format(FORMATTER) : "N/A");
        holder.createdAt.setText(event.getCreatedAt() != null ? event.getCreatedAt().format(FORMATTER) : "N/A");

        String creatorName = userNames.getOrDefault(event.getCreatedBy(), "Unknown");
        holder.createdBy.setText(creatorName);

        holder.btnEdit.setVisibility(View.VISIBLE);
        holder.btnDelete.setVisibility(View.VISIBLE);

        holder.btnEdit.setOnClickListener(v -> {
            if (currentUserRole == Role.STAFF) {
                actionListener.onEdit(event);
            } else {
                Toast.makeText(v.getContext(), "For STAFF ONLY", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (currentUserRole == Role.STAFF) {
                actionListener.onDelete(event);
            } else {
                Toast.makeText(v.getContext(), "For STAFF ONLY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateData(List<Event> newEvents, List<User> userList) {
        eventList.clear();
        eventList.addAll(newEvents != null ? newEvents : new ArrayList<>());

        userNames.clear();
        if (userList != null) {
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());
            }
        }

        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, location, startTime, endTime, createdAt, createdBy;
        ImageButton btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            location = itemView.findViewById(R.id.eventLocation);
            startTime = itemView.findViewById(R.id.eventStartTime);
            endTime = itemView.findViewById(R.id.eventEndTime);
            createdAt = itemView.findViewById(R.id.eventCreatedAt);
            createdBy = itemView.findViewById(R.id.eventCreatedBy);
            btnEdit = itemView.findViewById(R.id.btn_edit_event);
            btnDelete = itemView.findViewById(R.id.btn_delete_event);
        }
    }
}