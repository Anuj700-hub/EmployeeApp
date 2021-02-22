package com.hungerbox.customer.bluetooth

import android.content.Context
import com.hungerbox.customer.bluetooth.Model.UserViolation
import com.hungerbox.customer.model.db.DbHandler
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.SharedPrefUtil

class ViolationLogManager {

    companion object {

        const val BLE_ALL_TIME_REQUEST = 1700

        fun saveUserViolation(context: Context, deviceId: String,locationId: Long){

            var violation = UserViolation()
            violation.contact_id = deviceId
            violation.location_id = locationId
            violation.timestamp = DateTimeUtil.adjustCalender(context.applicationContext).timeInMillis / 1000

            val violationResult = DbHandler.getDbHandler(context).getUserViolationFromDB(violation)

            if(violationResult != null){
                var violationFromDB : UserViolation = violationResult
                violationFromDB.timestamp = violation.timestamp
                DbHandler.getDbHandler(context).updateUserViolationForCurrentDay(violationFromDB)
            }
            else{
                violation.count = 1
                DbHandler.getDbHandler(context).createUserViolation(violation)
            }
        }

        fun removeViolationsBeforeGivenDays(context: Context, days: Int){
            val xDaysBeforeTimestamp = DateTimeUtil.getTodayTimeStamp() - (days * 24 * 60 * 60)
            DbHandler.getDbHandler(context).deleteViolationsBefore(xDaysBeforeTimestamp)
        }

        fun getViolationForXDays(context: Context, days: Int) : List<UserViolation>{
            val xDaysBeforeTimestamp = DateTimeUtil.getTodayTimeStamp() - (days * 24 * 60 * 60)
            return DbHandler.getDbHandler(context).getViolationsForGivenTimeStamp(xDaysBeforeTimestamp)
        }

        fun isBluetoothAllDayEnabled(context: Context): Boolean {
            return AppUtils.getConfig(context).bluetooth_distancing != null && AppUtils.getConfig(context).bluetooth_distancing.is_all_day_tracking_enabled
        }

        fun shouldEnableBluetoothForAllDay(context: Context) : Boolean{

            when {
                isBluetoothAllDayEnabled(context) -> {
                    if(!SharedPrefUtil.getBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, AppUtils.getConfig(context).bluetooth_distancing.proximity_switch == 0) ){
                        return false
                    }
                    return isTimeForBT(context)
                }
                else -> {
                    return false
                }
            }
        }

        private fun isTimeForBT(context: Context) : Boolean{

            var timeString = AppUtils.getConfig(context).bluetooth_distancing.tracking_start_time_end_time

            if(timeString == null || timeString.isEmpty() || !timeString.contains("-")){
                val shiftDuration: Int = AppUtils.getConfig(context).bluetooth_distancing.shift_duration
                val bleServiceStartTime = SharedPrefUtil.getLong(ApplicationConstants.BLE_START_TIME, -1)

                return if(shiftDuration == 0 || bleServiceStartTime == -1L)
                    true
                else
                    DateTimeUtil.adjustCalender(context).timeInMillis < (bleServiceStartTime + (shiftDuration * 60 * 1000))
            }
            else{
                val timeArray: Array<String> = timeString.split("-").toTypedArray()
                val shiftDuration: Int = AppUtils.getConfig(context).bluetooth_distancing.shift_duration
                val bleServiceStartTime = SharedPrefUtil.getLong(ApplicationConstants.BLE_START_TIME, -1)

                var endTime =
                        if ((shiftDuration == 0) || bleServiceStartTime == -1L)
                            DateTimeUtil.getTodaysTimeFromString(timeArray[1])
                        else
                            minOf(DateTimeUtil.getTodaysTimeFromString(timeArray[1]), (bleServiceStartTime + (shiftDuration * 60 * 1000)))

                return DateTimeUtil.adjustCalender(context).timeInMillis in DateTimeUtil.getTodaysTimeFromString(timeArray[0])..endTime
            }
        }

        fun deleteAllViolations(context: Context){
            DbHandler.getDbHandler(context).deleteUserViolationTable()
        }
    }
}