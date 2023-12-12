package com.cs407.shotpal.ui.timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs407.shotpal.R;

public class StopFragment extends Fragment {

    private static final String KEY_SHOT_COUNT = "shotCount";

    private Handler timerHandler = new Handler();
    private Runnable updateTimerThread;
    private long startTime;
    private TextView timerTextView;
    private int shotCount;

    public StopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stop, container, false);

        timerTextView = view.findViewById(R.id.timeCountingTextView);
        Button stopButton = view.findViewById(R.id.stopButton);

        startTime = SystemClock.elapsedRealtime();
        startTimer();

        stopButton.setOnClickListener(v -> {
            stopTimer();
            // Reset shotCount to 0 when moving to retry fragment
            shotCount = 0;
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_retry);
        });

        // Restore shotCount if it was saved
        if (savedInstanceState != null) {
            shotCount = savedInstanceState.getInt(KEY_SHOT_COUNT, 0);
        }

        return view;
    }

    public void startDetection() {
        startTimer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of shotCount
        outState.putInt(KEY_SHOT_COUNT, shotCount);
    }

    private void startTimer() {
        updateTimerThread = new Runnable() {
            public void run() {
                long millisElapsed = SystemClock.elapsedRealtime() - startTime;
                int seconds = (int) (millisElapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(updateTimerThread, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(updateTimerThread);
        timerTextView.setText("00:00");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }
}