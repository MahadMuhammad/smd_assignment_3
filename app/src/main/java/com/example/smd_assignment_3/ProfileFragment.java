package com.example.smd_assignment_3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    private EditText nameEditText;
    private EditText emailEditText;
    private Switch themeSwitch;
    private Button updateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameEditText = view.findViewById(R.id.et_profile_name);
        emailEditText = view.findViewById(R.id.et_profile_email);
        themeSwitch = view.findViewById(R.id.switch_theme);
        updateButton = view.findViewById(R.id.btn_update_profile);

        // Load saved user data
        nameEditText.setText(ThemeUtils.getUserName(requireContext()));
        emailEditText.setText(ThemeUtils.getUserEmail(requireContext()));
        themeSwitch.setChecked(ThemeUtils.isDarkMode(requireContext()));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.setDarkMode(requireContext(), isChecked);
        });

        updateButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ThemeUtils.saveUserProfile(requireContext(), name, email);
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}