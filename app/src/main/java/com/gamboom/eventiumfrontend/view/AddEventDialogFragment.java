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

import java.time.LocalDateTime;

public class AddEventDialogFragment extends DialogFragment {
    private EditText etEventTitle, etEventDescription, etEventLocation;
    private DatePicker datePickerStart, datePickerEnd;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnSave, btnCancel;

    private EventFragment parentFragment; // Add reference to the parent fragment

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

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String eventTitle = etEventTitle.getText().toString().trim();
            String eventDescription = etEventDescription.getText().toString().trim();
            String eventLocation = etEventLocation.getText().toString().trim();

            // Get the selected date and time for the start and end times
            int startYear = datePickerStart.getYear();
            int startMonth = datePickerStart.getMonth() + 1;  // Month is 0-based
            int startDay = datePickerStart.getDayOfMonth();
            int startHour = timePickerStart.getHour();
            int startMinute = timePickerStart.getMinute();

            int endYear = datePickerEnd.getYear();
            int endMonth = datePickerEnd.getMonth() + 1;  // Month is 0-based
            int endDay = datePickerEnd.getDayOfMonth();
            int endHour = timePickerEnd.getHour();
            int endMinute = timePickerEnd.getMinute();

            // Convert to LocalDateTime
            LocalDateTime eventStartTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
            LocalDateTime eventEndTime = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute);
            if (eventEndTime.isBefore(eventStartTime)) {
                Toast.makeText(getActivity(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate input and save event
            if (!eventTitle.isEmpty() && !eventLocation.isEmpty() && eventStartTime != null) {
                if (parentFragment != null) {
                    parentFragment.addEventToDatabase(eventTitle,
                                                      eventDescription,
                                                      eventLocation,
                                                      eventStartTime,
                                                      eventEndTime);
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
