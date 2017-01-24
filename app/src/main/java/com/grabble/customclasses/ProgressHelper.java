package com.grabble.customclasses;

import android.app.Activity;
import android.os.Handler;
import android.widget.Button;

import mbanje.kurt.fabbutton.FabButton;


public class ProgressHelper {

    private Handler handle;
    private FabButton button;
    private Activity activity;
    private final GameState state;

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
                state.incrementLosProgress();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setProgress(state.getLosProgress());
                        if(state.getLosProgress() <= 200){
                            handle.postDelayed(getRunnable(activity),50);
                        }
                        else {
                            state.setLosProgress(0);
                            button.setProgress(state.getLosProgress());
                        }
                    }
                });
            }
        };
    }

    public void startDeterminate() {
        state.setLosProgress(state.getLosProgress());
        button.showProgress(true);
        button.setProgress(state.getLosProgress());
        getRunnable(activity).run();
    }

}
