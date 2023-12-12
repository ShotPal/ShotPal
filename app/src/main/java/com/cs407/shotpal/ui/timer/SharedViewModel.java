package com.cs407.shotpal.ui.timer;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private int shotCount = 0;

    public int getShotCount() {
        return shotCount;
    }

    public void resetShotCount() {
        shotCount = 0;
    }

    public void incrementShotCount() {
        shotCount++;
    }
}
