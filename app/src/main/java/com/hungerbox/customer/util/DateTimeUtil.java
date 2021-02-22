package com.hungerbox.customer.util;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.model.TimeHMS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by peeyush on 23/6/16.
 */
public class DateTimeUtil {


    public static int getMinsFromMillis(long millis) {
        return getMinsFromSeconds(millis / 1000);
    }

    public static int getMinsFromSeconds(long seconds) {
        return (int) (seconds / 60);
    }


    public static long getMillisForServerSeconds(long seconds) {
        Calendar cal = Calendar.getInstance();
//cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        cal.setTimeInMillis(((seconds + 19800l) * 1000l));
//        cal.getTimeZone();
//        cal.get(Calendar.HOUR_OF_DAY);
//        cal.get(Calendar.MINUTE);
        return cal.getTimeInMillis();
    }

    public static Calendar getCalendarForServerSeconds(long seconds) {
        Calendar cal = Calendar.getInstance();
//cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        cal.setTimeInMillis(((seconds) * 1000l));
//        cal.getTimeZone();
//        cal.get(Calendar.HOUR_OF_DAY);
//        cal.get(Calendar.MINUTE);
        return cal;
    }


    public static long getTimeForNotification(long confirmedAt, long estimatedTime) {
        Calendar now = Calendar.getInstance(), serverTime = getCalendarForServerSeconds(confirmedAt), estServerTime = getCalendarForServerSeconds(estimatedTime);

        return estServerTime.getTimeInMillis();
    }

    public static String getDateString(long time) {
        String DATE_FORMAT = "HH:mm, dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date(time));
    }

    public static String getDateString12Hour(long time) {
        String DATE_FORMAT = "hh:mm aa, dd-MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date(time));
    }

    public static String getDateStringCustom(long time,String format) {
        try {
            String DATE_FORMAT = format;
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(new Date(time));
        } catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String getDateStringFromDateString(String dateString,String inputFormat,String outputFormat) {
        try {
        SimpleDateFormat fromFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat toFormat = new SimpleDateFormat(outputFormat);
        Date date = fromFormat.parse(dateString);
        return toFormat.format(date);
        } catch (ParseException parseException){
            parseException.printStackTrace();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return "";
    }


    public static long getTimeLeftMins(long time) {
        Calendar now = DateTimeUtil.adjustCalender(MainApplication.appContext);
//        Calendar now = Calendar.getInstance(),
        Calendar estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);
        if (now.compareTo(estTime) < 0) {
            long mins = TimeUnit.MILLISECONDS.toMinutes(estTime.getTimeInMillis() - now.getTimeInMillis());
            return mins + 1;
        } else {
            return -1;
        }
    }

    public static TimeHMS getTimeLeftCustom(long time) {
        Calendar now = DateTimeUtil.adjustCalender(MainApplication.appContext);
        Calendar estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);
        TimeHMS timeHMS = new TimeHMS();
        if (now.compareTo(estTime) < 0) {
            long secs = TimeUnit.MILLISECONDS.toSeconds(estTime.getTimeInMillis() - now.getTimeInMillis());
            timeHMS.setSeconds(secs%60);
            long mins = TimeUnit.MILLISECONDS.toMinutes(estTime.getTimeInMillis() - now.getTimeInMillis());
            timeHMS.setMinutes(mins%60);
            long hours = TimeUnit.MILLISECONDS.toHours(estTime.getTimeInMillis() - now.getTimeInMillis());
            timeHMS.setHours(hours);
            return timeHMS;
        } else {
            return timeHMS;
        }
    }

    public static long getTimeLeftHours(long time) {
        Calendar now = Calendar.getInstance(), estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);
        if (now.compareTo(estTime) < 0) {
            long mins = TimeUnit.MILLISECONDS.toHours(estTime.getTimeInMillis() - now.getTimeInMillis());
            return mins + 1;
        } else {
            return -1;
        }
    }

    public static String getTimeLeftString(long time) {

        String format = "Your order will be ready at %s";
        long mins = getTimeLeftMins(time);
//        format = String.format(format, mins);
        if (mins >= 1) {
            format = String.format(format, new SimpleDateFormat("HH:mm").format(new Date(time * 1000L)));
        } else if (mins <= 0) {
            format = "Your order will be ready in some time";
        }
        return format;
    }

    public static long getTodaysTimeFromString(String time) {
        if (time == null || time.isEmpty())
            return new Date().getTime();

        Calendar calenderToday = adjustCalender(MainApplication.appContext);
        String[] timeString = time.split(":");
        int[] times = new int[]{Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1])};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calenderToday.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
        calendar.set(Calendar.MINUTE, times[1]);
        return calendar.getTimeInMillis();
    }

    public static long getTodaysTimeFromStringNew(String time, Context context) {
        if (time == null || time.isEmpty())
            return new Date().getTime();

        Calendar calenderToday = adjustCalender(context);

        String[] timeString = time.split(":");
        int[] times = new int[]{Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1])};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calenderToday.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
        calendar.set(Calendar.MINUTE, times[1]);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Calendar getTodaysCalenderFromString(String time) {
        String[] timeString = time.split(":");
        int[] times = new int[]{Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1])};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, times[0]);
        calendar.set(Calendar.MINUTE, times[1]);
        return calendar;
    }

    public static Calendar getTodaysCalenderFromDateString(String date) {
        String[] dateString = date.split("-");
        int[] dateValues = new int[]{Integer.parseInt(dateString[0]),
                Integer.parseInt(dateString[1]), Integer.parseInt(dateString[2])};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dateValues[0]);
        calendar.set(Calendar.MONTH, dateValues[1] - 1);
        calendar.set(Calendar.YEAR, dateValues[2]);
        return calendar;
    }

    public static long getTodayTimeStamp() {
        Calendar cal = DateTimeUtil.adjustCalender(MainApplication.appContext);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    public static long getDateInMillis(String dateString, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (Exception exception){
            return -1;
        }
    }

    public static String getTodayTimeIn12HourFormat(String time){
        return DateFormat.format("hh:mm a", DateTimeUtil.getTodaysCalenderFromString(time).getTime()).toString();
    }

    public static long getTimeStampFromTime(String time){
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date  = fromFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return-1;

        }
    }

    public static Calendar adjustCalender(Context context) {

        if (context!=null) {

            Calendar calendar = Calendar.getInstance();
            long actualTime = calendar.getTimeInMillis() -
                    SharedPrefUtil.getLong(ApplicationConstants.TIME_DIFFERENCE, 0);

            calendar.setTimeInMillis(actualTime);

//        TimeZone tzIN = TimeZone.getTimeZone("Asia/Calcutta");
//        calendar.setTimeZone(tzIN);

            return calendar;
        } else{

            if (MainApplication.appContext!=null){
              try{
                  Intent restart = MainApplication.appContext.getPackageManager().getLaunchIntentForPackage(MainApplication.appContext.getPackageName());
                  restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  MainApplication.appContext.startActivity(restart);
                  System.exit(0);
                  return null;
              }catch (Exception exp){
                  exp.printStackTrace();
                  System.exit(0);
                  return null;
              }
            } else{
                System.exit(0);
                return null;
            }
        }
    }
}
