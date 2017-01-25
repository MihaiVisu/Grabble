package com.grabble;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.grabble.customclasses.GameState;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    private SwitchPreference nightMode, batterySavingMode;
    private EditTextPreference displayNameEdit;
    private GameState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // this code is deprecated
        // but not worth using general fragments as we have only one section
        addPreferencesFromResource(R.xml.pref_general);

        state = (GameState) getApplicationContext();

        // configure battery saving mode switch
        nightMode = (SwitchPreference) findPreference("battery_saving_mode");
        nightMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (state.getBatterySavingMode()) {
                    state.setBatterySavingMode(false);
                    return false;
                }
                state.setBatterySavingMode(true);
                return true;
            }
        });


    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}

