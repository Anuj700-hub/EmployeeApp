package com.example.logcatviewer.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * It contains constants used in the project.
 */
public class Constants {

    //Logcat source buffer. Note that values are case-sensitive.
    public static final String LOGCAT_SOURCE_BUFFER_MAIN = "main";
    public static final String LOGCAT_SOURCE_BUFFER_RADIO = "radio";
    public static final String LOGCAT_SOURCE_BUFFER_EVENTS = "events";

    /**
     * Top Logcat logger directory to save log entries.
     */
    private static final String LOG_RECORD_DIR = "/LogcatViewer/";

    /**
     * Get directory where logs are saved.
     *
     * @param context application context.
     * @return directory File object.
     */
    public static File getRecordDir(Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + LOG_RECORD_DIR + context.getPackageName());
    }

}
