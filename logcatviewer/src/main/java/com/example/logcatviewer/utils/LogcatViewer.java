package com.example.logcatviewer.utils;

import android.content.Context;

import com.example.logcatviewer.service.LogcatViewerFloatingView;
import com.example.logcatviewer.standout.StandOutWindow;


/**
 * This class will launch {@link LogcatViewerFloatingView} view.
 */
public class LogcatViewer {
    /**
     * Launch {@link LogcatViewerFloatingView} view.
     * @param context context.
     */
    public static void showLogcatLoggerView(Context context){
        StandOutWindow.closeAll(context, LogcatViewerFloatingView.class);
        StandOutWindow
                .show(context, LogcatViewerFloatingView.class, StandOutWindow.DEFAULT_ID);
    }

    /**
     * Close  {@link LogcatViewerFloatingView} view.
     *
     * @param context context.
     */
    public static void closeLogcatLoggerView(Context context) {
        StandOutWindow.closeAll(context, LogcatViewerFloatingView.class);
    }
}
