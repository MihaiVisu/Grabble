package com.grabble.customclasses;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabble.Fragments.GmapFragment;
import com.grabble.NavActivity;
import com.grabble.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;


/**
 * Class which keeps track of the current state of the user variables
 * throughout the game, like a singleton.
 */
public class GameState extends Application {

    private Context context;
    private int totalScore;
    private int gems;
    private int tokens;
    private int losBoosters;
    private int wordHelpers;
    private int distanceTraveled;
    private String username;

    private RelativeLayout headerContent;

    // --- variables for settings ---
    // variables for battery saving mode
    private boolean batterySavingMode;
    // variables for night mode
    private boolean nightMode;
    // ---

    // variables for progress bar
    private int losProgress;
    private boolean progressStarted;

    // variable in which we store the amount of the payment request
    private int paymentRequest;
    private double paymentPrice;

    private static final int[] scores = {
            3, 20, 13, 10, 1, 15, 18, 9, 5, 25, 22, 11, 14,
            6, 4, 19, 24, 8, 7, 2, 12, 21, 17, 23, 16, 26
    };

    private HashMap<String, Integer> lettersGrabbed,
            wordsCreated;
    private HashSet<String> markersGrabbed, wordsList;

    public ArrayList<Pair<String, Integer>> getSortedWordsList() {
        return sortedWordsList;
    }

    private ArrayList<Pair<String, Integer>> sortedWordsList;
    private SharedPreferences prefs;
    private ArrayList<Achievement> achievements;
    // array representing the states of all the achievements
    // which will be written to internal storage to preserve the state
    private ArrayList<Boolean> achievementState;

    // constructor
    @SuppressWarnings("unchecked")
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        // set the state variables from shared preferences
        prefs = context.getSharedPreferences("gamestate", MODE_PRIVATE);

        username = prefs.getString("username", "Player");
        totalScore = prefs.getInt("totalScore", 0);
        gems = prefs.getInt("gems", 0);
        tokens = prefs.getInt("tokens", 0);
        losBoosters = prefs.getInt("losBoosters", 0);
        wordHelpers = prefs.getInt("wordHelpers", 0);
        distanceTraveled = prefs.getInt("distanceTraveled", 0);
        batterySavingMode = prefs.getBoolean("batterySavingMode", false);
        nightMode = prefs.getBoolean("nightMode", false);

        try {
            // get data structures from internal storage
            File internalFile = new File(context.getFilesDir(), "internalFile.txt");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(internalFile));

            lettersGrabbed = (HashMap<String, Integer>) ois.readObject();

            wordsCreated = (HashMap<String, Integer>) ois.readObject();

            markersGrabbed = (HashSet<String>) ois.readObject();

            wordsList = (HashSet<String>) ois.readObject();

            achievementState = (ArrayList<Boolean>) ois.readObject();


            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // initialize new variables if internal memory file not found
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
            if (achievementState == null) {
                achievementState = new ArrayList<>();
            }
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

