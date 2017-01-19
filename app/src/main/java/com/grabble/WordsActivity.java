package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.grabble.customclasses.GameState;

import java.util.HashMap;

public class WordsActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button letterListButton, getSuggestionButton, createWordButton;
    EditText[] letterBoxes = new EditText[7];

    GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        state = (GameState) getApplicationContext();

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String typedChar = source.toString();

                if(!state.getLettersGrabbed().containsKey(typedChar)) {
                    Toast.makeText(getApplicationContext(), "Letter "+
                            typedChar.charAt(end-1)+" is not collected!",
                            Toast.LENGTH_SHORT).show();
                    return "";
                }

                return null;
            }
        };

        letterListButton = (Button) findViewById(R.id.button3);
        letterListButton.setOnClickListener(this);

        getSuggestionButton = (Button) findViewById(R.id.get_suggestion_button);
        getSuggestionButton.setOnClickListener(this);

        createWordButton = (Button) findViewById(R.id.create_word_button);
        createWordButton.setOnClickListener(this);

        LinearLayout topLayout = (LinearLayout) findViewById(R.id.activity_words);
        LinearLayout letterBoxesLayout = (LinearLayout) topLayout.getChildAt(0);

        for (int i = 0; i < letterBoxesLayout.getChildCount(); i++) {
            letterBoxes[i] = (EditText) letterBoxesLayout.getChildAt(i);
            letterBoxes[i].setFilters(new InputFilter[] {filter});
            letterBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // if it is not letter, then quit
                    if (!Character.isLetter(s.charAt(0))) {
                        return;
                    }

                    HashMap<String, Integer> lettersGrabbed = state.getLettersGrabbed();
                    String typedLetter = s.toString();
                    if (isWordFormFull() && lettersGrabbed.containsKey(typedLetter)) {
                        createWordButton.setEnabled(true);
                    }
                    else if (createWordButton.isEnabled()) {
                        createWordButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if(isWordFormFull()) {
            createWordButton.setEnabled(true);
        }

    }

    private boolean isWordFormFull() {
        if (letterBoxes.length == 0) {
            return false;
        }
        for(EditText letterBox : letterBoxes) {
            if (letterBox.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getTypedWord() {
        String word = "";
        for(EditText letterBox : letterBoxes) {
            word += letterBox.getText().toString().charAt(0);
        }
        return word;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
            // action when user presses the letter list button
            case R.id.button3:
                i = new Intent(getApplicationContext(), LetterListActivity.class);
                startActivity(i);
                break;
            case R.id.get_suggestion_button:
                //TODO: suggestion word code
                break;
            // action when user presses the create word button
            case R.id.create_word_button:
                String typedWord = getTypedWord();

                if(state.getWordsList().contains(typedWord)) {
                    state.addNewWord(typedWord);
                    for(int idx = 0; idx < typedWord.length(); idx++) {
                        state.removeLetter(typedWord.charAt(idx));
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid word!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
