package com.hungerbox.customer.bluetooth

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hungerbox.customer.util.ApplicationConstants

class AutoRestartJob {

    private var pendingIntent: PendingIntent? = null
    private var alarmManager: AlarmManager? = null

    fun setUp(context: Context, alarmManager: AlarmManager) {
        val uri =
            Uri.parse("${ApplicationConstants.URI_BLUETOOTH}://${AutoRestartReceiver.URI_PATH}")

        val intent = Intent(context, AutoRestartReceiver::class.java)
            .setAction(Intent.ACTION_RUN)
            .setData(uri)

        pendingIntent = PendingIntent.getBroadcast(
            context,
            AutoRestartReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        this.alarmManager = alarmManager

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1800000,
            INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancel() {
        pendingIntent?.let { alarmManager?.cancel(it) }
    }
}