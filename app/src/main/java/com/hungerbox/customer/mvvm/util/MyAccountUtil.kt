package com.hungerbox.customer.mvvm.util

import android.content.pm.PackageManager
import android.os.Build
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.bluetooth.Util
import com.hungerbox.customer.bluetooth.ViolationLogManager
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.SharedPrefUtil

interface MyAccountUtil {
    fun updateEmailSettingValue(isChecked : Boolean)
    fun updateSmsSettingValue(isChecked : Boolean)
    fun updateGeneralNotificationSettingValue(isChecked : Boolean)
    fun updateAppNotificationSettingValue(isChecked : Boolean)
    fun updateChatHeadSettingValue(isChecked : Boolean)
    fun updateGenericSettingValue(key : String, isChecked : Boolean)
    fun isSmsSettingChecked() : Boolean
    fun isAppNotificationSettingChecked() : Boolean
    fun isPipSettingChecked() : Boolean
    fun isLogSettingChecked() : Boolean
    fun getUserId() : String
    fun getDeskOrderingStatus() : Int
    fun isPipEnabled() : Boolean
    fun getPipSettingString() : String
    fun getDeskReferenceHintString() : String
    fun isLogEnabled() : Boolean
    fun isSsoLoginOnly() : Boolean
    fun isUserSettingVisible() : Boolean
    fun getProximitySwitchValue(): Int
    fun isShareOptionEnabled() : Boolean
    fun isDeleteOptionEnabled() : Boolean
    fun isProximitySwitchOn() : Boolean
    fun getContactTracingMaxDays() : Int
    fun updateBLEStartTime()
    fun updateBLEProximityStatus(status : Boolean)
    fun getTrackingStartTimeEndTime() : String
    fun getShiftDuration() : Int
    fun isGenderInfoRequired() : Boolean
    fun isBluetoothDistancingActive() : Boolean
    fun shouldEnableBluetoothForAllDay() : Boolean
    //related with pip settings
    //return AppUtils.getConfig(MainApplication.appContext).isPicture_in_picture && !AppUtils.isCafeApp() &&
    // Build.VERSION.SDK_INT >= 26 && MainApplication.appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    fun isPipEnabledFromConfig(): Boolean
    fun isCafeApp(): Boolean
    fun isPipSupportedBySystem():Boolean
    fun getAndroidVersion():Int

}