package com.grabble;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.games.Games;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.grabble.Fragments.GmapFragment;
import com.grabble.customclasses.GameState;


/**
 * Navigation activity which contains a navigation drawer
 * and the map fragment inside it
 */
public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the state object
        state = (GameState) getApplicationContext();

        // get action toolbar
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get the drawer layout object
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // get navigation view to update the header of the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        state.setHeaderContent((RelativeLayout) headerView.findViewById(R.id.header_content));
        // update content of the state
        updateContent(state);

        // update profile picture if logged in with facebook
        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.imageView);
            profilePictureView.setProfileId(profile.getId());
        }
        // initialize the map fragment
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new GmapFragment()).commit();

    }

    // static method to update the header content on the head drawer
    public static void updateContent(GameState state) {
        // update username
        TextView headerUsername = (TextView) state.getHeaderContent()
                .findViewById(R.id.header_user_name);
        // update tokens and gems
        TextView tokensAndGems = (TextView) state.getHeaderContent()
                .findViewById(R.id.tokens_and_gems);
        headerUsername.setText("Hello, " + state.getUsername() + "!");
        // initialize a spannable string builder in order to put
        // icons of gems and coins within text
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String message = "x" + state.getTokens() + "  ";
        builder.append(message);
        // set the coins icon and content
        Drawable coins = state.getDrawable(R.drawable.coins);
        coins.setBounds(0,0,50,50);
        builder.setSpan(new ImageSpan(coins), builder.length()-1, builder.length(), 0);
        message = " x" + state.getGems() + "  ";
        builder.append(message);
        // set the gems icon and content
        Drawable gems = state.getDrawable(R.drawable.gem);
        gems.setBounds(0,0,50,50);
        builder.setSpan(new ImageSpan(gems), builder.length()-1, builder.length(), 0);
        tokensAndGems.setText(builder);


    }

    // override when pressing back button
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        state.activityStopped();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    // listeners on drawer menu item selected
    @SuppressWarnings("StatementWithEmptyBody") // suppress some annoying warnings
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent i;

        // if map item do nothing
        if (id == R.id.nav_map) {
        } else if (id == R.id.nav_profile) { // if profile item
            i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_achievements) { // if achievements item
            i = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(i);
        } else if (id == R.id.letters_list) { // if letter list item
            i = new Intent(getApplicationContext(), LetterListActivity.class);
            startActivity(i);
        } else if (id == R.id.words_list) { // if words list item
            i = new Intent(getApplicationContext(), WordsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_shop) { // if shop item
            i = new Intent(getApplicationContext(), ShopActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) { // if share item
            i = new Intent(); // create an intent to share score via installed apps
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "I have a total score of " + state.getTotalScore() +
                    " on Grabble.");
            i.setType("text/plain");
            startActivity(Intent.createChooser(i, "Send To"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
