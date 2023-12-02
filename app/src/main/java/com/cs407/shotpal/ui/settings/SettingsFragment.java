package com.cs407.shotpal.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cs407.shotpal.R;
import com.cs407.shotpal.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private String sensLevel = "low";

    private String trackingOption = "mic";
    private String signalOption = "sound";

    ToggleButton lowButton, midButton, highButton;
    ToggleButton micButton, imuButton, dualTrackingButton;
    ToggleButton soundButton, vibrationButton, dualSignalButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lowButton = view.findViewById(R.id.sensLowButton);
        midButton = view.findViewById(R.id.sensMidButton);
        highButton = view.findViewById(R.id.sensHighButton);

        micButton = view.findViewById(R.id.micButton);
        imuButton = view.findViewById(R.id.imuButton);
        dualTrackingButton = view.findViewById(R.id.dualTrackingButton);

        soundButton = view.findViewById(R.id.soundButton);
        vibrationButton = view.findViewById(R.id.vibrationButton);
        dualSignalButton = view.findViewById(R.id.dualSignalButton);

        updateButtonSelection();

        lowButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sensLevel = "low";
                updateButtonSelection();
        });
        midButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sensLevel = "mid";
                updateButtonSelection();
        });

        highButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sensLevel = "high";
                updateButtonSelection();
        });

        micButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                trackingOption = "mic";
                updateButtonSelection();
        });

        imuButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                trackingOption = "imu";
                updateButtonSelection();
        });

        dualTrackingButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                trackingOption = "dual";
                updateButtonSelection();
        });

        soundButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                signalOption = "sound";
                updateButtonSelection();
        });
        soundButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                signalOption = "vibration";
                updateButtonSelection();
        });
        dualTrackingButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                signalOption = "dual";
                updateButtonSelection();
        });
    }

    private void updateButtonSelection() {
        if (sensLevel.equals("low")) {
            lowButton.setChecked(true);
            midButton.setChecked(false);
            highButton.setChecked(false);
        } else if (sensLevel.equals("mid")) {
            lowButton.setChecked(false);
            midButton.setChecked(true);
            highButton.setChecked(false);
        } else {
            lowButton.setChecked(false);
            midButton.setChecked(false);
            highButton.setChecked(true);
        }

        if (trackingOption.equals("mic")) {
            micButton.setChecked(true);
            imuButton.setChecked(false);
            dualTrackingButton.setChecked(false);
        } else if (sensLevel.equals("imu")) {
            micButton.setChecked(false);
            imuButton.setChecked(true);
            dualTrackingButton.setChecked(false);
        } else {
            micButton.setChecked(false);
            imuButton.setChecked(false);
            dualTrackingButton.setChecked(true);
        }

        if (signalOption.equals("sound")) {
            soundButton.setChecked(true);
            vibrationButton.setChecked(false);
            dualSignalButton.setChecked(false);
        } else if (sensLevel.equals("vibration")) {
            soundButton.setChecked(false);
            vibrationButton.setChecked(true);
            dualSignalButton.setChecked(false);
        } else {
            soundButton.setChecked(false);
            vibrationButton.setChecked(false);
            dualSignalButton.setChecked(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}