package com.hungerbox.customer.prelogin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.hungerbox.customer.order.activity.PaymentActivity
import com.hungerbox.customer.order.activity.PaymentFragment

/**
 * Created by manas on 29/11/18.
 */
open class MswipeActivity : AppCompatActivity() {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, paymentActivity: PaymentFragment) {

    }

    private fun getPaymentConfirmation(cardSaleResponseData: Any?, paymentActivity: PaymentFragment) {

    }

    private fun retryOrderStatus(paymentActivity: PaymentFragment, status: String) {

    }

    public fun startMswipeTransaction(paymentActivity: PaymentFragment) {


    }


    public fun LoginMswipe(paymentActivity: PaymentFragment) {

    }

}