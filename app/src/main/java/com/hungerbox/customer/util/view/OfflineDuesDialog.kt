package com.hungerbox.customer.util.view

import `in`.juspay.ec.sdk.api.Environment
import `in`.juspay.ec.sdk.api.PaymentInstrument
import `in`.juspay.ec.sdk.checkout.MobileWebCheckout
import `in`.juspay.juspaysafe.BrowserCallback
import `in`.juspay.juspaysafe.BrowserParams
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.R
import com.hungerbox.customer.model.OfflineInitiatePayment
import com.hungerbox.customer.model.PaymentStatus
import com.hungerbox.customer.model.PaymentStatusResposne
import com.hungerbox.customer.model.PostPaidResponse
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity
import com.hungerbox.customer.order.activity.OrderDetailNewActivity
import com.hungerbox.customer.order.adapter.PostPaidOrderAdapter
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil
import kotlinx.android.synthetic.main.dialog_offline_dues.view.*
import org.json.JSONObject
import java.util.*

class OfflineDuesDialog : androidx.fragment.app.DialogFragment {
    lateinit var postPaidResponse: PostPaidResponse
    lateinit var parentActivity: Activity
    lateinit var paymentSuccessListener : BookmarkPaymentActivity.PaymentCallBack
    var dialogNonCancelable: Boolean = true
    var tagForApiRequest = ""
    constructor() : super()

