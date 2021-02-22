package com.hungerbox.customer.order.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.hungerbox.customer.R
import com.hungerbox.customer.config.action.LogoutTask
import com.hungerbox.customer.model.ErrorResponse
import com.hungerbox.customer.model.Order
import com.hungerbox.customer.model.OrderResponse
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.printerUtils.ConnectUSBActivity
import com.hungerbox.customer.printerUtils.Prints
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil
import java.lang.Exception

class GuestOrderSuccess : AppCompatActivity() {

    private lateinit var order: Order
    private lateinit var tvTime: TextView
    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderPin: TextView
    private lateinit var tvPrintSuccess: TextView
    internal var pDialog: ProgressDialog? = null
    var printerResult = 1000
    internal var printerCalled = false
    private lateinit var rlProgressBar: RelativeLayout
    var timer: CountDownTimer? = null
    private lateinit var btExit: Button
    private lateinit var btSendInvoice: Button
    private lateinit var btSendSms : Button
    private lateinit var tvTimeText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_order_success)
        tvTime = findViewById(R.id.tv_time)
        rlProgressBar = findViewById(R.id.rl_progress)
        tvOrderId = findViewById(R.id.tv_order_id)
        tvOrderPin = findViewById(R.id.tv_order_pin)
        btExit = findViewById(R.id.bt_exit)
        btSendInvoice = findViewById(R.id.bt_send_invoice)
        tvTimeText = findViewById(R.id.tv_time_text)
        tvPrintSuccess = findViewById(R.id.tv_print_success)
        btSendSms = findViewById(R.id.bt_send_sms)

        LogoutTask.updateTime()
        LogoutTask.getInstance(this).stopTimer()
        val orderId = intent.getLongExtra(ApplicationConstants.BOOKING_ID, -1)
        getOrderDetail(orderId)

        btExit.setOnClickListener {
            AppUtils.doLogout(applicationContext)
            cancelTimer()
        }

        btSendInvoice.setOnClickListener {
            goToInvoiceActivity(orderId)
        }
        btSendSms.setOnClickListener {
            goToInvoiceActivity(orderId)
        }

    }

    private fun goToInvoiceActivity( orderId: Long){
        val intent = Intent(this,SendInvoiceActivity::class.java)
        intent.putExtra(ApplicationConstants.BOOKING_ID, orderId)
        startActivity(intent)
        finish()
    }

    fun startCountDownTimer() {
        timer = object : CountDownTimer(AppUtils.getConfig(applicationContext).guest_logout_time, 1000) {

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

                tvTime.visibility = View.GONE
                tvTimeText.visibility = View.GONE
                AppUtils.doLogout(applicationContext)
                cancelTimer()

            }
        }.start()
    }

    private fun getOrderDetail(orderId: Long) {

        rlProgressBar.setVisibility(View.VISIBLE)
        val url = UrlConstant.ORDER_DETAIL + orderId + "/1/1/0/1"
        val orderResponseSimpleHttpAgent = SimpleHttpAgent(
                this,
                url,
                ResponseListener { responseObject ->
                    rlProgressBar.setVisibility(View.GONE)
                    if (responseObject == null) {
                        AppUtils.showToast("Unable to fetch Your Order", false, 0)
                    } else {
                        if (SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)) {
                            order = responseObject.order
                            print(responseObject.order)
                            startCountDownTimer()
                        }
                        setOrderView(responseObject.order);

                    }
                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    rlProgressBar.setVisibility(View.GONE)
                    AppUtils.showToast("Unable to fetch Your Order", false, 0)
                },
                OrderResponse::class.java
        )
        orderResponseSimpleHttpAgent.get()
    }

    private fun setOrderView(order: Order) {

        tvOrderId.setText("Your Order Id : " + order.orderId)

        if (AppUtils.getConfig(this).isShow_guest_order_pin) {
            showOrderPin()
        }
    }

    private fun showOrderPin(){
        if(order!=null) {
            tvOrderPin.setText("Your Order PIN : " + order.pin)
            tvOrderPin.visibility = View.VISIBLE
        }
    }

    private fun print(order: Order) {
        if (!printerCalled) {
            val intent = Intent(this, ConnectUSBActivity::class.java)
            intent.putExtra("order", order)
            startActivityForResult(intent, printerResult)
            showProgress("Printing")
        }
        printerCalled = true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == printerResult) {
            if (data != null) {
                val printer_result = data.getIntExtra("printer_result", 100)
                if (printer_result < 0) {
                    showOrderPin()
                    setPrinterError(printer_result)
                }else{
                    tvPrintSuccess.visibility = View.VISIBLE
                }
            }
            hideProgress()
        }

    }

    private fun setPrinterError(error_code: Int) {

        tvPrintSuccess.setText("Oops! We couldn’t print the receipt for your order\nPlease enter your phone number to get order details via SMS")
        btSendSms.visibility = View.VISIBLE
        tvPrintSuccess.visibility = View.VISIBLE
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvPrintSuccess.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))
        } else {
            tvPrintSuccess.setTextColor(getResources().getColor(R.color.red))
        }


    }

    override fun finish() {
        cancelTimer()
        super.finish()
    }

    private fun cancelTimer() {
        try {
            timer?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgress(msg: String) {
        pDialog = ProgressDialog(this)
        pDialog?.setMessage("$msg...")
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    private fun hideProgress() {
        if (pDialog != null && pDialog?.isShowing()!!) {
            pDialog?.dismiss()
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    fun navigateBack() {

        if (AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout) {
            //AppUtils.showToast("order place logout",true,1);
            AppUtils.doLogout(this)
        } else {
            val intent = AppUtils.getHomeNavigationIntent(this)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        finish()
    }

}
