package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class WordsActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button letterListButton, getSuggestionButton, createWordButton;
    EditText[] letterBoxes = new EditText[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

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
            letterBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isWordFormFull()) {
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

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
            case R.id.button3:
                i = new Intent(getApplicationContext(), LetterListActivity.class);
                startActivity(i);
                break;
            case R.id.get_suggestion_button:
                //TODO: suggestion word code
                break;
            case R.id.create_word_button:
                //TODO: create new word code
                break;
            default:
                break;
        }
    }
}
