package com.hungerbox.customer.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");

        if(AppUtils.getConfig(context).getBluetooth_distancing() != null && !authToken.equals("") && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Intent intents = new Intent(context, BluetoothAlertService.class);
            if (Util.isForegroundCompatible(context)) {
                ContextCompat.startForegroundService(context, intents);
            } else {
                context.startService(intents);
            }
        }
    }
}
