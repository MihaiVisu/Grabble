package com.grabble;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import com.grabble.customclasses.GameState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LetterActivityTest {

    private GameState state;

    @Rule
    public ActivityTestRule<LetterListActivity> mActivityRule = new ActivityTestRule<>
            (LetterListActivity.class);


    @Before
    public void getState() {
        state = (GameState) mActivityRule.getActivity().getApplicationContext();
    }

    @Test
    public void isEmpty() {
        state.getLettersGrabbed().clear();
        ListView lv = (ListView) mActivityRule.getActivity().findViewById(R.id.letter_list);
        assertEquals(lv.getAdapter().getCount(), 0);
    }



}
