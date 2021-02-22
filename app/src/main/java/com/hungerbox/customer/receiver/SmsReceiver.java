package com.hungerbox.customer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by manas on 3/6/17.
 */
public class SmsReceiver extends BroadcastReceiver {

    public static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void stopListener() {
        mListener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try{
            Bundle data = intent.getExtras();

            Object[] pdus = (Object[]) data.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                String messageBody = smsMessage.getMessageBody().toLowerCase();

                //Pass on the text to our listener.
                if (mListener != null)
                    mListener.messageReceived(messageBody);
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }

    }

}
