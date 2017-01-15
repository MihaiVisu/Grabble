package com.grabble.CustomClasses;


import android.app.Application;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Class which keeps track of the current state of the user variables
 * throughout the game, like a singleton.
 */
public class GameState extends Application {

    private int totalScore;
    private int gems;
    private int cash;
    private String username;
    private static final int[] scores = {
            3, 20, 13, 10, 1, 15, 18, 9, 5, 25, 22, 11, 14,
            6, 4, 19, 24, 8, 7, 2, 12, 21, 17, 23, 16, 26
    };

    private HashMap<String, Integer> lettersGrabbed,
            wordsCreated;

    // constructor

    @SuppressWarnings("unchecked")
    public GameState() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences("gamestate", MODE_PRIVATE);

        username = prefs.getString("username", "Player");
        totalScore = prefs.getInt("totalScore", 0);
        gems = prefs.getInt("gems", 0);
        cash = prefs.getInt("cash", 0);

        try {
            File internalFile = new File(getApplicationContext().getFilesDir(), "internalFile.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(internalFile));

            lettersGrabbed = (HashMap<String, Integer>) ois.readObject();
            wordsCreated = (HashMap<String, Integer>) ois.readObject();

            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // getters for the global variables

    public String getUsername() {
        return username;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getGems() {
        return gems;
    }

    public int getCash() {
        return cash;
    }

    public HashMap<String, Integer> getLettersGrabbed() {
        return lettersGrabbed;
    }

    public HashMap<String, Integer> getWordsCreated() {
        return wordsCreated;
    }

    // setters for the global variables

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public void setLettersGrabbed(HashMap<String, Integer> lettersGrabbed) {
        this.lettersGrabbed = lettersGrabbed;
    }

    public void setWordsCreated(HashMap<String, Integer> wordsCreated) {
        this.wordsCreated = wordsCreated;
    }

    // custom query and update methods

    public int getLetterScore(String letter) {
        return scores[letter.charAt(0)-'A'];
    }

    public int getLetterScore(char letter) {
        return scores[letter-'A'];
    }

    public void addNewLetter(String letter) {
        Integer freqOfLetter = lettersGrabbed.get(letter);
        if (freqOfLetter == null) {
            lettersGrabbed.put(letter, 1);
        }
        else {
            lettersGrabbed.put(letter, freqOfLetter+1);
        }
    }

    public void updateState() {
        try {
            File internalFile = new File(getApplicationContext().getFilesDir(), "internalFile.txt");
            FileOutputStream fout = new FileOutputStream(internalFile);
            ObjectOutputStream oout = new ObjectOutputStream(fout);

            oout.writeObject(lettersGrabbed);
            oout.writeObject(wordsCreated);

            oout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
