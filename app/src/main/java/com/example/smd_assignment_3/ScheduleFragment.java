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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;
    private Calendar selectedDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        dbHelper = new DatabaseHelper(getContext());
        selectedDateTime = Calendar.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTasks();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void loadTasks() {
        taskList = dbHelper.getFutureTasks();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);
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
                loadTasks();
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
        loadTasks();
    }
}