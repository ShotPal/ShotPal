package com.cs407.shotpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cs407.shotpal.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

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

        // Find the button and set a click listener
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            navController.popBackStack(R.id.navigation_timer, false);
            navController.navigate(R.id.navigation_stop); // Navigate to fragment_stop
        });

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

    @Override
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