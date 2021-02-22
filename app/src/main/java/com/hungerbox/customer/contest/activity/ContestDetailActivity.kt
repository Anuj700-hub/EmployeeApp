package com.hungerbox.customer.contest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.JsonSerializer
import com.hungerbox.customer.R
import com.hungerbox.customer.contest.adapter.MilestoneListAdapter
import com.hungerbox.customer.contest.adapter.QuestionListAdapter
import com.hungerbox.customer.contest.model.*
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.fragment.NoNetFragment
import com.hungerbox.customer.order.listeners.CancelListener
import com.hungerbox.customer.order.listeners.RetryListener
import com.hungerbox.customer.prelogin.activity.ParentActivity
import com.hungerbox.customer.util.*
import com.hungerbox.customer.util.view.ErrorPopFragment
import com.hungerbox.customer.util.view.GenericPopUpFragment
import com.hungerbox.customer.util.view.HbTextView
import com.hungerbox.customer.util.view.LoaderFragment
import kotlinx.android.synthetic.main.activity_contest_detail.*
import kotlinx.android.synthetic.main.activity_contest_detail.button_submit
import kotlinx.android.synthetic.main.activity_contest_detail.cl_question
import kotlinx.android.synthetic.main.activity_contest_detail.iv_back
import kotlinx.android.synthetic.main.activity_contest_detail.iv_banner
import kotlinx.android.synthetic.main.activity_contest_detail.iv_option_1
import kotlinx.android.synthetic.main.activity_contest_detail.iv_option_2
import kotlinx.android.synthetic.main.activity_contest_detail.iv_option_3
import kotlinx.android.synthetic.main.activity_contest_detail.iv_option_4
import kotlinx.android.synthetic.main.activity_contest_detail.ll_option_1
import kotlinx.android.synthetic.main.activity_contest_detail.ll_option_2
import kotlinx.android.synthetic.main.activity_contest_detail.ll_option_3
import kotlinx.android.synthetic.main.activity_contest_detail.ll_option_4
import kotlinx.android.synthetic.main.activity_contest_detail.option_1
import kotlinx.android.synthetic.main.activity_contest_detail.option_2
import kotlinx.android.synthetic.main.activity_contest_detail.option_3
import kotlinx.android.synthetic.main.activity_contest_detail.option_4
import kotlinx.android.synthetic.main.activity_contest_detail.tv_banner
import kotlinx.android.synthetic.main.activity_contest_detail.tv_exp_time
import kotlinx.android.synthetic.main.activity_contest_detail.tv_question
import kotlinx.android.synthetic.main.activity_contest_detail.tv_title
import kotlinx.android.synthetic.main.activity_contest_detail_horizontal.*
import kotlinx.android.synthetic.main.toolbar_contest.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by Rishav on 10/5/19.
 */
class ContestDetailActivity: ParentActivity() {

    val STATUSSUCCESS = 1221
    val STATUSPASS = "pass"
    val STATUSFAIL = "fail"
    val STATUSPARTICIPATED = "participated"
    val STATUSWAITING = "waiting for result"

    val TASKSTATUSNEW = "new"
    val TASKSTATUSABANDONED = "abandoned"
    val TASKSTATUSTIED = "tie"
    val TASKSTATUSRES= "rescheduled"

    val TASKTYPEQUES = "question"
    private val REQ_CODE_UNLOCK = 234
    private val UNLOCK_SUCCESS = 235

