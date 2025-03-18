package com.gamboom.eventiumfrontend.view;

import android.content.Context;
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
import com.gamboom.eventiumfrontend.service.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with an empty list
        userAdapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        // Initialize repository and fetch users
        userRepository = new UserRepository();
        fetchUsers();

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> openAddUserDialog());

        return view;
    }

    private void fetchUsers() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Context context = getContext(); // Prevent null context
                if (context != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (!response.body().isEmpty()) {
                            userAdapter.updateData(response.body());
                        } else {
                            Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log response details for debugging
                        Log.e("UserFragment", "Failed to load users. Response code: " + response.code());
                        Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Context context = getContext();
                if (context != null) {
                    Toast.makeText(context, "Error loading users", Toast.LENGTH_SHORT).show();
                }
                // Log the error for debugging
                Log.e("UserFragment", "API call failed", t);
            }
        });
    }

    private void showError(String message) {
        Log.e("RegistrationFragment", message);
        // Display a Toast or Snackbar to the user
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void openAddUserDialog() {
        AddUserDialogFragment dialog = new AddUserDialogFragment();
        dialog.show(getParentFragmentManager(), "AddUserDialog");
    }

    public void addUserToDatabase(String name,
                                  String email,
                                  String role) {
        // Create a new User object with the provided data
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        // Handle potential invalid role input
        try {
            newUser.setRole(Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            // Handle invalid role
            Log.e("UserFragment", "Invalid role: " + role);
            Toast.makeText(getContext(), "Invalid role provided", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if role is invalid
        }
        newUser.setPassword("GITHUB_OAUTHENTICATION");
        newUser.setCreatedAt(LocalDateTime.now());

        // Call the addUser API method in the UserRepository
        userRepository.createUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Context context = getContext();
                if (context != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        // User successfully added
                        Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show();

                        // Optionally, update the UI or fetch the latest user list
                        fetchUsers();
                    } else {
                        // Handle the error response
                        Log.e("UserFragment", "Failed to add user. Response code: " + response.code());
                        Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Context context = getContext();
                if (context != null) {
                    Toast.makeText(context, "Error adding user", Toast.LENGTH_SHORT).show();
                }
                // Log the error for debugging
                Log.e("UserFragment", "API call failed", t);
            }
        });
    }

}
