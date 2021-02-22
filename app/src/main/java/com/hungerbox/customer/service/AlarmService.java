package com.hungerbox.customer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.event.CloseChatHeadEvent;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.receiver.AlarmReceiver;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

import java.util.Date;

import static android.view.View.VISIBLE;

public class AlarmService extends Service {

    private static final String ORDER_HEAD_TAG = "order head";
    WindowManager windowManager;
    ImageView chatHead;
    ClipData dragData;
    WindowManager.LayoutParams params;
    boolean isStarted = false;
    Order order;
    private View chatHeadView;
    private LinearLayout closeContainer;

    public AlarmService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Order orderTemp;
        int alarmType;
        long orderId = -1;
        boolean isChatEnabled = SharedPrefUtil.getBoolean(ApplicationConstants.CHAT_HEAD_SETTING, false);
        if (!isChatEnabled)
            return START_NOT_STICKY;
        if (isStarted && chatHead != null) {
            try {
                orderTemp = (Order) intent.getSerializableExtra(ApplicationConstants.ORDER);
                order = orderTemp;
            } catch (NullPointerException e) {
                orderTemp = null;
            }
            return START_NOT_STICKY;
        } else
            isStarted = true;

        try {
            MainApplication.bus.register(this);
        } catch (Exception e) {

        }


        if (!AppUtils.isCafeApp() && checkDrawOverlayPermission()) {
            try {
                orderTemp = (Order) intent.getSerializableExtra(ApplicationConstants.ORDER);
            } catch (NullPointerException e) {
                orderTemp = null;
            }
            order = orderTemp;
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            //TODO uncomment after adding chat head asset
//            chatHead = new ImageView(this);
//            chatHead.setImageResource(R.mipmap.ic_launcher);
////            chatHead.setBackgroundResource(R.drawable.circle_border);
//            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
////            chatHeadView = layoutInflater.inflate(R.layout.order_head, null, false);
//
//            chatHead = (ImageView) chatHeadView.findViewById(R.id.iv_order_head);
//            chatHead.setTag(ORDER_HEAD_TAG);
//
//            closeContainer = (LinearLayout) layoutInflater.inflate(R.layout.head_close_container, null, false);
//            closeContainer.setVisibility(View.GONE);
//            params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//            params.gravity = Gravity.TOP | Gravity.LEFT;
//            params.x = 0;
//            params.y = 200;
////          params.windowAnimations = android.R.style.Animation_Dialog;
//
//            closeContainer.setOnDragListener(new myDragEventListener());
//
//            chatHeadView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EventUtil.logEvent(new CustomEvent("ChatHead_Clicked"));
//                    if (order == null) {
//                        Intent intent = new Intent(getApplicationContext(), LiveOrderActivity.class);
//                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), OrderViewActivity.class);
//                        intent.putExtra(ApplicationConstants.ORDER, order);
//                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                    chatHead.setVisibility(View.GONE);
//                    stopSelf();
//                }
//            });
//
//            chatHeadView.setOnTouchListener(new View.OnTouchListener() {
//                GestureDetector gestureDetector = new GestureDetector(AlarmService.this, new LongPressConfirm());
//                private int initialX;
//                private int initialY;
//                private float initialTouchX;
//                private float initialTouchY;
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (gestureDetector.onTouchEvent(event)) {
//                        chatHeadView.performClick();
//                    } else {
//                        int[] coords = new int[2];
//                        int[] headCoords = new int[2];
//                        closeContainer.getLocationOnScreen(coords);
//                        chatHeadView.getLocationOnScreen(headCoords);
//                        int chatheadPos = headCoords[1] + chatHeadView.getHeight();
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                initialX = params.x;
//                                initialY = params.y;
//                                initialTouchX = event.getRawX();
//                                initialTouchY = event.getRawY();
//                                closeContainer.setVisibility(VISIBLE);
//
//                                AppUtils.HbLog("draging", "ACTION_DOWN");
//                                return true;
//                            case MotionEvent.ACTION_UP:
//                                AppUtils.HbLog("draging", "ACTION_UP");
//                                if (chatheadPos > coords[1])
//                                    stopSelf();
//                                closeContainer.setVisibility(View.GONE);
//                                return true;
//                            case MotionEvent.ACTION_MOVE:
//                                params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                                windowManager.updateViewLayout(chatHeadView, params);
//                                AppUtils.HbLog("draging", "ACTION_MOVE");
//                                if (chatheadPos > coords[1])
//                                    closeContainer.setBackgroundColor(getResources().getColor(R.color.red_transparent));
//                                else
//                                    closeContainer.setBackgroundColor(getResources().getColor(R.color.transparent_dark));
//                                return true;
//                        }
//                    }
//                    return false;
//                }
//            });
//
//            WindowManager.LayoutParams closeParams = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//            closeParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//
//            windowManager.addView(chatHeadView, params);
//            windowManager.addView(closeContainer, closeParams);


        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Subscribe
    public void onCloseChatHeadEvent(CloseChatHeadEvent closeChatHeadEvent) {
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MainApplication.bus.unregister(this);
        if (chatHeadView != null) {
            windowManager.removeView(chatHeadView);
        }
        if (closeContainer != null) {
            windowManager.removeView(closeContainer);
        }
    }

    private void setAlarmFor(Order order) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        long time = 0;
        if (order != null) {
            //TODO change for order
            time = DateTimeUtil.getTimeForNotification(order.getConfirmedAt(), order.getEstimatedDeliveryTime() + (10 * 60));//(new Date().getTime()*1000) + order.getWaitTime();
            intent.putExtra(ApplicationConstants.ORDER_ID, order.getId());
            intent.putExtra(ApplicationConstants.ORDER, order);
            intent.putExtra(ApplicationConstants.ALARM_TYPE, ApplicationConstants.ORDER_FEEDBACK);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) order.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        } else {
            time = new Date().getTime() + (1000 * 3);
        }

    }


