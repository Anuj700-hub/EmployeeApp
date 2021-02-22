package com.hungerbox.customer.occupancy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.anastr.speedviewlib.components.Section
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.fragment.FeedbackFragment
import com.hungerbox.customer.prelogin.activity.ParentActivity
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.ErrorPopFragment
import kotlinx.android.synthetic.main.activity_occupancy.*

class OccupancyActivity : ParentActivity() {

    private var occupancyItems : ArrayList<OccupancyItem>? = null
    private var occupancyItemLocationId : Long = 0
    private lateinit var toolbar: Toolbar
    private var isWashroomSelected : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occupancy)

        toolbar = findViewById(R.id.tb_occupancy)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_toolbar_title).text = "Occupancy"

        apiTag = System.currentTimeMillis().toString()

        iv_washroom_feedback.setOnClickListener { showWashroomFeedbackPopup() }

        srl_occupancy.setOnRefreshListener {
            updateLayout(isWashroomSelected)
        }

        initializeGauge()
        initializeSwitchListeners()
        updateLayout(false)
    }

    private fun showWashroomFeedbackPopup(){
        val fragment: FeedbackFragment = FeedbackFragment.newInstance( 0, "cafeteria", occupancyItemLocationId, object : FeedbackFragment.OnFragmentInteractionListener {
            override fun onFragmentInteraction() {
                AppUtils.showToast("Thank you for your feedback", true, 1)
            }

            override fun dismissPopup() {}
        })

        fragment.isCancelable = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(fragment, "feedback")
                .commitAllowingStateLoss()
    }

    private fun initializeGauge(){
        val gaugeWidth = congestion_view.speedometerWidth - 30
        congestion_view.clearSections()
        congestion_view.addSections(
                Section(0f, .5f, ContextCompat.getColor(this, R.color.green), gaugeWidth)
                , Section(.5f, .8f, ContextCompat.getColor(this, R.color.yellow), gaugeWidth)
                , Section(.8f, 1f, ContextCompat.getColor(this, R.color.red), gaugeWidth)
        )
    }

    private fun initializeSwitchListeners(){
        tv_lift.setOnClickListener {
            tv_lift.visibility = View.GONE
            bt_lift.visibility = View.VISIBLE
            tv_washroom.visibility = View.VISIBLE
            bt_washroom.visibility = View.GONE
            updateLayout(false)
            isWashroomSelected = false
        }

        tv_washroom.setOnClickListener{
            tv_lift.visibility = View.VISIBLE
            bt_lift.visibility = View.GONE
            tv_washroom.visibility = View.GONE
            bt_washroom.visibility = View.VISIBLE
            updateLayout(true)
            isWashroomSelected = true
        }
    }

    private fun showProgress(){
        rl_progress.visibility = View.VISIBLE
        ll_spinner.visibility = View.GONE
        rl_gauge.visibility = View.GONE
        rl_live_occupancy.visibility = View.GONE
        ll_bottom.visibility = View.GONE
    }

    private fun hideProgress(){
        rl_progress.visibility = View.GONE
        ll_spinner.visibility = View.VISIBLE
        rl_gauge.visibility = View.VISIBLE
        rl_live_occupancy.visibility = View.VISIBLE
        ll_bottom.visibility = View.VISIBLE
    }

    private fun updateLayout(forWashroom : Boolean){

        srl_occupancy.isRefreshing = false
        if(forWashroom)
            tv_select_heading.text = "Select Washroom"
        else
            tv_select_heading.text = "Select Lift Lobby"

        showProgress()

        if(forWashroom) {
            occupancy_title.text = "Washroom Occupancy"
            iv_washroom_feedback.visibility = View.VISIBLE
        }
        else{
            occupancy_title.text = "Lift Occupancy"
            iv_washroom_feedback.visibility = View.GONE
        }

        var url = UrlConstant.OCCUPANCY_URL + "?locationId=" + SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0) + "&location_type="

        url += if(forWashroom)
            ApplicationConstants.OCCUPANCY_WASHROOM_TYPE
        else
            ApplicationConstants.OCCUPANCY_LOBBY_TYPE

        val occupancyResponseSimpleHttpAgent = SimpleHttpAgent(
                this,
                url,
                {
                    responseObject ->

                    hideProgress()

                    if (responseObject == null || responseObject.occupancyItems?.size==0)
                        showError(forWashroom,null)
                    else
                        updateListAndUI(responseObject.occupancyItems)

                },
                { _, msg, _ ->
                    hideProgress()
                    showError(forWashroom,msg)
                },
                OccupancyResponse::class.java
        )
        occupancyResponseSimpleHttpAgent.get(apiTag)
    }

    private fun showError(forWashroom : Boolean,_msg:String?){

        var msg = _msg
        if(msg==null){
            msg = if(forWashroom)
                "No washrooms available for congestion tracking"
            else
                "No lift lobbies available for congestion tracking"
        }

        scroll_view.visibility = View.GONE
        tv_error_message.visibility = View.VISIBLE
        tv_error_message.text = msg

        val orderErrorHandleDialog = ErrorPopFragment.newInstance(
                msg, "Ok", true, ApplicationConstants.GENERAL_ERROR,
                object : ErrorPopFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {

                    }

                    override fun onNegativeInteraction() {

                    }
                })
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(orderErrorHandleDialog, "error")
                .commitAllowingStateLoss()
    }

    private fun updateListAndUI(items : ArrayList<OccupancyItem>?){


        this.occupancyItems = items

        if(occupancyItems != null && occupancyItems!!.size > 0){

            scroll_view.visibility = View.VISIBLE
            tv_error_message.visibility = View.GONE

            val occupancyArrayAdapter: ArrayAdapter<OccupancyItem> = ArrayAdapter(this, R.layout.simple_spinner_dropdown_accent, occupancyItems!!)
            occupancyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            sp_occupancy.adapter = occupancyArrayAdapter

            sp_occupancy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (occupancyItems != null && position < occupancyItems!!.size) {
                        setLocationId(occupancyItems!![position].locationId)
                        updateGauge(occupancyItems!![position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            var defLocation = getLocationId()
            if(defLocation == -1L){
                sp_occupancy.setSelection(0)
                setLocationId(occupancyItems!![0].locationId)
            }
            else{
                var i = 0
                var found = false
                while(i < occupancyItems!!.size){
                    if(occupancyItems!![i].locationId == defLocation){
                        found = true
                        break
                    }
                    i++
                }

                if(found){
                    sp_occupancy.setSelection(i)
                    setLocationId(occupancyItems!![i].locationId)
                }
                else{
                    sp_occupancy.setSelection(0)
                    setLocationId(occupancyItems!![0].locationId)
                }
            }
        }
    }

    private fun getLocationId() : Long{
        return if(occupancyItems!![0].type == ApplicationConstants.OCCUPANCY_WASHROOM_TYPE){
            SharedPrefUtil.getLong(ApplicationConstants.LAST_VISITED_WASHROOM, -1)
        }
        else{
            SharedPrefUtil.getLong(ApplicationConstants.LAST_VISITED_LIFT, -1)
        }
    }

    private fun setLocationId(loc : Long){
        return if(occupancyItems!![0].type == ApplicationConstants.OCCUPANCY_WASHROOM_TYPE){
            SharedPrefUtil.putLong(ApplicationConstants.LAST_VISITED_WASHROOM, loc)
        }
        else{
            SharedPrefUtil.putLong(ApplicationConstants.LAST_VISITED_LIFT, loc)
        }
    }

    private fun updateGauge(occupancyItem : OccupancyItem){

        this.occupancyItemLocationId = occupancyItem.locationId

        congestion_text.setTextColor(ContextCompat.getColor(this, occupancyItem.getCongestionColor()))
        congestion_text.text = occupancyItem.getCongestionString()

        val congestionMessage : String = getCongestionMessage(occupancyItem)

        if(congestionMessage.isNotEmpty()) {
            congestion_message.text = congestionMessage
            congestion_message.visibility = View.VISIBLE
        }
        else{
            congestion_message.visibility = View.GONE
        }

        when {
            occupancyItem.getCongestionLevel() == OccupancyItem.Companion.CongestionLevel.LOW -> {
                congestion_view.speedTo(25F, 500)
            }
            occupancyItem.getCongestionLevel()== OccupancyItem.Companion.CongestionLevel.MEDIUM -> {
                congestion_view.speedTo(65F, 500)
            }
            else -> {
                congestion_view.speedTo(90F, 500)
            }
        }
    }

    private fun getCongestionMessage(occupancyItem: OccupancyItem):String{

        var congestionMessage = ""
        val config : Config = AppUtils.getConfig(this.applicationContext)

        if(config.occupancy != null){

            var occupancySpace : Config.Occupancy.OccupancySpace = if(occupancyItem.type == ApplicationConstants.OCCUPANCY_WASHROOM_TYPE) config.occupancy.washroom else config.occupancy.lift_lobby

            if(occupancySpace?.message != null){

                congestionMessage = if(occupancyItem.getCongestionLevel() == OccupancyItem.Companion.CongestionLevel.LOW && occupancySpace.message.low != null){
                    occupancySpace.message.low
                }
                else if(occupancyItem.getCongestionLevel()== OccupancyItem.Companion.CongestionLevel.MEDIUM && occupancySpace.message.medium != null){
                    occupancySpace.message.medium
                }
                else if(occupancyItem.getCongestionLevel()== OccupancyItem.Companion.CongestionLevel.HIGH && occupancySpace.message.high != null){
                    occupancySpace.message.high
                }
                else{
                    ""
                }
            }
        }

        return congestionMessage;
    }
}
