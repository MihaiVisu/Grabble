package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.games.Game;
import com.grabble.customclasses.GameState;

public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener{

    private Button btn1;
    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        state = (GameState) getApplicationContext();

        btn1 = (Button)findViewById(R.id.show_letters_gathered);
        btn1.setOnClickListener(this);

        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.profile_image);
            profilePictureView.setProfileId(profile.getId());
        }

        TextView profileUserName = (TextView) findViewById(R.id.profile_user_name);
        profileUserName.setText(state.getUsername());

        TextView profileTokens = (TextView) findViewById(R.id.profile_tokens);
        profileTokens.setText(String.valueOf(state.getCash()));

        TextView profileGems = (TextView) findViewById(R.id.profile_gems);
        profileGems.setText(String.valueOf(state.getGems()));

        TextView profileLosBoosters = (TextView) findViewById(R.id.profile_los_boosters);
        profileLosBoosters.setText(String.valueOf(state.getLosBoosters()));

        TextView  profileWordHelpers = (TextView) findViewById(R.id.profile_word_helpers);
        profileWordHelpers.setText(String.valueOf(state.getWordHelpers()));

        TextView profileDistance = (TextView) findViewById(R.id.profile_distance);
        profileDistance.setText(String.valueOf(state.getDistanceTraveled()));

        TextView profileScore = (TextView) findViewById(R.id.profile_score);
        profileScore.setText(String.valueOf(state.getTotalScore()));
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
