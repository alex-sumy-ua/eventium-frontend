package com.gamboom.eventiumfrontend.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    public interface OnRegistrationActionListener {
        void onDeleteRegistration(Registration registration);
    }

    private final List<Registration> registrationList = new ArrayList<>();
    private final List<Registration> allRegistrations = new ArrayList<>();
    private final Map<UUID, String> userNames = new HashMap<>();
    private final Map<UUID, Event> eventMap = new HashMap<>();

    private final OnRegistrationActionListener listener;
    private final User currentUser;

    private boolean showOnlyMine = false;

    public RegistrationAdapter(List<Registration> registrations,
                               List<Event> eventList,
                               List<User> userList,
                               OnRegistrationActionListener listener) {
        this.listener = listener;
        this.currentUser = AppSession.getInstance().getCurrentUser();

        if (eventList != null) {
            for (Event event : eventList) {
                eventMap.put(event.getEventId(), event);
            }
        }

        if (userList != null) {
            for (User user : userList) {
                userNames.put(user.getUserId(), user.getName());
            }
        }

        if (registrations != null) {
            allRegistrations.addAll(registrations);
        }

        applyFilter(); // initial data population
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
        Event event = eventMap.get(registration.getEventId());

        String userName = userNames.getOrDefault(registration.getUserId(), "Unknown User");
        String eventTitle = (event != null) ? event.getTitle() : "Unknown Event";
        String formattedDate = formatDateTime(registration.getRegistrationTime());

        holder.eventTitleTextView.setText(eventTitle);
        holder.userNameTextView.setText(userName);
        holder.registrationTimeTextView.setText(formattedDate);

        boolean isSelf = currentUser != null && registration.getUserId().equals(currentUser.getUserId());
        boolean isStaff = currentUser != null && currentUser.getRole() == Role.STAFF;

        // Delete Button
        if (isSelf || isStaff) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteRegistration(registration);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Calendar Button — visible only for own registrations
        if (isSelf) {
            holder.btnAddToCalendar.setVisibility(View.VISIBLE);
            holder.btnAddToCalendar.setOnClickListener(v -> {
                if (event != null) {
                    Context context = v.getContext();
                    long startMillis = event.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long endMillis = event.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, event.getTitle());
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);

                    context.startActivity(intent);
                }
            });
        } else {
            holder.btnAddToCalendar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return registrationList.size();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitleTextView;
        TextView userNameTextView;
        TextView registrationTimeTextView;
        ImageButton btnDelete;
        ImageButton btnAddToCalendar;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            registrationTimeTextView = itemView.findViewById(R.id.registrationTimeTextView);
            btnDelete = itemView.findViewById(R.id.btn_delete_registration);
            btnAddToCalendar = itemView.findViewById(R.id.btn_add_to_calendar);
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

        if (newEvents != null) {
            eventMap.clear();
            for (Event event : newEvents) {
                eventMap.put(event.getEventId(), event);
            }
        }

        if (newUsers != null) {
            userNames.clear();
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
