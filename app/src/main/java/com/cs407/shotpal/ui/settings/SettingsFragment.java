package com.cs407.shotpal.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs407.shotpal.MainActivity;
import com.cs407.shotpal.R;
import com.cs407.shotpal.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements MainActivity.PermissionListener {

    private FragmentSettingsBinding binding;

    private SharedPreferences sharedPreferences;
    String sensOpt, trackOpt, targetTrackOpt, sigOpt;

    public static final String DEFAULT_SENS_OPT = "medium";
    public static final String DEFAULT_TRACK_OPT = "dual";
    public static final String DEFAULT_SIG_OPT = "dual";

    ToggleButton sensLowButton, sensMidButton, sensHighButton;
    ToggleButton micButton, imuButton, dualTrackingButton;
    ToggleButton soundButton, vibrationButton, dualSignalButton;
    Button calibrationButton, resetButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sensLowButton = view.findViewById(R.id.sensLowButton);
        sensMidButton = view.findViewById(R.id.sensMidButton);
        sensHighButton = view.findViewById(R.id.sensHighButton);

        micButton = view.findViewById(R.id.micButton);
        imuButton = view.findViewById(R.id.imuButton);
        dualTrackingButton = view.findViewById(R.id.dualTrackingButton);

        soundButton = view.findViewById(R.id.soundButton);
        vibrationButton = view.findViewById(R.id.vibrationButton);
        dualSignalButton = view.findViewById(R.id.dualSignalButton);

        calibrationButton = view.findViewById(R.id.calibrationButton);
        resetButton = view.findViewById(R.id.resetButton);

        // Load settings from shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("settings", 0);
        sensOpt = sharedPreferences.getString("sensOpt", DEFAULT_SENS_OPT);
        trackOpt = sharedPreferences.getString("trackOpt", DEFAULT_TRACK_OPT);
        sigOpt = sharedPreferences.getString("signOpt", DEFAULT_SIG_OPT);
        Log.println(Log.INFO, "Settings", "Loaded settings: " + sensOpt + " " + trackOpt + " " + sigOpt);

        updateButton();

        sensLowButton.setOnClickListener(v -> {
            sensOpt = "low";
            updateButton();
        });
        sensMidButton.setOnClickListener(v -> {
            sensOpt = "medium";
            updateButton();
        });
        sensHighButton.setOnClickListener(v -> {
            sensOpt = "high";
            updateButton();
        });

        micButton.setOnClickListener(v -> {
            targetTrackOpt = "mic";
            ((MainActivity) requireActivity()).requestMicrophonePermission(this);
        });
        imuButton.setOnClickListener(v -> {
            trackOpt = "imu";
            updateButton();
        });
        dualTrackingButton.setOnClickListener(v -> {
            targetTrackOpt = "dual";
            ((MainActivity) requireActivity()).requestMicrophonePermission(this);
        });

        // TODO: Implement sound and vibration feedback
        soundButton.setOnClickListener(v -> {
            sigOpt = "sound";
            updateButton();
        });
        vibrationButton.setOnClickListener(v -> {
            sigOpt = "vibration";
            updateButton();
        });
        dualSignalButton.setOnClickListener(v -> {
            sigOpt = "dual";
            updateButton();
        });

        calibrationButton.setOnClickListener(v -> calibrationButton());
        resetButton.setOnClickListener(v -> resetButton());
    }

    private void updateButton() {
        sharedPreferences = requireActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sensOpt", sensOpt);
        editor.putString("trackOpt", trackOpt);
        editor.putString("signOpt", sigOpt);
        editor.apply();
        Log.println(Log.INFO, "Settings", "Saved settings: " + sensOpt + " " + trackOpt + " " + sigOpt);

        sensLowButton.setChecked(sensOpt.equals("low"));
        sensMidButton.setChecked(sensOpt.equals("medium"));
        sensHighButton.setChecked(sensOpt.equals("high"));

        micButton.setChecked(trackOpt.equals("mic"));
        imuButton.setChecked(trackOpt.equals("imu"));
        dualTrackingButton.setChecked(trackOpt.equals("dual"));

        soundButton.setChecked(sigOpt.equals("sound"));
        vibrationButton.setChecked(sigOpt.equals("vibration"));
        dualSignalButton.setChecked(sigOpt.equals("dual"));
    }

    private void resetButton() {
        sensOpt = DEFAULT_SENS_OPT;
        trackOpt = DEFAULT_TRACK_OPT;
        sigOpt = DEFAULT_SIG_OPT;
        updateButton();
        Toast.makeText(requireActivity(), "Default Settings Restored", Toast.LENGTH_SHORT).show();
    }

    private void calibrationButton() {
        // TODO: Implement IMU calibration
        Toast.makeText(requireActivity(), "IMU Calibration Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPermissionGranted() {
        trackOpt = targetTrackOpt;
        updateButton();
    }

    @Override
    public void onPermissionDenied() {
        trackOpt = "imu";
        updateButton();
    }
}