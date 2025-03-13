package com.gamboom.eventiumfrontend.fragments;

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
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.repository.UserRepository;
import com.gamboom.eventiumfrontend.util.UserAdapter;
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
}