    private void showNotificationForFeedback(Order order) {
        String format = "Please give the feedback for today's food, so that we can serve you better. Thank you. :)";
//                "Your order with "+order.getVendorName()+"\nwith order ref: "+order.getOrderId()+"\n and pin :"+order.getPin();
        Notification.Builder mBuilder =
                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp)
                        .setContentTitle("hunger update")
                        .setContentText("Feedback for today's food")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(format));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);

        mBuilder.setAutoCancel(true);
//TODO uncomment afet Global activity

//        Intent resultIntent = new Intent(this, OrderViewActivity.class);
////        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        resultIntent.putExtra(ApplicationConstants.ORDER, order);
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
//        int mNotificationId = 001;
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void showNotificationForOrder(long orderId, Order order) {
        if (order == null || AppUtils.getConfig(getApplicationContext()).is_location_fixed()) {
            return;
        }
        String format = "Your order with " + order.getVendorName() + " is ready.\norder ref: " + order.getOrderId() + "\npin :" + order.getPin();
        Notification.Builder mBuilder =
                new Notification.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_restaurant_menu_white_18dp) TODO uncomment after asset
                        .setContentTitle("hunger update")
                        .setContentText("Your order is ready")
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(format));

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSound(uri);

        mBuilder.setAutoCancel(true);
//TODO unComment after ORderView
//        Intent resultIntent = new Intent(this, OrderViewActivity.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        resultIntent.putExtra(ApplicationConstants.ORDER, order);
////        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        mBuilder.setContentIntent(resultPendingIntent);
//        int mNotificationId = 001;
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

    private void closeOrderHead() {
        stopSelf();
    }

    public boolean checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(getApplicationContext());
        } else {
            return true;
        }
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private static Drawable shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            // Creates a draggable image that will fill the Canvas provided by the system.
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth() / 2;

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight() / 2;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }

    protected class myDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:

                    closeContainer.setVisibility(VISIBLE);
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
//                        v.setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
//                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    closeContainer.setVisibility(VISIBLE);
                    // Applies a green tint to the View. Return true; the return value is ignored.

//                    v.setColorFilter(Color.GREEN);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    AppUtils.HbLog("Peeyush", "Drag location");
                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    AppUtils.HbLog("Peeyush", "Drag exit");
//                    closeContainer.setVisibility(View.GONE);

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
//                    v.setColorFilter(Color.BLUE);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:
                    AppUtils.HbLog("Peeyush", "Drag drop");
                    closeContainer.setVisibility(View.GONE);

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
//                    dragData = item.getText();

                    // Displays a message containing the dragged data.
//                    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG);

                    // Turns off any color tints
//                    v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();
//                    chatHead.setX(event.getX());
//                    chatHead.setY(event.getY());

//                    if(event.getY()>=closeContainer.getY())
                    closeOrderHead();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    AppUtils.HbLog("Peeyush", "Drag ended");

                    closeContainer.setVisibility(View.GONE);
                    // Turns off any color tinting
//                    v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {

                    } else {

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    AppUtils.HbLog("Drag Drop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    }

    private class LongPressConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }
}
