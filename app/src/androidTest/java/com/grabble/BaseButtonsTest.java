package com.grabble;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseButtonsTest {

    private String username, displayedName;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>
            (MainActivity.class);

    @Before
    public void getUsername() {
        EditText te = (EditText) mActivityRule.getActivity().findViewById(R.id.editText);
        username = te.getText().toString();

        displayedName = "Hello";
    }

    @Test
    public void goToMapActivity() {
        onView(withId(R.id.button1))
                .perform(click());
        onView(allOf(withId(R.id.header_user_name), isDisplayed()));
    }

    @Test
    public void goToInstructionsActivity() {
        onView(withId(R.id.button2))
                .perform(click());
        onView(allOf(withId(R.id.textView2), isDisplayed()));
    }

    @Test
    public void checkNavigability() {

        onView(withId(R.id.button1))
                .perform(click());
        onView(allOf(withId(R.id.action_settings), isDisplayed()))
                .perform(click());
        onView(allOf(withText("Enable Night Mode"), isDisplayed()));
        pressBack();
        onView(allOf(withId(R.id.header_user_name), isDisplayed()));

    }



}
