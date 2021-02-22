package com.hungerbox.customer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.event.OccasionChangeEvent;

public class OccasionClearReceiver extends BroadcastReceiver {
    public OccasionClearReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (MainApplication.bus != null)
            MainApplication.bus.post(new OccasionChangeEvent());
    }
}
