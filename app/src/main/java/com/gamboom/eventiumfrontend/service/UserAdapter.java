package com.gamboom.eventiumfrontend.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users != null ? users : new ArrayList<>();
        this.listener = listener;
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

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditUser(user);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteUser(user);
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public void updateData(List<User> newUsers) {
        if (newUsers != null) {
            this.users.clear();
            this.users.addAll(newUsers);
            notifyDataSetChanged();
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userRole, userCreatedAt;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userNameTextView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
            userCreatedAt = itemView.findViewById(R.id.userCreatedAt);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
