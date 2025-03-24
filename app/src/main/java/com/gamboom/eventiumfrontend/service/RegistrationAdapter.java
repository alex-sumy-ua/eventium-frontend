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

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    public interface OnRegistrationActionListener {
        void onDeleteRegistration(Registration registration);
    }

    private final List<Registration> registrationList;
    private final List<Registration> allRegistrations;
    private final Map<UUID, String> eventTitles;
    private final Map<UUID, String> userNames;
    private final OnRegistrationActionListener listener;
    private final User currentUser;

    private boolean showOnlyMine = false;

    public RegistrationAdapter(List<Registration> registrationList,
                               List<Event> eventList,
                               List<User> userList,
                               OnRegistrationActionListener listener) {
        this.registrationList = new ArrayList<>();
        this.allRegistrations = new ArrayList<>();
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

        if (registrationList != null) {
            allRegistrations.addAll(registrationList);
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

        allRegistrations.clear();
        if (newRegistrations != null) {
            allRegistrations.addAll(newRegistrations);
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

        applyFilter();
    }

    public void toggleFilterOnlyMine() {
        showOnlyMine = !showOnlyMine;
        applyFilter();
    }

    private void applyFilter() {
        registrationList.clear();
        if (showOnlyMine && currentUser != null) {
            for (Registration reg : allRegistrations) {
                if (reg.getUserId().equals(currentUser.getUserId())) {
                    registrationList.add(reg);
                }
            }
        } else {
            registrationList.addAll(allRegistrations);
        }
        registrationList.sort(Comparator.comparing(Registration::getRegistrationTime).reversed());
        notifyDataSetChanged();
    }

    public boolean isShowingOnlyMine() {
        return showOnlyMine;
    }

}
