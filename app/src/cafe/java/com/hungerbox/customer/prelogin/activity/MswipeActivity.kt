package com.hungerbox.customer.prelogin.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hungerbox.customer.cardsaleactivityintegration.AppSharedPrefrences
import com.hungerbox.customer.cardsaleactivityintegration.CardSaleTransactionView
import com.hungerbox.customer.config.action.LogoutTask
import com.hungerbox.customer.model.PaymentStatusResposne
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.order.activity.MSGatewayConncetionObserver
import com.hungerbox.customer.order.activity.MSWisepadControllerResponseListenerObserver
import com.hungerbox.customer.order.activity.PaymentActivity
import com.hungerbox.customer.order.activity.PaymentFragment
import com.hungerbox.customer.util.ApplicationConstants
import com.mswipetech.wisepad.sdk.MSWisepadController
import com.mswipetech.wisepad.sdk.data.CardSaleResponseData
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by manas on 29/11/18.
 */
open class MswipeActivity : AppCompatActivity() {
    private var mMSGatewayConncetionObserver: MSGatewayConncetionObserver? = null
    private var mMSWisepadController: MSWisepadController? = null
    private var mMSWisepadControllerResponseListenerObserver: MSWisepadControllerResponseListener? = null


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, paymentActivity: PaymentFragment) {
        when (requestCode) {
            2002 -> {
                LogoutTask.updateTime()
                LogoutTask.getInstance(this).startTimer()
                if (resultCode == Activity.RESULT_OK) {
                    val cardSaleResponseData = data!!.getSerializableExtra("cardSaleResponseData") as CardSaleResponseData
                    if (cardSaleResponseData.responseStatus!!) {
                        getPaymentConfirmation(cardSaleResponseData, paymentActivity)
                    } else {
                        paymentActivity.showOrderErrorDialog(paymentActivity.order, cardSaleResponseData.responseFailureReason, false)
                    }
                    return
                } else {
                    paymentActivity.paymentRejectedbyBackRegisterfor(paymentActivity.order)
                }
            }
        }
    }

    private fun getPaymentConfirmation(cardSaleResponseData: Any?, paymentActivity: PaymentFragment) {

       var paymentResponseData = cardSaleResponseData as CardSaleResponseData

        val paymentConfirmationDialog = android.app.AlertDialog.Builder(paymentActivity.context).setCancelable(false)
                .setMessage("Confirming your payment status...").show()
        val url: String
        if (paymentResponseData == null) {
            url = UrlConstant.UPDATE_EXTERNAL_PAYMENT + paymentActivity.order.getId()
        } else {
            url = (UrlConstant.UPDATE_EXTERNAL_PAYMENT + paymentActivity.order.getId()
                    + "?switchCardType=" + paymentResponseData.switchCardType
                    + "&cardType=" + paymentResponseData.cardType + "&client_status="+paymentResponseData.responseStatus)
        }


        val simpleHttpAgent = SimpleHttpAgent(
                this,
                url,
                ResponseListener { responseObject ->
                    //                        Toast.makeText(PaymentsActivity.this, "Payment Status " + responseObject.paymentStatus.getStatus(), Toast.LENGTH_SHORT).show();
                    paymentConfirmationDialog.dismiss()
                    if (paymentResponseData!!.responseStatus!!) {
                        if (responseObject.paymentStatus.status.equals("payment_pending", ignoreCase = true) || responseObject.paymentStatus.status.equals("payment_failed", ignoreCase = true)) {
                            retryOrderStatus(paymentActivity,  responseObject.paymentStatus.status)
                        } else {
                            paymentActivity.order.setOrderStatus(responseObject.paymentStatus.status)
                            paymentActivity.startOrderView(paymentActivity.order)
                            //                                showOrderStatusDialog(order.getOrderId(), responseObject.paymentStatus.getStatus());
                        }
                    }
                }, ContextErrorListener { errorCode, error, errorResponse ->
            paymentConfirmationDialog.dismiss()
            //Toast.makeText(PaymentsActivity.this, "Payment Error: " + error, Toast.LENGTH_SHORT).show();
            if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                retryOrderStatus(paymentActivity, "Unavailable due to connectivity issues, please check your internet connection.")
            } else {
                retryOrderStatus(paymentActivity,  "Unavailable as " + error)
            }
        },
                PaymentStatusResposne::class.java
        )

        val paymentInfo = JSONObject()
        try {
            paymentInfo.put("getCardSaleApprovedMessage", paymentResponseData!!.cardSaleApprovedMessage)
            paymentInfo.put("getCardType", paymentResponseData.cardType)
            paymentInfo.put("getDate", paymentResponseData.date)
            paymentInfo.put("getMID", paymentResponseData.mid)
            paymentInfo.put("getRRNO", paymentResponseData.rrno)
            paymentInfo.put("getStrReceiptData", paymentResponseData.strReceiptData)
            paymentInfo.put("getTrxAmount", paymentResponseData.trxAmount)
            paymentInfo.put("getResponseStatus", paymentResponseData.responseStatus)
            paymentInfo.put("getResponseFailurMsg", paymentResponseData.responseFailureReason)
            paymentInfo.put("getResponseData", paymentResponseData.responseSuccessMessage)
            paymentInfo.put("getCardHolderName", paymentResponseData.cardHolderName)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        simpleHttpAgent.post(paymentInfo, HashMap())
    }

    private fun retryOrderStatus(paymentActivity: PaymentFragment, status: String) {
        if (status.equals("payment_failed", ignoreCase = true)) {
            val alertDialog = android.app.AlertDialog.Builder(paymentActivity.context)
                    .setCancelable(false)
                    .setTitle("Payment Failed")
                    .setMessage("Your Payment has Failed").setPositiveButton("OK") { dialogInterface, i -> finish() }.show()
            return
        } else {
            val alertDialog = android.app.AlertDialog.Builder(paymentActivity.context)
                    .setCancelable(false)
                    .setTitle("Payment Status")
                    .setMessage("Your Payment Status is " + status).setPositiveButton("Retry") { dialogInterface, i -> getPaymentConfirmation(null,paymentActivity) }.setNegativeButton("Cancel") { dialogInterface, i ->
                paymentActivity.pbPay.setVisibility(View.GONE)
                paymentActivity.btPay.setEnabled(true)
                finish()
            }.show()
        }
    }

    public fun startMswipeTransaction(paymentActivity: PaymentFragment) {

        if (AppSharedPrefrences.getAppSharedPrefrencesInstace().referenceId.length != 0 || AppSharedPrefrences.getAppSharedPrefrencesInstace().sessionToken.length != 0) {
            val intent = Intent(paymentActivity.context, CardSaleTransactionView::class.java)
            intent.putExtra("cardsale", true)
            intent.putExtra("autoconnect", true)
            intent.putExtra("checkcardAfterConnection", true)
            intent.putExtra("referenceid", AppSharedPrefrences.getAppSharedPrefrencesInstace().referenceId)
            intent.putExtra("sessiontoken", AppSharedPrefrences.getAppSharedPrefrencesInstace().sessionToken)
            intent.putExtra("tipenable", false)
            if (!paymentActivity.user.getPhoneNumber().isEmpty())
                intent.putExtra("mobileno", paymentActivity.user.getPhoneNumber())
            else
                intent.putExtra("mobileno", "8039514646")
            intent.putExtra("receiptno", paymentActivity.tempOrder.getOrderId())
            intent.putExtra("email", "")
            intent.putExtra("notes", "")
            if (paymentActivity.tempOrder.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload() != null) {
                intent.putExtra("extra1", paymentActivity.tempOrder.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload().get("mid"))
                intent.putExtra("extra2", paymentActivity.tempOrder.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload().get("tid"))
                intent.putExtra("extra3", "OVERRIDESODEXO")
            } else {
                if (paymentActivity.cart.getExternalPaymentMethod()!!.getPaymentSource().getPaymentData().getCode().equals(ApplicationConstants.MSWIPE_SODEXO, ignoreCase = true)) {
                    paymentActivity.showOrderErrorDialog(paymentActivity.order, "Mid/Tid unavailable for this vendor", false)
                    return
                } else {
                    intent.putExtra("extra1", "")
                    intent.putExtra("extra2", "")
                    intent.putExtra("extra3", "")
                }
            }
            intent.putExtra("extra4", "")
            intent.putExtra("extra5", "")
            intent.putExtra("extra6", "")
            intent.putExtra("extra7", "")
            intent.putExtra("extra8", "")
            intent.putExtra("extra9", "")
            intent.putExtra("extra10", "")
            intent.putExtra("gatewayenvironment", MSWisepadController.GATEWAY_ENVIRONMENT.PRODUCTION)
            val gateWayDefault = MSWisepadController.NETWORK_SOURCE.WIFI
            intent.putExtra("networksource", MSWisepadController.NETWORK_SOURCE.WIFI)

            val totalAmount = String.format("%.2f", paymentActivity.order.getTotalPrice())

            var text = totalAmount.toString()
            var ilen = text.length

            if (ilen > 6) {
                text = text.substring(0, ilen - 6) + "," + text.substring(ilen - 6, ilen)
            }

            ilen = text.length

            if (ilen > 9) {
                text = text.substring(0, ilen - 9) + "," + text.substring(ilen - 9, ilen)
            }
            intent.putExtra("totalamount", text)
            intent.putExtra("baseamount", text)
            intent.putExtra("tipamount", "0.00")
            LogoutTask.getInstance(paymentActivity.context).stopTimer()

            paymentActivity.startActivityForResult(intent, 2002)
        } else {
            paymentActivity.startTransaction = true
            LoginMswipe( paymentActivity)
        }
    }


    public fun LoginMswipe(paymentActivity: PaymentFragment) {
        mMSWisepadControllerResponseListenerObserver = MSWisepadControllerResponseListenerObserver(paymentActivity)

        mMSWisepadController = MSWisepadController.getSharedMSWisepadController(paymentActivity.context,
                AppSharedPrefrences.getAppSharedPrefrencesInstace().gatewayEnvironment,
                AppSharedPrefrences.getAppSharedPrefrencesInstace().networkSource,
                mMSGatewayConncetionObserver)

        MSWisepadController.setNetworkSource(AppSharedPrefrences.getAppSharedPrefrencesInstace().networkSource)

        //call the mswipe wisepad api concurrently which run in a different process separated from the UI process
        //                  //Production
        mMSWisepadController!!.authenticateMerchant("9400045912", "555555",
                mMSWisepadControllerResponseListenerObserver)

    }

}