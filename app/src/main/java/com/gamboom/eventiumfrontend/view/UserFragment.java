package com.gamboom.eventiumfrontend.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import com.gamboom.eventiumfrontend.service.AppSession;
import com.gamboom.eventiumfrontend.service.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements UserAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserRepository userRepository;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currentUser = AppSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("UserFragment", "Current user ID: " + currentUser.getUserId());
            Log.d("UserFragment", "Current user Role: " + currentUser.getRole());
            Log.d("UserFragment", "Current user Name: " + currentUser.getName());
        } else {
            Log.e("UserFragment", "No current user found in AppSession");
        }

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(userAdapter);

        String authToken = AppSession.getInstance().getAccessToken();
        if (authToken == null || authToken.equals("null")) {
            Toast.makeText(getContext(), "No access token. Please log in.", Toast.LENGTH_SHORT).show();
            return view;
        }

        userRepository = new UserRepository();
        fetchUsers();

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> openAddUserDialog());

        return view;
    }

    private void fetchUsers() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userAdapter.updateData(response.body());
                } else {
                    Log.e("UserFragment", "Failed to load users. Response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                showError("API: Unable to load users.");
                Log.e("UserFragment", "Network error: Unable to load users.", t);
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e("UserFragment", message);
    }

    private void openAddUserDialog() {
        AddUserDialogFragment dialog = new AddUserDialogFragment();
        dialog.setCurrentUserRole(getCurrentUserRole());
        dialog.setParentFragment(this);
        dialog.show(getParentFragmentManager(), "AddUserDialog");
    }

    private Role getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : Role.MEMBER;
    }

    public void addUserToDatabase(String name, String email, String role) {
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setRole(Role.valueOf(role));
        newUser.setPassword("GITHUB_OAUTHENTICATION");
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.createUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error adding user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditUser(User user) {
        if (currentUser.getRole() == Role.STAFF || currentUser.getRole() == Role.ADMIN) {
            AddUserDialogFragment dialog = new AddUserDialogFragment();
            dialog.setCurrentUserRole(currentUser.getRole());
            dialog.setParentFragment(this);
            dialog.setUserToEdit(user);
            dialog.show(getParentFragmentManager(), "EditUserDialog");
        } else {
            Toast.makeText(getContext(), "Only STAFF or ADMIN can edit users", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteUser(User user) {
        if (currentUser.getRole() == Role.STAFF ||
                (currentUser.getRole() == Role.ADMIN && user.getRole() != Role.ADMIN)) {
            userRepository.deleteUser(user.getUserId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                        fetchUsers();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Error deleting user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "You do not have permission to delete this user", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUserInDatabase(UUID userId, User updatedUser) {
        userRepository.updateUser(userId, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                    fetchUsers();
                } else {
                    Toast.makeText(getContext(), "Failed to update user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error updating user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}