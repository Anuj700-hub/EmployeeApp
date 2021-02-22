package com.hungerbox.customer.mvvm.view;

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.config.action.LogoutTask
import com.hungerbox.customer.databinding.ActivityUpdateAccDetailBinding
import com.hungerbox.customer.model.ChangePassword
import com.hungerbox.customer.model.UserEmail
import com.hungerbox.customer.model.UserMobile
import com.hungerbox.customer.mvvm.util.DesignPatternActions
import com.hungerbox.customer.mvvm.viewmodel.UpdateAccountViewModel
import com.hungerbox.customer.network.UrlConstant
import com.hungerbox.customer.prelogin.activity.ParentActivity
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.AppUtils.setupUI
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.ApplicationConstants.*
import com.hungerbox.customer.util.SharedPrefUtil
import kotlinx.android.synthetic.main.activity_update_acc_detail.*

class UpdateAccountActivity : ParentActivity(), View.OnClickListener {

    private var field: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogoutTask.updateTime()

        field = intent.getStringExtra(FIELD_TO_UPDATE)
        setupUI(findViewById(R.id.updateAccountActivityParent), this)

        val binding = DataBindingUtil.setContentView<ActivityUpdateAccDetailBinding>(this, R.layout.activity_update_acc_detail)
        val updateAccountViewModel = ViewModelProviders.of(this).get(UpdateAccountViewModel::class.java)
        binding.updateAccountAccountViewModel = updateAccountViewModel
        binding.lifecycleOwner = this

        updateAccountViewModel.userDetailsUpdate.observe(this, Observer<DesignPatternActions> {
            when (it.type) {
                Actions.ACTIVITY_BACK -> {
                    onBackPressed()
                }
                Actions.ENABLED_BUTTON_UPDATE -> {
                    btn_update_account.isEnabled = true
                }
                Actions.LOGOUT -> {
                    AppUtils.doLogout(this)
                }
            }
        })

        setView(field)

        initializeListeners()
    }

    private fun initializeListeners() {
        btn_update_account.setOnClickListener(this)
        iv_back.setOnClickListener(this)
    }

    private fun isInputValid() : Boolean{

        if ((et_old_password.text.toString().isEmpty()) && !AppUtils.getConfig(MainApplication.appContext).isPassword_change_on_nfc) {
            til_old_password.isErrorEnabled = true
            til_old_password.error = "Please enter your old password"
            return false
        }  else{
            til_old_password.isErrorEnabled = false
        }

        if (et_acc_field.text.toString().isEmpty()) {
            til_acc_field.isErrorEnabled = true
            til_acc_field.error = "Please enter your new password"
            return false
        } else{
            til_acc_field.isErrorEnabled = false
        }

        if (et_acc_field.text.toString().length < 8) {
            til_acc_field.isErrorEnabled = true
            et_acc_field.text!!.clear()
            til_acc_field.error = "The password length should be at least 8 characters"
            return false
        } else{
            til_acc_field.isErrorEnabled = false
        }

        if (et_acc_field.text.toString().isEmpty()) {
            til_acc_confirm_field.isErrorEnabled = true;
            til_acc_confirm_field.error = "Please enter your new password"
            return false
        } else{
            til_acc_confirm_field.isErrorEnabled = false
        }

        if (et_acc_confirm.text.toString().length < 8) {
            et_acc_confirm.text!!.clear()
            til_acc_confirm_field.error = "The password length should be at least 8 characters"
            return false
        } else{
            til_acc_confirm_field.isErrorEnabled = false
        }

        if (et_acc_field.text.toString() != et_acc_confirm.text.toString()) {
            et_acc_confirm.text!!.clear()
            til_acc_confirm_field.error = "Your new password did not match"
            return false
        } else{
            til_acc_confirm_field.isErrorEnabled = false
        }

        return true
    }

    private fun setView(field : String?) {

        when {
            field.equals("email", true) -> {
                et_acc_field.hint = "Enter Email"
                et_acc_field.inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                et_acc_confirm.visibility = View.GONE;
                et_old_password.visibility = View.GONE;
            }
            field.equals("phone_number", true) -> {
                et_acc_field.hint = "Enter Phone Number"
                et_acc_field.inputType = EditorInfo.TYPE_CLASS_NUMBER;
                et_acc_confirm.visibility = View.GONE;
                et_old_password.visibility = View.GONE;
            }
            field.equals("password", true) -> {
                til_old_password.hint = "Old Password";
                til_acc_field.hint = "New Password";
                et_acc_field.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                til_acc_confirm_field.hint = "Confirm New Password";
                et_acc_confirm.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                et_acc_confirm.visibility = View.VISIBLE;
                et_old_password.visibility = View.VISIBLE;
                et_acc_field.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                et_acc_confirm.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                et_old_password.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
                et_acc_field.transformationMethod = PasswordTransformationMethod.getInstance();
                et_acc_confirm.transformationMethod = PasswordTransformationMethod.getInstance();
                et_old_password.transformationMethod = PasswordTransformationMethod.getInstance();
            }
        }
    }

    private fun validateAndUpdate() {
        val updateAccountViewModel = ViewModelProviders.of(this).get(UpdateAccountViewModel::class.java)
        if (field.equals(EMAIL, true)) {
            if (et_acc_field.text.toString().length != 4 && !et_acc_field.text.toString().contains("@")) {
                AppUtils.showToast("Please enter a valid email", true, 0)
                return
            }
            val url = UrlConstant.CHANGE_EMPLOYEE_DETAILS + SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
            btn_update_account.isEnabled = false
            updateAccountViewModel.changeUserDetails(url, UserEmail().setEmail(et_acc_field.text.toString()), "Email changed successfully", Actions.ACTIVITY_BACK)
        } else if (field.equals(PHONE_NUMBER, true)) {
            if (et_acc_field.text.toString().length == 10 || (et_acc_field.text.toString().startsWith("+") && et_acc_field.text.toString().length == 13) ||
                    (et_acc_field.text.toString().startsWith("+") && et_acc_field.text.toString()[3] == '-' && et_acc_field.text.toString().length == 14)) {
                val url = UrlConstant.CHANGE_EMPLOYEE_DETAILS + SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
                btn_update_account.isEnabled = false
                updateAccountViewModel.changeUserDetails(url, UserMobile().setMobileNum(et_acc_field.text.toString()), "Phone Number changed successfully", Actions.ACTIVITY_BACK)
            } else {
                AppUtils.showToast("Please enter a valid Phone Number", false, 2);
            }
        } else if (field.equals(PASSWORD, true)) {
            if (isInputValid()) {
                btn_update_account.isEnabled = false
                updateAccountViewModel.changeUserDetails(UrlConstant.CHANGE_PASSWORD, ChangePassword().setUserId(SharedPrefUtil.getLong(PREF_USER_ID, 0)).setOldPassword(et_old_password.text.toString()).setPassword(et_acc_field.text.toString()).setPasswordConfirmation(et_acc_field.text.toString()), "Your Password was changed. Please log in with your new Password", Actions.LOGOUT)
            }
        }

    }

    override fun onClick(v: View?) {

        when(v?.id){

            R.id.iv_back -> finish()

            R.id.btn_update_account ->
                validateAndUpdate()
        }
    }

}
