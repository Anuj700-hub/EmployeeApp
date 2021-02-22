package com.hungerbox.customer.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hungerbox.customer.model.EnterExitConfig;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hungerbox.customer.util.ApplicationConstants.ACTION_START;

public class BluetoothStopService extends Service {

    ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private Order order;
    String tagForApiRequest = "";
    private long orderId;
    public Handler handler = null;
    public static Runnable runnable = null;

    public BluetoothStopService(){

    }


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
                orderId = intent.getLongExtra("order_id",0);
                tagForApiRequest = intent.getStringExtra("tag_for_api_request");
                getOrderDetail();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        if(System.currentTimeMillis() - SharedPrefUtil.getLong(Util.BLUETOOTH_LAST_ORDER_TIME) < SharedPrefUtil.getLong(Util.MAX_DURATION))
        {
            scheduler.scheduleAtFixedRate
                    (() -> getOrderDetail(), 0, 120000, TimeUnit.MILLISECONDS);
        }
        else {
            scheduler.shutdown();
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();


        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleAtFixedRate
                (() -> getOrderDetail(), 0, 120000, TimeUnit.MILLISECONDS);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getOrderDetail() {

        String url;
        url = UrlConstant.ORDER_DETAIL + orderId + "/1/1/0/1";

        SimpleHttpAgent<OrderResponse> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
                this,
                url,
                responseObject -> {

                    if (responseObject == null) {
                        AppUtils.showToast("Unable to fetch Your Order", false, 0);
                    }
                    else {
                        this.order = responseObject.order;
                        if(order.getUserExitTime()!=-1){
                            SharedPrefUtil.putBoolean(Util.IS_USER_EXIT,true);
                            stopBTStopService();
                        }
                    }

                },
                (errorCode, error, errorResponse) -> {
                    AppUtils.showToast("Unable to fetch Your Order", false, 0);
                },
                OrderResponse.class
        );
        orderResponseSimpleHttpAgent.get(tagForApiRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopBTStopService(){
        scheduler.shutdown();
        Intent intent = new Intent(this, BluetoothStopService.class);
        stopService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            if(scheduler != null){
                stopBTStopService();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }
}
