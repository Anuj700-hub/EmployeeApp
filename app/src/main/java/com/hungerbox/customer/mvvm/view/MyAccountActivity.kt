package com.hungerbox.customer.mvvm.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.logcatviewer.LogcatViewerUtil
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.bluetooth.BluetoothDeviceCheckActivity
import com.hungerbox.customer.bluetooth.Util
import com.hungerbox.customer.bluetooth.ViolationLogManager
import com.hungerbox.customer.config.action.LogoutTask
import com.hungerbox.customer.databinding.ActivityMyaccountBinding
import com.hungerbox.customer.mvvm.repository.MyAccountRepositoryImpl
import com.hungerbox.customer.mvvm.util.DesignPatternActions
import com.hungerbox.customer.mvvm.util.MyAccountUtil
import com.hungerbox.customer.mvvm.util.MyAccountUtilImpl
import com.hungerbox.customer.mvvm.viewmodel.MYAccountViewModel
import com.hungerbox.customer.mvvm.viewmodelfactory.MyAccountViewModelFactory
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.prelogin.activity.ParentActivity
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.DateTimeUtil
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.GenericPopUpFragment
import kotlinx.android.synthetic.main.activity_myaccount.*


class MyAccountActivity : ParentActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var myAccountViewModel : MYAccountViewModel
    private lateinit var binding: ActivityMyaccountBinding
    private lateinit var myAccountUtil: MyAccountUtil


    override fun onRestart() {
        super.onRestart()
        AppUtils.HbLog("My Account", "View Model Activity Restart")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.HbLog("My Account", "View Model Activity Create")

        LogoutTask.updateTime()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_myaccount)
        myAccountUtil = MyAccountUtilImpl(AppUtils.getConfig(this))
        myAccountViewModel = ViewModelProviders.of(this,MyAccountViewModelFactory(MyAccountRepositoryImpl(myAccountUtil), myAccountUtil)).get(MYAccountViewModel::class.java)
        lifecycle.addObserver(myAccountViewModel)
        binding.myAccountViewModel = myAccountViewModel
        binding.lifecycleOwner = this

        myAccountViewModel.userDetails.observe(this, Observer<DesignPatternActions> {
            if(it.type == ApplicationConstants.Actions.FRAGMENT_DIALOG && it.data != null){
                supportFragmentManager.beginTransaction()
                        .add(it.data as DialogFragment, "")
                        .commitAllowingStateLoss()
            }else if(it.type == ApplicationConstants.Actions.LOG_STATE_CHANGED){
                if(it.data as Boolean){
                    checkForPermission()
                }else{
                    stopLogcatViewer()
                }
            }
        })
        myAccountViewModel.toast.observe(this, Observer {
            AppUtils.showToast(it.message, true, it.type)
        })
        myAccountViewModel.spGenderList.observe(this, Observer {
            initialiseGenderSpinner()
        })
        myAccountViewModel.resetGenderSpinner.observe(this, Observer {
            resetGenderSpinner()
        })
        myAccountViewModel.bleIntent.observe(this, Observer {
            startBLEScreen()
        })
        myAccountViewModel.stopBLEDeviceTracking.observe(this, Observer {
            stopBLEDeviceTracking()
        })
        myAccountViewModel.safeChangeProximitySwitchValue.observe(this, Observer {
            safeChangeProximitySwitchValue(it)
        })
        myAccountViewModel.showProximityPopup.observe(this, Observer {
            showProximityPopup(it.first, it.second)
        })
        myAccountViewModel.shouldUpdateWatermark.observe(this, Observer {
            if(it){
                AppUtils.resetCurrentUserDataClass(this.applicationContext);
                myAccountViewModel.fetchUserDetails()
            }
        })

        initializeListener()
    }

    override fun onAllApiCallSuccess(configUrl: String) {
        myAccountViewModel.fetchUserDetails()
    }

    private fun initializeListener(){

        binding.ctvLog.setOnCheckedChangeListener(this)
        binding.ctvSmsSetting.setOnCheckedChangeListener(this)
        binding.ctvPipSetting.setOnCheckedChangeListener(this)
        binding.ctvNotificationSetting.setOnCheckedChangeListener(this)
        binding.ctvBluetoothProximity.setOnCheckedChangeListener(this)

        binding.ivBack.setOnClickListener(this)
        binding.etPhoneNumber.setOnClickListener(this)
        binding.etEmail.setOnClickListener(this)
        binding.btnEditDesk.setOnClickListener(this)
        binding.btChangePassword.setOnClickListener(this)
        binding.tvPrivacyPolicy.setOnClickListener(this)
        binding.btnShareData.setOnClickListener(this)
        binding.btnDeleteData.setOnClickListener(this)

        binding.spGender.onItemSelectedListener = this

    }

    private fun checkForPermission() {

        val hasContactPermission = ActivityCompat.checkSelfPermission(MainApplication.appContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    ApplicationConstants.PERMISSION_REQUEST_CODE)
        } else {
            startLogcatViewer()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == ApplicationConstants.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLogcatViewer()
            } else {
                checkForPermission()
                AppUtils.HbLog("Permission", "Write Storage permission was NOT granted.")
            }
        }else if(requestCode == LogcatViewerUtil.APP_OVERLAY_PERMISSION_REQUEST){
            LogcatViewerUtil.logcatViewerRequestHandler(this, BuildConfig.DEBUG)
        }
    }

    private fun startLogcatViewer(){
        LogcatViewerUtil.startLogcatViewer(this, BuildConfig.DEBUG)
        SharedPrefUtil.putBoolean(ApplicationConstants.LOG_SETTING,true)
        AppUtils.showToast("Please restart the app",false,2)
        val myAccountViewModel = ViewModelProviders.of(this).get(MYAccountViewModel::class.java)
        myAccountViewModel.updateUserSettingUI()
    }

    private fun stopLogcatViewer(){
        SharedPrefUtil.putBoolean(ApplicationConstants.LOG_SETTING,false)
        LogcatViewerUtil.stopLogcatViewer(this, BuildConfig.DEBUG)
        val myAccountViewModel = ViewModelProviders.of(this).get(MYAccountViewModel::class.java)
        myAccountViewModel.updateUserSettingUI()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(buttonView?.isPressed == true){
            myAccountViewModel.checkedChange(buttonView.id , isChecked)
        }
    }

    private fun safeChangeProximitySwitchValue(isChecked: Boolean){
        ctv_bluetooth_proximity.setOnCheckedChangeListener(null)
        ctv_bluetooth_proximity.isChecked = isChecked
        ctv_bluetooth_proximity.setOnCheckedChangeListener(this)
    }

    private fun startBLEScreen(){
        AppUtils.settingBTServiceParameters(applicationContext)
        val intent = Intent(this, BluetoothDeviceCheckActivity::class.java)
        startActivityForResult(intent, ViolationLogManager.BLE_ALL_TIME_REQUEST)
        overridePendingTransition(0, 0)
    }

    private fun stopBLEDeviceTracking(){
        myAccountUtil.updateBLEProximityStatus(false)
        Util.stopDeviceTracking(applicationContext)
    }

    override fun onClick(view: View?) {

        when (view?.id ?: 0){

            R.id.bt_change_password -> {
                val intent = Intent(MainApplication.appContext, UpdateAccountActivity::class.java)
                intent.putExtra(ApplicationConstants.FIELD_TO_UPDATE, ApplicationConstants.PASSWORD)
                startActivity(intent)
            }

            R.id.tv_privacy_policy -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(UrlConstant.PRIVACY_POLICY)
                startActivity(intent)
            }

            R.id.iv_back -> {

                 onBackPressed()
            }

            R.id.et_email -> {
                val intent = Intent(MainApplication.appContext, UpdateAccountActivity::class.java)
                intent.putExtra(ApplicationConstants.FIELD_TO_UPDATE, ApplicationConstants.EMAIL)
                startActivity(intent)
            }

            R.id.et_phone_number -> {
                val intent = Intent(MainApplication.appContext, UpdateAccountActivity::class.java)
                intent.putExtra(ApplicationConstants.FIELD_TO_UPDATE, ApplicationConstants.PHONE_NUMBER)
                startActivity(intent)
            }

            R.id.btn_edit_desk -> {
                 myAccountViewModel.editDeskReference()
            }

            R.id.btn_share_data -> {
                showConfirmationPopup(true)
            }

            R.id.btn_delete_data -> {
                showConfirmationPopup(false)
            }
        }
    }

    private fun showConfirmationPopup(forShare: Boolean) {

        val title: String = if (forShare)
                                "Tap on YES to share your contact tracing data?"
                            else
                                "Are you sure you want to delete your contact tracing data?"

        val genericPopUpFragment = GenericPopUpFragment.newInstance(
                title,
                "Yes",
                "No",
                object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        if (forShare)
                            myAccountViewModel.sendViolations()
                        else
                            myAccountViewModel.deleteAllViolations()
                    }

                    override fun onNegativeInteraction() {}
                }
        )
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "share_delete_confirmation")
                .commitAllowingStateLoss()
    }

    private fun showProximityPopup(haveStartTimeEndTime: Boolean, forEnable: Boolean) {

        var message = ""
        if(haveStartTimeEndTime){
            val timeArray = myAccountUtil.getTrackingStartTimeEndTime().split("-").toTypedArray()
            message = if(forEnable)
                "As per company policy, you are allowed to enable Bluetooth Proxima between " + DateTimeUtil.getTodayTimeIn12HourFormat(timeArray[0]) + " and " + DateTimeUtil.getTodayTimeIn12HourFormat(timeArray[1])
            else
                "As per company policy, you cannot turn off Bluetooth Proxima between " + DateTimeUtil.getTodayTimeIn12HourFormat(timeArray[0]) + " and " + DateTimeUtil.getTodayTimeIn12HourFormat(timeArray[1])
        }
        else{
            message = "As per company policy, you cannot turn off Bluetooth Proxima"
            var shiftDuration = myAccountUtil.getShiftDuration()

            if(shiftDuration != 0){
                val dateTime = DateTimeUtil.getDateString12Hour(SharedPrefUtil.getLong(ApplicationConstants.BLE_START_TIME, 0) + shiftDuration * 60 * 1000).split(",").toTypedArray()
                message += " before " + dateTime[0]
            }
        }

        val genericPopUpFragment = GenericPopUpFragment.newInstance(message, "Ok", true, object : GenericPopUpFragment.OnFragmentInteractionListener {

            override fun onPositiveInteraction() {

            }

            override fun onNegativeInteraction() {

            }

        })

        genericPopUpFragment.show(supportFragmentManager, "proximity_restriction_dialog")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.getChildAt(0) != null) {
            (parent.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(applicationContext, R.color.violation_better_than))
            (parent.getChildAt(0) as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
        if (position > 0) {
            myAccountViewModel.updateGenderOnServer(position)
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun initialiseGenderSpinner(){
        val genderArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_accent, myAccountViewModel.spGenderList.value!!)
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sp_gender.adapter = genderArrayAdapter
        resetGenderSpinner()
    }

    private fun resetGenderSpinner(){
        sp_gender.setSelection(0)
    }
}
