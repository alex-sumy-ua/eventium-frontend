package com.gamboom.eventiumfrontend.util;

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

    public UserAdapter(List<User> users) {
        this.users = users != null ? users : new ArrayList<>(); // Prevent null pointer exception
    }

    // Method to update the user data when fetched
    public void updateData(List<User> newUsers) {
        if (newUsers != null) {
            this.users = newUsers;
            notifyDataSetChanged();  // Notify the adapter that the data has been updated
        }
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

        // Handle null and format the enum Role value
        holder.userRole.setText(user.getRole() != null ? user.getRole().toString() : "Unknown");

        // Format createdAt with DateTimeFormatter and handle null value
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        holder.userCreatedAt.setText(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A");
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0; // Return 0 if users is null
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
