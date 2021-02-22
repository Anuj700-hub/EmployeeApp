package com.hungerbox.customer.mvvm.util

import android.content.pm.PackageManager
import android.os.Build
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.bluetooth.Util
import com.hungerbox.customer.bluetooth.ViolationLogManager
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.SharedPrefUtil
import java.util.ArrayList

class MyAccountUtilImpl(private val config: Config):MyAccountUtil {

    override fun updateEmailSettingValue(isChecked : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.EMAIL_SETTING, isChecked)
    }
    override fun updateSmsSettingValue(isChecked : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.SMS_SETTING, isChecked)
    }
    override fun updateGeneralNotificationSettingValue(isChecked : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.GENERAL_NOTIFICATION, isChecked)
    }
    override fun updateAppNotificationSettingValue(isChecked : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, isChecked)
    }
    override fun updateChatHeadSettingValue(isChecked : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.CHAT_HEAD_SETTING, isChecked)
    }
    override fun updateGenericSettingValue(key : String, isChecked : Boolean){
        SharedPrefUtil.putBoolean(key, isChecked)
    }
    override fun isSmsSettingChecked() : Boolean{
        return SharedPrefUtil.getBoolean(ApplicationConstants.SMS_SETTING, true)
    }
    override fun isAppNotificationSettingChecked() : Boolean{
        return SharedPrefUtil.getBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, true)
    }
    override fun isPipSettingChecked() : Boolean{
        return SharedPrefUtil.getBoolean(ApplicationConstants.PIP_SETTING, true)
    }
    override fun isLogSettingChecked() : Boolean{
        return SharedPrefUtil.getBoolean(ApplicationConstants.LOG_SETTING, false)
    }
    override fun getUserId() : String{
        return SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0).toString()
    }
    override fun getDeskOrderingStatus() : Int{
        return SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0)
    }
    override fun isPipEnabled() : Boolean{
        return isPipEnabledFromConfig() && !isCafeApp() && getAndroidVersion() >= 26 && isPipSupportedBySystem()
    }
    override fun getPipSettingString() : String{
        return AppUtils.getConfig(MainApplication.appContext).getOrder_status_window()
    }
    override fun getDeskReferenceHintString() : String{
        return AppUtils.getConfig(MainApplication.appContext).workstation_address
    }
    override fun isLogEnabled() : Boolean{
        return BuildConfig.BUILD_TYPE.contains("qa") && SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE, "") == "qa"
    }
    override fun isSsoLoginOnly() : Boolean{
        return config.isIs_sso_login_only
    }
    override fun isUserSettingVisible() : Boolean{
        return config.isShow_user_settings
    }
    override fun getProximitySwitchValue(): Int{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.proximity_switch else 0
    }
    override fun isShareOptionEnabled() : Boolean{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.share_option else false
    }
    override fun isDeleteOptionEnabled() : Boolean{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.delete_option else false
    }
    override fun isProximitySwitchOn() : Boolean{
        return SharedPrefUtil.getBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, false)
    }
    override fun getContactTracingMaxDays() : Int{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.contact_tracing_api_max_days else 0
    }
    override fun updateBLEStartTime(){
        SharedPrefUtil.putLong(ApplicationConstants.BLE_START_TIME, DateTimeUtil.adjustCalender(MainApplication.appContext).timeInMillis)
    }
    override fun updateBLEProximityStatus(status : Boolean){
        SharedPrefUtil.putBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, status)
    }
    override fun getTrackingStartTimeEndTime() : String{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.tracking_start_time_end_time else ""
    }
    override fun getShiftDuration() : Int{
        return if (config.bluetooth_distancing != null) config.bluetooth_distancing.shift_duration else 0
    }
    override fun isGenderInfoRequired() : Boolean{
        return config.ask_gender_info
    }
    override fun isBluetoothDistancingActive() : Boolean{
        return Util.isBluetoothDistancingActive(MainApplication.appContext, null)
    }
    override fun shouldEnableBluetoothForAllDay() : Boolean{
        return isBluetoothDistancingActive() && ViolationLogManager.shouldEnableBluetoothForAllDay(MainApplication.appContext)
    }

    override fun isPipEnabledFromConfig(): Boolean {
        return AppUtils.getConfig(MainApplication.appContext).isPicture_in_picture
    }

    override fun isCafeApp(): Boolean {
        return AppUtils.isCafeApp()
    }

    override fun isPipSupportedBySystem(): Boolean {
        return MainApplication.appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    override fun getAndroidVersion(): Int {
        return Build.VERSION.SDK_INT
    }
}