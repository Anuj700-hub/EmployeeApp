package com.hungerbox.customer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        // TODO <pending>
//        // an Intent broadcast.
//        Intent orderNotificationServiceIntent = new Intent(context, AlarmService.class);
//        orderNotificationServiceIntent.putExtra(ApplicationConstants.ORDER_ID, intent.getLongExtra(ApplicationConstants.ORDER_ID, -1));
//        orderNotificationServiceIntent.putExtra(ApplicationConstants.ALARM_TYPE, intent.getIntExtra(ApplicationConstants.ALARM_TYPE, ApplicationConstants.ORDER_READY));
//        orderNotificationServiceIntent.putExtra(ApplicationConstants.ORDER, intent.getSerializableExtra(ApplicationConstants.ORDER));
//        context.startService(orderNotificationServiceIntent);
    }
}
