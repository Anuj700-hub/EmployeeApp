package com.hungerbox.customer.order.activity

import `in`.juspay.juspaysafe.BrowserCallback
import `in`.juspay.juspaysafe.BrowserParams
import `in`.juspay.juspaysafe.JuspaySafeBrowser
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import com.hungerbox.customer.R
import com.hungerbox.customer.model.JPWalletRechargeResponse
import com.hungerbox.customer.model.PaymentMethod
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.view.GenericPopUpFragment
import kotlinx.android.synthetic.main.activity_jpweb_view_new.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class JPWebViewActivityNew : AppCompatActivity(){

    companion object {
        @JvmStatic val SUCCESS = 1010
        @JvmStatic val FAILURE = 1011
    }
    private var paymentUrl: String? = null
    private var returnUrl:String? = null
    private var shown = false
    private var transactionId: String? = null
    private var amount = 0
    private var walletId = ""
    private var walletName = ""
    private var paymentMethodToLink: PaymentMethod? = null
    private var walletCurrentBalance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_jpweb_view_new)

        iv_back.setOnClickListener { onBackPressed() }
        amount = intent.getDoubleExtra("amountId",0.0).toInt()
        walletId = intent.getStringExtra("walletId")
        walletCurrentBalance = intent.getDoubleExtra("walletCurrentBalance", 0.0)
        walletName = intent.getStringExtra("walletName")
        paymentMethodToLink = intent.getSerializableExtra(ApplicationConstants.PAYMENT_METHOD) as PaymentMethod

        currentBalance.text = walletCurrentBalance.toString()
        remainingAmount.text = amount.toString()

        amountBox.setText(amount.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        if(amount > 1000){
            recharge_1000.visibility = View.GONE
        }
        if(amount > 500){
            recharge_500.visibility = View.GONE
        }
        if(amount > 200){
            recharge_200.visibility = View.GONE
        }
        recharge_1000.setOnClickListener {
            try {
                var amount = amountBox.text.toString().toInt()
                amount += 1000
                amountBox.setText(amount.toString())
            } catch (exp: Exception) {
                amountBox.setText("1000")
            }
        }

        recharge_500.setOnClickListener {
            try {
                var amount = amountBox.text.toString().toInt()
                amount += 500
                amountBox.setText(amount.toString())
            } catch (exp: Exception) {
                amountBox.setText("500")
            }
        }

        recharge_200.setOnClickListener {
            try {
                var amount = amountBox.text.toString().toInt()
                amount += 200
                amountBox.setText(amount.toString())
            } catch (exp: Exception) {
                amountBox.setText("200")
            }
        }
        buttonRecharge.setOnClickListener {

            AppUtils.hideKeyboard(this@JPWebViewActivityNew,amountBox)

            var enterAmount : Int? = amountBox.text.toString().toIntOrNull()
            if(enterAmount == null || enterAmount < amount){
                AppUtils.showToast("Please enter valid amount!!", true, 2);
            }else{
                amount = enterAmount

                val reqPayload = HashMap<String, Any>()
                reqPayload["walletId"] = walletId
                reqPayload["walletName"] = walletName
                reqPayload["amount"] = amount

                pb.visibility = View.VISIBLE

                val objectSimpleHttpAgent = SimpleHttpAgent(
                        this,
                        UrlConstant.JUSPAY_WALLET_RECHARGE,
                        ResponseListener { responseObject ->

                            pb.visibility = View.VISIBLE
                            if (responseObject != null && responseObject!!.jpWalletRecharges.get(0).paymentUrl.isNotEmpty()) {
                                paymentUrl = responseObject!!.jpWalletRecharges.get(0).paymentUrl
                                returnUrl = responseObject!!.jpWalletRecharges.get(0).returnUrl
                                transactionId = responseObject!!.jpWalletRecharges.get(0).transactionId
                                loadUrl()
                            }else{
                                val resultIntent = Intent()
                                resultIntent.putExtra("walletName", walletName)
                                resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink)
                                setResult(JPWebViewActivityNew.FAILURE, resultIntent)
                                finish()
                            }
                        },
                        ContextErrorListener { errorCode, error, errorResponse ->
                            pb.visibility = View.VISIBLE
                            val resultIntent = Intent()
                            resultIntent.putExtra("walletName", walletName)
                            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink)
                            setResult(JPWebViewActivityNew.FAILURE, resultIntent)
                            finish()
                        },
                        JPWalletRechargeResponse::class.java
                )
                objectSimpleHttpAgent.post(reqPayload, HashMap())
            }
        }
    }


    override fun onBackPressed() {
        if (!shown)
            showBackPressed()
    }

    private fun showBackPressed() {
        shown = true
        val genericPopUpFragment = GenericPopUpFragment.newInstance("Do you want to cancel the current transaction",
                "Go Back", "Cancel",
                object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        shown = false
                        goBackAndRefresh(false)

                    }

                    override fun onNegativeInteraction() {
                        shown = false
                    }
                })
        genericPopUpFragment.setCancelable(false)
        genericPopUpFragment.show(supportFragmentManager, "jpre_cancel")
    }

    fun loadUrl() {

        val callBack = object : BrowserCallback() {

            override fun onPageStarted(view: WebView?, url: String?) {
                super.onPageStarted(view, url)
            }

            override fun onTransactionAborted(paymentDetails: JSONObject?) {
                JuspaySafeBrowser.exit()
                AppUtils.showToast("Transaction cancelled.", false, 0)
                goBackAndRefresh(false)

            }

            override fun onWebViewReady(webView: WebView?) {
                AppUtils.HbLog("JP WV", "onWebViewReady: url " + webView!!.url)
            }

            override fun endUrlReached(view: WebView?, sessionInfo: JSONObject?) {
                val success = Pattern.compile(UrlConstant.PAYMENT_RESPONSE_REGEX_SUCCESS)

                var successMatcher: Matcher? = null
                try {
                    successMatcher = success.matcher(sessionInfo!!.getString("url"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                JuspaySafeBrowser.exit()
                AppUtils.showToast("Transaction completed.", false, 1)
                if (successMatcher != null && successMatcher.matches())
                    goBackAndRefresh(true)
                else
                    goBackAndRefresh(false)

            }
        }


        val browserParams = BrowserParams()
        browserParams.merchantId = UrlConstant.JUSPAY_MERCHANT_ID
        browserParams.transactionId = transactionId
        browserParams.orderId = ""
        browserParams.clientId = "hungerbox"
        browserParams.url = paymentUrl
        browserParams.amount = amount.toString()
        JuspaySafeBrowser.setEndUrls(arrayOf(UrlConstant.PAYMENT_RESPONSE_REGEX_SUCCESS, UrlConstant.PAYMENT_RESPONSE_REGEX_FAILURE))
        JuspaySafeBrowser.start(this, browserParams, callBack)
    }

    private fun goBackAndRefresh(status: Boolean) {
        val resultIntent = Intent()
        resultIntent.putExtra("walletName", walletName)
        if (status) {
            paymentMethodToLink!!.paymentDetails!!.curretBalance = paymentMethodToLink!!.paymentDetails!!.curretBalance + amount
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink)
            setResult(JPWebViewActivityNew.SUCCESS, resultIntent)
        } else {
            resultIntent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToLink)
            setResult(JPWebViewActivityNew.FAILURE, resultIntent)
        }
        goback()
    }

    private fun goback() {
        finish()
    }
}