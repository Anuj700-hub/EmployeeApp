package com.hungerbox.customer.config.action;

import android.app.ActivityManager;
import android.content.Context;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.util.AppUtils;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by peeyush on 12/8/16.
 */
public class CardClearTask extends TimerTask {

    private static Timer timer;
    private static CardClearTask cardClearTask;
    Context context;

    private CardClearTask(Context context) {
        this.context = context;
    }

    public static CardClearTask getInstance(Context context) {
        if (cardClearTask == null) {
            timer = new Timer();
            cardClearTask = new CardClearTask(context);
        }

        return cardClearTask;
    }

    @Override
    public void run() {
        //clear the cart if not used
        if (isAppOnForeground(context)) {
            MainApplication mainApplication = (MainApplication) context.getApplicationContext();
            Cart cart = mainApplication.getCart();
            long currentTime = new Date().getTime();
            if (cart != null && (currentTime - cart.getLastUpdatedTime()) > AppUtils.getConfig(context).getCart_timeout()) {
                mainApplication.clearOrder();
                MainApplication.bus.post(new OrderClear());
            }
        } else {
            context = null;
            stopTimer();
        }

    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public void startTimer() {
        try {
            timer = new Timer();
            timer.schedule(cardClearTask, new Date().getTime(), AppUtils.getConfig(context).getCart_timeout());
        } catch (IllegalStateException e) {

        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            cardClearTask = null;
        }
    }

}
