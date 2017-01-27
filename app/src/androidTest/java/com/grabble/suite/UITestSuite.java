package com.grabble.suite;

import com.grabble.BaseButtonsTest;
import com.grabble.LetterActivityTest;
import com.grabble.ShopActivity;
import com.grabble.ShopActivityTest;
import com.grabble.WordsActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all Junit3 and Junit4 Instrumentation tests.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseButtonsTest.class,
        WordsActivityTest.class,
        LetterActivityTest.class,
        ShopActivityTest.class
})

public class UITestSuite {}