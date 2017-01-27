package com.grabble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.grabble.customclasses.GameState;

/**
 * Activity of profile page
 */
public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener{

    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // set the state object
        state = (GameState) getApplicationContext();

        // initialize buttons and set listeners
        Button btn1 = (Button) findViewById(R.id.show_letters_gathered);
        btn1.setOnClickListener(this);

        Button btn2 = (Button) findViewById(R.id.show_my_achievements);
        btn2.setOnClickListener(this);

        Button btn3 = (Button) findViewById(R.id.show_words);
        btn3.setOnClickListener(this);

        Button btn4 = (Button) findViewById(R.id.buy_boosters);
        btn4.setOnClickListener(this);

        // get facebook profile picture if logged in with facebook
        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.profile_image);
            profilePictureView.setProfileId(profile.getId());
        }

        // set text views with user details
        TextView profileUserName = (TextView) findViewById(R.id.profile_user_name);
        profileUserName.setText(state.getUsername());

        TextView profileTokens = (TextView) findViewById(R.id.profile_tokens);
        profileTokens.setText(String.valueOf(state.getTokens()));

        TextView profileGems = (TextView) findViewById(R.id.profile_gems);
        profileGems.setText(String.valueOf(state.getGems()));

        TextView profileLosBoosters = (TextView) findViewById(R.id.profile_los_boosters);
        profileLosBoosters.setText(String.valueOf(state.getLosBoosters()));

        TextView  profileWordHelpers = (TextView) findViewById(R.id.profile_word_helpers);
        profileWordHelpers.setText(String.valueOf(state.getWordHelpers()));

        TextView profileDistance = (TextView) findViewById(R.id.profile_distance);
        profileDistance.setText(String.valueOf(state.getDistanceTraveled() + " meters"));

        TextView profileScore = (TextView) findViewById(R.id.profile_score);
        profileScore.setText(String.valueOf(state.getTotalScore()));
    }

    // set listener for buttons
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            // if letter list activity
            case R.id.show_letters_gathered:
                i = new Intent(getApplicationContext(), LetterListActivity.class);
                startActivity(i);
                break;
            // if achievements activity
            case R.id.show_my_achievements:
                i = new Intent(getApplicationContext(), AchievementsActivity.class);
                startActivity(i);
                break;
            // if words activity
            case R.id.show_words:
                i = new Intent(getApplicationContext(), WordsActivity.class);
                startActivity(i);
                break;
            // if shop activity
            case R.id.buy_boosters:
                i = new Intent(getApplicationContext(), ShopActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }

    // override when pressing back on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

}
