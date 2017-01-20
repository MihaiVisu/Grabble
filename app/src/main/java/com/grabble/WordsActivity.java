package com.grabble;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.grabble.customclasses.GameState;

import java.util.HashMap;

public class WordsActivity extends AppCompatActivity  implements View.OnClickListener {

    Button letterListButton, getSuggestionButton, createWordButton;

    EditText[] letterBoxes = new EditText[7];

    TableLayout tl;

    GameState state;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        state = (GameState) getApplicationContext();

        tl = (TableLayout) findViewById(R.id.words_table);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                String typedChar = source.toString();

                if (!Character.isLetter(typedChar.charAt(0)) && typedChar.length() != 0) {
                    toast.setText("You must type a letter!");
                    toast.show();
                    return "";
                }

                if(!state.getLettersGrabbed().containsKey(typedChar) && typedChar.length() != 0) {
                    toast.setText("Letter " + typedChar.charAt(0) + " is not collected!");
                    toast.show();
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
            letterBoxes[i].setFilters(new InputFilter[] {filter, new InputFilter.LengthFilter(1)});
            letterBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // if field is empty, do nothing
                    if (s.length() == 0) {
                        createWordButton.setEnabled(false);
                        return;
                    }

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

                // if word is in dictionary and not in list of words already collected
                if(state.getWordsList().contains(typedWord)) {
                    if (!state.getWordsCreated().containsKey(typedWord)) {
                        addNewWordToList(typedWord);
                    }
                    else {
                        toast.setText("Word already collected!");
                        toast.show();
                    }
                }
                else {
                    toast.setText("Invalid word!");
                    toast.show();
                }
                break;
            default:
                break;
        }
    }

    private void addNewWordToList(String typedWord) {
        state.addNewWord(typedWord);
        for(int idx = 0; idx < typedWord.length(); idx++) {
            state.removeLetter(typedWord.charAt(idx));
        }

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
        ));
        TextView word = new TextView(this), score = new TextView(this);

        int textViewPadding = state.changePixelToDp(3);

        word.setText(typedWord);
        word.setLayoutParams(new TableRow.LayoutParams(1));
        word.setPadding(textViewPadding, textViewPadding,
                textViewPadding, textViewPadding);

        String scoreString = "Score: " + state.getWordsCreated()
                .get(typedWord).toString();
        score.setText(scoreString);
        score.setPadding(textViewPadding, 0, 0, 0);

        // add text views created dynamically to the new table row
        tr.addView(word);
        tr.addView(score);
        tl.addView(tr);
    }
}
