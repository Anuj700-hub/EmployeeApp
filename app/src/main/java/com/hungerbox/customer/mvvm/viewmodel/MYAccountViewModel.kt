package com.hungerbox.customer.mvvm.viewmodel


import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.lifecycle.*
import com.hungerbox.customer.R
import com.hungerbox.customer.bluetooth.Model.ContactTracingResponse
import com.hungerbox.customer.bluetooth.Model.UserViolation
import com.hungerbox.customer.ksnetwork.ksnetworklib.KSApiClient
import com.hungerbox.customer.model.*
import com.hungerbox.customer.mvvm.listeners.*
import com.hungerbox.customer.mvvm.repository.MyAccountRepository
import com.hungerbox.customer.mvvm.util.DesignPatternActions
import com.hungerbox.customer.mvvm.util.MyAccountUtil
import com.hungerbox.customer.network.VolleyRequestFactory
import com.hungerbox.customer.order.fragment.DeskReferenceChangeDialog
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants


class MYAccountViewModel(private val myAccountRepository: MyAccountRepository, private val myAccountUtil: MyAccountUtil) : ViewModel(),LifecycleObserver{

    private val apiSignature : String by lazy {
        System.currentTimeMillis().toString()
    }
    var  userDetails : MutableLiveData<DesignPatternActions>  =  MutableLiveData()
    var etContactNumber: MutableLiveData<String> = MutableLiveData()
    var etEmployeeId: MutableLiveData<String> = MutableLiveData()
    var etEmployeeName: MutableLiveData<String> = MutableLiveData()
    var etEmpEmail: MutableLiveData<String> = MutableLiveData()
    var etDeskReference: MutableLiveData<String?> = MutableLiveData()
    var etDeskReferenceHint: MutableLiveData<String> = MutableLiveData()
    var txtInputLayout: MutableLiveData<String> = MutableLiveData()
    var deskReferenceVisibility: MutableLiveData<Int> = MutableLiveData()
    var btnChangePassVisibility: MutableLiveData<Int> = MutableLiveData()
    var etDeskReferenceFocusability: MutableLiveData<Int> = MutableLiveData()
    var etEmailRightDrawable: MutableLiveData<Int> = MutableLiveData()
    var privacyPolicy: MutableLiveData<SpannableString> = MutableLiveData()
    var btnCVSettingVisibility: MutableLiveData<Int> = MutableLiveData()
    var pipSettingValue: MutableLiveData<String> = MutableLiveData()
    var progressbarVisibility: MutableLiveData<Int> = MutableLiveData()
    var pipSettingVisibility: MutableLiveData<Int> = MutableLiveData()
    var smsSettingChecked: MutableLiveData<Boolean> = MutableLiveData()
    var notificationSettingChecked: MutableLiveData<Boolean> = MutableLiveData()
    var pipSettingChecked: MutableLiveData<Boolean> = MutableLiveData()
    var logSettingChecked: MutableLiveData<Boolean> = MutableLiveData()
    var logSettingVisibility: MutableLiveData<Int> = MutableLiveData()
    var cvProximityOptionsVisibility: MutableLiveData<Int> = MutableLiveData()
    var proximitySwitchVisibility: MutableLiveData<Int> = MutableLiveData()
    var shareOptionVisibility: MutableLiveData<Int> = MutableLiveData()
    var deleteOptionVisibility: MutableLiveData<Int> = MutableLiveData()
    var proximitySwitchChecked: MutableLiveData<Boolean> = MutableLiveData()
    var genderSectionVisibility: MutableLiveData<Int> = MutableLiveData()
    var tvGenderVisibility: MutableLiveData<Int> = MutableLiveData()
    var tipGenderVisibility: MutableLiveData<Int> = MutableLiveData()
    var etGenderVisibility: MutableLiveData<Int> = MutableLiveData()
    var spGenderVisibility: MutableLiveData<Int> = MutableLiveData()
    var etGender: MutableLiveData<String> = MutableLiveData()
    var spGenderList: MutableLiveData<List<String>> = MutableLiveData()
    var resetGenderSpinner: MutableLiveData<String> = MutableLiveData()
    var shouldUpdateWatermark: MutableLiveData<Boolean> = MutableLiveData()

