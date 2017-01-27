package com.grabble.customclasses;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.widget.Button;

import com.grabble.Fragments.GmapFragment;

import mbanje.kurt.fabbutton.FabButton;

/**
 * progress helper class to measure and keep track
 * of the progress bar around double line of sight booster,
 * which checks its usage
 */
public class ProgressHelper {

    private Handler handle;
    private FabButton button;
    private Activity activity;
    private final GameState state;

    // constructor for progress helper
    public ProgressHelper(FabButton button, Activity activity) {
        this.button = button;
        this.activity = activity;
        handle = new Handler();
        state = (GameState)activity.getApplicationContext();
    }

    private Runnable getRunnable(final Activity activity){
        return new Runnable() {
            @Override
            public void run() {
                // increment progress while running
                state.incrementLosProgress();
                // run the function on the UI thread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // set the progress of the button
                        button.setProgress(state.getLosProgress());
                        // if it is still completing
                        if(state.getLosProgress() <= 100){
                            // set time for the progress bar
                            // in our case just 10 seconds for testing purposes
                            // it can be set higher
                            handle.postDelayed(getRunnable(activity),50);
                        }
                        else {
                            // else if completed already
                            // reset the progress and the line of sight circles and colors
                            state.setLosProgress(0);
                            button.setProgress(state.getLosProgress());
                            GmapFragment.multiplyLineOfSightDistance(0.5);
                            // reset the line of sight radius
                            GmapFragment.setLineOfSightRadiusAndColor(
                                    GmapFragment.getLineOfSightDistance(),
                                    Color.parseColor("#70303F9F"));
                            GmapFragment.hideAllMarkers();
                            GmapFragment.updateMarkers(GmapFragment.getLocation(), state);
                        }
                    }
                });
            }
        };
    }

    // start the progress helper method
    public void startDeterminate() {
        state.setLosProgress(state.getLosProgress());
        button.showProgress(true);
        button.setProgress(state.getLosProgress());
        getRunnable(activity).run();
    }

}
