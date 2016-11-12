package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener{

    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn1 = (Button)findViewById(R.id.show_letters_gathered);
        btn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.show_letters_gathered:
                i = new Intent(getApplicationContext(), LetterListActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
