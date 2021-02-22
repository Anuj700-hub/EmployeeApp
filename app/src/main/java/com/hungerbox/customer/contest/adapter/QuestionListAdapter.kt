package com.hungerbox.customer.contest.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hungerbox.customer.R
import com.hungerbox.customer.contest.activity.ContestDetailActivity
import com.hungerbox.customer.contest.activity.RewardActivity
import com.hungerbox.customer.contest.activity.TermsAndCondition
import com.hungerbox.customer.contest.model.MilestoneData
import com.hungerbox.customer.contest.model.Task
import com.hungerbox.customer.contest.model.TaskData
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.CleverTapEvent
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.view.HBTextViewBold
import com.hungerbox.customer.util.view.HbTextView
import java.lang.Exception
import java.util.HashMap

/**
 * Created by Rishav on 10/5/19.
 */

class QuestionListAdapter(private var taskList:ArrayList<Any>,private var itemClickListener:ContestDetailActivity.ItemClick,private var context:Activity): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_FOOTER:Int = 1
    private val VIEW_TYPE_ITEM:Int = 0
    private val VIEW_TYPE_MILESTONE:Int = 2
    private val TYPEQUESTION = "question"
    private val TYPEORDER = "order"
    private val TYPEMILESTONE = "milestone"
    val TASKSTATUSNEW = "new"
    val TASKSTATUSABANDONED = "abandoned"
    val TASKSTATUSTIED = "tie"
    val TASKSTATUSRES= "rescheduled"

    val STATUSPASS = "pass"
    val STATUSFAIL = "fail"
    val STATUSPARTICIPATED = "participated"
    val STATUSWAITING = "waiting for result"
    //private lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        //context = p0.context
        return if (viewType == VIEW_TYPE_ITEM)
        {QuestionVH(LayoutInflater.from(p0.context).inflate(R.layout.contest_ques_detail_item,p0,false) as View)}
        else if (viewType == VIEW_TYPE_FOOTER)
        {FooterVH(LayoutInflater.from(p0.context).inflate(R.layout.contest_footer_item,p0,false) as View)}
        else
        {MilestoneVH(LayoutInflater.from(p0.context).inflate(R.layout.contest_milestone_item,p0,false) as View)}


    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setTaskList(taskList: ArrayList<Any>){
        this.taskList = taskList
    }

    //override fun getItemViewType(position: Int): Int = if (taskList[position].footer){VIEW_TYPE_FOOTER} else{VIEW_TYPE_ITEM}
    override fun getItemViewType(position: Int): Int {
        if (taskList[position] is TaskData){
            return if ((taskList[position] as TaskData).footer){VIEW_TYPE_FOOTER} else{VIEW_TYPE_ITEM}
        }else{
            return VIEW_TYPE_MILESTONE
        }
    }

    override fun onBindViewHolder(questionVH: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        try {

            var task = taskList[position]
            var calendar = DateTimeUtil.adjustCalender(context)
            if (questionVH is QuestionVH) {
                task as TaskData
                if (task.type == TYPEQUESTION) {
                    if (calendar.timeInMillis > (task.startTime * 1000)){
                        if (task.taskStatus == TASKSTATUSRES){
                            setFutureView(task,questionVH)
                        }
                        else{
                            setQuestionView(task,questionVH)
                        }

                    }
                    else{
                        setFutureView(task,questionVH)
                       //setQuestionView(task,questionVH)
                        //questionVH.clItemParent.alpha = 0.5f
                    }
//                    if (task.canAnswer == false && (calendar.timeInMillis < (task.startTime * 1000))) {
//                        //questionVH.clItemParent.alpha = 0.5f
//                        setFutureView("Question", questionVH)
//                    } else {
//                        questionVH.cvDetailQues.visibility = View.VISIBLE
//                        questionVH.tvAltQuestion.visibility = View.GONE
//                        questionVH.tvQuestionNumber.text = if (!task.question.isNullOrEmpty()) task.question else ""
//                        questionVH.tvPredictionStatus.text = if (!task.status.isNullOrEmpty()) task.status else ""
//                        if (task.startTime != null && task.startTime != 0L) {
//                            questionVH.tvContestDate.text = DateTimeUtil.getDateString(task.startTime * 1000).substring(7)
//                        }
//                        handleStatus(task, questionVH)
//                        questionVH.clItemParent.setOnClickListener { itemClickListener.OnItemClickListener(task) }
//                    }

                } else {
                    if (task.status == null) {
                        setFutureView(task, questionVH)
                    } else {
                        questionVH.cvDetailQues.visibility = View.VISIBLE
                        questionVH.tvAltQuestion.visibility = View.GONE
                        questionVH.tvQuestionNumber.text = AppUtils.getConfig(context).contestConfiguration.orderHeader
                        questionVH.tvPredictionStatus.setText(task.status)
                        if (task.taskDate != null) {
                            questionVH.tvContestDate.text = task.taskDate
                        }
                        questionVH.clItemParent.setOnClickListener { itemClickListener.OnItemClickListener(task,true) }
                    }
                }
            } else if (questionVH is FooterVH) {
    //            //get terms and conditions
                task as TaskData

                questionVH.tvTerms.setOnClickListener {

//                    try{
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.terms_and_condition_click,null,context)
//                    }catch (e:Exception){
//                        e.printStackTrace()
//                    }

                    val intent = Intent(context,TermsAndCondition::class.java)
                    if(task.footerText != null) {
                        intent.putExtra(ApplicationConstants.TERMS_AND_CONDITION, task.footerText)
                    }else{
                        intent.putExtra(ApplicationConstants.TERMS_AND_CONDITION," ")
                    }
                    context.startActivity(intent)
                }

                if (AppUtils.getConfig(context).isContest_reward_visible) {
                    questionVH.ivReward.setVisibility(View.VISIBLE)
                } else {
                    questionVH.ivReward.setVisibility(View.GONE)
                }

                questionVH.ivReward.setOnClickListener {

//                    try {
//                        val map = HashMap<String, Any>()
//                        map[CleverTapEvent.PropertiesNames.source] = "Campaign_Detail"
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.reward_click, map, context)
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }


                    val intent  = Intent(context,RewardActivity::class.java)
                    context.startActivity(intent)
                }
                //questionVH.tvTermsDescription.text = task.footerText


            } else if (questionVH is MilestoneVH) {
                task as MilestoneData
                questionVH.mText.text = task.title
                if (task.isUnlocked) {
                    questionVH.mText.background = context.resources.getDrawable(R.drawable.background_curved_white)
                    questionVH.mText.setTextColor(context.resources.getColor(R.color.contest_green))
                } else {
                    questionVH.mText.background = context.resources.getDrawable(R.drawable.background_curved_grey)
                    questionVH.mText.setTextColor(context.resources.getColor(R.color.colorGrey_Dot))
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun setFutureView(task: TaskData, questionVH: QuestionVH){
        setMarkerImage(R.drawable.grey_circle_fill,questionVH)
        if (questionVH is QuestionVH){
            questionVH.tvQuestionNumber.text = if (!task.question.isNullOrEmpty()) task.question else ""
            questionVH.tvPredictionStatus.text = if (!task.status.isNullOrEmpty()) task.status else ""
            if (task.startTime != null && task.startTime != 0L) {
                questionVH.tvContestDate.text = DateTimeUtil.getDateString(task.startTime * 1000).substring(7)
            }
            handleStatus(task, questionVH, true)

        }
        questionVH.cvDetailQues.alpha = 0.5f
        task.futureTask = true
        questionVH.clItemParent.setOnClickListener { itemClickListener.OnItemClickListener(task,true) }
        if (task.taskStatus == TASKSTATUSRES){
            questionVH.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.matchRescheduled
            questionVH.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.red))
        }
//        questionVH.cvDetailQues.visibility = View.GONE
//        questionVH.tvAltQuestion.visibility = View.VISIBLE

    }

    private fun setQuestionView(task :TaskData, questionVH : QuestionVH){
        questionVH.cvDetailQues.visibility = View.VISIBLE
        questionVH.tvAltQuestion.visibility = View.GONE
        questionVH.tvQuestionNumber.text = if (!task.question.isNullOrEmpty()) task.question else ""
        questionVH.tvPredictionStatus.text = if (!task.status.isNullOrEmpty()) task.status else ""
        if (task.startTime != null && task.startTime != 0L) {
            questionVH.tvContestDate.text = DateTimeUtil.getDateString(task.startTime * 1000).substring(7)
        }
        questionVH.cvDetailQues.alpha = 1f
        handleStatus(task, questionVH, false)
        task.futureTask = false
        questionVH.clItemParent.setOnClickListener { itemClickListener.OnItemClickListener(task,true) }
        if (task.status!=null && task.taskStatus!=TASKSTATUSNEW){
            when(task.taskStatus){
                TASKSTATUSABANDONED ->{
                    setMarkerImage(R.drawable.status_success,questionVH)
                    questionVH.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.matchAbandoned
                    questionVH.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.green))
                }
                TASKSTATUSTIED ->{
                    setMarkerImage(R.drawable.status_success,questionVH)
                    questionVH.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.matchTied
                    questionVH.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.green))
                }
            }
        }else if(task.status==null && task.taskStatus!=TASKSTATUSNEW ){
            setMarkerImage(R.drawable.ic_cancel_24_px,questionVH)
            questionVH.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.submissionExpired
            questionVH.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.red))
        }
    }
    private fun handleStatus(task :TaskData, viewHolder : QuestionVH, isFuture : Boolean){
        //todo new condition
        if (task.canAnswer!=null && task.canAnswer && task.status == null && isFuture){
            //setMarkerImage(R.drawable.status_info,viewHolder)
            setMarkerImage(R.drawable.i_round_blue,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.submissionNotOpen
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.contest_blue))
        }
        else if (task.canAnswer!=null && task.canAnswer && task.status == null && !isFuture){
            //setMarkerImage(R.drawable.status_info,viewHolder)
            setMarkerImage(R.drawable.orange_circle,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.submissionPending
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.contest_status_yellow))
        }
        else if (task.status!=null && (task.status == STATUSPARTICIPATED || task.status == STATUSWAITING)){
            //setMarkerImage(R.drawable.status_info,viewHolder)
            setMarkerImage(R.drawable.orange_circle,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.resultPending
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.contest_status_yellow))
        }
        else if (task.status!=null && task.status == STATUSPASS){
            setMarkerImage(R.drawable.status_success,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.correctAnswer
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.green))
        }
        else if (task.status!=null && task.status == STATUSFAIL){
            setMarkerImage(R.drawable.ic_cancel_24_px,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.incorrectAnswer
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.red))
        }
        else if (task.canAnswer!=null && task.canAnswer==false && task.status == null){
            setMarkerImage(R.drawable.ic_cancel_24_px,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.submissionExpired
            if (viewHolder.tvPredictionStatus.text.isNullOrEmpty()) viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.red))
        }
        else if (task.status==null && task.canAnswer==null){
            setMarkerImage(R.drawable.status_info,viewHolder)
            viewHolder.tvPredictionStatus.text = AppUtils.getConfig(context).contestConfiguration.submissionPending
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.contest_status_yellow))
        }
        else  if (task.status!=null){
            setMarkerImage(R.drawable.status_info,viewHolder)
            viewHolder.tvPredictionStatus.text = task.status.capitalize()
            viewHolder.tvPredictionStatus.setTextColor(context.resources.getColor(R.color.contest_status_yellow))
        }
    }

    private fun setMarkerImage(drawableID : Int,viewHolder : QuestionVH){
        var layoutParams = ConstraintLayout.LayoutParams(50,50)
        layoutParams.rightToRight = R.id.vertical_guide
        layoutParams.leftToLeft = R.id.vertical_guide
        layoutParams.topToTop = R.id.horizontal_guide
        layoutParams.bottomToBottom = R.id.horizontal_guide
        viewHolder.ivStatus.setImageResource(drawableID)
        viewHolder.ivStatus.setBackgroundColor(context.resources.getColor(R.color.contest_background))
        //viewHolder.ivStatus.setBackgroundColor(Color.parseColor("#FFFFFF"))
        viewHolder.ivStatus.layoutParams = layoutParams
    }


    class QuestionVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val clItemParent: ConstraintLayout = itemView.findViewById(R.id.cl_item)
        val tvQuestionNumber:HBTextViewBold = itemView.findViewById(R.id.tv_question_number)
        val tvContestDate:HbTextView = itemView.findViewById(R.id.tv_date_contest)
        val tvPredictionStatus:HbTextView = itemView.findViewById(R.id.tv_prediction_status)
        val cvDetailQues: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cv_detail_q)
        val tvAltQuestion:HbTextView = itemView.findViewById(R.id.tv_alternative_text)
        val ivStatus:ImageView = itemView.findViewById(R.id.iv_status)
    }

    class FooterVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val ivReward:ImageView = itemView.findViewById(R.id.iv_reward)
        val tvCashBackText:HBTextViewBold = itemView.findViewById(R.id.tv_cashback)
        val tvTerms:HbTextView = itemView.findViewById(R.id.tv_terms)
        val tvTermsDescription:HbTextView = itemView.findViewById(R.id.tv_terms_description)
    }

    class MilestoneVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val mText:HbTextView = itemView.findViewById(R.id.tv_mtext)
        val parentLayout: ConstraintLayout = itemView.findViewById(R.id.cl_ms_item)
    }
}