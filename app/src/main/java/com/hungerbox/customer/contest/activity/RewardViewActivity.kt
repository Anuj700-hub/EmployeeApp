package com.hungerbox.customer.contest.activity

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.gson.JsonSerializer
import com.hungerbox.customer.R
import com.hungerbox.customer.contest.model.Reward
import com.hungerbox.customer.contest.model.UnlockRewardResponse
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.fragment.NoNetFragment
import com.hungerbox.customer.order.listeners.CancelListener
import com.hungerbox.customer.order.listeners.RetryListener
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.ImageHandling
import com.hungerbox.customer.util.view.ScratchRelativeLayoutView
import kotlinx.android.synthetic.main.activity_reward_view.*
import kotlinx.android.synthetic.main.activity_reward_view.cl_parent
import kotlinx.android.synthetic.main.reward_layout_hidden.*
import java.lang.Exception
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.TextView.BufferType
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.View
import java.text.DateFormat
import java.text.SimpleDateFormat


class RewardViewActivity : AppCompatActivity() {

    private lateinit var reward: Reward
    private val scratchStrokeWidth = 20
    private val autoRevealPercent = 0.7
    var revealed = false
    var scratched = false
    var rewardUnlockedfromServer = false
    private val UNLOCK_SUCCESS = 235
    //view variables


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_view)
        reward = intent.getSerializableExtra("reward") as Reward

        cl_parent.background = ColorDrawable(resources.getColor(R.color.black_transparent))
        inflateScratchView()
    }

    private fun inflateScratchView(){
        scratch_card.setStrokeWidth(scratchStrokeWidth)
        if (reward.isUnlocked){
            scratch_card.setScratchView(ScratchRelativeLayoutView.ScratchedState.REVEALED)
            revealed = true
        } else {
            scratch_card.setScratchView(R.layout.reward_layout_scratch)
        }
        try {
            tv_date.text = DateTimeUtil.getDateStringFromDateString(reward.expiryTime,"yyyy-mm-dd HH:mm:ss","dd MMM yyyy")
            tv_reward.text = "Rs ${reward.value}"
            revealRewardDetails()
            if (!reward.logo.isNullOrEmpty()) {
                ImageHandling.loadRemoteImage(reward.logo, iv_reward_logo, R.drawable.ic_rewards_trophy, -1, applicationContext)
            }
            if(reward.isVoucher){
                initVoucherView()
            }

        } catch (e :Exception){
            e.printStackTrace()
        }
        iv_close.setOnClickListener {
            close()
        }

        scratch_card.setRevealListener(object : ScratchRelativeLayoutView.IRevealListener {
            override fun onRevealed(tv: ScratchRelativeLayoutView) {
                if (!reward.isUnlocked){
                    //unlockReward()
                    revealRewardDetails()
                }

            }

            override fun onRevealPercentChangedListener(siv: ScratchRelativeLayoutView, percent: Float) {
                // on percent change
                scratched = true
                if (!revealed){
                    if (percent>autoRevealPercent){
                        revealed = true
                        scratch_card.reveal()
                    }
                }
            }
        })
    }
    private fun revealRewardDetails(){
        try {
            if (reward.isUnlocked) {
                tv_detail.visibility = View.VISIBLE
                tv_detail.text = reward.campaignTitle
                tv_reference.visibility = View.VISIBLE
                tv_reference.text = reward.requiredTaskCount.toString()
            }
        }  catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun unlockReward(){
        try {

            val url: String = UrlConstant.UNLOCK_REWARD
            val submitHttpAgent = SimpleHttpAgent(
                    this,
                    url,
                    ResponseListener { responseObject ->
                        //reward unlocked
                        Log.d("unlock reward", "reward unlocked")
                        rewardUnlockedfromServer = true
                        //revealRewardDetails()

                    },
                    ContextErrorListener { errorCode, error, errorResponse ->
                        //error

                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            //some error
                            showNoNetFragment(RetryListener { unlockReward() }, CancelListener {
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
        } catch (exception:Exception){
            exception.printStackTrace()
        }
    }

    private fun initVoucherView(){
        tv_reward.visibility = View.GONE
        tv_voucher.visibility = View.VISIBLE
        tv_voucher.text = reward.voucherCode
        horizontal_guide_top.setGuidelinePercent(0.06f)
        horizontal_guide_middle.setGuidelinePercent(0.5f)
        horizontal_guide_bottom.setGuidelinePercent(0.94f)
        setVoucherText(tv_reward_label)
    }

    private fun setVoucherText(textView: TextView){
        val text = "You Won Rs. ${reward.value}\ncash back voucher"
        val span = SpannableString(text)
        val startIndex = 7
        val endIndex = 12+(reward.value.toString().length)
        span.setSpan(TextAppearanceSpan(this, R.style.MediumTextView), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorAccent)), startIndex, endIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        textView.text = span

    }

    private fun showNoNetFragment(retryListener: RetryListener, cancelListener: CancelListener) {

        val fragment = NoNetFragment.newInstance(retryListener,cancelListener)
        fragment.isCancelable = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(fragment, "exit").commit()
    }
    private fun close(){
        if (revealed) {
            setResult(UNLOCK_SUCCESS)
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }

    }

    override fun onBackPressed() {
        close()
    }
}
