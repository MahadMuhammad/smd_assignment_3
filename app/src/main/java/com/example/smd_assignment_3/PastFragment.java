package com.example.smd_assignment_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;
    private List<Task> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past, container, false);

        dbHelper = new DatabaseHelper(getContext());

        recyclerView = view.findViewById(R.id.recycler_view_past_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTasks();

        return view;
    }

    private void loadTasks() {
        taskList = dbHelper.getPastTasks();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }
}
