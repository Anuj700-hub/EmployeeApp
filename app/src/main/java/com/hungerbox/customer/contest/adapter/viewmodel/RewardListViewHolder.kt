package com.hungerbox.customer.contest.adapter.viewmodel

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.hungerbox.customer.R

class RewardListViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val tvReward: TextView = itemView.findViewById(R.id.tv_reward);
    val ivRewardLogo: ImageView = itemView.findViewById(R.id.iv_reward_logo)

}
