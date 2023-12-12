package com.cs407.shotpal.ui.timer;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs407.shotpal.MainActivity;
import com.cs407.shotpal.R;
import com.cs407.shotpal.databinding.FragmentTimerBinding;
import com.cs407.shotpal.ui.settings.SettingsFragment;

public class TimerFragment extends Fragment implements MainActivity.PermissionListener {

    public static final String LOWER_BOUND = "lowerBound";
    public static final String UPPER_BOUND = "upperBound";
    public static final int DEFAULT_LOWER_BOUND = 3;
    public static final int DEFAULT_UPPER_BOUND = 5;

    public static int lowerBoundInt = DEFAULT_LOWER_BOUND;
    public static int upperBoundInt = DEFAULT_UPPER_BOUND;

    private FragmentTimerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_stop, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", 0);

        EditText lowerBound = binding.lowerBound;
        EditText upperBound = binding.upperBound;

        lowerBound.setText(String.valueOf(sharedPreferences.getInt(LOWER_BOUND, DEFAULT_LOWER_BOUND)));
        upperBound.setText(String.valueOf(sharedPreferences.getInt(UPPER_BOUND, DEFAULT_UPPER_BOUND)));

        lowerBound.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(LOWER_BOUND, Integer.parseInt(lowerBound.getText().toString()));
                editor.apply();
                lowerBoundInt = Integer.parseInt(lowerBound.getText().toString());
                upperBoundInt = Integer.parseInt(upperBound.getText().toString());
                if (lowerBoundInt > upperBoundInt) {
                    upperBoundInt = lowerBoundInt;
                    upperBound.setText(String.valueOf(lowerBoundInt));
                }
            }
        });
        upperBound.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(UPPER_BOUND, Integer.parseInt(upperBound.getText().toString()));
                editor.apply();
                lowerBoundInt = Integer.parseInt(lowerBound.getText().toString());
                upperBoundInt = Integer.parseInt(upperBound.getText().toString());
                if (lowerBoundInt > upperBoundInt) {
                    lowerBoundInt = upperBoundInt;
                    lowerBound.setText(String.valueOf(upperBoundInt));
                }
            }
        });

        Button startButton = binding.startButton;
        startButton.setOnClickListener(v -> {
            String trackOpt = sharedPreferences.getString("trackOpt", SettingsFragment.DEFAULT_TRACK_OPT);
            if (trackOpt.equals("mic") || trackOpt.equals("dual")) {
                ((MainActivity) requireActivity()).requestMicrophonePermission(this);
            }
        });

        return binding.getRoot();
    }

    private void randomSignal(int lowerBound, int upperBound) {
        double delayTime = (int) ((Math.random() * (upperBound - lowerBound) + lowerBound) * 1000);
        Log.d("LowerBound", "lowerBound: " + lowerBound);
        Log.d("UpperBound", "upperBound: " + upperBound);
        Log.d("RandomSignal", "delayTime: " + delayTime);
        Toast.makeText(requireActivity(), "StandBy...", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        }, (long) delayTime);
    }

    @Override
    public void onPermissionGranted() {
        randomSignal(lowerBoundInt, upperBoundInt);
        goToStopFragment();
    }

    @Override
    public void onPermissionDenied() {
        // Set trackOpt to imu
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("trackOpt", "imu");
        editor.apply();

        randomSignal(lowerBoundInt, upperBoundInt);
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