    private lateinit var questionList: androidx.recyclerview.widget.RecyclerView
    private lateinit var layoutManager: androidx.recyclerview.widget.LinearLayoutManager
    private lateinit var questionListAdapter: QuestionListAdapter
    //private lateinit var taskList: ArrayList<TaskData>
    private var contentList = ArrayList<Any>()
    private lateinit var listener:ItemClick
    private var campaignID =-1
    private var campaignType = "";
    private var alphabetArray : CharArray = charArrayOf ('A','B','C','D')
    private lateinit var optionArray:Array<HbTextView>
    private lateinit var optionLayoutArray:Array<LinearLayout>
    private lateinit var optionImageArray:Array<ImageView>
    private val CURRENTSELECTED = 99
    private val SELECTEDANSWER = 100
    private val CORRECTANSWER = 101
    private val INCORRECTANSWER = 102
    private val CLEARSELECTION = 103
    private lateinit var activeTask: TaskData
    private lateinit var loaderFragment: LoaderFragment
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var clContainer : ConstraintLayout
    private lateinit var errorPopUp : ErrorPopFragment
    private lateinit var source : String
    private lateinit var calendar: Calendar
    private var logo =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_contest_detail)
        setContentView(R.layout.activity_contest_detail_horizontal)

        clContainer = findViewById(R.id.cl_container)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container)

        showShimmerLoader()


        optionArray = arrayOf(option_1,option_2,option_3,option_4)
        optionLayoutArray = arrayOf(ll_option_1,ll_option_2,ll_option_3,ll_option_4)
        optionImageArray = arrayOf(iv_option_1,iv_option_2,iv_option_3,iv_option_4)
        calendar = DateTimeUtil.adjustCalender(this)
        instantiateListener()
        //taskList = ArrayList<TaskData>()
        questionList = findViewById(R.id.rv_question_list)
        questionListAdapter = QuestionListAdapter(contentList,listener,this)
        swipeRefreshLayout = findViewById(R.id.srl_contest_detail);
        questionList.adapter = questionListAdapter
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        questionList.layoutManager = layoutManager
        campaignID = intent.getIntExtra(ApplicationConstants.CAMPAIGN_ID,-1)
        campaignType = intent.getStringExtra(ApplicationConstants.TEMPLATE_NAME)
        iv_back.setOnClickListener{ onBackPressed() }
        source = intent.getStringExtra(CleverTapEvent.PropertiesNames.source)


        if (campaignID!=-1) {
            getCampaignDetails(campaignID)
            sendCleverTapEvent(campaignID);
        }


        swipeRefreshLogic(campaignID, swipeRefreshLayout)
        swipeRefreshLogic(campaignID, srl_contest_milestone)
        //move to rewards screen on click
        cl_block3.setOnClickListener { goToRewardsScreen() }
        box3_header1.setOnClickListener { goToRewardsScreen() }
        box3_header2.setOnClickListener { goToRewardsScreen() }

    }

    private fun swipeRefreshLogic(campaignID: Int, swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout){
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            if(campaignID!=-1)
                getCampaignDetails(campaignID)
        }
    }

    private fun sendCleverTapEvent(campaignID: Int){
//        try {
//            val map = HashMap<String, Any>()
//            map[CleverTapEvent.PropertiesNames.source] = source
//            map[CleverTapEvent.PropertiesNames.campaign_id] = campaignID
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.campaign_list_click, map, applicationContext)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }

    private fun instantiateListener(){
        try {

            listener = object : ItemClick {
                override fun OnItemClickListener(task: TaskData,userClick: Boolean) {
//                    if(!task.clickable){
//                        showBanner()
//                        return
//                    }
                    if (task.clickable || !task.futureTask) {
                        if (!task.futureTask) {
                            activeTask = task
                            if (task.type == "question") {
                                hideBanner()
                                inflateQuestionView(task)
                                if(userClick)
                                 sendQuestionClickEvent()
                            } else {
                                showBanner()
                            }
                        }
                    } else{
                        showBanner()
                    }
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun sendQuestionClickEvent(){
//        try {
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.question_click, null, applicationContext)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }

    private fun showProgressLoader(){
        loaderFragment = LoaderFragment.newInstance("Loading..")
        val fragmentManager = getSupportFragmentManager()
        loaderFragment.show(fragmentManager,"contest_loader")

    }

    private fun showShimmerLoader(){
        shimmerFrameLayout.visibility = View.VISIBLE
        clContainer.visibility = View.GONE
    }

    private fun hideShimmerLoader(){
        shimmerFrameLayout.visibility = View.GONE
        clContainer.visibility = View.VISIBLE
    }

    private fun inflateQuestionView(task:TaskData){

        hideBanner()//hide the default banner text
        tv_question.text = task.question //set question to question textview
        task.currentSelection = -1 //refresh selection
        button_submit.isEnabled = task.type=="question" && task.canAnswer
                && task.status==null
        if (button_submit.isEnabled){
            setSelectionListeners(task)
        } else{
            clearSelectionListeners()
        }

        if(task.endTime!=null) {
            tv_exp_time.text = getTimeLeft(task.endTime)
            //tv_exp_time.text = "closes at ${DateTimeUtil.getDateString(task.endTime*1000)}"
        }
        if (task.options != null){//if options are available

            inflateOptions(task, option_1, 0)
            inflateOptions(task, option_2, 1)
            inflateOptions(task, option_3, 2)
            inflateOptions(task, option_4, 3)
        }
        clearAllSelectionExcept(10,false)
        //selction of correct/selected answer
        if (task.submittedAnswer!=null) {
            var count = 0
            for (item in task.options) {
                if (item == task.submittedAnswer && task.status != null) {
                    when (task.status) {
                        STATUSPARTICIPATED -> selectAnswer(count, SELECTEDANSWER)
                        STATUSWAITING -> selectAnswer(count, SELECTEDANSWER)
                        STATUSPASS -> selectAnswer(count, CORRECTANSWER)
                        STATUSFAIL -> selectAnswer(count, INCORRECTANSWER)
                        else -> selectAnswer(count, SELECTEDANSWER)
                    }
                }
                ++count
            }
        }
        else{
            clearAllSelectionExcept(10,false)
        }

    }

    private fun clearAllSelectionExcept(optionNumber: Int, selectGivenOption:Boolean){
        if (!optionArray.isNullOrEmpty()) {
            for (i in 0 until optionArray.size ) {
                if (i == optionNumber) {
                    if (selectGivenOption) {
                        selectAnswer(i, CURRENTSELECTED)
                    }
                } else {
                    selectAnswer(i, CLEARSELECTION)
                }
            }
        }
    }

    private fun setSelectionListeners(task: TaskData){
        if (!optionLayoutArray.isNullOrEmpty()){
            for (i in 0 until optionLayoutArray.size) {
                optionLayoutArray[i].setOnClickListener {
                    task.currentSelection = i
                    clearAllSelectionExcept(i, true)
//                    try {
//                        val map = HashMap<String, Any>()
//                        map.put(CleverTapEvent.PropertiesNames.option_name,task.options.get(i).option)
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.option_click, map, applicationContext)
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }


                }

            }
            button_submit.setOnClickListener {
                if (task.currentSelection != -1) {
                    submitAnswer(task)
                }
            }
        }

    }

    private fun clearSelectionListeners() {
        if (!optionLayoutArray.isNullOrEmpty()){
            for (i in 0 until optionLayoutArray.size) {
                optionLayoutArray[i].setOnClickListener {null
                }

            }
        }
        button_submit.setOnClickListener {null }

    }

    private fun inflateOptions(task : TaskData, option : HbTextView , optionNumber : Int){

        if (task.options != null && task.options.size > optionNumber){ //if option exists
            if (!task.options[optionNumber].logo.trim().isEmpty()){

                optionImageArray[optionNumber].visibility = View.VISIBLE
                if(applicationContext!=null) {

                    ImageHandling.loadRemoteImage(task.options[optionNumber].logo, optionImageArray[optionNumber],-1,-1,applicationContext)
                }
            }else{

                optionImageArray[optionNumber].visibility = View.GONE

            }
            optionLayoutArray[optionNumber].visibility = View.VISIBLE
            option.visibility = View.VISIBLE
            option.text = " ${task.options[optionNumber].option}"
        }
        else{
            optionLayoutArray[optionNumber].visibility = View.GONE
           option.visibility = View.GONE  //for third and fourth, remove view from there

        }
    }

    private fun inflateSingleOption(option:String, optionNumber:Int){
        optionArray[optionNumber].visibility = View.VISIBLE
        optionArray[optionNumber].text = " ${alphabetArray[optionNumber]}. $option"
    }

    private fun inflateToolbar(campaignData: CampaignData){

        if(campaignData.logo==null || campaignData.logo.isEmpty()){
            iv_banner.setImageResource(R.drawable.error_image)
        }else {
            if(applicationContext!=null) {

                ImageHandling.loadRemoteImage(campaignData.logo, iv_banner,R.drawable.error_image,R.drawable.error_image,applicationContext)

            }
        }
        tv_title.text = campaignData.title
        tv_banner.text = campaignData.description
        //showBanner()
    }
    private fun inflateQuestionRV(taskList: ArrayList<Any>, campaignData: CampaignData){
        //add footer for terms and conditions view
        val footer = TaskData()
        footer.footer = true
        footer.footerText = campaignData.termsAndConditions
        taskList.add(footer)
        //setting up task list with the recycler view
        //questionListAdapter.setTaskList(taskList)
        questionListAdapter.setTaskList(contentList)
        questionListAdapter.notifyDataSetChanged()
        //default ques
        setDefaultQuestion(taskList,campaignData)
        hideShimmerLoader()

    }

    private fun setDefaultQuestion(taskList: ArrayList<Any>, campaignData: CampaignData){
        try {
            calendar = DateTimeUtil.adjustCalender(this)
            if (!taskList.isNullOrEmpty()) {
                var found = false
                var last = 0
                var lastScrollIndex = 0
                for (i in 0 until taskList.size) {

                    if (taskList[i] is TaskData) {
                        val task = taskList[i] as TaskData
                        if (calendar!=null && task.startTime!=null && (calendar.timeInMillis > (task.startTime * 1000))) {
                            task.clickable = true
                        }
                        if (task.type == "question" && task.canAnswer && task.status == null && (calendar.timeInMillis > (task.startTime * 1000))) {
                            found = true
                            listener.OnItemClickListener(task,false)
                            layoutManager.scrollToPositionWithOffset(i, 0)
                            last = i
                            break
                        }
                        if (calendar!=null && task.startTime!=null && calendar.timeInMillis > (task.startTime * 1000)) lastScrollIndex = i

//                        if ((task.startTime * 1000) < calendar.timeInMillis && (task.endTime * 1000) > calendar.timeInMillis) {
//                            task.clickable = true
//                        }
                    }

                }
                if (!found) {
                    showBanner()
                    if(lastScrollIndex > 0) layoutManager.scrollToPositionWithOffset(lastScrollIndex, 0)
                    if (!campaignData.description.isNullOrEmpty()) {
                        tv_banner.text = campaignData.description
                    } else {
                        tv_banner.text = AppUtils.getConfig(this).contestConfiguration.emptyDescriptionText
                    }
                }
                else if (last+1<taskList.size){
                    for (i in last until taskList.size-1){
                        if (taskList[i] is TaskData) {
                            val task = taskList[i] as TaskData
                            if (calendar!=null && task.startTime!=null &&(task.startTime * 1000) < calendar.timeInMillis && (task.endTime * 1000) > calendar.timeInMillis) {
                                task.clickable = true
                            }
                        }
                    }
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun selectAnswer(optionNumber: Int, answerType:Int){
        when{
            answerType==INCORRECTANSWER -> {
                optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_background_red)
                optionArray[optionNumber].setTextColor(resources.getColor(R.color.white))
            }
            answerType ==CORRECTANSWER ->{
                optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_background_green)
                optionArray[optionNumber].setTextColor(resources.getColor(R.color.white))
            }
            answerType ==SELECTEDANSWER ->{
                optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_background_yellow)
                //optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_stroke_red)
                optionArray[optionNumber].setTextColor(resources.getColor(R.color.black))
            }
            answerType ==CURRENTSELECTED ->{
                //optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_background_yellow)
                optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.rounded_stroke_red)
                optionArray[optionNumber].setTextColor(resources.getColor(R.color.black))
            }
            else ->{
                optionLayoutArray[optionNumber].background = resources.getDrawable(R.drawable.spinner_border)
                optionArray[optionNumber].setTextColor(resources.getColor(R.color.black))
            }
        }
    }



    private fun getCampaignDetails(campaignID:Int){

        var locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID,0)

        val url:String = UrlConstant.CAMPAIGN_DETAIL + campaignID+"?locationId="+locationId;
        val campaignHttpAgent = SimpleHttpAgent(this,
                url,
                ResponseListener { responseObject ->


                    if (!responseObject.campaignData.isNullOrEmpty() && !campaignType.isNullOrEmpty()) {
                            if (campaignType.equals(ApplicationConstants.TEMPLATE_TYPE_QNA,true)){

                                try {
                                    swipeRefreshLayout.isRefreshing = false
                                    hideOrderCampaign()
                                    logo = responseObject.campaignData[0].logo
                                    insertMilestones(responseObject.campaignData[0])
                                    inflateToolbar(responseObject.campaignData[0])
                                    inflateQuestionRV(contentList, responseObject.campaignData[0])
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            }
                            else{
                                inflateOrderCampaign(responseObject.campaignData[0])
                            }

                    } else{
                        showErrorDialog("Some Error Occurred")
                    }
                },
                ContextErrorListener { errorCode, error, errorResponse ->

                    hideShimmerLoader()
                    swipeRefreshLayout.isRefreshing = false
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        //some error
                        showNoNetFragment(RetryListener { getCampaignDetails(campaignID) }, CancelListener {
                            onBackPressed()
                        })
                    }else{
                        if(error!=null && !error.isEmpty()){
                            showErrorDialog(error)
                        }else{
                            showErrorDialog("Some Error Occurred")
                        }
                    }
                },
                CampaignResponse::class.java
        )
        campaignHttpAgent.get()
    }

    private fun inflateOrderCampaign(campaignData: CampaignData) {
        try {

            setOrderViewListeners()
            var isMilestoneOpen = true
            //since milestone is open by default
            expandMilestone()
            var isRewardOpen = false
            shimmer_view_container.visibility = View.GONE
            cl_container.visibility = View.GONE
            cl_order_campaign.visibility = View.VISIBLE

            var milestoneList = campaignData.milestones.milestoneData as ArrayList
//        for (x in 0 until 10){
//            var milestoneData = MilestoneData()
//            milestoneData.id = x
//            milestoneData.title = "milestone ${x+1}"
//            milestoneData.requiredTaskCount = x
//            if (x < 4){
//                milestoneData.isUnlocked = true
//            }
//            milestoneList.add(milestoneData)
//        }
            var terms: String = campaignData.termsAndConditions
            if (terms == null || terms.trim().isEmpty()) {
                terms = "No Terms And Condition"
            }
            wv_terms.loadData(terms, "text/html; charset=UTF-8", null)
            var taskDataList = campaignData.tasks.taskData as ArrayList<TaskData>
            var taskDataMap = taskDataList.map{it.id to it}.toMap()
            val rewardClickListener = object : RewardActivity.RewardUnlock {
                override fun onRewardClick(reward: Reward) {
                    unlockReward(reward)
                }
            }
            val milestoneListAdapter = MilestoneListAdapter(milestoneList, taskDataList,taskDataMap, this,rewardClickListener,campaignData.campaignRewardType,isCampaignExpired(campaignData.endDate), campaignData.endDate, this.supportFragmentManager)
            rv_milestone.adapter = milestoneListAdapter
            rv_milestone.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            srl_contest_milestone.isRefreshing = false
            iv_milestone_dropdown.setOnClickListener {
                if (isMilestoneOpen) {
                    isMilestoneOpen = !isMilestoneOpen
                    closeMilestone()
                } else {
                    isMilestoneOpen = !isMilestoneOpen
                    expandMilestone()
                }

            }
            isRewardOpen = false
            closeReward()
            iv_terms_dropdown.setOnClickListener {
                if (isRewardOpen) {
                    isRewardOpen = !isRewardOpen
                    closeReward()
                } else {
                    isRewardOpen = !isRewardOpen
                    openReward()
                }

            }
            swipeRefreshLayout.isRefreshing = false
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun openReward(){
        wv_terms.visibility = View.VISIBLE
        iv_terms_dropdown.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_drop_up))
    }

    private fun closeReward(){
        wv_terms.visibility = View.GONE
        iv_terms_dropdown.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_drop_down))
    }

    private fun setOrderViewListeners(){
        iv_toolbar_back.setOnClickListener { super.onBackPressed() }
        //iv_toolbar_share.setOnClickListener { //yet to be decided }
    }

    private fun expandMilestone(){
        milestone_header.visibility = View.VISIBLE
        rv_milestone.visibility = View.VISIBLE
        iv_milestone_dropdown.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_drop_up))
    }

    private fun closeMilestone(){
        milestone_header.visibility = View.GONE
        rv_milestone.visibility = View.GONE
        iv_milestone_dropdown.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_drop_down))
    }

    private fun hideOrderCampaign(){
        shimmer_view_container.visibility = View.GONE
        cl_container.visibility = View.VISIBLE
        cl_order_campaign.visibility = View.GONE
    }

    private fun submitAnswer(task: TaskData){
        try {
            showProgressLoader()

            val url: String = UrlConstant.CAMPAIGN_SUBMIT_ANSWER
            val submitHttpAgent = SimpleHttpAgent(
                    this,
                    url,
                    ResponseListener { responseObject ->
                        loaderFragment.dismiss()
                        button_submit.isEnabled = false
                        getCampaignDetails(campaignID)
                        AppUtils.showToast(AppUtils.getConfig(this).contestConfiguration.answerSubmissionSuccess,true,1)

//                        try{
//
//                            val map = HashMap<String, Any>()
//                            map[CleverTapEvent.PropertiesNames.answer] = task.options[task.currentSelection].option
//                            map[CleverTapEvent.PropertiesNames.answer_id] = task.options[task.currentSelection].id
//                            map[CleverTapEvent.PropertiesNames.question_id] = task.typeId
//                            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.answer_submit, map, applicationContext)
//
//                        }catch (e: Exception)
//                        {
//                            e.printStackTrace()
//                        }

//                    task.canAnswer = false
//                    task.submittedAnswer = task.options[task.currentSelection]
//                    questionListAdapter.notifyDataSetChanged()
//                    loaderFragment.dismiss()
                    },
                    ContextErrorListener { errorCode, error, errorResponse ->
                        //error
                        loaderFragment.dismiss()
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            //some error
                            showNoNetFragment(RetryListener { getCampaignDetails(campaignID) }, CancelListener {
                                onBackPressed()
                            })
                            //AppUtils.showToast("No Internet Connection!",true,0)
                        }else{
                            if(error!=null && !error.isEmpty()){
                                AppUtils.showToast(error,false,0)
                            }else{
                                AppUtils.showToast("Some Error Occurred",true,0)
                            }
                        }

                    },
                    SubmitResponse::class.java
            )
            val payload = HashMap<String, Any>()
            payload.put("question_id", task.typeId)
            payload.put("option_id", task.options[task.currentSelection].id)
            payload.put("task_id", task.id)
            submitHttpAgent.post(payload, HashMap<String, JsonSerializer<Any>>())
        } catch (exception:Exception){
            exception.printStackTrace()
        }
    }

    private fun getTimeLeft(eventTime:Long): String {
        var calendar = DateTimeUtil.adjustCalender(this)
        if (calendar.timeInMillis>= (eventTime*1000)){
            return AppUtils.getConfig(this).contestConfiguration.getQuestionExpired()
        } else{
             return "${AppUtils.getConfig(this).contestConfiguration.getQuestionExpiresAt()} ${DateTimeUtil.getDateString(eventTime*1000)}"
        }
    }

    private fun insertMilestones( campaignData: CampaignData){
        contentList.clear()
        var milestoneList:ArrayList<MilestoneData> = campaignData.milestones.milestoneData as ArrayList<MilestoneData>

            var taskList: ArrayList<TaskData> = campaignData.tasks.taskData as ArrayList<TaskData>
            contentList.addAll(taskList)

            //check and count milestones applicable
        if (!milestoneList.isNullOrEmpty()) {
            for (i in 0 until milestoneList.size){
                val mileStone = milestoneList[i]
                val mTaskIDs = mileStone.taskIds.split(",").map { it -> it.toInt() }.toIntArray()
                //mTaskIDs.sort() todo new logic

                for(j in 0 until contentList.size){

                    if(contentList.get(j) is TaskData) {

                        val taskData: TaskData = contentList.get(j) as TaskData
                        if (mileStone.isUnlocked){
                            if (taskData.id == mileStone.lastTaskId){

                                contentList.add(j+1,mileStone)

                                break
                            }
                        } else if (taskData.id == mTaskIDs.last()){

                            contentList.add(j+1,mileStone)

                            break
                        }
                    }
                }

            }
        }
    }

