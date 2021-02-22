package com.hungerbox.customer.contest.adapter.viewmodel

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.hungerbox.customer.R

class LockedRewardViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val tvRewardLabel:TextView = itemView.findViewById(R.id.tv_label)
    val ivReward:AppCompatImageView = itemView.findViewById(R.id.iv_scratch_card)
}