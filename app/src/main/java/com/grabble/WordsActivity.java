package com.grabble;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.MenuItem;
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

import java.util.Arrays;
import java.util.HashMap;

public class WordsActivity extends AppCompatActivity  implements View.OnClickListener {

    Button letterListButton, getSuggestionButton, createWordButton;

    EditText[] letterBoxes = new EditText[7];

    TableLayout tl;

    GameState state;

    Toast toast;

    Snackbar snackbar;

    private int[] typedLetters = new int[26];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        // set the state object
        state = (GameState) getApplicationContext();

        // set the table layout
        tl = (TableLayout) findViewById(R.id.words_table);

        // set the toast for messages
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        // set the snackbar for messages
        snackbar = Snackbar.make(findViewById(R.id.activity_words), "",
                Snackbar.LENGTH_SHORT);



        // set an input filter for edit text inputs
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                // get the typed characters in a certain edit text
                String typedChar = source.toString().toLowerCase();

                // if not letter then go out
                if (!Character.isLetter(typedChar.charAt(0)) && typedChar.length() != 0) {
                    toast.setText("You must type a letter!");
                    toast.show();
                    return "";
                }

                // if we don't have that letter at all go out
                if(!state.getLettersGrabbed().containsKey(typedChar) && typedChar.length() != 0) {
                    toast.setText("Letter " + typedChar.charAt(0) + " is not collected!");
                    toast.show();
                    return "";
                }

                // check letter frequency
                updateletterFrequency();
                if (typedChar.length() > 0) {
                    // if no letters of that type left go out
                    if (typedLetters[typedChar.charAt(0) - 'a']+1 > state.getLettersGrabbed()
                            .get(typedChar)) {
                        toast.setText("You don't have any " + typedChar.toUpperCase() + " left!");
                        toast.show();
                        return "";
                    }
                }

                return null;
            }
        };

        // add already created words to the table
        for (String word : state.getWordsCreated().keySet()) {
            addNewWordToList(word);
        }

        System.out.println(state.getWordsCreated().size());

        // initialize buttons and set listeners
        letterListButton = (Button) findViewById(R.id.button3);
        letterListButton.setOnClickListener(this);

        getSuggestionButton = (Button) findViewById(R.id.get_suggestion_button);
        getSuggestionButton.setOnClickListener(this);

        createWordButton = (Button) findViewById(R.id.create_word_button);
        createWordButton.setOnClickListener(this);

        LinearLayout topLayout = (LinearLayout) findViewById(R.id.activity_words);
        LinearLayout letterBoxesLayout = (LinearLayout) topLayout.getChildAt(0);

        configureTextFilters(letterBoxesLayout, filter);

        // if all edit text inputs have letters in them
        if(isWordFormFull()) {
            // then enable the create word button
            createWordButton.setEnabled(true);
        }

    }

    // function to update typed letters frequency based on what we have typed in
    private void updateletterFrequency() {
        Arrays.fill(typedLetters, 0);
        for (EditText letterBox : letterBoxes) {
            if (letterBox.getText().length() > 0) {
                typedLetters[letterBox.getText().charAt(0) - 'a']++;
            }
        }
    }

    // configure text filters for the edit text forms
    private void configureTextFilters(LinearLayout letterBoxesLayout, InputFilter filter) {
        for (int i = 0; i < letterBoxesLayout.getChildCount(); i++) {
            letterBoxes[i] = (EditText) letterBoxesLayout.getChildAt(i);
            // set the filter
            letterBoxes[i].setFilters(new InputFilter[] {filter, new InputFilter.LengthFilter(1)});
            // add the text changed listeners after applying the filter
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
    }

    // check if we have completed the word form with all 7 edit texts
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

    // get typed word out of the letter boxes
    private String getTypedWord() {
        String word = "";
        for(EditText letterBox : letterBoxes) {
            word += letterBox.getText().toString().charAt(0);
        }
        return word;
    }

    // initialize the click listeners for the buttons
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
                // if we have any helpers left
                if (state.getWordHelpers() > 0) {
                    String wordSuggestion = getSuggestion();
                    if (wordSuggestion != null) {
                        createSuggestionAllert(wordSuggestion);
                    }
                    else {
                        toast.setText("No possible words at the moment." +
                                "Get more letters!");
                    }
                    state.useWordHelper();
                }
                else {
                    toast.setText("No word helpers left!");
                    toast.show();
                }
                break;
            // action when user presses the create word button
            case R.id.create_word_button:
                createWord();
                state.checkMilestones(snackbar);
                break;
            default:
                break;
        }
    }

    // create the alert showing the suggestion
    private void createSuggestionAllert(String suggestion) {
        new AlertDialog.Builder(this)
                .setTitle("The Best Match Is:")
                .setMessage("### " + suggestion + " ###")
                .setIcon(R.drawable.grabble_logo_main).show();
    }

    // method that triggers the action of giving a word suggestion
    private String getSuggestion() {
        HashMap<String, Integer> lettersGrabbed = state.getLettersGrabbed();
        for (Pair<String, Integer> word : state.getSortedWordsList()) {
            int[] freqs = new int[26];
            boolean wordFound = true;
            for (char c : word.first.toLowerCase().toCharArray()) {
                int index = (int)c-'a';
                freqs[index]++;
                if (!lettersGrabbed.containsKey(String.valueOf(c))) {
                    wordFound = false;
                }
                else if (freqs[index] > lettersGrabbed.get(String.valueOf(c))) {
                    wordFound = false;
                }
            }
            if (wordFound && !state.getWordsCreated().containsKey(word.first)) {
                return word.first;
            }
        }
        return null;
    }

    // method that triggers the action when create a word button is pressed
    private void createWord() {
        String typedWord = getTypedWord();
        // if word is in dictionary and not in list of words already collected
        if(state.getWordsList().contains(typedWord)) {
            if (!state.getWordsCreated().containsKey(typedWord)) {
                addNewWordToList(typedWord);
                NavActivity.updateContent(state);
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
    }

    // add new word to list of words created in state
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

    // override when pressing back button in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
