package com.example.logcatviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.logcatviewer.utils.LogcatViewer;


public class LogcatViewerUtil {

    public static int APP_OVERLAY_PERMISSION_REQUEST = 5010;

    public static void startLogcatViewer(Activity context, boolean isDebugEnabled){
        if(!canDrawOverlays(context)){
            requestOverlayPermission(context);
        }
        else{
            displayLogcatViewer(context, isDebugEnabled);
        }
    }

    public static void logcatViewerRequestHandler(Activity context, boolean isDebugEnabled){
        if(LogcatViewerUtil.canDrawOverlays(context)){
            LogcatViewerUtil.displayLogcatViewer(context, isDebugEnabled);
        }
        else{
            LogcatViewerUtil.startLogcatViewer(context, isDebugEnabled);
        }
    }

    private static void requestOverlayPermission(Activity context){

        Toast.makeText(context, "Please Enable App Overlay Permission for Hungerbox App.", Toast.LENGTH_LONG).show();

        try{
            final Intent pintent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(pintent, APP_OVERLAY_PERMISSION_REQUEST);
        }
        catch (Exception e){
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }


    private static boolean canDrawOverlays(Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context.getApplicationContext());
        } else {
            return true;
        }
    }

    private static void displayLogcatViewer(Activity context, boolean isDebugEnabled){
        if (isDebugEnabled) {
            LogcatViewer.showLogcatLoggerView(context);
        }
    }

    public static void stopLogcatViewer(Activity context, boolean isDebugEnabled){
        if(isDebugEnabled){
            LogcatViewer.closeLogcatLoggerView(context);
        }
    }

}
