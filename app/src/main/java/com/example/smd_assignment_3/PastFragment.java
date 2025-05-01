package com.example.smd_assignment_3;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PastFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;
    private Calendar selectedDate;
    private Button datePickerButton;
    private Button clearFilterButton;
    private TextView currentFilterTextView;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past, container, false);

        dbHelper = new DatabaseHelper(getContext());
        selectedDate = null; // Initially no date selected (show all past tasks)
        dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

        recyclerView = view.findViewById(R.id.recycler_view_past_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentFilterTextView = view.findViewById(R.id.tv_current_filter);
        datePickerButton = view.findViewById(R.id.btn_date_picker);
        clearFilterButton = view.findViewById(R.id.btn_clear_filter);

        datePickerButton.setOnClickListener(v -> showDatePicker());
        clearFilterButton.setOnClickListener(v -> clearDateFilter());

        loadTasks();

        return view;
    }

    private void clearDateFilter() {
        selectedDate = null;
        currentFilterTextView.setText("Showing all past tasks");
        datePickerButton.setText("Select Date");
        loadTasks();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    if (selectedDate == null) {
                        selectedDate = Calendar.getInstance();
                    }
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String formattedDate = dateFormat.format(selectedDate.getTime());
                    datePickerButton.setText("Date: " + formattedDate);
                    currentFilterTextView.setText("Showing tasks from: " + formattedDate);
                    loadTasksByDate();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // Set maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadTasksByDate() {
        if (selectedDate == null) {
            loadTasks(); // No date selected, load all past tasks
            return;
        }

        // Get start and end of selected day
        Calendar startOfDay = (Calendar) selectedDate.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);

        Calendar endOfDay = (Calendar) selectedDate.clone();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);

        taskList = dbHelper.getTasksByDateRange(
                startOfDay.getTimeInMillis(),
                endOfDay.getTimeInMillis()
        );

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(getContext(), taskList);
            recyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateTasks(taskList);
        }
    }

    private void loadTasks() {
        taskList = dbHelper.getPastTasks();

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(getContext(), taskList);
            recyclerView.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateTasks(taskList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedDate != null) {
            loadTasksByDate();
        } else {
            loadTasks();
        }
    }
}