    var toast: MutableLiveData<ToastData> = MutableLiveData()
    var bleIntent: MutableLiveData<String> = MutableLiveData()
    var stopBLEDeviceTracking: MutableLiveData<String> = MutableLiveData()
    var safeChangeProximitySwitchValue: MutableLiveData<Boolean> = MutableLiveData()
    var showProximityPopup: MutableLiveData<Pair<Boolean, Boolean>> = MutableLiveData()


    init {

        AppUtils.HbLog("My Account", "View Model Initialize")

        val content =  SpannableString("Read our Privacy Policy here")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        privacyPolicy.value = content

        if (!myAccountUtil.isUserSettingVisible()) {
            btnCVSettingVisibility.value = View.GONE
        } else{
            btnCVSettingVisibility.value = View.VISIBLE
        }
        if (myAccountUtil.isSsoLoginOnly()) {
            btnChangePassVisibility.value = View.GONE
        } else{
            btnChangePassVisibility.value = View.VISIBLE
        }

        progressbarVisibility.value = View.GONE

        if (myAccountUtil.isPipEnabled()) {
            pipSettingVisibility.value = View.VISIBLE
        } else {
            pipSettingVisibility.value = View.GONE
        }

        pipSettingValue.value = myAccountUtil.getPipSettingString()

        if (myAccountUtil.isLogEnabled())
            logSettingVisibility.value = View.VISIBLE
        else
            logSettingVisibility.value = View.GONE

        etDeskReferenceHint.value = myAccountUtil.getDeskReferenceHintString()

        if(myAccountUtil.isBluetoothDistancingActive()){
            if((myAccountUtil.getProximitySwitchValue() == 0) && !myAccountUtil.isShareOptionEnabled() && !myAccountUtil.isDeleteOptionEnabled()){
                cvProximityOptionsVisibility.value = View.GONE
            }
            else{
                cvProximityOptionsVisibility.value = View.VISIBLE

                if(myAccountUtil.getProximitySwitchValue() != 0)
                    proximitySwitchVisibility.value = View.VISIBLE
                else
                    proximitySwitchVisibility.value = View.GONE

                if(myAccountUtil.isShareOptionEnabled())
                    shareOptionVisibility.value = View.VISIBLE
                else
                    shareOptionVisibility.value = View.GONE

                if(myAccountUtil.isDeleteOptionEnabled())
                    deleteOptionVisibility.value = View.VISIBLE
                else
                    deleteOptionVisibility.value = View.GONE
            }
        }
        else{
            cvProximityOptionsVisibility.value =  View.GONE
        }


        if(myAccountUtil.isGenderInfoRequired())
            genderSectionVisibility.value = View.VISIBLE
        else
            genderSectionVisibility.value = View.GONE

        etEmailRightDrawable.value = R.drawable.tp_image

        fetchUserDetails()
        fetchUserSetting()
    }

    fun checkedChange(id: Int?, isChecked: Boolean) {

        when (id){
            R.id.ctv_log -> {

                userDetails.value = DesignPatternActions(isChecked, ApplicationConstants.Actions.LOG_STATE_CHANGED)
            }

            R.id.ctv_sms_setting -> {
                updateUserSetting(ApplicationConstants.SMS_SETTING, isChecked)
            }

            R.id.ctv_notification_setting -> {
                updateUserSetting(ApplicationConstants.APP_NOTIFICATION_SETTING, isChecked)
            }

            R.id.ctv_pip_setting -> {
                myAccountUtil.updateGenericSettingValue(ApplicationConstants.PIP_SETTING, isChecked)
            }

            R.id.ctv_bluetooth_proximity -> {
                if(isChecked){
                    myAccountUtil.updateBLEStartTime()
                    myAccountUtil.updateBLEProximityStatus(true)

                    if (myAccountUtil.shouldEnableBluetoothForAllDay()) {
                        bleIntent.value = "startBLEScreen"
                    }
                    else{
                        stopBLEDeviceTracking.value = "stopTracking"
                        safeChangeProximitySwitchValue.value = false

                        if(myAccountUtil.getTrackingStartTimeEndTime().isNotEmpty())
                            showProximityPopup.value = Pair(first = true, second = true)
                    }
                }
                else{
                    if(myAccountUtil.getProximitySwitchValue() == 2){

                        safeChangeProximitySwitchValue.value = true

                        if(myAccountUtil.getTrackingStartTimeEndTime().isNotEmpty())
                            showProximityPopup.value = Pair(first = true, second = false)
                        else
                            showProximityPopup.value = Pair(first = false, second = false)

                    }
                    else{
                        stopBLEDeviceTracking.value = "stopTracking"
                    }
                }
            }
        }
    }

