package com.hungerbox.customer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FeedbackEta;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.DateTimeUtil;

public class NotificationReciever extends BroadcastReceiver {
    final String TAG = getClass().getName();
    String orderId;
    long notificationTime;
    String response;
    String location = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        orderId = intent.getStringExtra("order-id");
        response = intent.getStringExtra("response");
        location = intent.getStringExtra("location-id");
        notificationTime = intent.getLongExtra("notification-time",0);
        if (orderId!=null && response!=null && notificationTime>0){
            getFeedbackReasonFromServer();
        }
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void getFeedbackReasonFromServer() {
        String url = UrlConstant.FEEDBACK_ETA+"?order-id="+orderId+"&notification-time="+notificationTime
                +"&response-time="+ DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis()
                +"&response="+response;
        if(location != null && !location.isEmpty()){
            url += "&location-id=" + location;
        }

        SimpleHttpAgent<FeedbackEta> feedbackRatingReasonResponseSimpleHttpAgent = new SimpleHttpAgent<FeedbackEta>(
                MainApplication.appContext,
                url,
                new ResponseListener<FeedbackEta>() {
                    @Override
                    public void response(FeedbackEta responseObject) {
                        if (responseObject != null && responseObject.getSuccess()) {
                            Log.d(TAG, "response: feedback response submitted successfully");
                        } else{
                            Log.d(TAG, "response: Error in submitting feedback response via notification");
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        Log.d(TAG, "handleError: Error in submitting feedback response via notification");
                    }
                },
                FeedbackEta.class
        );

        feedbackRatingReasonResponseSimpleHttpAgent.get();
    }
}
