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

public class AddUserDialogFragment extends DialogFragment {
    private EditText etUserName, etEmail;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel;

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

//        // Populate the Spinner with roles
//        String[] roles = {"ADMIN", "STAFF", "MEMBER"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
//                                       android.R.layout.simple_spinner_dropdown_item, roles);
//        spinnerRole.setAdapter(adapter);

        // Populate the Spinner with roles from the Role enum
        Role[] roles = Role.values(); // Get all enum values
        String[] roleNames = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleNames[i] = roles[i].name(); // Extract the role names (e.g., "ADMIN", "STAFF")
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, roleNames);
        spinnerRole.setAdapter(adapter);

        // Set button actions
        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String selectedRole = spinnerRole.getSelectedItem().toString(); // Get selected role

            if (!username.isEmpty() && !email.isEmpty()) {
                assert getParentFragment() != null;
                ((UserFragment) getParentFragment()).addUserToDatabase(username,
                                                                       email,
                                                                       selectedRole);
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
