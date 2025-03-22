package com.gamboom.eventiumfrontend.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.Role;

import java.util.ArrayList;
import java.util.List;

public class AddUserDialogFragment extends DialogFragment {

    private EditText etUserName, etEmail;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel;

    private Role currentUserRole; // Track the current user's role
    private UserFragment parentFragment; // Reference to the parent fragment

    // Method to set the current user's role
    public void setCurrentUserRole(Role currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    // Method to set the parent fragment
    public void setParentFragment(UserFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

@Override
public void onStart() {
    super.onStart();

    // Set the dialog's width and height
    if (getDialog() != null && getDialog().getWindow() != null) {
        getDialog().getWindow().setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, // Width
            ViewGroup.LayoutParams.WRAP_CONTENT  // Height
        );
    }
}    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_update_user, container, false);

        etUserName = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        spinnerRole = view.findViewById(R.id.spinner_role);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        // Populate the Spinner with roles based on the current user's role
        if (currentUserRole != null) {
            List<String> allowedRoles = getAllowedRoles(currentUserRole);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, allowedRoles);
            spinnerRole.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "Current user role is not set", Toast.LENGTH_SHORT).show();
            dismiss(); // Close the dialog if the role is not set
        }

        // Set button actions
        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String selectedRole = spinnerRole.getSelectedItem().toString(); // Get selected role

            if (!username.isEmpty() && !email.isEmpty()) {
                if (parentFragment != null) {
                    parentFragment.addUserToDatabase(username, email, selectedRole);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Parent fragment is not set", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Method to determine allowed roles based on the current user's role
    private List<String> getAllowedRoles(Role currentUserRole) {
        List<String> allowedRoles = new ArrayList<>();

        switch (currentUserRole) {
            case ADMIN:
                allowedRoles.add(Role.ADMIN.name());
                allowedRoles.add(Role.STAFF.name());
                allowedRoles.add(Role.MEMBER.name());
                break;
            case STAFF:
                allowedRoles.add(Role.STAFF.name());
                allowedRoles.add(Role.MEMBER.name());
                break;
            case MEMBER:
                // MEMBER cannot create any users, so the list remains empty
                break;
        }

        return allowedRoles;
    }
}
