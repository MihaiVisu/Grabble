package com.grabble;

import android.support.v7.app.AppCompatActivity;

import com.grabble.customclasses.GameState;

public class CustomAppCompatActivity extends AppCompatActivity {

    @Override
    public void onStop() {
        ((GameState) getApplicationContext()).activityStopped();
        super.onStop();

    }
}
