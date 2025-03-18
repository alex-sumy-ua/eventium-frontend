package com.gamboom.eventiumfrontend.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    // Constructor with listener
    public UserAdapter(List<User> users, OnUserClickListener listener) {
        this.users = users != null ? users : new ArrayList<>();
        this.listener = listener;
    }

    // Constructor without listener (for backward compatibility)
    public UserAdapter(List<User> users) {
        this(users, null);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());
        holder.userRole.setText(user.getRole() != null ? user.getRole().toString() : "Unknown");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        holder.userCreatedAt.setText(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A");

        // Set click listener if listener is not null
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    // Method to update the user data
    public void updateData(List<User> newUsers) {
        if (newUsers != null) {
            this.users.clear();
            this.users.addAll(newUsers);
            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userRole, userCreatedAt;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userNameTextView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
            userCreatedAt = itemView.findViewById(R.id.userCreatedAt);
        }
    }

}