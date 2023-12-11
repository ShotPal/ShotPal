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

    private Handler timerHandler = new Handler();
    private Runnable updateTimerThread;
    private long startTime;
    private TextView timerTextView;

    public StopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stop, container, false);

        timerTextView = view.findViewById(R.id.timeCountingTextView); // This is the TextView to update with the timer
        Button stopButton = view.findViewById(R.id.stopButton);

        // Start the timer when the Fragment view is created
        startTime = SystemClock.elapsedRealtime();
        startTimer();

        stopButton.setOnClickListener(v -> {
            stopTimer();
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_retry);
        });
        return view;
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
        // Reset the TextView when stopped if necessary
        timerTextView.setText("00:00");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }
}