package com.grabble.adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grabble.R;
import com.grabble.customclasses.Achievement;
import com.grabble.customclasses.GameState;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class AchievementsAdapter extends
        RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private ArrayList<Achievement> achievements;

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView text;
        TextView rewardText;
        ImageView achievementImg, achievedImg;
        GameState state;

        AchievementViewHolder(View itemView, GameState state) {
            super(itemView);
            this.state = state;
            cv = (CardView) itemView.findViewById(R.id.achievement_cv);
            text = (TextView) itemView.findViewById(R.id.achievement_text);
            rewardText = (TextView) itemView.findViewById(R.id.achievement_reward);
            achievementImg = (ImageView) itemView.findViewById(R.id.achievement_img);
            achievedImg = (ImageView) itemView.findViewById(R.id.achieved);
        }
    }

    public AchievementsAdapter(ArrayList<Achievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_cv, parent, false);
        return new AchievementViewHolder(v, (GameState) v.getContext().getApplicationContext());
    }

    @Override
    public void onBindViewHolder(AchievementViewHolder holder, int position) {
        Achievement currentAchievement = achievements.get(position);
        holder.text.setText(currentAchievement.getText());
        holder.rewardText.setText(currentAchievement.getRewardText());
        holder.achievementImg.setImageResource(currentAchievement.getImgId());
        if (currentAchievement.getAchieved()) {
            holder.achievedImg.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

}
