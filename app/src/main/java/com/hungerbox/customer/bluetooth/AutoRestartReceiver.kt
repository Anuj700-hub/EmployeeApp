package com.hungerbox.customer.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import androidx.core.content.ContextCompat
import com.hungerbox.customer.util.ApplicationConstants

class AutoRestartReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRAKEY_START = "AUTO_RESTART_START"
        const val EXTRAKEY_CANCEL = "AUTO_RESTART_CANCEL"

        const val REQUEST_CODE = 123
        const val URI_PATH = "restart"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val uri = Uri.parse("${ApplicationConstants.URI_BLUETOOTH}://$URI_PATH")

        if (intent.action == Intent.ACTION_RUN && intent.data == uri) {
            if (BluetoothAlertService.isRunning(context)) {
                context.startService(
                        BluetoothAlertService.stopService(context)
                        .putExtra(EXTRAKEY_CANCEL, false)
                )

                Handler().postDelayed({
                    ContextCompat.startForegroundService(
                        context,
                            BluetoothAlertService.startService(context).putExtra(EXTRAKEY_START, false)
                    )
                }, 2000)
            }
        }
    }
}