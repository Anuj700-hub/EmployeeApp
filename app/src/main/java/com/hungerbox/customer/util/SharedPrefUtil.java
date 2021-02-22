package com.hungerbox.customer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hungerbox.customer.MainApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefUtil {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPrefEditor;
    private static int defIntValue = -1;
    private static float defFloatValue = -1;
    private static boolean defBooleanValue = false;
    private static String defStringValue = "";

    private SharedPrefUtil() {
    }

    public static SharedPreferences getSharedPref(){
        if (sharedPreferences == null){
            sharedPreferences = MainApplication.appContext.getSharedPreferences(ApplicationConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getSharedPrefEditor(){
        if (sharedPrefEditor == null) {
            sharedPrefEditor = getSharedPref().edit();
        }
        return sharedPrefEditor;
    }

    public static void remove(String key){
        getSharedPrefEditor().remove(key).apply();
    }

    public static void putInt(String key, int value){
        getSharedPrefEditor().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue){
        return getSharedPref().getInt(key, defValue);
    }

    public static int getInt(String key){

        return getSharedPref().getInt(key, defIntValue);
    }

    public static void putLong(String key, long value){
        getSharedPrefEditor().putLong(key, value).apply();
    }

    public static long getLong(String key, long defValue){
        return getSharedPref().getLong(key, defValue);
    }

    public static long getLong(String key){
        return getSharedPref().getLong(key, defIntValue);
    }

    public static void putFloat(String key, float value){
        getSharedPrefEditor().putFloat(key, value).apply();
    }

    public static float getFloat(String key, float defValue){
        return getSharedPref().getFloat(key, defValue);
    }

    public static float getFloat(String key){
        return getSharedPref().getFloat(key, defFloatValue);
    }

    public static void putString(String key, String value){
        getSharedPrefEditor().putString(key, value).apply();
    }

    public static String getString(String key, String defValue){
        return getSharedPref().getString(key, defValue);
    }

    public static String getString(String key){
        return getSharedPref().getString(key, defStringValue);
    }

    public static void putBoolean(String key, boolean value){
        getSharedPrefEditor().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue){
        return getSharedPref().getBoolean(key, defValue);
    }

    public static boolean getBoolean(String key){
        return getSharedPref().getBoolean(key, defBooleanValue);
    }

    public static ArrayList<Long> getLongArrayList(String key){

        String arrayListString = getSharedPref().getString(key, "");
        if(arrayListString == ""){
            return new ArrayList<Long>();
        }

        Type baseType = new TypeToken<ArrayList<Long>>() {}.getType();
        Gson gson = new Gson();

        ArrayList<Long> list = new ArrayList<Long>();
        try{
            list = gson.fromJson(arrayListString, baseType);
            if (list == null){
                list = new ArrayList<Long>();
            }
        }catch (Exception exp){
            list = new ArrayList<Long>();
        }
        return list;
    }

    public static void putLongArrayList(String key, ArrayList<Long> value){
        try{
            Gson gson = new Gson();
            String arrayListString = gson.toJson(value);
            putString(key, arrayListString);
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    public static ArrayList<String> getStringArrayList(String key){

        String arrayListString = getSharedPref().getString(key, "");
        if(arrayListString == ""){
            return new ArrayList<String>();
        }

        Type baseType = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();

        ArrayList<String> list = new ArrayList<String>();
        try{
            list = gson.fromJson(arrayListString, baseType);
            if (list == null){
                list = new ArrayList<String>();
            }
        }catch (Exception exp){
            list = new ArrayList<String>();
        }
        return list;
    }

    public static void putStringArrayList(String key, ArrayList<String> value){
        try{
            Gson gson = new Gson();
            String arrayListString = gson.toJson(value);
            putString(key, arrayListString);
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }




    public static void putMap(String key, HashMap<String,String> value){
        Gson gson = new Gson();
        String hashMapString = gson.toJson(value);
        getSharedPrefEditor().putString(key, hashMapString).apply();
    }

    public static HashMap<String, String> getMap(String key){
        String hashMapString = getSharedPref().getString(key, defStringValue);
        Gson gson = new Gson();

        HashMap map = new HashMap<String, String>();;
        try{
            map = (HashMap<String, String>) gson.fromJson(hashMapString, HashMap.class);
            if (map == null){
                map = new HashMap<String, String>();
            }
        }catch (Exception exp){
            map = new HashMap<String, String>();
        }
        return map;
    }

    public static HashMap<String, String> getMap(String key, String defValue){
        String hashMapString = getSharedPref().getString(key, defValue);
        Gson gson = new Gson();
        return (HashMap<String, String>) gson.fromJson(hashMapString, HashMap.class);
    }
}

