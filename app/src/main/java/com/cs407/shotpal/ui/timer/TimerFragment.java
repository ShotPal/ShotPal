package com.cs407.shotpal.ui.timer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs407.shotpal.MainActivity;
import com.cs407.shotpal.R;
import com.cs407.shotpal.databinding.FragmentTimerBinding;
import com.cs407.shotpal.ui.settings.SettingsFragment;

public class TimerFragment extends Fragment implements MainActivity.PermissionListener {


    private FragmentTimerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);

        Button startButton = binding.startButton;
        startButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", 0);
            String trackOpt = sharedPreferences.getString("trackOpt", SettingsFragment.DEFAULT_TRACK_OPT);
            if (trackOpt.equals("mic") || trackOpt.equals("dual")) {
                ((MainActivity) requireActivity()).requestMicrophonePermission(this);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onPermissionGranted() {
        goToStopFragment();
    }

    @Override
    public void onPermissionDenied() {
        // Set trackOpt to imu
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("trackOpt", "imu");
        editor.apply();

        goToStopFragment();
    }

    public void goToStopFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_stop);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}