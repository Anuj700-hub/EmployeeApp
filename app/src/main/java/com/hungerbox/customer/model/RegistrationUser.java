package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 7/14/17.
 */

public class RegistrationUser implements Serializable {

    @SerializedName("simpl_payload")
    String simplPayload;

    public int getEmailActivated() {
        return emailActivated;
    }

    public void setEmailActivated(int emailActivated) {
        this.emailActivated = emailActivated;
    }

    @SerializedName("emailActivated")
    int emailActivated = 0;

    @SerializedName("company_code")
    String company_code;
    @SerializedName("unified")
    boolean unified;
    @SerializedName("id")
    long id;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("mobile_no")
    String mobileNum;
    @SerializedName("password")
    String password;
    @SerializedName("confirm_password")
    String confirmPassword;
    @SerializedName("company_id")
    long companyId;
    @SerializedName("employee_id")
    String userName;
    @SerializedName("otp")
    String otp;
    @SerializedName("registration_qr_code")
    String qrCode;
    @SerializedName("location_id")
    long locationId;
    @SerializedName("verification_id")
    String verificationId;
    boolean show_simpl;
    @SerializedName("registration_id")
    long regId;
    @SerializedName("referral_qr_code")
    String referralQrCode;
    @SerializedName("otp_medium")
    String otpMedium;
    @SerializedName("gender")
    String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReferralQrCode() {
        return referralQrCode;
    }

    public void setReferralQrCode(String referralQrCode) {
        this.referralQrCode = referralQrCode;
    }

    public String getSimplPayload() {
        return simplPayload;
    }

    public void setSimplPayload(String simplPayload) {
        this.simplPayload = simplPayload;
    }

    public String getCompany_code() {
        return company_code;
    }

    public void setCompany_code(String company_code) {
        this.company_code = company_code;
    }

    public boolean getUnified() {
        return unified;
    }

    public void setUnified(boolean unified) {
        this.unified = unified;
    }

    public long getRegId() {
        return regId;
    }

    public void setRegId(long regId) {
        this.regId = regId;
    }

    public boolean isShow_simpl() {
        return show_simpl;
    }

    public void setShow_simpl(boolean show_simpl) {
        this.show_simpl = show_simpl;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getOtpMedium() {
        return otpMedium;
    }

    public void setOtpMedium(String otpMedium) {
        this.otpMedium = otpMedium;
    }
}
