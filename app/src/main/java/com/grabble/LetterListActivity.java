package com.grabble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.grabble.CustomClasses.CustomListAdapter;

import com.amulyakhare.textdrawable.TextDrawable;

public class LetterListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_list);

        ListView lst = (ListView)findViewById(R.id.letter_list);

        String[] letters = {"A", "B", "C", "E", "F", "H", "R", "T", "U", "V", "W"};
        int[] left = {4,1,3,5,1,2,3,2,1,1,1};
        String[] leftTags = new String[left.length];
        for (int i = 0; i < left.length; i++) {
            leftTags[i] = "x" + left[i] + " left";
        }
        ColorGenerator cgen = ColorGenerator.MATERIAL;
        TextDrawable[] drawables = new TextDrawable[letters.length];
        for (int i = 0; i < letters.length; i++ ) {
            drawables[i] = TextDrawable.builder().buildRound(letters[i], cgen.getRandomColor());
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.activity_letter_list, letters, leftTags, drawables);

        lst.setAdapter(adapter);

    }
}
