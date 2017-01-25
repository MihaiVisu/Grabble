package com.grabble.customclasses;


import android.widget.ImageView;

public class Achievement {

    private String text;
    private int tokenReward, gemReward;
    private int imgId;
    private boolean achieved;

    public Achievement(String text, int tokenReward, int gemReward, int imgId) {
        this.text = text;
        this.tokenReward = tokenReward;
        this.gemReward = gemReward;
        this.imgId = imgId;
        this.achieved = false;
    }

    public boolean getAchieved() {
        return achieved;
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
