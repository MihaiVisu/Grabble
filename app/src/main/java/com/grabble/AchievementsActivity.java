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

import java.util.ArrayList;


public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<Achievement> achievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        rv = (RecyclerView) findViewById(R.id.achievements_list);

        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        initializeAchievementsData();

        rv.setAdapter(new AchievementsAdapter(achievements));

    }

    private void initializeAchievementsData() {
        achievements = new ArrayList<>();

        achievements.add(new Achievement("Travel 500m", 100, 2, R.drawable.road));
        achievements.add(new Achievement("Score 500 points", 100, 2, R.drawable.medal));
        achievements.add(new Achievement("Score 1000 points", 100, 2, R.drawable.trophy));
        achievements.add(new Achievement("Create 5 words", 100, 2, R.drawable.book));
        achievements.add(new Achievement("Collect 50 letters", 100, 2, R.drawable.letters));
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
