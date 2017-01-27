package com.grabble.customclasses;


import android.widget.ImageView;

import java.util.concurrent.Callable;


/**
 * achievement class
 */
public class Achievement {

    private String text;
    private int tokenReward, gemReward;
    private int imgId;
    private boolean achieved;
    // it contains a callable method which implements a function
    // returning a boolean to check whether the achievement has been reached or not
    private Callable<Boolean> callable;

    public Achievement(String text, int tokenReward, int gemReward, int imgId,
                       Callable<Boolean> callable) {
        this.text = text;
        this.tokenReward = tokenReward;
        this.gemReward = gemReward;
        this.imgId = imgId;
        this.callable = callable;
        this.achieved = false;
    }

    public boolean getAchieved() {
        return achieved;
    }

    // function to check the milestone of the achievement
    public void checkMilestone() throws Exception {
        try {
            achieved = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getText() {
        return text;
    }

    public int getImgId() {
        return imgId;
    }

    // function to format the reward to text
    public String getRewardText() {
        String reward = "Reward: ";
        if (tokenReward > 0) {
            reward += "x" + tokenReward + " tokens ";
        }
        if (gemReward > 0) {
            reward += "x" + gemReward + " gems";
        }
        return reward;
    }

    public int getTokenReward() {
        return tokenReward;
    }

    public int getGemReward() {
        return gemReward;
    }

}
