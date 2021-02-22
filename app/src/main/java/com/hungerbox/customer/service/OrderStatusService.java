package com.hungerbox.customer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.model.AppEvent;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;

import java.util.HashMap;
import java.util.List;

public class OrderStatusService extends Service {

    private static boolean isRunning = false;

    public OrderStatusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (isRunning) {
            return START_NOT_STICKY;
        } else {
            isRunning = true;
        }


        processOrders();

        return START_NOT_STICKY;
    }

    private void processOrders() {
        if (!DbHandler.isStarted()) {
            DbHandler.start(this);
        }
        DbHandler dbHandler = DbHandler.getDbHandler(this);
        List<AppEvent> appEvents = dbHandler.getFirstAppEvents();
        if (appEvents != null && !appEvents.isEmpty()) {
            updateAppEvents(appEvents);
        } else {
            stopSelf();
        }
    }

    private void updateAppEvents(final List<AppEvent> appEvents) {
        String url = UrlConstant.EVENT_API;


        SimpleHttpAgent<Object> vendorHttpAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        DbHandler.getDbHandler(getApplicationContext()).removeAppEvents(appEvents);
                        processOrders();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode != ContextErrorListener.NO_NET_CONNECTION && errorCode != ContextErrorListener.TIMED_OUT) {
                            DbHandler.getDbHandler(getApplicationContext()).removeAppEvents(appEvents);
                            processOrders();
                        } else
                            stopSelf();
                    }
                },
                Object.class
        );

        vendorHttpAgent.post(appEvents, new HashMap<String, JsonSerializer>());
    }

    private void updateAppEvent(final AppEvent appEvent) {
        String url = UrlConstant.EVENT_API;
        SimpleHttpAgent<Object> vendorHttpAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        DbHandler.getDbHandler(getApplicationContext()).removeAppEvent(appEvent);
                        processOrders();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode != ContextErrorListener.NO_NET_CONNECTION && errorCode != ContextErrorListener.TIMED_OUT) {
                            DbHandler.getDbHandler(getApplicationContext()).removeAppEvent(appEvent);
                            processOrders();
                        } else
                            stopSelf();
                    }
                },
                Object.class
        );

        vendorHttpAgent.post(appEvent, new HashMap<String, JsonSerializer>());
    }


    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

}
