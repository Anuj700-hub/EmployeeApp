package com.hungerbox.customer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.receiver.OrderAlarmReceiver;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {
            Order orderTemp = (Order) intent.getSerializableExtra(ApplicationConstants.ORDER);
            boolean showNotification = intent.getBooleanExtra(ApplicationConstants.SHOW_ORDER_NOTIFICATION, false);
            boolean appNotficationSetting = SharedPrefUtil.getBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, false);
            if (appNotficationSetting && !AppUtils.isCafeApp()) {
                if (orderTemp != null && showNotification)
                    checkOrderForNotification(orderTemp);
                else
                    scheduleOrderAlarm(orderTemp);
            }
        }

        return START_NOT_STICKY;
    }

    private void scheduleOrderAlarm(Order orderTemp) {
        if (orderTemp == null)
            return;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), OrderAlarmReceiver.class);

        long alarmTime;
        try {
            if (orderTemp.getVendor().getVendor().isRestaurant() && !orderTemp.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED))
                alarmTime = orderTemp.getEstimatedDeliveryTimeInmillis() + (ApplicationConstants.THIRTY_MIN_MILLIS);
            else
                alarmTime = orderTemp.getEstimatedDeliveryTimeInmillis();
        } catch (Exception e) {
            alarmTime = orderTemp.getEstimatedDeliveryTimeInmillis();
        }


//        long time = 0;
        if (orderTemp != null) {
            //TODO change for order
//            time = DateTimeUtil.getTimeForNotification(orderTemp.getConfirmedAt(), alarmTime);//(new Date().getTime()*1000) + order.getWaitTime();
            intent.putExtra(ApplicationConstants.ORDER_ID, orderTemp.getId());
            intent.putExtra(ApplicationConstants.ORDER, orderTemp);
            intent.putExtra(ApplicationConstants.ALARM_TYPE, ApplicationConstants.ORDER_NOTIFICATION);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) orderTemp.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
        }
//        else {
////            time = new Date().getTime() + (1000 * 3);
//        }
    }


    private void checkOrderForNotification(final Order orderTemp) {

        orderTemp.getOrderDetail(true, true, true, true, this, new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        if (responseObject != null && responseObject.getOrder() != null &&
                                !responseObject.getOrder().getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)
                                && !responseObject.getOrder().getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)) {
                            fireNotificationFor(orderTemp);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                }
        );
//        String url = UrlConstant.ORDER_DETAIL+orderTemp.getId()+"/1/1/1/1";

//        SimpleHttpAgent<OrderResponse> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
//                this,
//                url,
//                new ResponseListener<OrderResponse>() {
//                    @Override
//                    public void response(OrderResponse responseObject) {
//                            if(responseObject!=null && responseObject.getOrder()!=null &&
//                                    responseObject.getOrder().getOrderStatus()!= OrderUtil.REJECTED
//                                    && responseObject.getOrder().getOrderStatus()!=OrderUtil.DELIVERED){
//                                fireNotificationFor(orderTemp);
//                            }
//                    }
//                },
//                new ContextErrorListener() {
//                    @Override
//                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
//
//                    }
//                },
//                OrderResponse.class
//        );
//
//        orderResponseSimpleHttpAgent.get();
    }

    private void sendNotificationOrderCollect(Order orderTemp) {
        String format = "Your order is ready, please collect it";
//                "Your order with "+order.getVendorName()+"\nwith order ref: "+order.getOrderId()+"\n and pin :"+order.getPin();
        Notification.Builder mBuilder =
                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp)
                        .setContentTitle("Hungerbox update")
                        .setContentText("Your Order is ready")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(format));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);

        mBuilder.setAutoCancel(true);

//TODO uncomment after adding activity

//        String format = "Your order is ready, please collect it";
////                "Your order with "+order.getVendorName()+"\nwith order ref: "+order.getOrderId()+"\n and pin :"+order.getPin();
//        Notification.Builder mBuilder =
//                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp)
//                        .setContentTitle("Hungerbox update")
//                        .setContentText("Your Order is ready")
//                        .setStyle(new Notification.BigTextStyle()
//                                .bigText(format));
//
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        mBuilder.setSound(uri);
//
//        mBuilder.setAutoCancel(true);
//
//
//        Intent resultIntent = new Intent(this, OrderViewActivity.class);
////        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        resultIntent.putExtra(ApplicationConstants.ORDER, orderTemp);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(GlobalActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////                PendingIntent.getActivity(
////                        this,
////                        0,
////                        resultIntent,
////                        PendingIntent.FLAG_UPDATE_CURRENT
////                );
//
//        mBuilder.setContentIntent(resultPendingIntent);
//        int mNotificationId = Long.toString(orderTemp.getId()).hashCode();
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void fireNotificationFor(Order orderTemp) {
        if (orderTemp.getVendor().getVendor().isRestaurant()) {
            sendDeliveredNotification(orderTemp);
        } else {
            sendNotificationOrderCollect(orderTemp);
        }
    }

    private void sendDeliveredNotification(Order orderTemp) {
        String format = "Did You receive your order";
//                "Your order with "+order.getVendorName()+"\nwith order ref: "+order.getOrderId()+"\n and pin :"+order.getPin();
        Notification.Builder mBuilder =
                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp)
                        .setContentTitle("Hungerbox update")
                        .setContentText("Did you receive your order")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(format));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);

        mBuilder.setAutoCancel(true);

//TODO uncomment after adding activity


//TODO <pending>
//        String format = "Did You receive your order";
////                "Your order with "+order.getVendorName()+"\nwith order ref: "+order.getOrderId()+"\n and pin :"+order.getPin();
//        Notification.Builder mBuilder =
//                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp)
//                        .setContentTitle("Hungerbox update")
//                        .setContentText("Did you receive your order")
//                        .setStyle(new Notification.BigTextStyle()
//                                .bigText(format));
//
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        mBuilder.setSound(uri);
//
//        mBuilder.setAutoCancel(true);
//
//
//        Intent resultIntent = new Intent(this, OrderViewActivity.class);
////        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        resultIntent.putExtra(ApplicationConstants.ORDER, orderTemp);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(GlobalActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////                PendingIntent.getActivity(
////                        this,
////                        0,
////                        resultIntent,
////                        PendingIntent.FLAG_UPDATE_CURRENT
////                );
//
//        mBuilder.setContentIntent(resultPendingIntent);
//        int mNotificationId = Long.toString(orderTemp.getId()).hashCode();
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
