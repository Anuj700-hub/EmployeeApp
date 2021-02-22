package com.hungerbox.customer.contest.adapter.viewmodel

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.hungerbox.customer.R

class VoucherRewardViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    val ivReward:AppCompatImageView = itemView.findViewById(R.id.iv_reward_logo)
    val tvRewardLabel:TextView = itemView.findViewById(R.id.tv_reward_label)
    val tvVoucher:TextView = itemView.findViewById(R.id.tv_voucher)
    val tvDate:TextView = itemView.findViewById(R.id.tv_date)
}