//    private fun insertMilestones( campaignData: CampaignData){
//        var milestoneList:ArrayList<MilestoneData> = campaignData.milestones.milestoneData as ArrayList<MilestoneData>
//        if (!milestoneList.isNullOrEmpty()) {
//            var taskList: ArrayList<TaskData> = campaignData.tasks.taskData as ArrayList<TaskData>
//            var contentList = ArrayList<Any>()
//            contentList.addAll(taskList)
//            var milestoneCount = 0
//            var completedTaskIDs = ArrayList<Int>()
//            for (i in 0 until taskList.size) {
//                if (i<3){
//                    taskList[i].status = STATUSPASS  //remove
//                }
//                if (taskList[i].status == STATUSPASS){
//                    completedTaskIDs.add(taskList[i].id)
//                }
//            }
//            //check and count milestones applicable
//            for (i in 0 until milestoneList.size){
//                val mileStone = milestoneList[i]
//                val mTaskIDs = mileStone.taskIds.split(",").map { it -> it.toInt() }.toIntArray()
//                mTaskIDs.sort()
//                var completedCount = 0;
//                for (c in mTaskIDs.size-1 downTo 0){
//
//                    val foundIndex = completedTaskIDs.binarySearch (mTaskIDs[c])
//                    if (foundIndex>=0){
//                        if (++completedCount>=mileStone.requiredTaskCount){
//                            break
//                        }
//                    }
//
//                }
//                if (completedCount>=mileStone.requiredTaskCount) {
//                    mileStone.isUnlocked = true
//                }
//                contentList.add((mTaskIDs.last()+i),mileStone)
//
//            }
//        }
//    }

    private fun showErrorDialog(message : String){
        errorPopUp = ErrorPopFragment
                .newInstance(message, "Retry",true,ApplicationConstants.GENERAL_ERROR, object : ErrorPopFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        if (campaignID!=-1){
                            getCampaignDetails(campaignID)
                        }
                    }

                    override fun onNegativeInteraction() {
                        errorPopUp.dismiss()
                        onBackPressed()
                    }
                })
        errorPopUp.isCancelable = false
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(errorPopUp,"Error in loading").commitAllowingStateLoss()
    }

    private fun showNoNetFragment(retryListener: RetryListener, cancelListener: CancelListener) {

        val fragment = NoNetFragment.newInstance(retryListener,cancelListener)
        fragment.isCancelable = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(fragment, "exit").commit()
    }

    private fun setBannerImageInView(img_url: String) {
        iv_alt_banner.visibility = View.VISIBLE
        tv_banner.visibility = View.GONE
        cl_question.visibility = View.GONE
        try {

            iv_alt_banner.postDelayed(Runnable {  if (this!= null) {
                val width = iv_alt_banner.getMeasuredWidth()
                val height = width / 2
                val lp = iv_alt_banner.getLayoutParams()
                lp.height = height
                iv_alt_banner.setLayoutParams(lp)
                iv_alt_banner.requestLayout()
                iv_alt_banner.setVisibility(View.VISIBLE)

                if (img_url == null || img_url.isEmpty()) {
                    iv_alt_banner.setBackgroundResource(R.drawable.error_image)
                } else {

                    ImageHandling.loadRemoteImage(img_url, iv_alt_banner,R.drawable.error_image,-1,applicationContext)

                }
            }  }, 200);


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun hideBannerImage(){
        iv_alt_banner.visibility = View.GONE
        tv_banner.visibility = View.GONE
        cl_question.visibility = View.VISIBLE
    }

    private fun showBanner(){

        cl_question.visibility = View.GONE
        tv_banner.visibility = View.VISIBLE
        setBannerImageInView(logo)
    }

    private fun hideBanner(){

        cl_question.visibility = View.VISIBLE
        tv_banner.visibility = View.GONE
        hideBannerImage()
    }

    private fun goToRewardsScreen(){
        val intent  = Intent(this,RewardActivity::class.java)
        startActivity(intent)
    }

    private fun unlockReward(reward:Reward){
        try {

            val url: String = UrlConstant.UNLOCK_REWARD
            val submitHttpAgent = SimpleHttpAgent(
                    this,
                    url,
                    ResponseListener { responseObject ->
                        //reward unlocked
                        Log.d("unlock reward", "reward unlocked")
                        moveToRewardDetails(reward)

                    },
                    ContextErrorListener { errorCode, error, errorResponse ->
                        //error

                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            //some error
                            showNoNetFragment(RetryListener { unlockReward(reward) }, CancelListener {
                                onBackPressed()
                            })
                        }else{
                            if(error!=null && !error.isEmpty()){
                                AppUtils.showToast(error,false,0)
                            }else{
                                AppUtils.showToast("Some Error Occurred",true,0)
                            }
                        }

                    },
                    UnlockRewardResponse::class.java
            )
            val payload = HashMap<String, Any>()
            payload.put("id", reward.id)
            submitHttpAgent.post(payload, HashMap<String, JsonSerializer<Any>>())
        } catch (exception: java.lang.Exception){
            exception.printStackTrace()
        }
    }

    private fun moveToRewardDetails(reward: Reward){
        val intent = Intent(this,RewardViewActivity::class.java)
        intent.putExtra("reward",reward)
        startActivityForResult(intent,REQ_CODE_UNLOCK)
    }

    private fun isCampaignExpired(campaignEndTime :Long):Boolean{
        val now = DateTimeUtil.adjustCalender(this)
        val estTime = Calendar.getInstance()
        estTime.timeInMillis = campaignEndTime * 1000
        return (now>estTime)
    }



    interface ItemClick{
        fun OnItemClickListener(task:TaskData,userClick:Boolean)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_UNLOCK && resultCode == UNLOCK_SUCCESS){
            if (campaignID!=-1) {
               getCampaignDetails(campaignID)
            }
        }
    }
}