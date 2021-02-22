package com.hungerbox.customer.mvvm.repository

import com.hungerbox.customer.bluetooth.Model.UserViolation
import com.hungerbox.customer.mvvm.listeners.*
import java.util.ArrayList

interface MyAccountRepository {
    fun updateUserSetting(key:String, isChecked:Boolean, userSettingListener: UserSettingsListener, apiSignature : String)
    fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener, apiSignature : String)
    fun fetchUserDetail(userId: String, userDetailResponseListener: UserDetailResponseListener, apiSignature : String)
    fun sendContactTracingData(userViolations : List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener, apiSignature : String)
    fun getViolationsFromDb(): List<UserViolation>
    fun deleteAllViolationsFromDb()
    fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener, apiSignature: String)
    fun getGendersList() : ArrayList<String>


    //Retrofit calls
    fun fetchUserSetting(userSettingResponseListener: UserSettingResponseListener)
    fun updateUserSetting(key:String, isChecked:Boolean, userSettingListener: UserSettingsListener)
    fun sendContactTracingData(userViolations : List<UserViolation>, contactTracingResponseListener: ContactTracingResponseListener)
    fun updateGenderOnServer(gender: String, updateUserInfoResponseListener: UpdateUserInfoResponseListener)


}