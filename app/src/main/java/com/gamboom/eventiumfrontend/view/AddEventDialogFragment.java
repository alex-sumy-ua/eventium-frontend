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
import com.gamboom.eventiumfrontend.model.Event;
import com.gamboom.eventiumfrontend.model.Role;
import com.gamboom.eventiumfrontend.model.User;
import com.gamboom.eventiumfrontend.service.AppSession;

import java.time.LocalDateTime;

public class AddEventDialogFragment extends DialogFragment {
    private EditText etEventTitle, etEventDescription, etEventLocation;
    private DatePicker datePickerStart, datePickerEnd;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnSave, btnCancel;

    private EventFragment parentFragment;
    private final User currentUser;

    private Event editingEvent;

    public AddEventDialogFragment() {
        this.currentUser = AppSession.getInstance().getCurrentUser();
    }

    public void setParentFragment(EventFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setEditingEvent(Event editingEvent) {
        this.editingEvent = editingEvent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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

        if (currentUser != null && currentUser.getRole() == Role.MEMBER) {
            etEventTitle.setText("Available for STAFF or ADMIN only");
            etEventTitle.setEnabled(false);
            etEventDescription.setEnabled(false);
            etEventLocation.setEnabled(false);
            datePickerStart.setEnabled(false);
            timePickerStart.setEnabled(false);
            datePickerEnd.setEnabled(false);
            timePickerEnd.setEnabled(false);
            btnSave.setEnabled(false);
            Toast.makeText(getActivity(), "Your current role is a MEMBER", Toast.LENGTH_SHORT).show();
            return view;
        }

        if (editingEvent != null) {
            etEventTitle.setText(editingEvent.getTitle());
            etEventDescription.setText(editingEvent.getDescription());
            etEventLocation.setText(editingEvent.getLocation());

            LocalDateTime start = editingEvent.getStartTime();
            LocalDateTime end = editingEvent.getEndTime();

            if (start != null) {
                datePickerStart.updateDate(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
                timePickerStart.setHour(start.getHour());
                timePickerStart.setMinute(start.getMinute());
            }
            if (end != null) {
                datePickerEnd.updateDate(end.getYear(), end.getMonthValue() - 1, end.getDayOfMonth());
                timePickerEnd.setHour(end.getHour());
                timePickerEnd.setMinute(end.getMinute());
            }
        }

        btnSave.setOnClickListener(v -> {
            String title = etEventTitle.getText().toString().trim();
            String description = etEventDescription.getText().toString().trim();
            String location = etEventLocation.getText().toString().trim();

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

            LocalDateTime startTime = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute);
            LocalDateTime endTime = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute);

            if (endTime.isBefore(startTime)) {
                Toast.makeText(getActivity(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!title.isEmpty() && !location.isEmpty()) {
                if (parentFragment != null) {
                    if (editingEvent != null) {
                        editingEvent.setTitle(title);
                        editingEvent.setDescription(description);
                        editingEvent.setLocation(location);
                        editingEvent.setStartTime(startTime);
                        editingEvent.setEndTime(endTime);
                        parentFragment.updateEventInDatabase(editingEvent);
                    } else {
                        parentFragment.addEventToDatabase(title, description, location, startTime, endTime);
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
}
