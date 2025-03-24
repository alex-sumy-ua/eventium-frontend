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
import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private final OnUserActionListener listener;
    private final Role currentUserRole;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users != null ? users : new ArrayList<>();
        this.listener = listener;
        this.currentUserRole = AppSession.getInstance().getCurrentUser().getRole();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
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
            if ((currentUserRole == Role.STAFF || currentUserRole == Role.ADMIN) && listener != null) {
                listener.onEditUser(user);
            }else {
                Toast.makeText(v.getContext(), "For STAFF or ADMIN ONLY", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (currentUserRole == Role.STAFF && listener != null) {
                listener.onDeleteUser(user);
                }else {
                Toast.makeText(v.getContext(), "For STAFF ONLY", Toast.LENGTH_SHORT).show();
            }
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
