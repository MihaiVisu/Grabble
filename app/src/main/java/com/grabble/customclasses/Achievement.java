package com.grabble.customclasses;


import android.widget.ImageView;

import java.util.concurrent.Callable;

public class Achievement {

    private String text;
    private int tokenReward, gemReward;
    private int imgId;
    private boolean achieved;
    private Callable<Boolean> callable;

    public Achievement(String text, int tokenReward, int gemReward, int imgId,
                       Callable<Boolean> callable) {
        this.text = text;
        this.tokenReward = tokenReward;
        this.gemReward = gemReward;
        this.imgId = imgId;
        this.achieved = false;
        this.callable = callable;
    }

    public boolean getAchieved() {
        return achieved;
    }

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

}
