package com.grabble.suite;

import com.grabble.BaseButtonsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all Junit3 and Junit4 Instrumentation tests.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BaseButtonsTest.class,
})

public class UITestSuite {}