package com.grabble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.grabble.adapters.CustomListAdapter;

import com.amulyakhare.textdrawable.TextDrawable;
import com.grabble.customclasses.GameState;

import java.util.HashMap;

public class LetterListActivity extends AppCompatActivity {

    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_list);

        state = (GameState)getApplicationContext();

        ListView lst = (ListView)findViewById(R.id.letter_list);

        int counter = 0;

        HashMap<String, Integer> lettersGrabbed = state.getLettersGrabbed();

        String[] letters = new String[lettersGrabbed.size()];
        String[] scores = new String[lettersGrabbed.size()];
        int[] left = new int[lettersGrabbed.size()];

        for (char letter = 'a'; letter <= 'z'; letter++) {
            String let = String.valueOf(letter);
            if (state.getLettersGrabbed().containsKey(let)) {
                letters[counter] = let;
                scores[counter] = "Score: " + state.getLetterScore(let);
                left[counter++] = lettersGrabbed.get(let);
            }
        }

        String[] leftTags = new String[left.length];
        for (int i = 0; i < left.length; i++) {
            leftTags[i] = "x" + left[i] + " left";
        }
        ColorGenerator cgen = ColorGenerator.MATERIAL;
        TextDrawable[] drawables = new TextDrawable[letters.length];
        for (int i = 0; i < letters.length; i++ ) {
            drawables[i] = TextDrawable.builder().buildRound(letters[i], cgen.getRandomColor());
        }

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.activity_letter_list, scores, leftTags, drawables);

        lst.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }
}
