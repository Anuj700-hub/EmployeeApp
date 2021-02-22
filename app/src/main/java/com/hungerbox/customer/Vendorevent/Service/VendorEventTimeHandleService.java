package com.hungerbox.customer.Vendorevent.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.IBinder;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.Vendorevent.receiver.VendorEventTimeReceiver;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.BatteryEventPayload;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.Calendar;

public class VendorEventTimeHandleService extends Service {
    public VendorEventTimeHandleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long lastBatteryTime = SharedPrefUtil.getLong(ApplicationConstants.LAST_BATTERY_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (lastBatteryTime - ApplicationConstants.FOUR_MINUTES >= currentTime) {
            return START_NOT_STICKY;
        }

        logBatteryLevelAndUpdateTimer();

        return START_NOT_STICKY;
    }

    private void logBatteryLevelAndUpdateTimer() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        batteryPct *= 100;
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        long vendorId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);

        BatteryEventPayload batteryEventPayload = new BatteryEventPayload(batteryPct, isCharging, address);
        String versionName = AppUtils.getVersionName();
        batteryEventPayload.setAppVersion(versionName);

        AppEvent appEvent = new AppEvent();
        appEvent.setVendorEventRegistrable(batteryEventPayload);
        appEvent.setDeviceId(address).setLocationId(locationId).setVendorId(vendorId).setEventName(ApplicationConstants.BATTERY_EVENT);

        if (vendorId > 0)
            storeAppEvent(appEvent);
        updateTimer();

    }

    private void updateTimer() {
        Intent intent = new Intent(this, VendorEventTimeReceiver.class);
        AppUtils.setAlarmForBattery(this, intent);
    }

    private void storeAppEvent(AppEvent appEvent) {
        if (!DbHandler.isStarted()) {
            DbHandler.start(getApplicationContext());
        }
        DbHandler dbHandler = DbHandler.getDbHandler(getApplicationContext());
        dbHandler.addAppEvent(appEvent);
        SharedPrefUtil.putLong(ApplicationConstants.LAST_BATTERY_TIME, Calendar.getInstance().getTimeInMillis());
        startOrderStatusService();
    }

    private void startOrderStatusService() {
        return;
//        Intent intent = new Intent(this, OrderStatusService.class);
//        startService(intent);
    }
}
