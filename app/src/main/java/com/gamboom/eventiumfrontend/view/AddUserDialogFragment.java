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
import com.gamboom.eventiumfrontend.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddUserDialogFragment extends DialogFragment {

    private EditText etUserName, etEmail;
    private Spinner spinnerRole;
    private Button btnSave, btnCancel;

    private Role currentUserRole;
    private UserFragment parentFragment;
    private User userToEdit;

    public void setCurrentUserRole(Role currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public void setParentFragment(UserFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setUserToEdit(User userToEdit) {
        this.userToEdit = userToEdit;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_update_user, container, false);

        etUserName = view.findViewById(R.id.et_username);
        etEmail = view.findViewById(R.id.et_email);
        spinnerRole = view.findViewById(R.id.spinner_role);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        List<String> allowedRoles = getAllowedRoles(currentUserRole);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, allowedRoles);
        spinnerRole.setAdapter(adapter);

        if (userToEdit != null) {
            etUserName.setText(userToEdit.getName());
            etEmail.setText(userToEdit.getEmail());
            spinnerRole.setSelection(allowedRoles.indexOf(userToEdit.getRole().name()));
        }

        if (currentUserRole == Role.MEMBER) {
            etUserName.setEnabled(false);
            etEmail.setEnabled(false);
            spinnerRole.setEnabled(false);
            btnSave.setEnabled(false);
            Toast.makeText(getActivity(), "For STAFF or ADMIN only", Toast.LENGTH_SHORT).show();
        }

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String selectedRole = spinnerRole.getSelectedItem().toString();

            if (!username.isEmpty() && !email.isEmpty()) {
                if (parentFragment != null) {
                    if (userToEdit != null) {
                        userToEdit.setName(username);
                        userToEdit.setEmail(email);
                        userToEdit.setRole(Role.valueOf(selectedRole));
                        parentFragment.updateUserInDatabase(userToEdit.getUserId(), userToEdit);
                    } else {
                        parentFragment.addUserToDatabase(username, email, selectedRole);
                    }
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
                allowedRoles.add("Available for STAFF or ADMIN only");
                break;
        }
        return allowedRoles;
    }

}