    companion object{
        fun newInstance(response: PostPaidResponse, paymentSuccessListener : BookmarkPaymentActivity.PaymentCallBack, dialogNonCancelable : Boolean, parentActivity:Activity):OfflineDuesDialog{
            var fragment = OfflineDuesDialog()
            fragment.postPaidResponse = response
            fragment.paymentSuccessListener = paymentSuccessListener
            fragment.parentActivity = parentActivity
            fragment.dialogNonCancelable = dialogNonCancelable
            return fragment
        }

        fun newInstance():OfflineDuesDialog{
            return OfflineDuesDialog()
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (parentActivity != null && activity != null) {
            if (parentActivity is BookmarkPaymentActivity) {
                tagForApiRequest = (activity as BookmarkPaymentActivity?)!!.apiTag
            }
        }

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.tranparent)))
        var view = inflater.inflate(R.layout.dialog_offline_dues,container)
        view.iv_close.visibility = if (dialogNonCancelable) View.INVISIBLE else View.VISIBLE
        if (AppUtils.getConfig(activity).payment_due_header.isNotEmpty()){
            view.tv_alert_message.text = AppUtils.getConfig(activity).payment_due_header
        }
        view.iv_close.setOnClickListener{ dialog?.dismiss() }
        view.btn_clear_dues.setOnClickListener { initiateBillPayment() }
        //on bill item click
        val clickListener = object :ItemClick{
            override fun onItemClick(orderID: String) {
                val intent = Intent(activity, OrderDetailNewActivity::class.java)
                intent.putExtra(ApplicationConstants.BOOKING_ID, orderID.toLong())
                activity!!.startActivity(intent)
            }
        }
        //setting the values in view
        if (postPaidResponse!=null){
            view.tv_amount.text = "Your payment due is ₹${postPaidResponse.amount}"
            view.tv_bill_amount.text = "₹${postPaidResponse.amount}"
            var transactionListAdapter = PostPaidOrderAdapter(postPaidResponse.transactions.postPaidOrders,clickListener)
            view.rv_orders.adapter = transactionListAdapter
        }
        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun initiateBillPayment(){

//        val responseListener = object :ResponseListener<OfflineInitiatePayment>{
//            override fun response(responseObject: OfflineInitiatePayment?) {

//            }
//        }
//        val contextErrorListener = object : ContextErrorListener{
//            override fun handleError(errorCode: Int, error: String?, errorResponse: ErrorResponse?) {

//            }
//        }
        val request = SimpleHttpAgent(
                context,
                UrlConstant.GET_ORDER_ID,
                ResponseListener { responseObject ->if (!responseObject.orderID.isNullOrBlank())
                    performTransaction(responseObject.orderID)
                    else {
                        dialog?.dismiss()
                        if (paymentSuccessListener!=null)paymentSuccessListener.onAborted()
                    }
                },
                ContextErrorListener { errorCode, error, errorResponse -> AppUtils.showToast(error,true,0)},
                OfflineInitiatePayment::class.java
        )

        request.post(Any(), HashMap(),tagForApiRequest)

    }

    private fun performTransaction(orderID: String) {
        var paymentInstruments: Array<PaymentInstrument>? = null
        var ignore = false

        if (AppUtils.getConfig(context).juspay_payment_option == null || AppUtils.getConfig(context).juspay_payment_option.size == 0) {
            paymentInstruments = arrayOf<PaymentInstrument>(PaymentInstrument.CARD, PaymentInstrument.NB)
        } else {
            //todo juspay

            val paymentInstrumentsList = ArrayList<PaymentInstrument>()
            for (juspay in AppUtils.getConfig(context).juspay_payment_option) {
                if (juspay.key == "NB") {
                    paymentInstrumentsList.add(PaymentInstrument.NB)
                } else if (juspay.key == "CARD") {
                    paymentInstrumentsList.add(PaymentInstrument.CARD)
                } else if (juspay.key == "WALLET") {
                    paymentInstrumentsList.add(PaymentInstrument.WALLET)
                } else if (juspay.key == "UPI") {
                    paymentInstrumentsList.add(PaymentInstrument.UPI)
                } else if (juspay.key == "SAVED_CARD") {
                    paymentInstrumentsList.add(PaymentInstrument.SAVED_CARD)
                }
            }

            paymentInstruments = paymentInstrumentsList.toTypedArray<PaymentInstrument>()
        }

        val targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE, "")

        if (BuildConfig.BUILD_TYPE.equals("qa", ignoreCase = true) && targetEnv.equals("qa"))
            Environment.configure(Environment.SANDBOX, UrlConstant.JUSPAY_MERCHANT_ID)
        else
            Environment.configure(Environment.PRODUCTION, UrlConstant.JUSPAY_MERCHANT_ID)

        val checkout = MobileWebCheckout(
                orderID,
                arrayOf(UrlConstant.PAYMENT_RESPONSE_REGEX),
                paymentInstruments,
                HashMap<String, String>()
        )

        dialog?.dismiss()

        checkout.startPayment(parentActivity,
                BrowserParams(),
                object : BrowserCallback() {

                    override fun endUrlReached(webView: WebView, jsonObject: JSONObject?) {
                        if (!ignore) {
                            ignore = true

                            parentActivity.runOnUiThread(Runnable { checkOrderStatus(orderID) })
                        }
                    }

                    override fun onTransactionAborted(jsonObject: JSONObject?) {
                        parentActivity.runOnUiThread(Runnable {
                            AppUtils.showToast("aborted", false, 0)
                            if (paymentSuccessListener!=null){
                                paymentSuccessListener.onAborted()
                            }
                        })

                    }
                }
        )
    }

    private fun checkOrderStatus(orderId: String) {
        val url = UrlConstant.PAYMENT_STATUS + orderId

        val simpleHttpAgent = SimpleHttpAgent(
                context,
                url,
                ResponseListener { responseObject -> navigateToSuccessFailView(responseObject.paymentStatus) },
                ContextErrorListener { errorCode, error, errorResponse -> checkOrderStatus(orderId) },
                PaymentStatusResposne::class.java
        )

        simpleHttpAgent.get(tagForApiRequest)
    }

    private fun navigateToSuccessFailView(paymentStatus: PaymentStatus) {

        if (paymentSuccessListener!=null){
            paymentSuccessListener.onSuccess(paymentStatus)
        }

    }

    interface ItemClick{
        fun onItemClick(orderID:String);
    }
}