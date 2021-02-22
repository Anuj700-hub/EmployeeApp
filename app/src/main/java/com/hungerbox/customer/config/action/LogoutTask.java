package com.hungerbox.customer.config.action;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hungerbox.customer.prelogin.activity.HBWelcomeActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by peeyush on 12/8/16.
 */
public class LogoutTask extends TimerTask {

    public static long lastUsedTime = 0;
    private static Timer timer;
    private static LogoutTask logoutTask;
    private static long idle_timeout;
    Context context;

    private LogoutTask(Context context) {
        this.context = context;
    }

    public static LogoutTask getInstance(Context context) {
        if (logoutTask == null) {
            timer = new Timer();
            logoutTask = new LogoutTask(context.getApplicationContext());
            idle_timeout = AppUtils.getConfig(context.getApplicationContext()).getLogout_idle_timeout();
        }

        return logoutTask;
    }

    public static void updateTime() {
        lastUsedTime = new Date().getTime();
    }

    @Override
    public void run() {
            long currentTime = new Date().getTime();
        Log.e("update time", ""+currentTime+" - "+lastUsedTime+" = "+(currentTime-lastUsedTime) +" shoulbe be > "+idle_timeout);
            if(currentTime-lastUsedTime>idle_timeout){
                logout();
            }
    }


    private void logout() {
        if (AppUtils.isCafeApp()) {
            AppUtils.doLogout(context);
            startEventService();
            SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN);
            SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID);


            Intent intent = new Intent(context, HBWelcomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void startEventService() {
        return;
//        Intent intent = new Intent(context, OrderStatusService.class);
//        context.startService(intent);
    }

    public void startTimer() {
        try {
            timer.schedule(logoutTask, 1000, AppUtils.getConfig(context).getLogout_idle_timeout());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopTimer() {
        if (timer != null) {
            try {
                timer.cancel();
                timer = null;
                logoutTask = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
