package com.grabble;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Tests that modify the internal storage.
 */

@SuppressWarnings("unchecked")
@RunWith(AndroidJUnit4.class)
public class InternalStorageTest {

    private ObjectOutputStream oout;
    private ObjectInputStream iin;

    private Context context;

    @Before
    public void before() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        File internalFile = new File(context.getFilesDir(), "internalTestFile.txt");
        FileOutputStream fout = new FileOutputStream(internalFile);
        oout = new ObjectOutputStream(fout);

        FileInputStream fin = new FileInputStream(internalFile);
        iin = new ObjectInputStream(fin);
    }

    @Test
    public void testFileLocation() {
        File file = new File(context.getFilesDir(), "internalTestFile.txt");
        assertEquals(file.exists(), true);
    }

    @Test
    public void writeAndGetList() throws Exception {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("input1");
        testList.add("input2");
        testList.add("input3");
        testList.add("input4");
        oout.writeObject(testList);

        ArrayList<String> testInputList = (ArrayList<String>) iin.readObject();

        assertEquals(testList, testInputList);
    }

    @Test
    public void writeAndGetHashSet() throws Exception {
        HashSet<String> testSet = new HashSet<>();
        testSet.add("input1");
        testSet.add("input2");
        testSet.add("input3");
        testSet.add("input4");
        oout.writeObject(testSet);

        HashSet<String> testInputSet = (HashSet<String>) iin.readObject();

        assertEquals(testSet, testInputSet);
    }

    @Test
    public void writeMultipleObjects() throws Exception {
        HashMap<String, Integer> hashMap = new HashMap<>();
        ArrayList<Integer> array = new ArrayList<Integer>();
        array.add(1);
        array.add(2);
        String str = "testString";

        oout.writeObject(hashMap);
        oout.writeObject(array);
        oout.writeObject(str);

        HashMap<String, Integer> inputHashMap = (HashMap<String, Integer>) iin.readObject();
        ArrayList<Integer> inputArray = (ArrayList<Integer>) iin.readObject();
        String inputStr = (String) iin.readObject();

        // assert equality between elements
        assertEquals(hashMap, inputHashMap);
        assertEquals(array, inputArray);
        assertEquals(str, inputStr);
    }

    @After
    public void after() throws IOException {
        iin.close();
        oout.close();
    }

}