    private fun fetchUserSetting() {
        showProgress()
        myAccountRepository.fetchUserSetting(object : UserSettingResponseListener {
            override fun onSuccess(responseObject: UserSettingsResponse?) {
                hideProgress()
                responseObject?.run {
                    myAccountUtil.updateEmailSettingValue(userSettings.isEmail)
                    myAccountUtil.updateSmsSettingValue(userSettings.isSms)
                    myAccountUtil.updateGeneralNotificationSettingValue(userSettings.isGeneralNotification)
                    myAccountUtil.updateAppNotificationSettingValue(userSettings.isAppNotification)
                    myAccountUtil.updateChatHeadSettingValue(userSettings.isChatHeadEnabled)
                }
                updateUserSettingUI()
            }

            override fun onError() {
                hideProgress()
                updateUserSettingUI()
            }
        })
    }

    fun updateUserSettingUI() {
        myAccountUtil.apply{
            smsSettingChecked.value = isSmsSettingChecked()
            notificationSettingChecked.value = isAppNotificationSettingChecked()
            pipSettingChecked.value = isPipSettingChecked()
            logSettingChecked.value = isLogSettingChecked()
        }
    }

    fun fetchUserDetails(){

        showProgress()
        myAccountRepository.fetchUserDetail(myAccountUtil.getUserId(), object : UserDetailResponseListener {
            override fun onSuccess(userResponse: UserReposne?) {
                hideProgress()
                userResponse?.run {
                    if (user.userName == null || user.userName.trim { it <= ' ' }.isEmpty()) etEmployeeId.value = user.email else etEmployeeId.value = user.userName
                    if (user.registration_type.equals("sso", ignoreCase = true)) {
                        btnChangePassVisibility.value = View.GONE
                    } else {
                        btnChangePassVisibility.value = View.VISIBLE
                    }

                    if(myAccountUtil.isGenderInfoRequired()){
                        genderSectionVisibility.value = View.VISIBLE
                        if(userResponse.user.gender.isEmpty()){
                            etGenderVisibility.value = View.GONE
                            spGenderVisibility.value = View.VISIBLE
                            tvGenderVisibility.value = View.VISIBLE
                            tipGenderVisibility.value = View.GONE
                            spGenderList.value = myAccountRepository.getGendersList()
                        }
                        else{
                            spGenderVisibility.value = View.GONE
                            tvGenderVisibility.value = View.GONE
                            etGenderVisibility.value = View.VISIBLE
                            tipGenderVisibility.value = View.VISIBLE
                            etGender.value = user.gender
                        }
                    }
                    else{
                        genderSectionVisibility.value = View.GONE
                    }

                    etEmployeeName.value = user.name
                    etContactNumber.value = user.phoneNumber
                    etEmpEmail.value = user.empEmail
                    if (user.emailVerifiedflag == 2) {
                        etEmailRightDrawable.value = R.drawable.status_success
                    } else if (user.emailVerifiedflag == 1) {
                        etEmpEmail.value = user.updatedEmail
                        etEmailRightDrawable.value = R.drawable.status_info
                    }
                    setDeskReference(user)
                }
            }

            override fun onError() {
                hideProgress()
            }
        },apiSignature)
    }

