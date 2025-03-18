package com.gamboom.eventiumfrontend.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.User;

import java.util.UUID;

public class AddRegistrationDialogFragment extends DialogFragment {
    private EditText etEventTitle;
    private Button btnSave, btnCancel;

    private User currentUser;  // Add reference for currentUser

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_update_registration, container, false);

        etEventTitle = view.findViewById(R.id.et_eventtitle);

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String eventTitle = etEventTitle.getText().toString().trim();

            UUID userId = currentUser.getUserId(); // Ensure currentUser is initialized

            if (!eventTitle.isEmpty()) {
                ((RegistrationFragment) getParentFragment()).addRegistrationToDatabase(eventTitle,
                                                                                       userId);
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Please fill the event title", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Add method to set currentUser
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}