package com.gamboom.eventiumfrontend.service;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Registration;
import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import android.util.Log;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    public interface OnRegistrationActionListener {
        void onDeleteRegistration(Registration registration);
    }

    private final List<Registration> registrationList;
    private final Map<UUID, String> eventTitles;
    private final Map<UUID, String> userNames;
    private final OnRegistrationActionListener listener;
    private final User currentUser;

    public RegistrationAdapter(List<Registration> registrationList,
                               List<Event> eventList,
                               List<User> userList,
                               OnRegistrationActionListener listener) {
        this.registrationList = new ArrayList<>();
        this.eventTitles = new HashMap<>();
        this.userNames = new HashMap<>();
        this.listener = listener;
        this.currentUser = AppSession.getInstance().getCurrentUser();

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
                .inflate(R.layout.item_registration, parent, false);
        return new RegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        Log.d("AdapterBind", "Binding position: " + position);
        Registration registration = registrationList.get(position);

        String userName = userNames.getOrDefault(registration.getUserId(), "Unknown User");
        String eventTitle = eventTitles.getOrDefault(registration.getEventId(), "Unknown Event");
        String formattedDate = formatDateTime(registration.getRegistrationTime());

        holder.eventTitleTextView.setText(eventTitle);
        holder.userNameTextView.setText(userName);
        holder.registrationTimeTextView.setText(formattedDate);

        boolean isSelf = currentUser != null && registration.getUserId().equals(currentUser.getUserId());
        boolean isStaff = currentUser != null && currentUser.getRole() == Role.STAFF;

        if (isSelf || isStaff) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteRegistration(registration);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // ✅ Add this log for debugging
        Log.d("AdapterBind", "Bound item for event: " + eventTitle + ", user: " + userName);
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView userNameTextView;
        TextView registrationTimeTextView;
        ImageButton btnDelete;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            registrationTimeTextView = itemView.findViewById(R.id.registrationTimeTextView);
            btnDelete = itemView.findViewById(R.id.btn_delete_registration);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Registration> newRegistrations,
                           List<Event> newEvents,
                           List<User> newUsers) {

        Log.d("AdapterUpdate", "Adapter received " + (newRegistrations != null ? newRegistrations.size() : 0) + " registrations");

        registrationList.clear();
        if (newRegistrations != null) {
            registrationList.addAll(newRegistrations);
            registrationList.sort(Comparator.comparing(Registration::getRegistrationTime).reversed());
        }

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