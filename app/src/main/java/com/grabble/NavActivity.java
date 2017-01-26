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

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GameState state;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        state = (GameState) getApplicationContext();

        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        state.setHeaderContent((RelativeLayout) headerView.findViewById(R.id.header_content));

        updateContent(state);

        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.imageView);
            profilePictureView.setProfileId(profile.getId());
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new GmapFragment()).commit();

    }

    // static method to update the header content on the head drawer
    public static void updateContent(GameState state) {
        TextView headerUsername = (TextView) state.getHeaderContent()
                .findViewById(R.id.header_user_name);
        TextView tokensAndGems = (TextView) state.getHeaderContent()
                .findViewById(R.id.tokens_and_gems);
        headerUsername.setText("Hello, " + state.getUsername() + "!");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String message = "x" + state.getTokens() + "  ";
        builder.append(message);
        Drawable coins = state.getDrawable(R.drawable.coins);
        coins.setBounds(0,0,50,50);
        builder.setSpan(new ImageSpan(coins), builder.length()-1, builder.length(), 0);
        message = " x" + state.getGems() + "  ";
        builder.append(message);
        Drawable gems = state.getDrawable(R.drawable.gem);
        gems.setBounds(0,0,50,50);
        builder.setSpan(new ImageSpan(gems), builder.length()-1, builder.length(), 0);
        tokensAndGems.setText(builder);


    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        MenuItem item = menu.findItem(R.id.nav_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent i;

        if (id == R.id.nav_map) {
        } else if (id == R.id.nav_profile) {
            i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_achievements) {
            i = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(i);
        } else if (id == R.id.letters_list) {
            i = new Intent(getApplicationContext(), LetterListActivity.class);
            startActivity(i);
        } else if (id == R.id.words_list) {
            i = new Intent(getApplicationContext(), WordsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_shop) {
            i = new Intent(getApplicationContext(), ShopActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, "I have a total score of " + state.getTotalScore() +
                    "on Grabble.");
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
