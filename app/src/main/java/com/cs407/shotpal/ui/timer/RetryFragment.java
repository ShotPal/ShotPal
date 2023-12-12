package com.cs407.shotpal.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs407.shotpal.R;

public class RetryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private SharedViewModel sharedViewModel;

    public RetryFragment() {
        // Required empty public constructor
    }

    public static RetryFragment newInstance(String param1, String param2) {
        RetryFragment fragment = new RetryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // RetryFragment.java
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retry, container, false);

        Bundle args = getArguments();
        if (args != null) {
            long endTime = args.getLong("endTime", 0);

            // Set the end time in the TextView
            TextView endTimeTextView = view.findViewById(R.id.endTimeTextView);
            endTimeTextView.setText(formatTime(endTime));
        }

        Button retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_stop);
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        return view;
    }

    // Helper method to format time
    private String formatTime(long endTime) {
        long millisElapsed = endTime - getArguments().getLong("startTime", 0);
        int seconds = (int) (millisElapsed / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}