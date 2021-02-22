package com.hungerbox.customer.bluetooth;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;
import static com.hungerbox.customer.util.ApplicationConstants.ACTION_START;
import static com.hungerbox.customer.util.ApplicationConstants.ACTION_STOP;

public class BluetoothAlertService extends Service {

    ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private BluetoothDeviceReceiver bluetoothReceiver = new BluetoothDeviceReceiver();
    private GattClient gattClient;
    AlarmManager alarmManager;
    PendingIntent alarmPendingIntent;
    int WAKE_UP_INTERVAL_MINUTES = 30;
    AutoRestartJob autoRestartJob = new AutoRestartJob();

    public BluetoothAlertService(){

    }

    private static String TAG = "BluetoothAlertService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            if (intent == null || intent.getAction() == null || intent.getAction() == ACTION_START) {
                startBLE(intent);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (Util.isForegroundCompatible(this)){
            manageForeground();
        }

        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        scheduler.scheduleAtFixedRate
                (() -> discovery(), 0, 10000, TimeUnit.MILLISECONDS);
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            alarmPendingIntent = wrapAsForegroundService(pingService(getApplicationContext()), getApplicationContext());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (Util.isForegroundCompatible(this)){
            manageForeground();
        }

        gattClient = new GattClient();

        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleAtFixedRate
                (() -> discovery(), 0, 10000, TimeUnit.MILLISECONDS);
    }

    public static Intent startService(Context context) {
        Intent serviceIntent = new Intent(context, BluetoothAlertService.class);
        serviceIntent.setAction(ACTION_START);
        return serviceIntent;
    }

    public static Intent stopService(
            Context context
    ) {
        Intent serviceIntent = new Intent(context, BluetoothAlertService.class);
        serviceIntent.setAction(ACTION_STOP);
        return serviceIntent;
    }

    public static Intent pingService(Context context) {
        Intent serviceIntent = new Intent(context, BluetoothAlertService.class);
        serviceIntent.setAction(ACTION_START);
        return serviceIntent;
    }

     void startBLE(Intent intent) {
        try {
            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(
                    ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * 60 * WAKE_UP_INTERVAL_MINUTES,
                    alarmPendingIntent
            );

            if (intent != null && intent.getBooleanExtra(AutoRestartReceiver.EXTRAKEY_START, true) && autoRestartJob != null) {
                autoRestartJob.setUp(this, alarmManager);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Boolean isRunning(Context context) {
        ActivityManager manager;
        if(context.getSystemService(Context.ACTIVITY_SERVICE) instanceof ActivityManager){
             manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        else{
            return false;
        }

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BluetoothAlertService.class.getName() == service.service.getClassName()) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void discovery() {


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkAndResetName(bluetoothAdapter);

        registerReceiver();
        bluetoothAdapter.startDiscovery();

        String message = AppUtils.checkBTPermissions(this.getApplicationContext());
        if(!message.equals("")){
            if(ViolationLogManager.Companion.isBluetoothAllDayEnabled(this.getApplicationContext())){
                AppUtils.sendNotificationForDeepLink(this.getApplicationContext(), message, ApplicationConstants.Navigation.ACCOUNT_SCREEN);
                SharedPrefUtil.putBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, false);
                if(autoRestartJob != null)
                    autoRestartJob.cancel();

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getSource(),"BLE_Service");
                    map.put(CleverTapEvent.PropertiesNames.getBle_service_stopped_reason(),message);
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBle_service_stopped(),map,getApplicationContext());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                Util.stopDeviceTracking(this);
                return;
            }
            else if(SharedPrefUtil.getBoolean(Util.NEARBY_SWITCH,true)) {
                Util.showNotification(message, this, 0);
            }
        }

        if(SharedPrefUtil.getBoolean(Util.NEARBY_SWITCH,true)) {
            String deviceInLastOneMin = getDevicesListName();
            if (deviceInLastOneMin.length() > 0) {
                String alertMessage = "Maintain a distance of more than 6ft to stay safe";
                if(SharedPrefUtil.getBoolean(Util.NOTIFICATION_SWITCH,true)) {
                    Util.showNotification(alertMessage, this, 3);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bluetoothAdapter.isEnabled()) {
            startBLEScan();
        }

        if(ViolationLogManager.Companion.isBluetoothAllDayEnabled(getApplicationContext())){
            if(!ViolationLogManager.Companion.shouldEnableBluetoothForAllDay(getApplicationContext())){

                try{
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getSource(),"BLE_Service");
                    map.put(CleverTapEvent.PropertiesNames.getBle_service_stopped_reason(), "not satisfying condition for all day");
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getBle_service_stopped(),map,getApplicationContext());
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                SharedPrefUtil.putBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, false);
                stopBTService();
            }
        }
        else{
            if(System.currentTimeMillis() - SharedPrefUtil.getLong(Util.BLUETOOTH_LAST_ORDER_TIME) >= SharedPrefUtil.getLong(Util.MAX_DURATION) || SharedPrefUtil.getBoolean(Util.IS_USER_EXIT,false)){
                stopBTService();
                SharedPrefUtil.putBoolean(Util.IS_USER_EXIT,false);
            }
        }
    }

    void checkAndResetName(BluetoothAdapter bluetoothAdapter){
        try{
            long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
            String deviceName = bluetoothAdapter.getName();

            if(userId != 0 && ( deviceName == null || (deviceName != null && !deviceName.startsWith("HB-")))){
                bluetoothAdapter.setName("HB-"+ userId);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopBTService(){
        Util.sendViolationLog(this);
        stopBLEScan();
        scheduler.shutdown();
        Util.stopDeviceTracking(this);
    }

    private String getDevicesListName() {

        long lastServiceTime = SharedPrefUtil.getLong(Util.LAST_SERVICE_TIME, 0);

        if(System.currentTimeMillis() - lastServiceTime < SharedPrefUtil.getLong(Util.DURATION)){
            return "";
        }

        StringBuilder  name = new StringBuilder();
        Comparator<Map.Entry<String, HBDevice>> valueComparator = (o1, o2) -> {
            long v1 = o1.getValue().time;
            long v2 = o2.getValue().time;
            return (int) (v2 - v1);
        };

        HashMap<String, HBDevice> map = Util.getHashMap("map", this);

        long serviceRecall = SharedPrefUtil.getLong(Util.DURATION,0);

        if (map != null){
            List<Map.Entry<String, HBDevice>> listOfEntries = new ArrayList<Map.Entry<String, HBDevice>>(map.entrySet());
            Collections.sort(listOfEntries, valueComparator);
            LinkedHashMap<String, HBDevice> sortedByValue = new LinkedHashMap<String, HBDevice>(listOfEntries.size());
            for(Map.Entry<String, HBDevice> entry : listOfEntries){
                sortedByValue.put(entry.getKey(), entry.getValue());
            }
            Iterator mapIterator = sortedByValue.entrySet().iterator();
            ArrayList<HBDevice> serverData = Util.getServerData(Util.SERVER_DATA);
            while (mapIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry)mapIterator.next();
                HBDevice device = (HBDevice)mapElement.getValue();
                if((device.time + serviceRecall) >= System.currentTimeMillis() && device.count>=SharedPrefUtil.getInt(Util.THRESOLD)) {

                    ViolationLogManager.Companion.saveUserViolation(this.getApplicationContext(), device.name, device.locationId);

                    if(name.length() == 0) {
                        name.append(device.name);
                    }else{
                        name.append(",").append(device.name);
                    }
                    serverData.add(device);
//                    Util.saveDeviceToCSV(device);
                    //api call
                }
            }
            Util.saveHashMap(Util.SERVER_DATA,serverData,this);

            if(serverData.size()>SharedPrefUtil.getInt(Util.SEND_VIOLATION_COUNT)){
                Util.sendViolationLog(this);
            }
        }

        SharedPrefUtil.putLong(Util.LAST_SERVICE_TIME, System.currentTimeMillis());
        return name.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            AppUtils.HbLog(TAG,"OnScanResult");

           try{
               if(result.getDevice().getName() != null && (result.getDevice().getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE || result.getDevice().getName().contains("HB"))){
                   Util.logDevice(result.getDevice(), (short) result.getRssi(), BluetoothAlertService.this);
               }
               else{
                   gattClient.startGattClient(getApplicationContext(), result);
               }
           }catch (Exception exp){
               exp.printStackTrace();
           }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            AppUtils.HbLog(TAG,"onScanFailed => "+errorCode);

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startBLEScan() {

        ScanSettings.Builder settings = new ScanSettings.Builder().setScanMode(SCAN_MODE_LOW_POWER);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getBluetoothLeScanner().startScan(null,settings.build(),scanCallback);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopBLEScan() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void manageForeground()
    {

        CharSequence channelName = "HungerBox Proxima";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String channelId = "Syncing Data";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(false);
        channel.setSound(null, null);
        channel.setShowBadge(false);
        channel.setDescription("Stay safe from COVID-19");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(getResources().getString(R.string.app_name));
        bigTextStyle.bigText("HungerBox Proxima is scanning for devices nearby to keep you safe!");
        notificationBuilder.setStyle(bigTextStyle);

        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Stay safe from COVID-19")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setSmallIcon(R.drawable.icon_orderconfirmed)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            if(scheduler != null){
                scheduler.shutdown();
                stopBLEScan();
            }
            if(bluetoothReceiver!=null){
                unregisterReceiver(bluetoothReceiver);
            }

        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ContextCompat.startForegroundService(this, startService(this.getApplicationContext()));
    }

    PendingIntent wrapAsForegroundService(Intent intent, Context context){
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return PendingIntent.getForegroundService(context, 0, intent, 0);
            }
            else {
                return wrapAsService(intent, context);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    PendingIntent wrapAsService(Intent intent, Context context){
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
