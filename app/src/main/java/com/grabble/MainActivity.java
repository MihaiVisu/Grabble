package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btn1, btn2;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(this);

        btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(this);

        // add change listener to the editText view
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && !btn1.isEnabled()) {
                    btn1.setEnabled(true);
                }
                else if (s.length() == 0 && btn1.isEnabled()) {
                    btn1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()) {
            case R.id.button1:
                i = new Intent(getApplicationContext(), NavActivity.class);
                startActivity(i);
                break;
            case R.id.button2:
                i = new Intent(getApplicationContext(), InstructionsActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
