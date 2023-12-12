package com.cs407.shotpal.ui.timer;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs407.shotpal.MainActivity;
import com.cs407.shotpal.R;
import com.cs407.shotpal.shotClass;

public class StopFragment extends Fragment {

    private static final String KEY_SHOT_COUNT = "shotCount";

    private Handler timerHandler = new Handler();
    private Runnable updateTimerThread;
    private long startTime;
    private TextView timerTextView;
    private int shotCount;

    private boolean isRecording = false;

    final int SAMPLE_RATE = 8000;
    final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    AudioRecord mAudioRecord;
    Object mLock;

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

        startDetection();

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


    @SuppressLint("MissingPermission")
    public void startDetection() {
        if (isRecording) {
            Log.e("AudioRecord", "Recording is current in progress");
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        isRecording = true;
        mLock = new Object();
        new Thread(() -> {
            mAudioRecord.startRecording();
            short[] buffer = new short[BUFFER_SIZE];
            while (isRecording) {
                int reading = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                long volume = 0;

                for (short value : buffer) {
                    volume += value * value;
                }

                double mean = volume / (double) reading;
                double actualDecibel = Math.log10(mean) * 10;
//                    Log.d("AudioRecorder", "decibel: " + actualDecibel);
                if (actualDecibel >= 40) {
                    long shotTime = SystemClock.elapsedRealtime() - startTime;
                    if (((MainActivity) requireActivity()).shotList.size() == 0) {
                        Log.d("GunshotDetection", "Gunshot detected! Sound level: " + actualDecibel + "at:" + shotTime);
                        ((MainActivity) requireActivity()).shotList.add(new shotClass(shotTime, 0));
                    } else {
                        shotClass lastShot = ((MainActivity) requireActivity()).shotList.get(((MainActivity) requireActivity()).shotList.size() - 1);
                        if (shotTime - lastShot.getShotTime() > 500) {
                            long splitTime = shotTime - lastShot.getShotTime();
                            ((MainActivity) requireActivity()).shotList.add(new shotClass(shotTime, splitTime));
                            Log.d("GunshotDetection", "Gunshot detected! Sound level: " + actualDecibel + "at:" + shotTime);
                        }
                    }
                }

                synchronized (mLock) {
                    try {
                        mLock.wait(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of shotCount
        outState.putInt(KEY_SHOT_COUNT, shotCount);
    }

//    private void startTimer() {
//        updateTimerThread = new Runnable() {
//            public void run() {
//                long millisElapsed = SystemClock.elapsedRealtime() - startTime;
//                int seconds = (int) (millisElapsed / 1000);
//                int minutes = seconds / 60;
//                seconds = seconds % 60;
//                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
//                timerHandler.postDelayed(this, 1000);
//            }
//        };
//        timerHandler.postDelayed(updateTimerThread, 0);
//    }

    private void startTimer() {
        updateTimerThread = new Runnable() {
            public void run() {
                long millisElapsed = SystemClock.elapsedRealtime() - startTime;
                int seconds = (int) (millisElapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                int milliseconds = (int) (millisElapsed % 1000);

                timerTextView.setText(String.format("%02d:%02d.%03d", minutes, seconds, milliseconds));
                timerHandler.postDelayed(this, 10); // Update every 10 milliseconds for smoother display
            }
        };
        timerHandler.postDelayed(updateTimerThread, 0);
    }


    private void stopTimer() {
        timerHandler.removeCallbacks(updateTimerThread);
        timerTextView.setText("00:00.000");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }
}