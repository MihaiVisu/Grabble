package com.grabble.customclasses;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Class which keeps track of the current state of the user variables
 * throughout the game, like a singleton.
 */
public class GameState extends Application {

    private Context context;
    private int totalScore;
    private int gems;
    private int cash;
    private int losBoosters;
    private int wordHelpers;
    private int distanceTraveled;
    private String username;
    private static final int[] scores = {
            3, 20, 13, 10, 1, 15, 18, 9, 5, 25, 22, 11, 14,
            6, 4, 19, 24, 8, 7, 2, 12, 21, 17, 23, 16, 26
    };

    private HashMap<String, Integer> lettersGrabbed,
            wordsCreated;
    private HashSet<String> markersGrabbed, wordsList;
    private SharedPreferences prefs;

    // constructor
    @SuppressWarnings("unchecked")
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        prefs = context.getSharedPreferences("gamestate", MODE_PRIVATE);

        username = prefs.getString("username", "Player");
        totalScore = prefs.getInt("totalScore", 0);
        gems = prefs.getInt("gems", 0);
        cash = prefs.getInt("cash", 0);
        losBoosters = prefs.getInt("losBoosters", 0);
        wordHelpers = prefs.getInt("wordHelpers", 0);
        distanceTraveled = prefs.getInt("distanceTraveled", 0);

        try {
            File internalFile = new File(context.getFilesDir(), "internalFile.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(internalFile));

            lettersGrabbed = (HashMap<String, Integer>) ois.readObject();

            wordsCreated = (HashMap<String, Integer>) ois.readObject();

            markersGrabbed = (HashSet<String>) ois.readObject();

            wordsList = (HashSet<String>) ois.readObject();


            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (lettersGrabbed == null) {
                lettersGrabbed = new HashMap<>();
            }
            if (wordsCreated == null) {
                wordsCreated = new HashMap<>();
            }
            if (markersGrabbed == null) {
                markersGrabbed = new HashSet<>();
            }
            if (wordsList == null) {
                wordsList = new HashSet<>();
            }
        }

        for(char letter = 'a'; letter <= 'z'; letter++) {
            lettersGrabbed.put(String.valueOf(letter), 10);
        }

        // if the list of words hasn't been initialized previously in internal storage
        // initialize it from the grabble.txt raw file and add it to the set
        if (wordsList.isEmpty()) {
            try {
                InputStream iograbble = getApplicationContext().getResources().openRawResource(
                        getResources().getIdentifier("grabble", "raw", getPackageName()));
                BufferedReader buffer = new BufferedReader(new InputStreamReader(iograbble));
                String line;
                while ((line = buffer.readLine()) != null) {
                    wordsList.add(line);
                }
                // close the buffer
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public int getLosBoosters() {
        return losBoosters;
    }

    public int getWordHelpers() {
        return wordHelpers;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public HashMap<String, Integer> getLettersGrabbed() {
        return lettersGrabbed;
    }

    public HashMap<String, Integer> getWordsCreated() {
        return wordsCreated;
    }

    public HashSet<String> getMarkersGrabbed() {
        return markersGrabbed;
    }

    public HashSet<String> getWordsList() {
        return wordsList;
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

    public void setLosBoosters(int losBoosters) {
        this.losBoosters = losBoosters;
    }

    public void setWordHelpers(int wordHelpers) {
        this.wordHelpers = wordHelpers;
    }

    public void setLettersGrabbed(HashMap<String, Integer> lettersGrabbed) {
        this.lettersGrabbed = lettersGrabbed;
    }

    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public void setWordsCreated(HashMap<String, Integer> wordsCreated) {
        this.wordsCreated = wordsCreated;
    }

    public void setMarkersGrabbed(HashSet<String> markersGrabbed) {
        this.markersGrabbed = markersGrabbed;
    }

    public void setWordsList(HashSet<String> wordsList) {
        this.wordsList = wordsList;
    }

    // custom query and update methods

    public int getLetterScore(String letter) {
        return scores[(int)letter.charAt(0)-'a'];
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

    public void removeLetter(String letter) {
        if (lettersGrabbed.containsKey(letter)) {
            Integer freq = lettersGrabbed.get(letter);
            if (freq == 1) {
                lettersGrabbed.remove(letter);
            }
            else {
                lettersGrabbed.put(letter, freq-1);
            }
        }
    }

    public void removeLetter(char letter) {
        removeLetter(String.valueOf(letter));
    }

    public void addNewMarker(String markerId) {
        markersGrabbed.add(markerId);
    }

    public void addNewWord(String word) {
        int score = 0;
        for (char c : word.toCharArray()) {
            score += scores[(int)c-'a'];
        }
        wordsCreated.put(word, score);
    }

    public int changePixelToDp(int sizeInDp) {
        return (int)(getResources().getDisplayMetrics().density*sizeInDp + 0.5f);
    }

    // private method used to update the shared preferences
    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putInt("totalScore", totalScore);
        editor.putInt("gems", gems);
        editor.putInt("cash", cash);
        editor.putInt("losBoosters", losBoosters);
        editor.putInt("wordHelpers", wordHelpers);

        editor.apply();
    }

    public void updateState() {

        // shared preferences stuff
        updateSharedPreferences();

        try {
            File internalFile = new File(context.getFilesDir(), "internalFile.txt");
            FileOutputStream fout = new FileOutputStream(internalFile);
            ObjectOutputStream oout = new ObjectOutputStream(fout);

            oout.writeObject(lettersGrabbed);
            oout.writeObject(wordsCreated);
            oout.writeObject(markersGrabbed);
            oout.writeObject(wordsList);

            oout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method used to be called when onStop is triggered in any activity
    public void activityStopped() {
        updateState();
    }

}
