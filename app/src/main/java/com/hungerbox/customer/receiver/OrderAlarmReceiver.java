package com.hungerbox.customer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hungerbox.customer.service.NotificationService;
import com.hungerbox.customer.util.ApplicationConstants;

public class OrderAlarmReceiver extends BroadcastReceiver {
    public OrderAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent orderNotificationServiceIntent = new Intent(context, NotificationService.class);
        orderNotificationServiceIntent.putExtra(ApplicationConstants.ORDER, intent.getSerializableExtra(ApplicationConstants.ORDER));
        orderNotificationServiceIntent.putExtra(ApplicationConstants.SHOW_ORDER_NOTIFICATION, true);
        context.startService(orderNotificationServiceIntent);
    }
}
