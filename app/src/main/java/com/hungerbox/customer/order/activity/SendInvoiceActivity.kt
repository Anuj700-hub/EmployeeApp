package com.hungerbox.customer.order.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.google.gson.JsonSerializer
import com.hungerbox.customer.R
import com.hungerbox.customer.model.SendOrderDetail
import com.hungerbox.customer.network.ContextErrorListener
import com.hungerbox.customer.network.ResponseListener
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import java.util.*

class SendInvoiceActivity : AppCompatActivity() {


    private lateinit var rlHeader: RelativeLayout
    private lateinit var etPhone: EditText
    private lateinit var btSubmit: Button
    private lateinit var progressBar: ProgressBar
    var orderId: Long = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_invoice)
        rlHeader = findViewById(R.id.layout_header)
        etPhone = findViewById(R.id.et_phone)
        btSubmit = findViewById(R.id.bt_submit)
        progressBar = findViewById(R.id.progress_bar)
        orderId = intent.getLongExtra(ApplicationConstants.BOOKING_ID, -1)

        btSubmit.setOnClickListener {
            if (etPhone.text != null && etPhone.text.toString().length == 10) {
                AppUtils.hideKeyboard(this@SendInvoiceActivity, etPhone)
                getUserOrderDetail(etPhone.text.toString(), orderId)
            }
        }

        rlHeader.setOnClickListener { navigateBack() }

    }

    override fun onBackPressed() {
        navigateBack()
    }

    fun getUserOrderDetail(phone: String, orderID: Long) {
        btSubmit.isEnabled = false
        progressBar.visibility = View.VISIBLE
        val url = UrlConstant.SEND_ORDER_DETAIL

        val orderDetail = SimpleHttpAgent(applicationContext,
                url,
                ResponseListener { responseObject ->
                    btSubmit.isEnabled = true
                    progressBar.visibility = View.GONE
                    AppUtils.showToast("Order Detail Successfully Sent To Your Phone.", false, 1)
                    val handler = Handler()
                    handler.postDelayed({ navigateBack() }, 2000)


                },
                ContextErrorListener { errorCode, error, errorResponse ->
                    btSubmit.isEnabled = true
                    progressBar.visibility = View.GONE
                    AppUtils.showToast("Unable to fetch Your Order", true, 0)

                },
                Object::class.java);
        orderDetail.post(SendOrderDetail(orderID, phone), HashMap())

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
