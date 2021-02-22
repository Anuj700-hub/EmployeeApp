package com.hungerbox.customer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.OrderUtil;

public class OrderUpdateService extends Service {
    public OrderUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            long orderId = intent.getLongExtra(ApplicationConstants.ORDER_ID, 0);
            if (orderId > 0) {
                Order order = new Order();
                order.setId(orderId);
                updateOrderAndPerformAction(order);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateOrderAndPerformAction(Order order) {
        order.getOrderDetail(true, true, true, true, this, new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        if (responseObject != null && responseObject.order != null) {
                            if (responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)
                                    ||
                                    responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {
                                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                                intent.putExtra(ApplicationConstants.ORDER, responseObject.order);
                                startService(intent);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                }
        );
    }
}
