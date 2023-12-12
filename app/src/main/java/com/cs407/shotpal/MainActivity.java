package com.cs407.shotpal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cs407.shotpal.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MediaRecorder mediaRecorder;
    private Handler handler;
    private boolean isRecording = false;
    private boolean isShotFired = false;
    private long startTime = 0L;
    private long lastShotTime = 0L;
    private int shotCount = 0;

    public static ArrayList<shotClass> shotList = new ArrayList<shotClass>();

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

        handler = new Handler();
    }

    private PermissionListener permissionListener;

    public interface PermissionListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    public void requestMicrophonePermission(PermissionListener listener) {
        this.permissionListener = listener;
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionListener.onPermissionGranted();
            } else {
                permissionListener.onPermissionDenied();
            }
        }
    }

    public void randomSignal(int lowerBound, int upperBound) {
        double delayTime = (int) ((Math.random() * (upperBound - lowerBound) + lowerBound) * 1000);
        Log.d("LowerBound", "lowerBound: " + lowerBound);
        Log.d("UpperBound", "upperBound: " + upperBound);
        Log.d("RandomSignal", "delayTime: " + delayTime);
        Toast.makeText(this, "StandBy...", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_stop);
        }, (long) delayTime);
    }

    private Runnable soundLevelChecker = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                int amplitude = mediaRecorder.getMaxAmplitude();
                double soundLevel = 20 * Math.log10(amplitude / 32767.0);

                Log.d("SoundLevel", "Current sound level: " + soundLevel);

                // Adjust this threshold based on your testing and requirements
                double gunshotThreshold = -10.0;

                if (soundLevel >= gunshotThreshold) {
                    Log.d("GunshotDetection", "Gunshot detected! Sound level: " + soundLevel);
                    Log.d("GunshotDetection", "Amplitude: " + amplitude);
                    handleShotFired();
                }

                // Continue checking sound levels
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void handleShotFired() {
        // currentTimeMillis is now from the system boot time (elapsed realtime)
        long shotTime = SystemClock.elapsedRealtime() - startTime;
        // splitTime is the difference between this shot time and the last shot time
        long splitTime = shotTime - lastShotTime;

        // Update shot count and last shot time
        shotCount++;
        lastShotTime = shotTime;

        // Store shot data (e.g., in shared preferences or database)
        saveShotData(shotTime, splitTime, lastShotTime, shotCount);

        // Update UI in real-time
        updateUI(shotTime, splitTime, lastShotTime, shotCount);
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

    public void updateUI(long shotTime, long splitTime, long lastShotTime, int shotCount) {
        // Assuming you have TextViews in your fragment_stop.xml for displaying shot details
        TextView timeCountingTextView = findViewById(R.id.timeCountingTextView);
        TextView recentShotTimeTextView = findViewById(R.id.recentShotTimeTextView);
        TextView splitTimeTextView = findViewById(R.id.splitTimeTextView);
        TextView shotCountTextView = findViewById(R.id.shotCountTextView);

        // Update the UI with shot details
//        timeCountingTextView.setText(formatTime(shotTime));
        recentShotTimeTextView.setText(formatTime(shotTime));
        splitTimeTextView.setText(formatTime(splitTime));
        shotCountTextView.setText(String.valueOf(shotCount));
    }

    private String formatTime(long timeInMillis) {
        Log.d("Time", "Time in milliseconds: " + timeInMillis);
        // Format time as minutes and seconds
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
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

    public void startRecording() {
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