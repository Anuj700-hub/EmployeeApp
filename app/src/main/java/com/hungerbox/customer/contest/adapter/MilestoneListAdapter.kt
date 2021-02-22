package com.hungerbox.customer.contest.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.hungerbox.customer.R
import com.hungerbox.customer.contest.activity.ContestActivity.STATUSSUCCESS
import com.hungerbox.customer.contest.activity.RewardActivity
import com.hungerbox.customer.contest.activity.RewardViewActivity
import com.hungerbox.customer.contest.model.MilestoneData
import com.hungerbox.customer.contest.model.Reward
import com.hungerbox.customer.contest.model.TaskData
import com.hungerbox.customer.navmenu.activity.HistoryActivity
import com.hungerbox.customer.order.activity.GlobalActivity
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.view.GenericPopUpFragment

class MilestoneListAdapter (private var milestoneList:ArrayList<MilestoneData>,private var taskList:ArrayList<TaskData>,private var taskMap:Map<Int,TaskData>, private var context: Activity,private var rewardClickListener: RewardActivity.RewardUnlock,var campaignRewardType : String,val isCampaignExpired :Boolean, val endDate : Long, val fragmentManager: FragmentManager): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(){
    private val viewTypeHeader = 1
    private val viewTypeMilestone = 2
    private val TASKTYPEORDER = "order"
    private val REQ_CODE_UNLOCK = 234
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            return MilestoneVH(LayoutInflater.from(p0.context).inflate(R.layout.milestone_item,p0,false) as View)

    }

    override fun getItemCount(): Int {
        return milestoneList.size
    }

    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
            inflateMilestone(viewHolder as MilestoneVH, position)

    }

    private fun inflateMilestone(viewHolder:MilestoneVH,position: Int){
        try {
            val milestoneData = milestoneList[position] as MilestoneData
            lateinit var mTaskIDs: IntArray
            if (!milestoneData.taskIds.isNullOrEmpty()) {
                mTaskIDs = milestoneData.taskIds.split(",").map { it -> it.toInt() }.toIntArray()
            }else{
                mTaskIDs = IntArray(0)
            }
            if (mTaskIDs.isNotEmpty()) {
                if (taskMap[mTaskIDs[0]]?.type.equals(TASKTYPEORDER))
                {
                    viewHolder.rewardIncompleteText.text="Order Now"
                    viewHolder.rewardIncompleteText.setOnClickListener{
                        var intent = Intent(context,GlobalActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        context.setResult(STATUSSUCCESS)
                        context.finish()
                    }
                } else{
                    viewHolder.rewardIncompleteText.text="Review Now"
                    viewHolder.rewardIncompleteText.setOnClickListener{
                        var intent = Intent(context,HistoryActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
//            viewHolder.milestoneNumber.text = milestoneData.id.toString()
            viewHolder.milestoneNumber.text = milestoneData.requiredTaskCount.toString()
            viewHolder.milestoneText.text = milestoneData.title
            viewHolder.milestoneBadge.text = "${milestoneData.taskLeft} left"
            if (position == 0) viewHolder.lineTop.visibility = View.GONE
            if (position == milestoneList.size-1) viewHolder.lineBottom.visibility = View.GONE
            if (milestoneData.isUnlocked){
                activateMilestone(viewHolder,position)
            } else{
                deActivateMilestone(viewHolder,position)
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun activateMilestone(viewHolder: MilestoneVH,position: Int){
        if (milestoneList[position].reward!=null ){
            if (milestoneList[position].reward.isUnlocked ) {
                viewHolder.rewardCompleteText.text = "Unlocked"
            } else{
                viewHolder.rewardCompleteText.text = "Unlock now"
            }
        }
        val color:ColorDrawable = ColorDrawable(context.resources.getColor(R.color.contest_green))
        viewHolder.lineTop.background = color
        viewHolder.lineBottom.background = color
        viewHolder.statusTick.setImageDrawable(context.resources.getDrawable(R.drawable.tick))
        viewHolder.rewardCompleteImage.visibility = View.VISIBLE
        viewHolder.rewardCompleteText.visibility = View.VISIBLE
        viewHolder.rewardIncompleteText.visibility = View.GONE

        if (milestoneList[position].reward!=null){
            viewHolder.rewardCompleteText.setOnClickListener{
               moveToRewardScratchScreen(milestoneList[position].reward)
            }

            viewHolder.rewardCompleteImage.setOnClickListener {
                moveToRewardScratchScreen(milestoneList[position].reward)
            }
        }

        if (campaignRewardType == "highest_at_end"){
            if (isCampaignExpired){
                viewHolder.rewardCompleteText.setTextColor(context.resources.getColor(R.color.black_40))

                viewHolder.rewardCompleteImage.visibility = View.GONE
                viewHolder.rewardDisableImage.visibility = View.VISIBLE

            } else{

                viewHolder.rewardCompleteText.setTextColor(context.resources.getColor(R.color.contest_green))

                if (milestoneList[position].reward!=null) {
                    viewHolder.rewardCompleteText.setOnClickListener {
                        clickRewardInteration(milestoneList[position].reward)
                    }
                    viewHolder.rewardCompleteImage.setOnClickListener {
                        clickRewardInteration(milestoneList[position].reward)
                    }
                }

            }
        }
        else if(campaignRewardType == "all_at_end"){
            if(!isCampaignExpired){

                viewHolder.rewardCompleteText.setTextColor(context.resources.getColor(R.color.contest_green))

                if (milestoneList[position].reward!=null) {
                    viewHolder.rewardCompleteText.setOnClickListener {
                        clickRewardInteration(milestoneList[position].reward)
                    }
                    viewHolder.rewardCompleteImage.setOnClickListener {
                        clickRewardInteration(milestoneList[position].reward)
                    }
                }

            }
        }
    }

    private fun clickRewardInteration(reward: Reward){
        if(reward != null && reward.isUnlocked){
            moveToRewardScratchScreen(reward)
        }
        else{
            showRewardMessage()
        }
    }

    private fun showRewardMessage(){

        var message: String
        if(campaignRewardType == "highest_at_end"){
            message = "Reward are available based on the highest milestone achieved once the campaign ends"
        }
        else if(campaignRewardType == "all_at_end"){
            message = "Rewards will be available on or after the " + DateTimeUtil.getDateStringCustom(endDate, "dd MMM yyyy")
        }
        else{
            return
        }

        val popUpFragment = GenericPopUpFragment
                .newInstance(message, "OK", true, object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {

                    }

                    override fun onNegativeInteraction() {}
                })

        fragmentManager.beginTransaction()
                .add(popUpFragment, "reward_error_popup")
                .commitAllowingStateLoss()
    }
    private fun moveToRewardScratchScreen(reward: Reward){

        if(reward.isUnlocked){
            var intent = Intent(context,RewardViewActivity::class.java)
            intent.putExtra("reward",reward)
            context.startActivityForResult(intent,REQ_CODE_UNLOCK)
        }
        else{
            rewardClickListener.onRewardClick(reward)
        }
    }
    private fun deActivateMilestone(viewHolder: MilestoneVH, position: Int){
        val color:ColorDrawable = ColorDrawable(context.resources.getColor(R.color.contest_grey))
        val colorGreen = ColorDrawable(context.resources.getColor(R.color.contest_green))
        if (position>0 && milestoneList[position-1].isUnlocked){
            viewHolder.lineTop.background = colorGreen
        } else {
            viewHolder.lineTop.background = color
        }
        viewHolder.lineBottom.background = color
        viewHolder.statusTick.setImageDrawable(context.resources.getDrawable(R.drawable.locked))
        viewHolder.rewardCompleteImage.visibility = View.GONE
        viewHolder.rewardCompleteText.visibility = View.GONE
        viewHolder.rewardIncompleteText.visibility = View.VISIBLE
    }

    class MilestoneVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val milestoneNumber: TextView = itemView.findViewById(R.id.tv_number)
        val milestoneText: TextView = itemView.findViewById(R.id.tv_offer_text)
        val milestoneBadge: TextView = itemView.findViewById(R.id.tv_reward_badge)
        val rewardIncompleteText: TextView = itemView.findViewById(R.id.tv_reward_text)
        val rewardCompleteText: TextView = itemView.findViewById(R.id.tv_reward_label)
        val rewardCompleteImage : ImageView = itemView.findViewById(R.id.iv_rewards)
        val rewardDisableImage : ImageView = itemView.findViewById(R.id.iv_rewards_dark)
        val lineTop : View = itemView.findViewById(R.id.line1)
        val lineBottom : View = itemView.findViewById(R.id.line2)
        val statusTick : ImageView = itemView.findViewById(R.id.iv_status)
    }

//    class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        val orders: TextView = itemView.findViewById(R.id.tv_orders)
//        val milestones: TextView = itemView.findViewById(R.id.tv_milestones)
//        val status: TextView = itemView.findViewById(R.id.tv_status)
//        val rewards: TextView = itemView.findViewById(R.id.tv_rewards)
//    }
}