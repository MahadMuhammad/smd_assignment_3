package com.example.smd_assignment_3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;
    private Calendar selectedDateTime;
    private LinearLayout dateContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        dbHelper = new DatabaseHelper(getContext());
        selectedDateTime = Calendar.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dateContainer = view.findViewById(R.id.date_container);
        setupDateChips();

        loadTasksForSelectedDate();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void setupDateChips() {
        // Clear existing views
        dateContainer.removeAllViews();

        // Create chip group for date selection
        ChipGroup chipGroup = new ChipGroup(requireContext());
        chipGroup.setSingleSelection(true);
        chipGroup.setSelectionRequired(true);

        // Format for date chips
        SimpleDateFormat chipDateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

        // Add chips for next 7 days
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Chip chip = new Chip(requireContext());

            // Format date and set special text for today
            String dateText = i == 0 ?
                    "Today, " + chipDateFormat.format(calendar.getTime()) :
                    chipDateFormat.format(calendar.getTime());

            chip.setText(dateText);
            chip.setCheckable(true);
            chip.setClickable(true);

            // Store date with chip for reference
            final Calendar chipDate = (Calendar) calendar.clone();
            chip.setTag(chipDate);

            // Select first day (today) by default
            if (i == 0) {
                chip.setChecked(true);
            }

            chip.setOnClickListener(v -> {
                selectedDateTime = (Calendar) chip.getTag();
                loadTasksForSelectedDate();
            });

            chipGroup.addView(chip);

            // Move to next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        dateContainer.addView(chipGroup);
    }

    private void loadTasksForSelectedDate() {
        // Get start and end of selected day
        Calendar startOfDay = (Calendar) selectedDateTime.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = (Calendar) selectedDateTime.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);

        // Get tasks for the selected date range
        taskList = dbHelper.getTasksByDateRange(
                startOfDay.getTimeInMillis(),
                endOfDay.getTimeInMillis()
        );

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(getContext(), taskList);
            recyclerView.setAdapter(taskAdapter);
        } else {
            // Update existing adapter with new tasks
            taskAdapter.updateTasks(taskList);
        }
    }

    private void showAddTaskDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_task);
        dialog.setTitle("Add New Task");

        EditText titleEditText = dialog.findViewById(R.id.et_task_title);
        EditText descriptionEditText = dialog.findViewById(R.id.et_task_description);
        Button dateButton = dialog.findViewById(R.id.btn_select_date);
        Button timeButton = dialog.findViewById(R.id.btn_select_time);
        Button addButton = dialog.findViewById(R.id.btn_add_task);

        // Set current date and time as default
        dateButton.setText(DateTimeUtils.formatDate(selectedDateTime.getTimeInMillis()));
        timeButton.setText(DateTimeUtils.formatTime(selectedDateTime.getTimeInMillis()));

        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateButton.setText(DateTimeUtils.formatDate(selectedDateTime.getTimeInMillis()));
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        timeButton.setText(DateTimeUtils.formatTime(selectedDateTime.getTimeInMillis()));
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        addButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDatetime(selectedDateTime.getTimeInMillis());
            task.setStatus("pending");

            long id = dbHelper.addTask(task);
            if (id != -1) {
                task.setId(id);
                loadTasksForSelectedDate();
                dialog.dismiss();
                Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasksForSelectedDate();
    }
}