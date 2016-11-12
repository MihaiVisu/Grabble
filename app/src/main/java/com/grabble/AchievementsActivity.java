package com.grabble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.grabble.CustomClasses.CustomListAdapter;


public class AchievementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        ListView lst = (ListView)findViewById(R.id.achievement_list);
        String[] achievements = new String[10];
        for (int i = 1; i<= 10; i++) {
            achievements[i-1] = "Achievement"+i + " description";
        }

        String[] status = {
                "unlocked",
                "locked",
                "locked",
                "unlocked",
                "unlocked",
                "unlocked",
                "locked",
                "locked",
                "locked",
                "locked"
        };

        ColorGenerator cgen = ColorGenerator.MATERIAL;

        TextDrawable[] drawables = new TextDrawable[achievements.length];
        for (int i = 1; i <= 10; i++) {
            drawables[i-1] = TextDrawable.builder().buildRound(Integer.toString(i), cgen.getRandomColor());
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.activity_achievements, achievements, status, drawables);
        lst.setAdapter(adapter);
    }
}
