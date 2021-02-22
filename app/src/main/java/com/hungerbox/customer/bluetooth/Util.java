package com.hungerbox.customer.bluetooth;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.text.format.DateFormat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.Model.SaveViolation;
import com.hungerbox.customer.bluetooth.Model.Violation;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.OrderDetailActvity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.measite.minidns.record.A;

public class Util {

    public static final String BT_DEVICES = "nearby_devices";
    public static final String NEARBY_SWITCH = "nearby_switch";
    public static final String NOTIFICATION_SWITCH = "notification_switch";
    public static final String VIOLATION_SUMMARY = "Violation";
    public static final String BLUETOOTH_INIT_FOR_FIRST_TIME = "bluetooth_init_for_first_time";
    public static final int BLUETOOTH_INIT_REQUEST = 5054;

    final public static String LAST_SERVICE_TIME = "last_service_time";
    final public static String BLUETOOTH_LAST_ORDER_TIME = "bluetooth_last_order_time";
    final public static String BLUETOOTH_LAST_ORDER_LOCATION = "bluetooth_last_order_location";
    final public static String BLUETOOTH_LAST_ORDER_ID = "bluetooth_last_order_id";

    final public static String RSSI = "rssi_bt";
    final public static String THRESOLD = "thresold_bt";
    final public static String DURATION = "duration_bt";
    final public static String MAX_DURATION = "max_duration_bt";
    final public static String INFO_BT = "info_bt";
    public static final String BT_INFO_TEXT = "bt_info_text";
    public static final String INTERACTION_LOST_DURATION = "interaction_lost_duration";
    public static final String SERVER_DATA = "server_data";
    public static final String SEND_VIOLATION_COUNT = "send_violation_count";
    public static final String VIOLATION_API_MAX_DAYS = "violation_max_days";
    public static final String IS_USER_EXIT = "is_user_exit";

    public static void saveHashMap(String key, Object obj, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);

