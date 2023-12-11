package com.cs407.shotpal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.cs407.shotpal.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MediaRecorder mediaRecorder;
    private Handler handler;
    private boolean isRecording = false;
    private boolean isShotFired = false;
    private long startTime = 0L;
    private long lastShotTime = 0L;
    private int shotCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_timer, R.id.navigation_profile, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Prompt the user to grant permission to record audio
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);
        }

        handler = new Handler();

        // Start recording when the activity starts
        startRecording();

        // Find the button and set a click listener
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this::onStartButtonClick);
    }

    public void onStartButtonClick(View view) {
        // This method will be called when the start button is clicked
        startTimer();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_stop);
    }

    public void onStopButtonClick(View view) {
        // This method will be called when the stop button is clicked
        stopTimer();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_retry);
    }

    private Runnable soundLevelChecker = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                int amplitude = mediaRecorder.getMaxAmplitude();
                double soundLevel = 20 * Math.log10(amplitude / 32767.0);

                Log.d("SoundLevel", "Current sound level: " + soundLevel);

                // Adjust this threshold based on your testing and requirements
                double gunshotThreshold = 0;

                if (soundLevel == gunshotThreshold) {
                    Log.d("GunshotDetection", "Gunshot detected! Sound level: " + soundLevel);
                    handleShotFired();
                }


                // Continue checking sound levels
                handler.postDelayed(this, 1000);
            }
        }
    };

    public static class UIHelper {

        public static void updateUI(MainActivity mainActivity, long shotTime, long splitTime, long lastShotTime, int shotCount) {
            mainActivity.updateUI(shotTime, splitTime, lastShotTime, shotCount);
        }
    }

    private void handleLowSound() {
        // Increment the shot count
        shotCount++;

        // Update UI in real-time
        updateUI(0, 0, 0, shotCount);

        // Store shot data (e.g., in shared preferences or database)
        saveShotData(0, 0, 0, shotCount);
    }


    private void handleShotFired() {
        if (!isShotFired) {
            isShotFired = true;
            startTime = System.currentTimeMillis();
        } else {
            // Calculate shot time and split time
            long shotTime = System.currentTimeMillis() - startTime;
            long splitTime = startTime - lastShotTime;

            // Update shot count and last shot time
            shotCount++;
            lastShotTime = startTime;

            // Store shot data (e.g., in shared preferences or database)
            saveShotData(shotTime, splitTime, lastShotTime, shotCount);

            // Update UI in real-time
            updateUI(shotTime, splitTime, lastShotTime, shotCount);

            // Reset shot-fired flag
            isShotFired = false;
        }
    }

    private void saveShotData(long shotTime, long splitTime, long lastShotTime, int shotCount) {
        // Use shared preferences to store shot data
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("last_shot_time", lastShotTime);
        editor.putInt("shot_count", shotCount);

        // Assuming you want to keep a record of all shots, you may use a unique key for each shot
        String shotKey = "shot_" + shotCount;
        editor.putLong(shotKey + "_time", shotTime);
        editor.putLong(shotKey + "_split_time", splitTime);

        // Commit the changes
        editor.apply();
    }

    private void updateUI(long shotTime, long splitTime, long lastShotTime, int shotCount) {
        // Assuming you have TextViews in your fragment_stop.xml for displaying shot details
        TextView timeCountingTextView = findViewById(R.id.timeCountingTextView);
        TextView recentShotTimeTextView = findViewById(R.id.recentShotTimeTextView);
        TextView splitTimeTextView = findViewById(R.id.splitTimeTextView);
        TextView shotCountTextView = findViewById(R.id.shotCountTextView);

        // Update the UI with shot details
        timeCountingTextView.setText(formatTime(shotTime));
        recentShotTimeTextView.setText(formatTime(lastShotTime));
        splitTimeTextView.setText(formatTime(splitTime));
        shotCountTextView.setText(String.valueOf(shotCount));
    }

    private String formatTime(long timeInMillis) {
        // Format time as minutes and seconds
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void startTimer() {
        // Your existing code for starting the timer
    }

    private void stopTimer() {
        // Your existing code for stopping the timer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }


    private void startRecording() {
        if (!isRecording) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // Specify a valid path for the output file in your app's internal storage directory
            mediaRecorder.setOutputFile(getFilesDir().getAbsolutePath() + "/audio.3gp");


            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;

                // Schedule a task to check sound levels every second
                handler.postDelayed(soundLevelChecker, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Recording", "Error starting recording: " + e.getMessage());
            }
        }
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_save) {
            // Handle save action
            return true;
        } else if (itemId == R.id.action_share) {
            // Handle share action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Check the current destination and perform custom navigation if necessary
        if (navController.getCurrentDestination() != null) {
            int id = navController.getCurrentDestination().getId();
            if (id == R.id.navigation_retry || id == R.id.navigation_stop) {
                // Navigate to the main screen
                navController.navigate(R.id.navigation_timer); // Use the actual ID of your main screen
                return true;
            }
        }

        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}