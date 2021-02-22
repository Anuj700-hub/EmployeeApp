package com.hungerbox.customer.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.BluetoothDeviceCheckActivity;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.bluetooth.ViolationLogManager;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.AppReview;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FCMDevice;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.ShortCuts;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.activity.BookMarkActivity;
import com.hungerbox.customer.navmenu.activity.HistoryActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.DataHandler;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.activityOffline.GlobalActivityOffline;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.NotificationHomeActivity;
import com.hungerbox.customer.order.activity.OrderSuccessActivity;
import com.hungerbox.customer.prelogin.activity.HBWelcomeActivity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.receiver.OccasionClearReceiver;
import com.hungerbox.customer.util.view.ToastHB;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.juspay.juspaysafe.JuspaySafeBrowser;
import okhttp3.OkHttpClient;

/**
 * Created by peeyush on 22/6/16.
 */
public class AppUtils {

    public static final int ALARM_REQUEST_CODE = 707;


    public static void setupUser(String id, String email, String userName) {

    }

    public static void logException(Exception e) {

    }

    public static Config getConfig(Context context) {
        Config config;
        try {
            MainApplication mainApplication = (MainApplication) context.getApplicationContext();
            config = mainApplication.getConfig();
        } catch (Exception e) {
            EventUtil.FbEventLog(context, "Config Error", e.toString());
            e.printStackTrace();
            config = new Config();
        }
        return config;
    }