        // initialize the sorted array list of words
        // in terms of their score
        if (!wordsList.isEmpty() && sortedWordsList == null) {
            sortedWordsList = new ArrayList<>();
            for (String word : wordsList) {
                sortedWordsList.add(new Pair<>(word, calculateWordScore(word)));
            }
            Collections.sort(sortedWordsList, new Comparator<Pair<String, Integer>>() {
                @Override
                public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                    if (o1.second > o2.second) {
                        return -1;
                    } else if (o1.second.equals(o2.second)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }

    }

    // getters for the global variables

    public RelativeLayout getHeaderContent() {
        return headerContent;
    }

    public boolean getNightMode() {
        return nightMode;
    }

    public boolean getBatterySavingMode() {
        return batterySavingMode;
    }

    public int getLosProgress() {
        return losProgress;
    }

    public boolean getProgressStarted() {
        return progressStarted;
    }

    public void setProgressStarted(boolean progressStarted) {
        this.progressStarted = progressStarted;
    }

    public ArrayList<Achievement> getAchievements() {
        return achievements;
    }

    // check milestones of all achievements and update the achieved state
    // if new achievement is reached, show a snackbar with a message
    public void checkMilestones(Snackbar snackbar) {
        for (int i = 0; i < achievements.size(); i++) {
            try {
                boolean previousState = achievementState.get(i);
                achievements.get(i).checkMilestone();
                // if achievement just unlocked
                if (achievements.get(i).getAchieved() && !previousState) {
                    showAchievementSnackbar(snackbar, achievements.get(i));
                    achievementState.set(i, true);
                    gems += achievements.get(i).getGemReward();
                    tokens += achievements.get(i).getTokenReward();
                    NavActivity.updateContent(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //show achievements snackbar when achievement is unlocked
    private void showAchievementSnackbar(Snackbar snackbar, Achievement achievement) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String message = "New Achievement: " + achievement.getText() + "   ";
        builder.append(message);
        // set achievement message icon
        Drawable icon = getDrawable(achievement.getImgId());
        icon.setBounds(0,0,80,80);
        builder.setSpan(new ImageSpan(icon), builder.length()-1, builder.length(), 0);
        snackbar.setText(builder);
        snackbar.show();
    }

    // function to initialize the achievements
    public void initializeAchievements() {
        // initialize achievements with callables to check
        // the unlocked status
        if (achievements == null) {
            achievements = new ArrayList<>();
            achievements.add(new Achievement("Create first word", 50, 1, R.drawable.pacifier,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return wordsCreated.size() >= 1;
                        }
                    }));
            achievements.add(new Achievement("Collect 200 letters", 50, 1, R.drawable.first_letters,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            int sum = 0;
                            for(String key : lettersGrabbed.keySet()) {
                                sum += lettersGrabbed.get(key);
                            }
                            return sum >= 200;
                        }
                    }));
            achievements.add(new Achievement("Travel 1000m", 100, 2, R.drawable.road,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return distanceTraveled >= 1000;
                        }
                    }));
            achievements.add(new Achievement("Score 1500 points", 100, 2, R.drawable.medal,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return totalScore >= 1500;
                        }
                    }));
            achievements.add(new Achievement("Create 50 words", 400, 8, R.drawable.book,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return wordsCreated.size() >= 50;
                        }
                    }));
            achievements.add(new Achievement("Travel 3000m", 300, 6, R.drawable.road_map,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return distanceTraveled >= 3000;
                        }
                    }));

            achievements.add(new Achievement("Score 3000 points", 300, 6, R.drawable.trophy,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return totalScore >= 3000;
                        }
                    }));
            achievements.add(new Achievement("Travel 5000m", 400, 8, R.drawable.worldwide,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return distanceTraveled >= 5000;
                        }
                    }));

            achievements.add(new Achievement("Collect 400 letters", 100, 2, R.drawable.letters,
                    new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            int sum = 0;
                            for(String key : lettersGrabbed.keySet()) {
                                sum += lettersGrabbed.get(key);
                            }
                            return sum >= 400;
                        }
                    }));
        }
        // initialize the array of statuses of the achievements
        if (achievementState == null || achievementState.size() == 0) {
            // initialize array of achievement state
            achievementState = new ArrayList<>(achievements.size());
            for (int i = 0; i < achievements.size(); i++) {
                achievementState.add(false);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getGems() {
        return gems;
    }

    public int getTokens() {
        return tokens;
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

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public void setBatterySavingMode(boolean batterySavingMode) {
        this.batterySavingMode = batterySavingMode;
        GmapFragment.setLocationRequestVariables(batterySavingMode);
    }

    public void setHeaderContent(RelativeLayout ly) {
        headerContent = ly;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
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

    public void setLosProgress(int losProgress) {
        this.losProgress = losProgress;
    }

    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public void useLosBooster() {
        losBoosters -= 1;
    }

    public void useWordHelper() {
        wordHelpers -= 1;
    }

    public void addDistance(double distance) {
        this.distanceTraveled += distance;
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

    // increment the progress of line of sight status
    public void incrementLosProgress() {
        losProgress++;
    }

    // add new letter to group of letters grabbed
    public void addNewLetter(String letter) {
        letter = letter.toLowerCase();
        Integer freqOfLetter = lettersGrabbed.get(letter);
        if (freqOfLetter == null) {
            lettersGrabbed.put(letter, 1);
        }
        else {
            lettersGrabbed.put(letter, freqOfLetter+1);
        }
    }

    // remove letter from group of letters grabbed
    // when a new word has been created
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

    // get payment request for the fictive payment method
    // with braintree
    public int getPaymentRequest() {
        return paymentRequest;
    }

    // get payment price
    public double getPaymentPrice() {
        return paymentPrice;
    }

    // set payment request for the fictive paument method
    // with braintree
    public void setPaymentRequest(int amount, double price) {
        paymentRequest = amount;
        paymentPrice = price;
    }

    public void removeLetter(char letter) {
        removeLetter(String.valueOf(letter));
    }

    // add new marker to list of grabbed markers
    // this is done to prevent adding same marker twice
    public void addNewMarker(String markerId) {
        markersGrabbed.add(markerId);
    }

    // calculate score of a word
    public int calculateWordScore(String word) {
        int score = 0;
        for (char c : word.toLowerCase().toCharArray()) {
            score += scores[(int)c-'a'];
        }
        return score;
    }

    // add new word to list of words
    public void addNewWord(String word) {
        int score = calculateWordScore(word);
        wordsCreated.put(word, score);
        totalScore += score;
        tokens += score;
    }

    // method which updates tokens and amount of boosters
    // assuming we have enough money
    public void buyBoosters(int quantity, String type, String currency, int price) {
        if (type.equals("los")) {
            losBoosters += quantity;
            if (currency.equals("tokens")) {
                tokens -= price;
            }
            else if (currency.equals("gems")) {
                gems -= price;
            }
        }
        else if (type.equals("helper")) {
            wordHelpers += quantity;
            gems -= price;
        }
    }

    // change measurement in pixels to dp
    public int changePixelToDp(int sizeInDp) {
        return (int)(getResources().getDisplayMetrics().density*sizeInDp + 0.5f);
    }

    // private method used to update the shared preferences
    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putInt("totalScore", totalScore);
        editor.putInt("gems", gems);
        editor.putInt("tokens", tokens);
        editor.putInt("losBoosters", losBoosters);
        editor.putInt("wordHelpers", wordHelpers);
        editor.putInt("distanceTraveled", distanceTraveled);
        editor.putBoolean("batterySavingMode", batterySavingMode);
        editor.putBoolean("nightMode", nightMode);

        editor.apply();
    }

    // method called to update the state and save it to internal storage
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
            oout.writeObject(achievementState);

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
