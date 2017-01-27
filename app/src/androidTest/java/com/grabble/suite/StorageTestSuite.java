package com.grabble.suite;

import com.grabble.InternalStorageTest;
import com.grabble.PackageNameTest;
import com.grabble.SharedPrefsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all Junit3 and Junit4 Instrumentation tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PackageNameTest.class,
        SharedPrefsTest.class,
        InternalStorageTest.class
})

public class StorageTestSuite {}