    public static void setAlarmFor(long time, Context context) {
        long currentTime = new Date().getTime();
        if (currentTime < time) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, OccasionClearReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        }
    }

    public static void doLogout(Context context) {

        try{
            if(Build.VERSION.SDK_INT > 25){
                ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

                shortcutManager.removeAllDynamicShortcuts();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }

        try{
            JuspaySafeBrowser.performLogout(context);
        }catch (Exception exp){
            exp.printStackTrace();
        }

        doLogoutServer(context);
        MainApplication.clearCart();//clear cart at logout
        SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
        SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);
        Intent intent = new Intent(context, HBWelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void doLogoutServer(Context context) {


        try {
            if (!DbHandler.isStarted())
                DbHandler.start(context);
            DbHandler.getDbHandler(context).deleteDataTable();
        }catch (Exception e ){
            e.printStackTrace();
        }
        Util.sendViolationLog(context);
        Util.stopDeviceTracking(context);
        ViolationLogManager.Companion.deleteAllViolations(context.getApplicationContext());

        SharedPrefUtil.remove(ApplicationConstants.OFFLINE_CERTIFICATE);
        SharedPrefUtil.remove(ApplicationConstants.OFFLINE_PRIVATE_KEY);

        String logoutUrl = UrlConstant.LOGOUT;
        SimpleHttpAgent<Object> logoutObjectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                context,
                logoutUrl,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                Object.class
        );

        logoutObjectSimpleHttpAgent.get();
    }


    public static void logUser(String userName) {

    }

    public static boolean isCafeApp() {
        String applicationId = BuildConfig.APPLICATION_ID;
        return applicationId.contains("cafe");
    }

    public static boolean isGuestApp(){
        if(BuildConfig.FLAVOR.equalsIgnoreCase("guest"))
            return true;
        return false;
    }

    public static boolean isSocialDistancingActive(Location location){

        if(MainApplication.appContext != null){
            if(getConfig(MainApplication.appContext).getSocial_distancing() == null){
                return false;
            }
        }

        if(location != null){
            return (location.getEnforcedCapacity() >= 1);
        }
        else if(SharedPrefUtil.getInt(ApplicationConstants.ENFORCED_CAPACITY, 0) >= 1){
            return true;
        }
        return false;
    }

    public static void setAlarmForBattery(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        long time = 0;
        time = new Date().getTime() + (1000l * 60l * 4l);
        time += (Math.random() * 1000l);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }


    public static void postDeviceData(Context context) {
        if (AppUtils.isCafeApp())
            return;
        String token = SharedPrefUtil.getString(ApplicationConstants.PREF_FCM_TOKEN, "");
        String token_to_server = SharedPrefUtil.getString(ApplicationConstants.PREF_FCM_TOKEN_SEND_TO_SERVER, "");
        Log.i("FCM FCM", token);
        String authToken = SharedPrefUtil.getString(ApplicationConstants.PREF_AUTH_TOKEN, "");
        if (!token.isEmpty() && !authToken.isEmpty() && !token.equals(token_to_server)) {
            String url = UrlConstant.DEVICE_REGISTER;
            SimpleHttpAgent<Object> fcmDeviceSimpleHttpAgent = new SimpleHttpAgent<Object>(
                    context,
                    url,
                    (ResponseListener<Object>) responseObject -> SharedPrefUtil.putString(ApplicationConstants.PREF_FCM_TOKEN_SEND_TO_SERVER, token),
                    (ContextErrorListener) (errorCode, error, errorResponse) -> {

                    },
                    Object.class
            );

            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            fcmDeviceSimpleHttpAgent.post(new FCMDevice().setDeviceId(android_id).setFcmId(token), new HashMap<>());
            CleverTapEvent.pushFCMEvents(token, context);
        }

    }


    public static Intent getHomeNavigationIntent(Context context) {
        Intent intent = new Intent(context, GlobalActivity.class);;
        return intent;
    }

    public static Intent getHomeNavigationIntentOffline(Context context){
        Intent intent = new Intent(context, GlobalActivityOffline.class);;
        return intent;
    }

    public static void addSubdomain(Context context, String subdomain) {
        SharedPrefUtil.putString(ApplicationConstants.COMPANY_SUBDOMAIN, subdomain);
    }

    public static void HbLog(String tag, Object message) {
        if (!BuildConfig.BUILD_TYPE.equalsIgnoreCase("release") && message != null) {
            Log.v(tag, getLog(message));
        }
    }

    public static String getLog(Object param) {

        StringBuilder strBuilder = new StringBuilder();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        strBuilder.append("(")
                .append(stack[4].getFileName())
                .append(':')
                .append(stack[4].getLineNumber())
                .append(')')
                .append("  ");
        strBuilder.append(param);

        return strBuilder.toString();
    }


    public static void setupUI(View view, Activity activity) {

        // Set up touch listener for non-text box views to hide keyboard.
        try {
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        AppUtils.hideKeyboard(activity, null);
                        return false;
                    }
                });
            }
        }
        catch (Exception e){

        }

        //If a layout container, iterate over children and seed recursion.
        try {
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(innerView, activity);
                }
            }
        }
        catch (Exception e){

        }
    }

    public static boolean isKitchenSystemEnabled(Order order){
        if(order.getVendor() != null && order.getVendor().getVendor()!= null && order.getVendor().getVendor().getIsKitchenSystemEnabled() == 1){
            return true;
        }
        return false;
    }

    public static void showKeyboard(Activity activity, EditText ed) {
        try {
            if(ed != null && ed.requestFocus()){
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(ed, InputMethodManager.SHOW_IMPLICIT);

                ed.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        imm.showSoftInput(ed, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 100);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity, EditText ed) {

        try {
            if (ed == null) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(ed.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showRatingDialog(final Context context, String title, String description, final String feedbackTitle, final String feedbackTDesc, final int deliveryCount, final OrderSuccessActivity activity) {

        try {


            final Dialog ratingDialog = new Dialog(context);
            ratingDialog.setCancelable(true);
            ratingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    activity.navigate();
                }
            });
            ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = View.inflate(context, R.layout.rating_dialog, null);
            final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
            final ImageView topStarImageView = view.findViewById(R.id.topStarImageView);
            final RelativeLayout secondContainer = view.findViewById(R.id.secon_container);

            final TextView enjoyingLabelTextView = view.findViewById(R.id.enjoyingLabelTextView);
            final Button rateBtn = view.findViewById(R.id.playStoreBtn);
            final EditText commentEditText = view.findViewById(R.id.commentEditText);
            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {

                        if (ratingDialog.isShowing()) {
                            ratingDialog.dismiss();
                        }

                        activity.navigate();

                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
                            context.startActivity(intent);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }

                        try {
                            JSONObject mixPanelValues = new JSONObject();
                            mixPanelValues.put(EventUtil.MixpanelEvent.SuperProperties.USER_RATING, String.valueOf(ratingBar.getRating()));
                            HBMixpanel.getInstance().registerSuperProperties(context, mixPanelValues);

                            EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.SuperProperties.USER_RATING, String.valueOf(ratingBar.getRating()));

                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    AppUtils.showToast("Scroll down to rate us", false, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);

                        try {
                            String url = UrlConstant.USER_APP_REVIEW;

                            SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                                    context,
                                    url,
                                    new ResponseListener<Object>() {
                                        @Override
                                        public void response(Object responseObject) {

                                        }
                                    },
                                    new ContextErrorListener() {
                                        @Override
                                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                                        }
                                    },
                                    Object.class
                            );

                            AppReview appReview = new AppReview();
                            appReview.setRating(ratingBar.getRating());
                            appReview.setText(commentEditText.getText().toString());

                            objectSimpleHttpAgent.post(appReview, new HashMap<String, JsonSerializer>());
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }
                }
            });
            enjoyingLabelTextView.setText(title);


            Button submitButton = view.findViewById(R.id.ratingDialogSubmitBtn);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        if (commentEditText.getText().toString().trim().equals("")) {
                            AppUtils.showToast("Please enter your valuable feedback..", true, 2);
                            return;
                        }
                        String url = UrlConstant.USER_APP_REVIEW;

                        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                                context,
                                url,
                                new ResponseListener<Object>() {
                                    @Override
                                    public void response(Object responseObject) {

                                    }
                                },
                                new ContextErrorListener() {
                                    @Override
                                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                                    }
                                },
                                Object.class
                        );

                        AppReview appReview = new AppReview();
                        appReview.setRating(ratingBar.getRating());
                        appReview.setText(commentEditText.getText().toString());

                        objectSimpleHttpAgent.post(appReview, new HashMap<String, JsonSerializer>());

                        if (ratingDialog.isShowing()) {
                            ratingDialog.dismiss();
                        }

                        final Dialog ratingDialog = new Dialog(context);
                        ratingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                activity.navigate();
                            }
                        });
                        ratingDialog.setCancelable(true);
                        ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        view = View.inflate(context, R.layout.rating_dialog_thank_you, null);

                        TextView feedbackTitleTextView = view.findViewById(R.id.feedbackTitleTextView);
                        feedbackTitleTextView.setText(feedbackTitle);

                        TextView feedbackTextView = view.findViewById(R.id.feedbackTextView);
                        feedbackTextView.setText(feedbackTDesc);

                        ratingDialog.show();
                        ratingDialog.setContentView(view);

                        try {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ratingDialog.cancel();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 1000);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }


                    try {
                        JSONObject mixPanelValues = new JSONObject();
                        mixPanelValues.put(EventUtil.MixpanelEvent.SuperProperties.USER_RATING, String.valueOf(ratingBar.getRating()));
                        HBMixpanel.getInstance().registerSuperProperties(context, mixPanelValues);

                        EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.SuperProperties.USER_RATING, String.valueOf(ratingBar.getRating()));

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });

            final LinearLayout bottomLayout = view.findViewById(R.id.bottomLayout);


            ratingBar.setStepSize(1.0f);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                    secondContainer.setBackground(ContextCompat.getDrawable(context, R.color.white));
                    if (v <= 3) {

                        topStarImageView.setImageResource(R.drawable.ic_star_sad_popup);
                        bottomLayout.setVisibility(View.VISIBLE);
                        rateBtn.setVisibility(View.GONE);
                    } else {
                        topStarImageView.setImageResource(R.drawable.ic_star_happy_popup);
                        bottomLayout.setVisibility(View.GONE);
                        rateBtn.setVisibility(View.VISIBLE);
                    }
                }
            });

            ratingDialog.show();
            ratingDialog.setContentView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void settingBTServiceParameters(Context context){
        SharedPrefUtil.putInt(Util.RSSI, AppUtils.getConfig(context).getBluetooth_distancing().getRssi());
        SharedPrefUtil.putInt(Util.THRESOLD, AppUtils.getConfig(context).getBluetooth_distancing().getThreshold());
        SharedPrefUtil.putLong(Util.DURATION, AppUtils.getConfig(context).getBluetooth_distancing().getDuration());
        SharedPrefUtil.putLong(Util.MAX_DURATION, AppUtils.getConfig(context).getBluetooth_distancing().getMax_duration());
        SharedPrefUtil.putString(Util.INFO_BT, AppUtils.getConfig(context).getBluetooth_distancing().getInfo_url());
        SharedPrefUtil.putString(Util.BT_INFO_TEXT,AppUtils.getConfig(context).getBluetooth_distancing().getBt_info_text());
        SharedPrefUtil.putLong(Util.INTERACTION_LOST_DURATION,AppUtils.getConfig(context).getBluetooth_distancing().getInteraction_lost_duration());
        SharedPrefUtil.putInt(Util.SEND_VIOLATION_COUNT,AppUtils.getConfig(context).getBluetooth_distancing().getSend_violation_count());
        SharedPrefUtil.putInt(Util.VIOLATION_API_MAX_DAYS, AppUtils.getConfig(context).getBluetooth_distancing().getViolation_api_max_days());
    }

    public static void sendCrashlyticsLogs(){
        try {
            FirebaseCrashlytics.getInstance().setUserId(SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID)+"");
        } catch (Exception e) {
        }

    }

    public static void sendNotificationForDeepLink(Context context, String body, String screen) {

        String messageBody = body;
        String title = "Hungerbox";
        Intent intent = new Intent(context, NotificationHomeActivity.class);
        intent.putExtra(ApplicationConstants.NotificationsConstants.DEEPLINK_SCREEN, screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_orderconfirmed)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(ApplicationConstants.DEEP_LINK_NOTIFICATION_ID, "Default Notification", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId(ApplicationConstants.DEEP_LINK_NOTIFICATION_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        try {
            notificationManager.notify(0, notificationBuilder.build());
        }
        catch (NullPointerException ex) {
            AppUtils.logException(ex);
        }
    }

    public static String checkBTPermissions(Context context){
        String message = "";
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            if(message.equals("")){
                message = "Please turn on your bluetooth";
            }else{
                message = message +"\nPlease turn on your bluetooth";
            }
        }

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if(message.equals("")){
                message = "Please turn on your Location";
            }else{
                message = message +"\nPlease turn on your Location";
            }
        }

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if(message.equals("")){
                message = "Please turn on your Location";
            }else{
                message = message +"\nPlease turn on your Location";
            }
        }

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean statusOfNetwork = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!statusOfGPS && !statusOfNetwork){
            if(message.equals("")){
                message = "Please turn on your Gps";
            }else{
                message = message +"\nPlease turn on your Gps";
            }
        }

        if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Method mSetScanMode;

            try {
                mSetScanMode = BluetoothAdapter.getDefaultAdapter().getClass().getMethod("setScanMode", int.class, int.class);
                mSetScanMode.invoke(BluetoothAdapter.getDefaultAdapter() , BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, BluetoothDeviceCheckActivity.BLUETOOTH_DISCOVERABLE_TIME);
            }
            catch (Exception e) {

                Log.e("discoverable", e.getMessage());
                if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){

                    if(message.equals("")){
                        message = "Your bluetooth device is not discoverable";
                    }
                    else{
                        message = message +"\nYour bluetooth device is not discoverable";
                    }
                }
            }
        }

        return message;
    }

    public static int getWidthImageBG(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static void showToast(String msg, boolean shortDuration, int type){

        showToast(msg, shortDuration, type, false);
    }

    public static void showToast(String msg, boolean shortDuration, int type, boolean center){

        int duration;
        if(shortDuration){
            duration = Toast.LENGTH_SHORT;
        }else{
            duration = Toast.LENGTH_LONG;
        }

        int backgroundColor = 0;
        int toastIcon = 0;
        if(type == 0){
            toastIcon = R.drawable.ic_clear_black_24dp;
            backgroundColor = R.drawable.toast_error_shape;
        }else if(type == 1){
            toastIcon = R.drawable.ic_check_black_24dp;
            backgroundColor = R.drawable.toast_success_shape;
        }else if(type == 2){
            toastIcon = R.drawable.ic_info_outline_black_24dp;
            backgroundColor = R.drawable.toast_info_shape;
        }

        Toast toast = new ToastHB().make(
                MainApplication.appContext,
                msg,
                duration,
                backgroundColor,
                "#ffffff",
                toastIcon
        );

        if(center){
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }else{
            toast.show();
        }
    }

    public static void createShortcut(Context context){
         try{
             if(Build.VERSION.SDK_INT < 26)
                 return;

             Intent intent;
             ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

             shortcutManager.removeAllDynamicShortcuts();

             ArrayList<ShortcutInfo> shortCutList = new ArrayList<>();

             for(ShortCuts sh : AppUtils.getConfig(context).getShortcuts()){
                 switch (sh.getKey()){
                     case ApplicationConstants.ShortCutsConstants.SHORTCUTS_HISTORY:

                         intent = new Intent(context, HistoryActivity.class).setAction(Intent.ACTION_CREATE_SHORTCUT);
                         intent.putExtra("fromShortcut", true);
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                         ShortcutInfo shortcut1 = new ShortcutInfo.Builder(context, "id1")
                                 .setShortLabel(sh.getName())
                                 .setLongLabel(sh.getName())
                                 .setIcon(Icon.createWithResource(context, R.drawable.ic_history_unselected))
                                 .setIntent(intent)
                                 .build();
                         shortCutList.add(shortcut1);
                         break;

                     case ApplicationConstants.ShortCutsConstants.SHORTCUTS_LATEST_ORDER:

                         intent = new Intent(context, HistoryActivity.class).setAction(Intent.ACTION_CREATE_SHORTCUT);
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                         intent.putExtra("fromShortcut", true);
                         intent.putExtra("value", "last_order");
                         ShortcutInfo shortcut2 = new ShortcutInfo.Builder(context, "id2")
                                 .setShortLabel(sh.getName())
                                 .setLongLabel(sh.getName())
                                 .setIcon(Icon.createWithResource(context, R.drawable.ic_history_unselected))
                                 .setIntent(intent)
                                 .build();
                         shortCutList.add(shortcut2);
                         break;

                     case ApplicationConstants.ShortCutsConstants.SHORTCUTS_BOOKMARK:

                         intent = new Intent(context, BookMarkActivity.class).setAction(Intent.ACTION_CREATE_SHORTCUT);
                         intent.putExtra("fromShortcut", true);
                         intent.putExtra("value", "bookmark");
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                         intent.putExtra(CleverTapEvent.PropertiesNames.getSource(),"Shortcut");
                         intent.putExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION, ApplicationConstants.BOOKMARK_FROM_NAVBAR);
                         ShortcutInfo shortcut3 = new ShortcutInfo.Builder(context, "id3")
                                 .setShortLabel(sh.getName())
                                 .setLongLabel(sh.getName())
                                 .setIcon(Icon.createWithResource(context, R.drawable.ic_bookmark_unselected))
                                 .setIntent(intent)
                                 .build();
                         shortCutList.add(shortcut3);
                         break;

                     default:
                         break;
                 }

                 shortcutManager.addDynamicShortcuts(shortCutList);
             }
         }catch (Exception exp){
             exp.printStackTrace();
         }
    }

    public static long getAuthExpiry(long timeLeft,Context context){
        try {
            //calculating the value and converting to millisecond
            //timeLeft * Expiry Percent/100 * 1000
            long diff = getConfig(context).getAuth_expiry_as_percent() * timeLeft * 10;
            long currentTimeInMillis = DateTimeUtil.adjustCalender(context).getTimeInMillis();
            long refreshTimeInMillis = currentTimeInMillis + diff;
            Log.d("Auth token", "token refresh time : "+DateTimeUtil.getDateString12Hour(refreshTimeInMillis));
            Log.d("Auth token", "token expires at : "+DateTimeUtil.getDateString12Hour(currentTimeInMillis+(timeLeft*1000)));
            return refreshTimeInMillis;
        } catch (Exception e){
            return -1;
        }

    }

    public static boolean isTimeInBetween(String startTime, String endTime, Calendar currentTime){
        if(currentTime.getTimeInMillis() >= DateTimeUtil.getTodaysTimeFromString(startTime) && currentTime.getTimeInMillis() <= DateTimeUtil.getTodaysTimeFromString(endTime)){
            return true;
        }
        return false;
    }

    public static boolean isFlavorAllowed(){
        if(AppUtils.isCafeApp()){
            return false;
        }
        return true;
    }

    private static String getApplicationIdForWatermark(){
        String applicationId = BuildConfig.APPLICATION_ID;
        if (BuildConfig.APPLICATION_ID.contains("common"))
            return SharedPrefUtil.getString(ApplicationConstants.PREF_UNIFIED_COMPANY_ID, "");
        else
            return applicationId;
    }

    public static String getConfigUrlForWatermark(){
        String applicationId = getApplicationIdForWatermark();
        if (BuildConfig.APPLICATION_ID.contains("common"))
            return UrlConstant.CONFIG_URL_GET + applicationId + "/android?unified=true";
        else
            return UrlConstant.CONFIG_URL_GET + applicationId + "/android";
    }

    public static OkHttpClient getstethoOkHttpBuilder(){
        return new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
    }

    public static ArrayList<String> getCertificate(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            KeyPairGenerator keyGen128 = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(512, random);//128 and 256 bit doesn't work

            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();//to keep
            PublicKey publicKey = pair.getPublic();//to send

            ArrayList<String> keyList = new ArrayList<>();
            keyList.add(Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));
            keyList.add(Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT));
            return keyList;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileExtension(String filename){
        String extension = "";
        try {
            if (filename.contains(".")) {
                extension = filename.substring(filename.lastIndexOf(".") + 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return extension;
    }

    public static boolean compareAppVersion(String compareVersion){
        try {

            if(compareVersion == null || compareVersion.trim().length()==0){
                return false;
            }

            int res = 0;
            String appCurrentVersion = AppUtils.getVersionName();

            String[] appNumbers = appCurrentVersion.split("\\.");
            String[] compareNumbers = compareVersion.split("\\.");

            // To avoid IndexOutOfBounds
            int maxIndex = Math.min(appNumbers.length, compareNumbers.length);

            for (int i = 0; i < maxIndex; i++) {
                int appVersionPart = Integer.parseInt(appNumbers[i]);
                int compareVersionPart = Integer.parseInt(compareNumbers[i]);

                if (appVersionPart < compareVersionPart) {
                    res = -1;
                    break;
                } else if (appVersionPart > compareVersionPart) {
                    res = 1;
                    break;
                }
            }
            return res >= 0;
        }catch (Exception e){
            return false;
        }

    }

    public static Vendor getVendorById(Context context , long vendorId){
        if (!DbHandler.isStarted())
            DbHandler.start(context);

        return DbHandler.getDbHandler(context).getVendorFor(vendorId);
    }

    public static List<Vendor> getAllActiveVendor(Context context){
        if (!DbHandler.isStarted())
            DbHandler.start(context);

        return DbHandler.getDbHandler(context).getActiveVendorList();
    }
    public static String getVersionName(){
        try {
            return BuildConfig.VERSION_NAME.split("_")[0];
        }catch (Exception e){
            e.printStackTrace();
            return BuildConfig.VERSION_NAME;
        }

    }

    public static void resetCurrentUserDataClass(Context context){
        HashMap<String, String> objectIds = new HashMap<>();
        objectIds.put(ApplicationConstants.OBJECT_ID_1, SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0) + "");
        objectIds.put(ApplicationConstants.OBJECT_ID_2,"");
        DataHandler.resetDataClass(context, UserReposne.class, objectIds);
    }

    public static boolean isEligibleForBluetooth(){
        return (BuildConfig.FLAVOR.equalsIgnoreCase("common") || BuildConfig.FLAVOR.equalsIgnoreCase("eatgood"));
    }

    public static long getNextWorkingDayTime(Ocassion.PreOrderDayType preOrderDayType, long vendorStartTime){

        int today = DateTimeUtil.adjustCalender(MainApplication.appContext).get(Calendar.DAY_OF_WEEK);
        int diff = 1;

        if(preOrderDayType == Ocassion.PreOrderDayType.TODAY){
            diff = 0;
        }
        else if(preOrderDayType == Ocassion.PreOrderDayType.NEXT_WORKING_DAY_5){

            if(today == Calendar.FRIDAY)
                diff = 3;
            else if(today == Calendar.SATURDAY)
                diff = 2;
        }
        else if(preOrderDayType == Ocassion.PreOrderDayType.NEXT_WORKING_DAY_6 && today == Calendar.SATURDAY){
            diff = 2;
        }

        return (vendorStartTime + (24 * 60 * 60 * 1000) * diff);
    }

    public static Config.SpaceManagement getSpaceConfig(Activity activity){
        return AppUtils.getConfig(activity).getSpace_management();
    }

    public static Config.SpaceType getSpaceType(Activity activity,String key){
        Config.SpaceManagement spaceManagement = getSpaceConfig(activity);
        if(spaceManagement!=null && spaceManagement.getSpaceTypes()!=null){
            for(Config.SpaceType spaceType : spaceManagement.getSpaceTypes()){
                if(spaceType.getKey().equalsIgnoreCase(key)){
                    return spaceType;
                }
            }
        }
        return null;
    }
}
