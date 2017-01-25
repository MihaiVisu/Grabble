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


public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        state = (GameState)getApplicationContext();

        rv = (RecyclerView) findViewById(R.id.achievements_list);

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        rv.setAdapter(new AchievementsAdapter(state.getAchievements()));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
