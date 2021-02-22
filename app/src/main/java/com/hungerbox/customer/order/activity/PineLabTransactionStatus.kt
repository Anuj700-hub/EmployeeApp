package com.hungerbox.customer.order.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hungerbox.customer.R
import com.hungerbox.customer.model.Order
import android.os.Handler
import com.hungerbox.customer.util.AppUtils
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.zxing.WriterException
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.config.action.LogoutTask
import com.hungerbox.customer.model.OrderResponse
import com.hungerbox.customer.model.PaymentMethod
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.OrderUtil
import com.hungerbox.customer.util.QrUtil
import com.hungerbox.customer.util.view.GenericPopUpFragment


class PineLabTransactionStatus : AppCompatActivity() {


    private lateinit var tvTime: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvWait : TextView
    private lateinit var ivBack: ImageView
    private lateinit var ltLoader : LottieAnimationView
    var retryHandler: Handler? = null
    lateinit var order: Order
    private var progressDialog: AlertDialog? = null
    var timer: CountDownTimer? = null
    lateinit var paymentMethod: PaymentMethod


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pine_lab_transaction_status)
        tvTime = findViewById(R.id.tv_time)
        ivBack = findViewById(R.id.iv_back)
        order = intent.getSerializableExtra("order") as Order
        paymentMethod = intent.getSerializableExtra("payment") as PaymentMethod
        tvAmount = findViewById(R.id.tv_amount)
        ltLoader = findViewById(R.id.iv_status)
        tvWait = findViewById(R.id.tv_wait)

        retryHandler = Handler()


        startCountDownTimer()
        retryHandler?.postDelayed(object : Runnable {

            override fun run() {
                getOrderStatusFromServer()
                retryHandler?.postDelayed(this, AppUtils.getConfig(applicationContext).pinelab_retry_interval)
            }
        }, 1000)
        ivBack.setOnClickListener {
            showCancelDialog()
        }

        tvAmount.setText("Please swipe/tap your card at CARD MACHINE to Pay "+String.format("₹ %.2f", paymentMethod.amount))

        LogoutTask.updateTime()
        LogoutTask.getInstance(this).stopTimer()


    }



    fun startCountDownTimer() {
        timer = object : CountDownTimer(AppUtils.getConfig(applicationContext).pinelab_time_out, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000 % 60).toInt()
                if (minutes.equals(0L)) {
                    tvTime.setText("$seconds seconds")
                } else {
                    tvTime.setText("$minutes minutes $seconds seconds")
                }

            }

            override fun onFinish() {
                removeCallBack()
                cancelTransaction()

            }
        }.start()
    }

    fun getOrderStatusFromServer() {

        AppUtils.HbLog("payment_status", "Called");
        val url = UrlConstant.EXTERNAL_PAYMENT_STATUS + order.id
        val externalPayment = SimpleHttpAgent(applicationContext,
                url,
                ResponseListener { responseObject ->
                    if (responseObject != null) {
                        if (responseObject.order.orderStatus != null) {
                            if (responseObject.order.orderStatus.equals(OrderUtil.PAYMENT_FAILED, ignoreCase = true)) {
                                setResultAndGoBack(Activity.RESULT_CANCELED)
                            } else if (responseObject.order.orderStatus.equals(OrderUtil.PAYMENT_PENDING, ignoreCase = true)) {
                                //do nothing
                            } else if (responseObject.order.orderStatus.equals(OrderUtil.NEW, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.CONFIRMED, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.PROCESSED, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.PRE_ORDER, ignoreCase = true)) {
                                goToSuccessActivity(responseObject.order)
                            }
                        }
                    }
                },
                ContextErrorListener { errorCode, error, errorResponse ->

                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        AppUtils.showToast("NO INTERNET CONNECTION !", true, 0)
                    } else {
                        removeCallBack()
                        if (error != null && !error.isEmpty()) {
                            showErrorDialog(error, true, null)
                        } else {
                            showErrorDialog("Some Error Occurred", true, null)
                        }
                    }

                },
                OrderResponse::class.java
        )

        externalPayment.get()

    }

    fun cancelTransaction() {

        showProgressDialog("Cancelling Your Transaction ...")
        val url = UrlConstant.CANCEL_EXTERNAL_PAYMENT + order.id

        val cancelTransaction = SimpleHttpAgent(applicationContext,
                url,
                ResponseListener { responseObject ->
                    dismissDialog()
                    if (responseObject != null) {
                        if (responseObject.order.orderStatus != null) {
                            if (responseObject.order.orderStatus.equals(OrderUtil.PAYMENT_FAILED, ignoreCase = true)) {
                                setResultAndGoBack(Activity.RESULT_CANCELED)
                            } else if (responseObject.order.orderStatus.equals(OrderUtil.PAYMENT_PENDING, ignoreCase = true)) {
                                //do nothing
                            } else if (responseObject.order.orderStatus.equals(OrderUtil.NEW, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.CONFIRMED, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.PROCESSED, ignoreCase = true) ||
                                    responseObject.order.orderStatus.equals(OrderUtil.PRE_ORDER, ignoreCase = true)) {
                                showErrorDialog("Sorry ! You can't cancel this order now.Order is already place successfully", false, responseObject.order);
                            }
                        }
                    }
                },

                ContextErrorListener { errorCode, error, errorResponse ->
                    dismissDialog()
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        AppUtils.showToast("NO INTERNET CONNECTION !", true, 0)
                    } else {
                        removeCallBack()
                        if (error != null && !error.isEmpty()) {
                            showErrorDialog(error, true, null)
                        } else {
                            showErrorDialog("Some Error Occurred", true, null)
                        }
                    }
                },
                OrderResponse::class.java
        )
        cancelTransaction.get()

    }

    private fun setResultAndGoBack(result: Int) {
        var intent = Intent()
        setResult(result, intent)
        finish()
    }

    override fun onBackPressed() {
        showCancelDialog()
    }

    fun showCancelDialog() {
        val errorPopUp = GenericPopUpFragment
                .newInstance("Do you want to cancel this transaction?", "Yes", "No", object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        cancelTransaction()
                    }

                    override fun onNegativeInteraction() {
                    }
                })
        errorPopUp.isCancelable = false
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(errorPopUp, "Cancel Transaction").commitAllowingStateLoss()
    }

    override fun finish() {
        removeCallBack()
        super.finish()
    }

    private fun removeCallBack() {
        try {
            retryHandler?.removeCallbacksAndMessages(null)
            timer?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgressDialog(message: String) {
        try {
            progressDialog = AlertDialog.Builder(this).setMessage(message).setCancelable(false).create()
            progressDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun dismissDialog() {
        try {
            if (progressDialog != null) {
                progressDialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun goToSuccessActivity(order: Order?) {
        val intent = Intent(this, OrderSuccessActivity::class.java)
        intent.putExtra(ApplicationConstants.BOOKING_ID, order?.id)
        intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true)
        intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD)
        startActivity(intent)
        clearLocalOrder()
        finish()
    }

    private fun clearLocalOrder() {
        val mainApplication = getApplication() as MainApplication
        mainApplication.clearOrder()
    }

    private fun showErrorDialog(message: String, goBack: Boolean, order: Order?) {
        var errorPopUp = GenericPopUpFragment
                .newInstance(message, "OK", true, object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        if (goBack) {
                            setResultAndGoBack(Activity.RESULT_CANCELED)
                        } else {
                            goToSuccessActivity(order)
                        }
                    }

                    override fun onNegativeInteraction() {

                    }
                })
        errorPopUp.isCancelable = false
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(errorPopUp, "Error in loading").commitAllowingStateLoss()
    }
}