    fun updateGenderOnServer(position: Int){
        var genderData = myAccountRepository.getGendersList()[position]

        if (genderData.equals("Non-Binary", ignoreCase = true))
            genderData = "Others"

        myAccountRepository.updateGenderOnServer(genderData, object: UpdateUserInfoResponseListener{
            override fun onSuccess(updateUserInfoResponse: Object) {
                shouldUpdateWatermark.value = true
            }

            override fun onError() {
                toast.value = ToastData("Error in updating your gender", 0)
                resetGenderSpinner.value = "reset"
            }
        })

    }

    fun editDeskReference() {
        val deskReferenceChangeDialog = DeskReferenceChangeDialog.newInstance(etDeskReference.value, false, object : DeskReferenceChangeDialog.OnFragmentInteractionListener {
            override fun onPositiveInteraction(ref: String) {
                etDeskReference.value = ref
                etDeskReferenceFocusability.value = View.FOCUSABLE
                shouldUpdateWatermark.value = true
            }

            override fun onNegativeInteraction() {}
            override fun onDissmis() {}
        })
        userDetails.value = DesignPatternActions(deskReferenceChangeDialog, ApplicationConstants.Actions.FRAGMENT_DIALOG)
    }

    private fun setDeskReference(user: User) {
        val deskOrderingEnabled = myAccountUtil.getDeskOrderingStatus()
        if (deskOrderingEnabled == 1) {
            deskReferenceVisibility.value = View.VISIBLE
            if (user.deskReference != null && user.deskReference.trim { it <= ' ' }.isNotEmpty()) {
                etDeskReference.value = user.deskReference
                etDeskReferenceFocusability.value = View.FOCUSABLE
            }
        } else if (deskOrderingEnabled == 2 && user.deskReference != null && user.deskReference.trim { it <= ' ' }.isNotEmpty()) {
            deskReferenceVisibility.value = View.VISIBLE
            etDeskReference.value = user.deskReference
            etDeskReferenceFocusability.value = View.FOCUSABLE
        } else {
            deskReferenceVisibility.value = View.GONE
        }
    }

    private fun updateUserSetting(key: String, isChecked: Boolean) {

        showProgress()
        myAccountRepository.updateUserSetting(key,isChecked, object : UserSettingsListener {
            override fun onSuccess(responseObject: UserSettings?) {
                hideProgress()
                myAccountUtil.updateGenericSettingValue(key,isChecked)
                updateUserSettingUI()
            }

            override fun onError(error: String) {
                hideProgress()
                toast.value = ToastData(error, 0)
                updateUserSettingUI()
            }
        })
    }

    fun sendViolations() {
        // make db operations async
        val violations: List<UserViolation> = myAccountRepository.getViolationsFromDb()

        if (violations.isNotEmpty()) {
            myAccountRepository.sendContactTracingData(violations, object : ContactTracingResponseListener{
                override fun onSuccess(contactTracingResponse: ContactTracingResponse) {

                    if(contactTracingResponse != null && contactTracingResponse.success)
                        toast.value = ToastData("Thanks for sharing your contact tracing data", 1)
                    else
                        toast.value = ToastData("Unable to share your contact tracing data", 0)

                }

                override fun onError() {
                    toast.value = ToastData("Unable to share your contact tracing data", 0)
                }
            })
        }
        else {
            toast.value = ToastData("Thanks for sharing your contact tracing data", 1)
        }
    }

    // make db operations async
    fun deleteAllViolations(){
        myAccountRepository.deleteAllViolationsFromDb()
        toast.value = ToastData("Contact Tracing Data Deleted", 1)
    }

    private fun showProgress(){
        progressbarVisibility.value = View.VISIBLE
    }

    private fun hideProgress(){
        progressbarVisibility.value = View.GONE
    }

    override fun onCleared() {
        super.onCleared()
        AppUtils.HbLog("My Account", "View Model Cleared")
        KSApiClient.clear()
        VolleyRequestFactory.mRequestQueue.cancelAll(apiSignature)
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun updateProximitySwitch(){
        proximitySwitchChecked.value = myAccountUtil.isProximitySwitchOn()
    }
}