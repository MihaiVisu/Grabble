package com.grabble;

import android.support.v7.app.AppCompatActivity;

public class CustomAppCompatActivity extends AppCompatActivity {

    @Override
    public void onStop() {
        ((GameState) getApplicationContext()).activityStopped();
        super.onStop();

    }
}
