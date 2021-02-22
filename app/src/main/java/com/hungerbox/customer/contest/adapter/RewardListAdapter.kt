package com.hungerbox.customer.contest.adapter

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hungerbox.customer.R
import com.hungerbox.customer.contest.adapter.viewmodel.LockedRewardViewHolder
import com.hungerbox.customer.contest.adapter.viewmodel.CashBackRewardViewHolder
import com.hungerbox.customer.contest.model.Reward
import com.hungerbox.customer.util.ImageHandling
import java.util.ArrayList
import android.graphics.ColorMatrixColorFilter
import android.graphics.ColorMatrix
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.hungerbox.customer.contest.activity.RewardActivity
import com.hungerbox.customer.contest.adapter.viewmodel.VoucherRewardViewHolder
import com.hungerbox.customer.order.activity.GlobalActivity
import com.hungerbox.customer.util.DateTimeUtil


class RewardListAdapter(private var activity: Activity, private var rewardDetails: ArrayList<Reward>,private val cellHeight:Int,private var rewardClickListener:RewardActivity.RewardUnlock) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val LOCKED_REWARD = 1
    private val UNLOCKED_VOUCHER = 2
    private val UNLOCKED_CASHBACK = 3
    private val REWARD_TYPE_VOUCHER = "voucher"
    private val REWARD_TYPE_CASHBACK = "cashback"
    private var inflater: LayoutInflater = LayoutInflater.from(activity)
    private val REQ_CODE_UNLOCK = 234
    private var defaultLayerType = -1


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return if (i == LOCKED_REWARD){
            LockedRewardViewHolder(inflater.inflate(R.layout.reward_view_locked, viewGroup, false))
        } else if (i == UNLOCKED_CASHBACK) {
            CashBackRewardViewHolder(inflater.inflate(R.layout.reward_view_cashback, viewGroup, false))
        } else{
            VoucherRewardViewHolder(inflater.inflate(R.layout.reward_view_voucher, viewGroup, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (holder is LockedRewardViewHolder){
            inflateLockedReward(holder,rewardDetails[i],i)
        } else if (holder is VoucherRewardViewHolder) {
            inflateVoucherReward(holder,rewardDetails[i])
        } else if (holder is CashBackRewardViewHolder){
            inflateCashBackReward(holder ,rewardDetails[i])
        }

    }

    private fun inflateLockedReward(holder: LockedRewardViewHolder, reward : Reward, position: Int){
        setBackground(holder.ivReward,position)
        holder.itemView.setOnClickListener { onRewardClick(reward) }
    }

    private fun inflateCashBackReward(holder: CashBackRewardViewHolder, reward : Reward){
//        if (cellHeight!=0) {
//            holder.itemView.layoutParams.height = cellHeight
//        }
        if (!reward.logo.isNullOrEmpty() && activity!=null ){
            ImageHandling.loadRemoteImage(reward.logo, holder.ivReward, R.drawable.ic_rewards_trophy, -1, activity)
        }
        holder.tvDate.text = DateTimeUtil.getDateStringFromDateString(reward.expiryTime,"yyyy-mm-dd HH:mm:ss","dd MMM yyyy")
        holder.tvReward.text = "Rs ${reward.value}"
        holder.itemView.setOnClickListener { onRewardClick(reward) }
//        if (expired)
//        setLocked(holder.itemView)
    }

    private fun inflateVoucherReward(holder: VoucherRewardViewHolder, reward : Reward){
        if (!reward.logo.isNullOrEmpty() && activity!=null ){
            ImageHandling.loadRemoteImage(reward.logo, holder.ivReward, R.drawable.ic_rewards_trophy, -1, activity)
        }
        holder.tvDate.text = DateTimeUtil.getDateStringFromDateString(reward.expiryTime,"yyyy-mm-dd HH:mm:ss","dd MMM yyyy")
        holder.itemView.setOnClickListener { onRewardClick(reward) }
        holder.tvVoucher.text = reward.voucherCode
        setVoucherText(holder.tvRewardLabel,reward)
//        if (expired)
//        setLocked(holder.itemView)
    }
    private fun setVoucherText(textView: TextView,reward: Reward){
        val text = "You Won Rs. ${reward.value}\ncash back voucher"
        val span = SpannableString(text)
        val startIndex = 7
        val endIndex = 12+(reward.value.toString().length)
        span.setSpan(ForegroundColorSpan(activity.resources.getColor(R.color.colorAccent)), startIndex, endIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        textView.text = span

    }

    private fun onRewardClick(reward: Reward){
//        val intent = Intent(activity,RewardViewActivity::class.java)
//        intent.putExtra("reward",reward)
//        activity.startActivityForResult(intent,REQ_CODE_UNLOCK)
        rewardClickListener.onRewardClick(reward)
    }

    override fun getItemViewType(position: Int): Int {
        if (!rewardDetails[position].isUnlocked){
            return LOCKED_REWARD
        } else if (rewardDetails[position].type.equals(REWARD_TYPE_VOUCHER)){
            return UNLOCKED_VOUCHER
        } else{
            //assuming type is cashback
            return UNLOCKED_CASHBACK
        }
    }

    override fun getItemCount(): Int {
        return rewardDetails.size
    }
    fun setBackground(imageView: AppCompatImageView, position: Int){
        when{
            position%3==0 ->imageView.setImageResource(R.drawable.ic_rewards_card3)
            position%2==0 ->imageView.setImageResource(R.drawable.ic_rewards_card2)
            else ->imageView.setImageResource(R.drawable.ic_rewards_card1)
        }
    }

    fun setLocked(v: View) {
//        val matrix = ColorMatrix()
//        matrix.setSaturation(0f)  //0 means grayscale
//        val cf = ColorMatrixColorFilter(matrix)
//        v.setColorFilter(cf)
//        v.setImageAlpha(128)   // 128 = 0.5

        defaultLayerType = v.layerType
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val greyscalePaint = Paint()
        greyscalePaint.setColorFilter(ColorMatrixColorFilter(cm))
// Create a hardware layer with the greyscale paint
        v.setLayerType(LAYER_TYPE_HARDWARE, greyscalePaint)
    }

    fun setUnlocked(v: View) {
        v.setLayerType(defaultLayerType,null);
    }
}
