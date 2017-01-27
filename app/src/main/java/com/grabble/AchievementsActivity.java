package com.grabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.grabble.adapters.AchievementsAdapter;
import com.grabble.adapters.CustomListAdapter;
import com.grabble.customclasses.Achievement;
import com.grabble.customclasses.GameState;

import java.util.ArrayList;
import java.util.concurrent.Callable;


/**
 * Activity representing the achievements list
 */
public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // set the state object
        state = (GameState)getApplicationContext();

        // get the recycler view
        rv = (RecyclerView) findViewById(R.id.achievements_list);

        // set fixed size for improved performance
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        // set layout manager to make it look like a list view
        rv.setLayoutManager(llm);

        // set the adapter on the achievements list
        rv.setAdapter(new AchievementsAdapter(state.getAchievements()));

    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }

    // override when pressing the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
