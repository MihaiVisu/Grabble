package com.grabble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Tests that modify the shared preferences.
 */
@RunWith(AndroidJUnit4.class)
public class SharedPrefsTest {
    private static final String PREFS_NAME = "preferences";
    private static final String KEY_PREF = "KEY_PREF";
    private SharedPreferences sharedPreferences;

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Test
    public void put_and_get_preference() throws Exception {
        String string1 = "testInput";

        sharedPreferences.edit().putString(KEY_PREF, string1).apply();
        String string2 = sharedPreferences.getString(KEY_PREF, "");

        // Verify that the received data is correct.
        assertEquals(string1, string2);
    }

    @After
    public void after() {
        sharedPreferences.edit().putString(KEY_PREF, null).apply();
    }
}