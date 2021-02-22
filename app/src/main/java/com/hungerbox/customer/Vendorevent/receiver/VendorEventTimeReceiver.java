package com.hungerbox.customer.Vendorevent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hungerbox.customer.Vendorevent.Service.VendorEventTimeHandleService;


public class VendorEventTimeReceiver extends BroadcastReceiver {
    public VendorEventTimeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent serviceIntent = new Intent(context, VendorEventTimeHandleService.class);
        context.startService(serviceIntent);
    }
}
