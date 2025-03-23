package com.gamboom.eventiumfrontend.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gamboom.eventiumfrontend.R;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.time.LocalDateTime;

public class AddEventDialogFragment extends DialogFragment {
    private EditText etEventTitle, etEventDescription, etEventLocation;
    private DatePicker datePickerStart, datePickerEnd;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnSave, btnCancel;

    private EventFragment parentFragment; // Add reference to the parent fragment

    private final User currentUser;

    public AddEventDialogFragment() {
        this.currentUser = AppSession.getInstance().getCurrentUser();
    }

    // Add a method to set the parent fragment
    public void setParentFragment(EventFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_add_update_event, container, false);

        etEventTitle = view.findViewById(R.id.et_eventtitle);
        etEventDescription = view.findViewById(R.id.et_eventdescription);
        etEventLocation = view.findViewById(R.id.et_eventlocation);

        datePickerStart = view.findViewById(R.id.date_picker_start);
        timePickerStart = view.findViewById(R.id.time_picker_start);
        datePickerEnd = view.findViewById(R.id.date_picker_end);
        timePickerEnd = view.findViewById(R.id.time_picker_end);

        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        // Cancel always enabled
        btnCancel.setOnClickListener(v -> dismiss());

        // Restrict MEMBER access
        if (currentUser != null && currentUser.getRole().name().equals("MEMBER")) {
            // Set message in the title field
            etEventDescription.setText("Available for STAFF or ADMIN only");

            // Disable all inputs
            etEventTitle.setEnabled(false);
            etEventDescription.setEnabled(false);
            etEventLocation.setEnabled(false);
            datePickerStart.setEnabled(false);
            timePickerStart.setEnabled(false);
            datePickerEnd.setEnabled(false);
            timePickerEnd.setEnabled(false);
            btnSave.setEnabled(false);

            // Show a toast
            Toast.makeText(getActivity(), "Your current role is a MEMBER", Toast.LENGTH_SHORT).show();

            return view;
        }

        // Staff or Admin logic
        btnSave.setOnClickListener(v -> {
            String eventTitle = etEventTitle.getText().toString().trim();
            String eventDescription = etEventDescription.getText().toString().trim();
            String eventLocation = etEventLocation.getText().toString().trim();

            int startYear = datePickerStart.getYear();
            int startMonth = datePickerStart.getMonth() + 1;
            int startDay = datePickerStart.getDayOfMonth();
            int startHour = timePickerStart.getHour();
            int startMinute = timePickerStart.getMinute();

            int endYear = datePickerEnd.getYear();
            int endMonth = datePickerEnd.getMonth() + 1;
            int endDay = datePickerEnd.getDayOfMonth();
            int endHour = timePickerEnd.getHour();
            int endMinute = timePickerEnd.getMinute();

            LocalDateTime eventStartTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
            LocalDateTime eventEndTime = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute);

            if (eventEndTime.isBefore(eventStartTime)) {
                Toast.makeText(getActivity(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!eventTitle.isEmpty() && !eventLocation.isEmpty()) {
                if (parentFragment != null) {
                    parentFragment.addEventToDatabase(eventTitle, eventDescription, eventLocation, eventStartTime, eventEndTime);
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

}
