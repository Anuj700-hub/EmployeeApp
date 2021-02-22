package com.hungerbox.customer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.event.InternetConnectivityEvent;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;


public class InternetService extends Service {

    private static boolean isRunning = false;

    public InternetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (isRunning)
            return START_STICKY;
        else {
            isRunning = true;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        boolean wasConnected = SharedPrefUtil.getBoolean(ApplicationConstants.WAS_CONNECTED, false);

        if (wasConnected != isConnected) {
            SharedPrefUtil.putBoolean(ApplicationConstants.WAS_CONNECTED, isConnected);
            MainApplication.bus.post(new InternetConnectivityEvent(isConnected));
        }

        stopSelf();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;
    }
}
