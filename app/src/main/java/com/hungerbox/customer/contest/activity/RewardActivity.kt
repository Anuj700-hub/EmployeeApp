package com.hungerbox.customer.contest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.JsonSerializer

import com.hungerbox.customer.R
import com.hungerbox.customer.contest.adapter.RewardListAdapter
import com.hungerbox.customer.contest.model.Reward
import com.hungerbox.customer.contest.model.RewardData
import com.hungerbox.customer.contest.model.RewardResponse
import com.hungerbox.customer.contest.model.UnlockRewardResponse
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.activity.GlobalActivity
import com.hungerbox.customer.order.fragment.NoNetFragment
import com.hungerbox.customer.order.listeners.CancelListener
import com.hungerbox.customer.order.listeners.RetryListener
import com.hungerbox.customer.util.AppUtils
import kotlinx.android.synthetic.main.activity_reward.*
import java.util.ArrayList


class RewardActivity : AppCompatActivity() {

    private lateinit var rvRewardContainer: androidx.recyclerview.widget.RecyclerView
    private lateinit var tvRewardAmount: TextView
    private lateinit var rlContainer: RelativeLayout
    private lateinit var rlNoReward: RelativeLayout
    private lateinit var rewardListAdapter: RewardListAdapter
    private lateinit var shimmerLayout : ShimmerFrameLayout
    private val REQ_CODE_UNLOCK = 234
    private val UNLOCK_SUCCESS = 235

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)
        tvRewardAmount = findViewById(R.id.tv_reward_amount)
        rvRewardContainer = findViewById(R.id.rv_reward_container)
        rlContainer = findViewById(R.id.rl_container)
        rlNoReward = findViewById(R.id.rl_no_reward)
        shimmerLayout = findViewById(R.id.shimmer_view_container)
        iv_back.setOnClickListener { onBackPressed() }
        getRewardsFromServer()
    }

    private fun getRewardsFromServer() {

        val url: String = UrlConstant.CAMPAIGN_REWARD
        shimmerLayout.visibility = View.VISIBLE
        val rewardSimpleHttpAgent = SimpleHttpAgent(applicationContext,
                url,
                ResponseListener { responseObject ->

                    shimmerLayout.visibility = View.GONE

                    if (responseObject != null && responseObject.userRewards!= null) {
                        rlContainer.visibility = View.VISIBLE
                        rlNoReward.visibility = View.GONE
                        setRewardData(responseObject)
                    } else {
                        rlContainer.visibility = View.GONE
                        rlNoReward.visibility = View.VISIBLE
                    }

                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    shimmerLayout.visibility = View.GONE
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        showNoNetFragment(RetryListener { getRewardsFromServer() }, CancelListener {
                            onBackPressed()
                        })
                    } else {
                        if (error != null && !error.isEmpty()) {
                            AppUtils.showToast(error, false, 0)
                        } else {
                            AppUtils.showToast("Some Error Occurred", true, 0)
                        }
                    }

                },
                RewardResponse::class.java
        )
        rewardSimpleHttpAgent.get()

    }


    private fun setRewardData(rewardData: RewardResponse) {

        try {
            tvRewardAmount.text = "₹ ${rewardData.totalValue}"

            if (rewardData.userRewards != null && rewardData.userRewards.size > 0) {
                var heightPixels = 0
                if (GlobalActivity.displayMetrics!=null){
                    heightPixels = (GlobalActivity.displayMetrics.widthPixels*0.45).toInt()
                }
                val rewardClickListener = object :RewardUnlock{
                    override fun onRewardClick(reward: Reward) {
                        if (!reward.isUnlocked) {
                            unlockReward(reward)
                        }
                    }
                }
                rewardListAdapter = RewardListAdapter(this, rewardData.userRewards as ArrayList<Reward>,heightPixels,rewardClickListener)
                rvRewardContainer.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
                rvRewardContainer.adapter = rewardListAdapter
                rewardListAdapter.notifyDataSetChanged()
            } else{
                rlNoReward.visibility = View.VISIBLE;
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
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


    private fun showNoNetFragment(retryListener: RetryListener, cancelListener: CancelListener) {

        val fragment = NoNetFragment.newInstance(retryListener, cancelListener)
        fragment.isCancelable = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(fragment, "exit").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_UNLOCK && resultCode == UNLOCK_SUCCESS){
            getRewardsFromServer()
        }
    }

    public interface RewardUnlock{
        fun onRewardClick(reward: Reward)
    }
}
