package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.view.ViewCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.contest.activity.TermsAndCondition;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.RegistrationUser;
import com.hungerbox.customer.model.RegistrationUserResposne;
import com.hungerbox.customer.navmenu.fragment.LocationDialog;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.SimplOtpVerificationActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;


public class SignUpActivityBasic extends ParentActivity {


    TextInputLayout tilName, tilEmail, tilPhone, tilPassword, tilPasswordRepeat;
    TextInputEditText tetName, tetEmail, tetPhone, tetPassword, tetPasswordAgain;
    CheckBox cbMale, cbFemale, cbNonBinary;
    LinearLayout llGender;
    Button btSignUp;
    Config config;
    long locationId;
    String locationName, qrToken;
    ImageView ivBack,ivlogo;
    TextView tbTitle,tbHeader;
    private LocationDialog locationDialog;
    RelativeLayout rlProgress;
    CheckBox cbTerms;
    SpannableString cbText;
    Config.Terms terms;
    TextView tvTerms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_basic);
        AppUtils.setupUI(findViewById(R.id.signupActivityParent), SignUpActivityBasic.this);

        ivBack = findViewById(R.id.iv_back);
        tbTitle = findViewById(R.id.tb_title);
        tbHeader = findViewById(R.id.tb_header);
        ivlogo = findViewById(R.id.iv_logo);
        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPasswordRepeat = findViewById(R.id.til_password_again);
        tilPassword = findViewById(R.id.til_password);
        rlProgress = findViewById(R.id.rl_progress);
        tvTerms = findViewById(R.id.tv_terms);
        cbMale = findViewById(R.id.cb_male);
        cbFemale = findViewById(R.id.cb_female);
        cbNonBinary = findViewById(R.id.cb_non_binary);
        llGender = findViewById(R.id.ll_gender);


        tetName = findViewById(R.id.tet_name);
        tetEmail = findViewById(R.id.tet_email);
        tetPhone = findViewById(R.id.tet_phone);
        tetPassword = findViewById(R.id.tet_password);
        tetPasswordAgain = findViewById(R.id.tet_password_again);
        cbTerms = findViewById(R.id.cb_terms);
        btSignUp = findViewById(R.id.bt_sign_up);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                AppUtils.HbLog("test","spannable string click");
                Intent intent = new Intent(getApplicationContext(),TermsAndCondition.class);
                intent.putExtra("isUrl",true);
                intent.putExtra("url",AppUtils.getConfig(getApplicationContext()).getTerms().getUrl());
                intent.putExtra(ApplicationConstants.HEADER_TITLE,"Terms and Conditions");
                startActivity(intent);
            }
        };

        if (AppUtils.getConfig(this).getTerms()!=null) {
            terms = AppUtils.getConfig(this).getTerms();
            if (terms.getTerms_text() != null && !terms.getTerms_text().isEmpty() && !terms.getTerms_text().equals(" ")) {
                cbText = new SpannableString(terms.getTerms_text());
                if (terms.getStart_index() >= 0 && terms.getStart_index()<cbText.length() && terms.getStart_index()<=terms.getEnd_index()
                        &&terms.getEnd_index()>=0 && terms.getEnd_index()<cbText.length()) {
                        cbText.setSpan(new ForegroundColorSpan(Color.RED), terms.getStart_index(),terms.getEnd_index()+1, 0);
                        cbText.setSpan(clickableSpan, terms.getStart_index(), terms.getEnd_index()+1, 0);
                    tvTerms.setText(cbText);
                    tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
                    tvTerms.setVisibility(View.VISIBLE);
                    cbTerms.setVisibility(View.VISIBLE);
                }
            }
        }
        locationId = getIntent().getLongExtra(ApplicationConstants.LOCATION_ID, 0);
        locationName = getIntent().getStringExtra(ApplicationConstants.LOCATION_NAME);
        qrToken = getIntent().getStringExtra(ApplicationConstants.QR_CODE);
        RegistrationUser registrationUser = new Gson().fromJson(SharedPrefUtil.getString(ApplicationConstants.REGISTRATERED_USER, null), RegistrationUser.class);

        config = AppUtils.getConfig(this);
        if(registrationUser != null && config.isSignup_email_activation()){
            preFillUser(registrationUser);
        }
        if(config.getAsk_gender_info()){
            llGender.setVisibility(View.VISIBLE);
            setCheckBoxListeners();
        }


        tbTitle.setText("Welcome to");
        ivlogo.setVisibility(View.VISIBLE);
        tbHeader.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateBack();
            }
        });
        ArrayList<String> emailList = AppUtils.getConfig(this).getEmail_verification();
        if (emailList.size() > 0) {
            tilEmail.setHint("Company Email Id");
        } else {
            tilEmail.setHint("Email Id");
        }

       /* cbTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (AppUtils.getConfig(getApplicationContext()).getTerms_and_conditions_url()!=null){
                    if (isChecked){
                        btSignUp.setVisibility(View.VISIBLE);
                    }
                    else {
                        btSignUp.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });*/
        tetName.requestFocus();
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        ViewCompat.setBackgroundTintList(tetName, colorStateList);
        tetEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(tetEmail, colorStateList);
                tilEmail.setError("");
                if (charSequence.length() > 300) {
                    tetEmail.setText(charSequence.subSequence(0, 299));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tetName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(tetName, colorStateList);
                if (charSequence.length() > 50) {
                    tetName.setText(charSequence.subSequence(0, 49));
                }
                tilName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tetPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tetPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilPassword.setPasswordVisibilityToggleTintList(colorStateList);
                } else {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black_40));
                    tilPassword.setPasswordVisibilityToggleTintList(colorStateList);
                }
            }
        });

        tetPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPasswordRepeat.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tetPasswordAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                    tilPasswordRepeat.setPasswordVisibilityToggleTintList(colorStateList);
                } else {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black_40));
                    tilPasswordRepeat.setPasswordVisibilityToggleTintList(colorStateList);
                }
            }
        });

        tetPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
                ViewCompat.setBackgroundTintList(tetPhone, colorStateList);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    btSignUp.setEnabled(false);
                    performSignUp();
                }

            }
        });

        showLocationDialog();
    }

    void setCheckBoxListeners(){
        cbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){
                cbFemale.setChecked(false);
                cbNonBinary.setChecked(false);
                CompoundButtonCompat.setButtonTintList(cbMale, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }
            else{
                CompoundButtonCompat.setButtonTintList(cbMale, ColorStateList.valueOf(getResources().getColor(R.color.black_40)));
            }
        });
        cbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){
                cbMale.setChecked(false);
                cbNonBinary.setChecked(false);
                CompoundButtonCompat.setButtonTintList(cbFemale, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }
            else{
                CompoundButtonCompat.setButtonTintList(cbFemale, ColorStateList.valueOf(getResources().getColor(R.color.black_40)));
            }
        });
        cbNonBinary.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){
                cbFemale.setChecked(false);
                cbMale.setChecked(false);
                CompoundButtonCompat.setButtonTintList(cbNonBinary, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }
            else{
                CompoundButtonCompat.setButtonTintList(cbNonBinary, ColorStateList.valueOf(getResources().getColor(R.color.black_40)));
            }
        });
    }

    private void preFillUser(RegistrationUser registrationUser) {
        tetName.setText(registrationUser.getName());
        tetPhone.setText(registrationUser.getMobileNum());
        tetPassword.setText(registrationUser.getPassword());
        tetPasswordAgain.setText(registrationUser.getPassword());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showLocationDialog() {

        locationDialog = LocationDialog.newInstance(new LocationDialog.OnLocationChangeListener() {
            @Override
            public void onLocationChanged(long locationId, String locationName) {
                SignUpActivityBasic.this.locationId = locationId;
                SignUpActivityBasic.this.locationName = locationName;
            }
        }, true);
        locationDialog.setCancelable(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(locationDialog, "location")
                .commitAllowingStateLoss();
    }

    private void performSignUp() {
        final RegistrationUser registrationUser = new RegistrationUser();
        registrationUser.setName(tetName.getText().toString().trim());
        registrationUser.setEmail(tetEmail.getText().toString().trim());
        registrationUser.setMobileNum(tetPhone.getText().toString().trim());
        registrationUser.setUserName(tetPhone.getText().toString().trim());
        registrationUser.setPassword(tetPassword.getText().toString().trim());
        registrationUser.setConfirmPassword(tetPasswordAgain.getText().toString().trim());
        registrationUser.setCompanyId(config.getCompany_id());
        registrationUser.setLocationId(locationId);
        registrationUser.setShow_simpl(true);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("common")) {
            String companyCode = SharedPrefUtil.getString(ApplicationConstants.PREF_UNIFIED_COMPANY_CODE, "");
            registrationUser.setCompany_code(companyCode);
            String referralQrCode = SharedPrefUtil.getString(ApplicationConstants.REFERRAL_QR_CODE,"");
            if (referralQrCode!=null) {
                registrationUser.setReferralQrCode(referralQrCode);
            }
        } else {
            registrationUser.setQrCode(qrToken);
        }

        if(config.getAsk_gender_info()){
            if(cbMale.isChecked())
                registrationUser.setGender("Male");
            else if(cbFemale.isChecked())
                registrationUser.setGender("Female");
            else if(cbNonBinary.isChecked())
                registrationUser.setGender("Others");
        }

        showProgress();
        String url = UrlConstant.SIGN_UP_BASIC;
        SimpleHttpAgent<RegistrationUserResposne> registrationUserSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<RegistrationUserResposne>() {
                    @Override
                    public void response(RegistrationUserResposne responseObject) {
                        hideProgress();
                        responseObject.getRegistrationUser().setPassword(registrationUser.getPassword());
                        responseObject.getRegistrationUser().setRegId(responseObject.getRegistrationUser().getId());
                        if(config.isSignup_email_activation()){
                            SharedPrefUtil.putString(ApplicationConstants.REGISTRATERED_USER,
                                    new Gson().toJson(responseObject.getRegistrationUser()));
                            Intent intent = new Intent(SignUpActivityBasic.this, EmailActivationRequired.class);
                            intent.putExtra("reg_user",responseObject.getRegistrationUser());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else if (config.getRegistration_wallet() != null &&
                                config.getRegistration_wallet().getWallet_code()!=null&&
                                !config.getRegistration_wallet().getWallet_code().equalsIgnoreCase("")&&
                                config.getRegistration_wallet().getWallet_name()!=null&&
                                !config.getRegistration_wallet().getWallet_name().equalsIgnoreCase(""))
                            goForOtpValidation(responseObject.getRegistrationUser(), config.getRegistration_wallet());
                        else
                            getRegistrationOtp(responseObject.getRegistrationUser());
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        btSignUp.setEnabled(true);
                        hideProgress();

                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showErrorDialog(ApplicationConstants.NO_NET_STRING, ApplicationConstants.NO_INTERNET_IMAGE);
                        }else {
                            if (errorResponse != null)
                                showErrorDialog(errorResponse.message, ApplicationConstants.GENERAL_ERROR);
                            else
                                showErrorDialog(error, ApplicationConstants.GENERAL_ERROR);
                        }
                    }
                },
                RegistrationUserResposne.class
        );
        registrationUserSimpleHttpAgent.post(registrationUser, new HashMap<String, JsonSerializer>());
    }

    private void showErrorDialog(String msg , String imageType){
        ErrorPopFragment errorPopUp = ErrorPopFragment.Companion.newInstance(msg, "Retry", true, imageType,
                new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        performSignUp();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(errorPopUp, "Signup error")
                .commit();    }



    private void getRegistrationOtp(final RegistrationUser registrationUser) {
        Intent intent = new Intent(SignUpActivityBasic.this, OtpVerificationActivity.class);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_CONT, true);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
        intent.putExtra("name", OtpVerificationActivity.SIGN_UP_ACTIVITY);
        startActivity(intent);
    }

    private void goForOtpValidation(RegistrationUser registrationUser, Config.RegistrationWallet registrationWallet) {
        Intent intent = new Intent(this, SimplOtpVerificationActivity.class);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_WALLET, registrationWallet);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_OBJ, registrationUser);
        intent.putExtra(ApplicationConstants.USER_REGISTRATION_CONT,true);
        startActivity(intent);
    }

    private boolean isInputValid() {
        boolean isValid = true;
        if (tetName.getText() == null || tetName.getText().toString().trim().isEmpty()) {
            tilName.setErrorEnabled(true);
            tilName.setError("Name cannot be empty");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tilName.startAnimation(shake);
            isValid = false;
            return isValid;
        } else {
            tilName.setErrorEnabled(false);
        }

        ArrayList<String> emailList = AppUtils.getConfig(this).getEmail_verification();
        boolean isEmailPresent = false;
        for (String email : emailList) {
            if (tetEmail.getText().toString().contains(email))
                isEmailPresent = true;
        }
        if (emailList.size() <= 0)
            isEmailPresent = true;

        try {
            Pattern pattern = Patterns.EMAIL_ADDRESS;

            if (!AppUtils.getConfig(SignUpActivityBasic.this).getEmail_pattern().equals("")) {
                pattern = Pattern.compile(AppUtils.getConfig(SignUpActivityBasic.this).getEmail_pattern(), Pattern.CASE_INSENSITIVE);
            }

            if (!pattern.matcher(tetEmail.getText()).matches() || !isEmailPresent) {
                tilEmail.setErrorEnabled(true);
                tilEmail.setError("Email not valid");
                Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
                tilEmail.startAnimation(shake);
                if (!AppUtils.getConfig(SignUpActivityBasic.this).getEmail_pattern().equals("") && tetEmail.getText().toString() != null && !tetEmail.getText().toString().equalsIgnoreCase("")) {

                    AppUtils.showToast("Please enter valid email.", true, 2, true);
                }
                isValid = false;
                return isValid;
            } else
                tilEmail.setErrorEnabled(false);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (tetPhone.getText() == null || tetPhone.getText().toString().isEmpty()) {
            tilPhone.setErrorEnabled(true);
            tilPhone.setError("No phone number given");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tetPhone.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPhone.setErrorEnabled(false);


        if (tetPhone.getText().toString().trim().length() != 10) {
            tilPhone.setErrorEnabled(true);
            tilPhone.setError("Enter valid phone number");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tetPhone.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPhone.setErrorEnabled(false);


        if (tetPassword.getText() == null || tetPassword.getText().toString().isEmpty()) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("No password given");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tetPassword.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPassword.setErrorEnabled(false);


        if (tetPasswordAgain.getText() == null || tetPasswordAgain.getText().toString().isEmpty()) {
            tilPasswordRepeat.setErrorEnabled(true);
            tilPasswordRepeat.setError("Enter new password");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tetPasswordAgain.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPasswordRepeat.setErrorEnabled(false);


        if (!tetPasswordAgain.getText().toString().equalsIgnoreCase(tetPassword.getText().toString())) {
            tilPasswordRepeat.setErrorEnabled(true);
            tilPasswordRepeat.setError("Passwords do not match");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tilPasswordRepeat.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPasswordRepeat.setErrorEnabled(false);

        if (tetPassword.getText().toString().length() < 8) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Password should be a minimum of 8 characters");
            Animation shake = AnimationUtils.loadAnimation(SignUpActivityBasic.this, R.anim.vibrate);
            tilPassword.startAnimation(shake);
            isValid = false;
            return isValid;
        } else
            tilPassword.setErrorEnabled(false);
        if (terms!=null && terms.getUrl()!=null && !terms.getUrl().isEmpty() &&
                !terms.getUrl().equals(" ") && !cbTerms.isChecked()){
            AppUtils.showToast("Please agree to Terms & Conditions",true,2);
            isValid=false;
            return isValid;
        }
        if((config.getAsk_gender_info()) && (!cbMale.isChecked() && !cbFemale.isChecked() && !cbNonBinary.isChecked())){
            AppUtils.showToast("Please select your Gender",true,2);
            isValid = false;
        }

        return isValid;
    }

    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        rlProgress.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btSignUp.setEnabled(true);
    }
}
