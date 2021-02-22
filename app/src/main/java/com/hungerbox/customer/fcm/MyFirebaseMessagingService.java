package com.hungerbox.customer.fcm;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.NotificationInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.event.BannerRefreshEvent;
import com.hungerbox.customer.event.CloseChatHeadEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderDate;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.OrdersReponse;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.NotificationHomeActivity;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.receiver.NotificationReciever;
import com.hungerbox.customer.service.NotificationService;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.NotificationUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.UrlNavigationConstatnt;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String ORD_NOTIFICATION_CHANNEL_ID = "10";
    private static final String TAG = "MyFirebaseMsgService";
    static final int NOTIFICATION_ID = 1;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        AppUtils.HbLog(TAG, "Refreshed token: " + s);
        CleverTapEvent.pushFCMEvents(s, getApplicationContext());
        SharedPrefUtil.putString(ApplicationConstants.PREF_FCM_TOKEN, s);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        AppUtils.HbLog(TAG, "From: " + remoteMessage.getFrom());
        AppUtils.HbLog(TAG, "Notification Message Body: " + remoteMessage.getData());
        Map<String, String> payload = remoteMessage.getData();
        Bundle extras = null;

        try {
            if (remoteMessage.getData().size() > 0) {
                extras = new Bundle();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       if (payload.containsKey(NotificationUtil.REF_TYPE) && payload.containsKey(NotificationUtil.REF_ID)) {
            if (payload.get("ref_type").equalsIgnoreCase("order")) {
                if (payload.get("category") != null && payload.get("category").equalsIgnoreCase("feedback")){
                    if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null) {
                        String body = remoteMessage.getNotification().getBody();
                        sendNotificationForFeedback(body, true,payload);
                    }
                    else if(payload.containsKey("body") && payload.get("body")!=null){
                        sendNotificationForFeedback(payload.get("body"),true,payload);
                    }
                } else {
                    sendOrderNotification(payload);
                }
            }
        } else if (payload.containsKey(NotificationUtil.ROUTE)) {
            if (payload.containsKey("company_id")) {
                long companyId = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, -1);
                if (payload.get("company_id").equals(companyId + "")) {
                    routeToUi(payload,extras);
                }
            } else {
                routeToUi(payload,extras);
            }
        } else if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null) {
            String body = remoteMessage.getNotification().getBody();
            sendNotification(body);
        }
    }

    private void routeToUi(Map<String, String> payload, Bundle extras) {

        Intent intent = null;
        if (payload.containsKey(NotificationUtil.ROUTE)
                && (payload.get(NotificationUtil.ROUTE).contains(UrlNavigationConstatnt.CATEGORY)
                || payload.get(NotificationUtil.ROUTE).contains(UrlNavigationConstatnt.ITEM)
                || payload.get(NotificationUtil.ROUTE).contains(UrlNavigationConstatnt.FOOD_MENU))
        ){
            intent = new Intent(this,NotificationHomeActivity.class);
            intent.putExtras(extras);
        }
        else{
            UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(this);
            intent = urlNavigationHandler.getUrlNavPath(payload.get(NotificationUtil.ROUTE));
        }
        showRouteNotification(payload, intent);
    }

    private void showClevertapNotification(Bundle extras){

        try{
            String notifMessage = extras.getString("nm");
            notifMessage = (notifMessage != null) ? notifMessage : "";
            String notifTitle = extras.getString("nt", "");
            notifTitle = notifTitle.isEmpty() ? "HungerBox" : notifTitle;
            String bigPictureUrl = extras.getString("wzrk_bp");
            String channelId = extras.getString("channel_id");
            String channelName = extras.getString("channel_name");

            Intent intent = new Intent(this,NotificationHomeActivity.class);
            intent.putExtras(extras);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_orderconfirmed)
                    .setContentTitle(notifTitle)
                    .setContentText(notifMessage)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notifMessage))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationBuilder.setChannelId(channelId);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            if (bigPictureUrl!=null && !bigPictureUrl.equals("")) {

                downloadImage(bigPictureUrl, NOTIFICATION_ID, notificationBuilder);
            } else {
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    private void sendNotificationForFeedback(String body, boolean getFeedback, Map<String, String> payload) {
        if (payload!=null && payload.containsKey("order_ref")) {
            String messageBody = body;
            String title = "Hungerbox";
            if (payload.containsKey("title")) {
                title = payload.get("title");
            }
            //todo to be review
            Intent intent = new Intent(this, OrderDetailNewActivity.class);
            intent.putExtra("order_ref", payload.get("order_ref"));
            try {
                intent.putExtra(ApplicationConstants.BOOKING_ID, Long.parseLong(payload.get("ref_id")));
            } catch (Exception e) {
                e.printStackTrace();
                intent.putExtra(ApplicationConstants.BOOKING_ID, 0);
            }
            intent.putExtra("notification-time", DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(ApplicationConstants.FROM_NOTIFICATION, true);
            intent.putExtra(ApplicationConstants.FOR_FEEDBACK, true);
            intent.putExtra(CleverTapEvent.PropertiesNames.getSource(), ApplicationConstants.NOTIFICATION_SOURCE);
            intent.putExtra("title", payload.get("title"));
            intent.putExtra("body", payload.get("body"));
            intent.setAction("ACTION_SEND");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            PendingIntent yesNotiFeedbackIntent = null;
            PendingIntent noNotiFeedbackIntent = null;


            Intent yesBroadcastIntent = new Intent(this, NotificationReciever.class);
            yesBroadcastIntent.putExtra("order-id", payload.get("order_ref"));

            if(payload.containsKey("location_id"))
                yesBroadcastIntent.putExtra("location-id", payload.get("location_id"));

            yesBroadcastIntent.putExtra("notification-time", DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis());
            yesBroadcastIntent.putExtra("response", "yes");
            yesNotiFeedbackIntent = PendingIntent.getBroadcast(this, 300, yesBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent noBroadcastIntent = new Intent(this, NotificationReciever.class);
            noBroadcastIntent.putExtra("order-id", payload.get("order_ref"));

            if(payload.containsKey("location_id"))
                noBroadcastIntent.putExtra("location-id", payload.get("location_id"));

            noBroadcastIntent.putExtra("notification-time", DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis());
            noBroadcastIntent.putExtra("response", "no");
            noNotiFeedbackIntent = PendingIntent.getBroadcast(this, 301, noBroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon_orderconfirmed)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (getFeedback && yesNotiFeedbackIntent != null && noNotiFeedbackIntent != null) {
                notificationBuilder
                        .addAction(R.mipmap.ic_launcher, "Yes", yesNotiFeedbackIntent)
                        .addAction(R.mipmap.ic_launcher, "No", noNotiFeedbackIntent);
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Default Notification", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            try {
                notificationManager.notify(0, notificationBuilder.build());
            } catch (NullPointerException ex) {
                AppUtils.logException(ex);
            }
        }
    }

    private void sendNotification(String body) {
        String messageBody = body;
        String title = "Hungerbox";
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),ApplicationConstants.NOTIFICATION_SOURCE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_orderconfirmed)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Default Notification", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        try {
            notificationManager.notify(0, notificationBuilder.build());
        } catch (NullPointerException ex) {
            AppUtils.logException(ex);
        }
    }

    private void showRouteNotification(Map<String, String> payload, Intent intent) {
        String messageBody = (payload.containsKey(NotificationUtil.TEXT)) ? payload.get(NotificationUtil.TEXT) : "";
        String title = (payload.containsKey(NotificationUtil.TITLE)) ? payload.get(NotificationUtil.TITLE) : "Hungerbox";
        intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),ApplicationConstants.NOTIFICATION_SOURCE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_orderconfirmed)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String channelId = payload.get("channel_id");
            String channelName = payload.get("channel_name");

            if(channelId == null || channelId.equals("") || channelName == null || channelName.equals("")){
                channelId = NOTIFICATION_CHANNEL_ID;
                channelName = "Others";
            }

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId(channelId);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        if (payload != null && payload.containsKey(NotificationUtil.IMAGE)) {

            downloadImage(payload.get(NotificationUtil.IMAGE), payload.hashCode(), notificationBuilder);
        } else {
            try {
                notificationManager.notify(payload.hashCode(), notificationBuilder.build());
            } catch (NullPointerException ex) {
                AppUtils.logException(ex);
            }
        }
    }

    private void sendOrderNotification(Map<String, String> orderPayload) {
        if(orderPayload.get(NotificationUtil.REF_STATUS) == null || orderPayload.get(NotificationUtil.REF_BODY)== null ){
            return;
        }
        String messageBody = orderPayload.get(NotificationUtil.REF_BODY);
        Intent intent = new Intent(this, OrderDetailNewActivity.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Notification");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ApplicationConstants.BOOKING_ID, Long.parseLong(orderPayload.get(NotificationUtil.REF_ID)));
        intent.putExtra(ApplicationConstants.FROM_NOTIFICATION,true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_orderconfirmed)
                .setContentTitle(orderPayload.get(NotificationUtil.TITLE))
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if(orderPayload.get(NotificationUtil.REF_STATUS).equalsIgnoreCase(OrderUtil.PROCESSED)){
            notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String channelId = orderPayload.get("channel_id");
            String channelName = orderPayload.get("channel_name");


            if(channelId == null || channelId.equals("") || channelName == null || channelName.equals("")){
                channelId = ORD_NOTIFICATION_CHANNEL_ID;
                channelName = "Orders";
            }

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            if (orderPayload.get(NotificationUtil.REF_STATUS).equalsIgnoreCase(OrderUtil.PROCESSED)) {
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            }
            notificationBuilder.setChannelId(channelId);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            if (orderPayload.get(NotificationUtil.REF_STATUS).equalsIgnoreCase(OrderUtil.PROCESSED)) {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(3000);
            }
        }

        String orderId = orderPayload.get(NotificationUtil.REF_ID);

        if (orderPayload != null && orderPayload.containsKey(NotificationUtil.IMAGE)) {

            downloadImage(orderPayload.get(NotificationUtil.IMAGE), orderId.hashCode(), notificationBuilder);
        } else {
            notificationManager.notify(orderId.hashCode(), notificationBuilder.build());
        }
    }

    private void downloadImage(final String image, final int id, final NotificationCompat.Builder notificationBuilder) {

        new ImageDownload(image, id, notificationBuilder, getApplicationContext()).execute();

    }

    static class ImageDownload extends AsyncTask<Void, Void, Bitmap> {

        String image;
        int id;
        NotificationCompat.Builder notificationBuilder;
        Context context;

        ImageDownload(final String image, final int id, final NotificationCompat.Builder notificationBuilder, Context context) {
            this.id = id;
            this.image = image;
            this.notificationBuilder = notificationBuilder;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL ulrn = new URL(image);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                return BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap == null) {
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notificationBuilder.build());
            } else {
                try {
                    NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
                    notiStyle.bigPicture(bitmap);

                    notificationBuilder.setStyle(notiStyle);
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(id, notificationBuilder.build());
                } catch (Exception exp) {
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(id, notificationBuilder.build());
                    exp.printStackTrace();
                }
            }
        }
    }
}