        SharedPrefUtil.putString(key, json);
    }
    static HashMap<String, HBDevice> getHashMap(String key, Context context) {

        String json = SharedPrefUtil.getString(key,"");

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, HBDevice>>() {
        }.getType();
        HashMap<String, HBDevice> obj;

        try{
            if (json.equals("")) {
                obj = new HashMap<>();
            }
            else {
                obj = gson.fromJson(json, type);
            }
        }catch (Exception exp){
            exp.printStackTrace();
            obj = new HashMap<>();
        }
        return obj;
    }

    static ArrayList<HBDevice> getServerData(String key){
         String json = SharedPrefUtil.getString(key,"");
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<HBDevice>>() {
        }.getType();

        ArrayList<HBDevice> obj;

        if (json.equals("")) {
            obj = new ArrayList<HBDevice>();
        }
        else {
            obj = gson.fromJson(json, type);
        }
        return obj;
    }

    static void showNotification(String message, Context context, int id){


        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.ble);

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        if(isScreenOn==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"hungerboxApp:MyLock");
            wl.acquire(600000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"hungerboxApp:MyCpuLock");
            wl_cpu.acquire(600000);
        }


        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("REQUEST_FOR", "PROXIMITY_FEATURE");
        intent.putExtra(CleverTapEvent.PropertiesNames.getSource(), ApplicationConstants.NOTIFICATION_SOURCE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Proximity Alert")
                .setSound(sound)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        notificationBuilder.setSmallIcon(R.drawable.icon_orderconfirmed);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("2", "Be Safe", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(sound, attributes);
            notificationBuilder.setChannelId("2");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = notificationBuilder.build();
        notificationManager.notify(id, notification);
    }

    public static void stopDeviceTracking(Context context){
        if(context!=null) {
            Intent intent = new Intent(context, BluetoothAlertService.class);
            context.stopService(intent);
        }
    }

    public static void saveDeviceToCSV(HBDevice device){

        String folderPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HB";
        File folder = new File(folderPath);
        if(!folder.exists()){
            folder.mkdir();
        }

        File file = new File(folderPath+"/device_data.csv");

        String deviceName = device.name;
        if(deviceName == null){
            deviceName = "";
        }
        String data = deviceName+","+device.address + "," + device.rssi + "," + DateFormat.format(" dd hh:mm:ss", System.currentTimeMillis()).toString();

        FileWriter writer;
        if(file.exists()){
            try {
                writer = new FileWriter(folderPath+"/device_data.csv", true);
                writer.write(System.getProperty( "line.separator" ));
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                writer = new FileWriter(folderPath+"/device_data.csv");
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void logDevice(BluetoothDevice device, short rssi, Context context) {



        HashMap<String, HBDevice> map = Util.getHashMap("map", context);

        if (map == null) {
            map = new HashMap<>();
        }

        HBDevice hb = map.get(device.getName());

        HBDevice sameAddressDevice = isSameAdressDeviceAvailable(device.getAddress(), map);

        long serviceDuration = SharedPrefUtil.getLong(Util.DURATION);

        if(hb != null){
            if(System.currentTimeMillis() - hb.time < serviceDuration){
                if(rssi<SharedPrefUtil.getInt(Util.RSSI)){
                    //Setting count for the devices 0 which are not nearby now
                    hb.count = 0;
                    map.put(hb.name,hb);
                    Util.saveHashMap("map",map,context);
                }
                return;
            }
        }

        if(hb == null && sameAddressDevice != null){
            if(System.currentTimeMillis() - sameAddressDevice.time < serviceDuration){
                if(rssi<SharedPrefUtil.getInt(Util.RSSI)){
                    //Setting count for the devices 0 which are not nearby now
                    sameAddressDevice.count = 0;
                    map.put(sameAddressDevice.name,sameAddressDevice);
                    Util.saveHashMap("map",map,context);
                }
                return;
            }else{
                hb = sameAddressDevice;
            }
        }

        String deviceIdentifier = device.getAddress();
        if (deviceIdentifier == null) {
            return;
        }

        if (hb == null) {
            hb = new HBDevice();
            hb.name = device.getName();
            hb.address = device.getAddress();
            hb.time = System.currentTimeMillis();
            hb.rssi = rssi;
            hb.locationId = SharedPrefUtil.getLong(Util.BLUETOOTH_LAST_ORDER_LOCATION);
        }else{
            if(System.currentTimeMillis() - hb.time > SharedPrefUtil.getLong(Util.INTERACTION_LOST_DURATION)){
                //Setting counter to 1 if the two devices interacting after duration interaction_lost_duration
                hb.count = 1;
            }else {
                hb.count = hb.count + 1;
            }
            hb.time = System.currentTimeMillis();
            hb.rssi = rssi;
            hb.locationId = SharedPrefUtil.getLong(Util.BLUETOOTH_LAST_ORDER_LOCATION);
        }

        if(ViolationLogManager.Companion.shouldEnableBluetoothForAllDay(context)){
            hb.locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, -1);
        }

        if(rssi >= SharedPrefUtil.getInt(Util.RSSI)){
            map.put(hb.name, hb);
            Util.saveHashMap("map", map,context);
        }else{
            hb.count = 0;
            map.put(hb.name, hb);
            Util.saveHashMap("map", map,context);
        }
        AppUtils.HbLog("device",hb.name+","+hb.count+","+hb.rssi);
    }

    private static HBDevice isSameAdressDeviceAvailable(String address, HashMap<String, HBDevice> map) {

        List<HBDevice> devices = new ArrayList<>(map.values());
        for (HBDevice device : devices) {
            if(address != null && device.address != null && device.address.equals(address)){
                return device;
            }
        }
        return null;
    }

    public static boolean isForegroundCompatible(Context context) {
        try {
            int targetSdk = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).applicationInfo.targetSdkVersion;

            return (targetSdk >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBluetoothDistancingActive(Context context, Order order) {

        if(context == null)
            return false;

        try {
            boolean isDeskOrder = false;
            if(order != null){
                isDeskOrder = (order.getDeskReference() != null && !order.getDeskReference().trim().isEmpty());
            }
            if (AppUtils.getConfig(context).getBluetooth_distancing() != null &&  android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();

                if(bluetoothAdapter == null){
                    return false;
                }
                if(AppUtils.getConfig(context).getBluetooth_distancing().getSupported_company() == 1){
                    return !isDeskOrder;
                }else if(AppUtils.getConfig(context).getBluetooth_distancing().getSupported_company() == 0){
                    return false;
                }
                else if(AppUtils.getConfig(context).getBluetooth_distancing().getSupported_company() == 2 &&
                        AppUtils.getConfig(context).getBluetooth_distancing().getSupported_locations() != null
                ){
                    if(order == null){
                        return AppUtils.getConfig(context).getBluetooth_distancing().getSupported_locations().contains(SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, -1));
                    }
                    else if(AppUtils.getConfig(context).getBluetooth_distancing().getSupported_locations().contains(order.getLocationId())){
                        return !isDeskOrder;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void sendViolationLog(Context activity){

        ArrayList<HBDevice> hbDevices = getServerData(Util.SERVER_DATA);
        ArrayList<HBDevice> latestDevices = new ArrayList<>();
        for(HBDevice hbDevice : hbDevices){
            if((System.currentTimeMillis() - hbDevice.time)<SharedPrefUtil.getInt(Util.VIOLATION_API_MAX_DAYS)*24*60*60*1000){
                latestDevices.add(hbDevice);
            }
        }
        saveHashMap(Util.SERVER_DATA,latestDevices,activity);
        hbDevices = getServerData(Util.SERVER_DATA);

        if(hbDevices.size()<=0){
            return;
        }
        Set<Long> locationSet = new HashSet<>();
        for(HBDevice hbDevice : hbDevices){
            locationSet.add(hbDevice.locationId);
        }
        ArrayList<Violation> violations = new ArrayList<>();
        for(long location : locationSet) {
            Violation violation = new Violation();
            violation.setLocation_id(location);
            ArrayList<Long> timeStamp = new ArrayList<>();
            for (HBDevice hbDevice : hbDevices) {
                if(hbDevice.locationId == location) {
                    timeStamp.add(hbDevice.time / 1000);
                }
            }
            violation.setTimestamps(timeStamp);
            violations.add(violation);
        }
        SaveViolation saveViolation = new SaveViolation();
        saveViolation.setViolations(violations);
        SimpleHttpAgent<SaveViolation> saveViolationsSimpleHttpAgent = new SimpleHttpAgent<SaveViolation>(
                activity,
                UrlConstant.VIOLATION_LOG,
                new ResponseListener<SaveViolation>() {
                    @Override
                    public void response(SaveViolation responseObject) {
                        saveHashMap(Util.SERVER_DATA,new ArrayList<>(),activity);
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        },SaveViolation.class);



        saveViolationsSimpleHttpAgent.post(saveViolation,new HashMap<>());

    }
}
