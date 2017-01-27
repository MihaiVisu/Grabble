package com.grabble.suite;

import com.grabble.BaseButtonsTest;
import com.grabble.WordsActivity;
import com.grabble.WordsActivityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all Junit3 and Junit4 Instrumentation tests.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseButtonsTest.class,
        WordsActivityTest.class
})

public class UITestSuite {}