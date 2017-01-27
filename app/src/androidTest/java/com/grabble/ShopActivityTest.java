package com.grabble;

import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.runner.RunWith;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;

import com.grabble.customclasses.GameState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShopActivityTest {

    private GameState state;

    public static class FirstViewMatcher extends BaseMatcher<View> {


        static boolean matchedBefore = false;

        public FirstViewMatcher() {
            matchedBefore = false;
        }

        @Override
        public boolean matches(Object o) {
            if (matchedBefore) {
                return false;
            } else {
                matchedBefore = true;
                return true;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(" is the first view that comes along ");
        }

        @Factory
        public static <T> Matcher<View> firstView() {
            return new FirstViewMatcher();
        }
    }

    @Rule
    public ActivityTestRule<ShopActivity> mActivityRule = new ActivityTestRule<>
            (ShopActivity.class);

    @Before
    public void getState() {
        state = (GameState) mActivityRule.getActivity().getApplicationContext();
    }

    @Test
    public void attemptToBuyBooster() {
        onView(allOf(withId(R.id.cv), FirstViewMatcher.firstView())).perform(click());
        onView(allOf(withText("Choose Currency"), isDisplayed()));
    }

}