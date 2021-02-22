package com.hungerbox.customer.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.hungerbox.customer.receiver.SmsListener;

public class SmsRetrieverReceiver extends BroadcastReceiver {

    public static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static void stopListener() {
        mListener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String messageBody = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (mListener != null)
                        mListener.messageReceived(messageBody);

                    break;
                case CommonStatusCodes.TIMEOUT:
                    if (mListener != null)
                        mListener.messageReceived(null);
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
