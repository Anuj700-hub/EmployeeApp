package com.hungerbox.customer.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.util.ApplicationConstants;

import java.io.Serializable;

/*
 * Created by manas on 17/8/17.
 * Sample Response
       {
           "id": 56,
           "name": "Dummy Wallet",
           "amount" : "234.0",
           "display_name": "Simpl Wallet",
           "default_recharge": 0,
           "monthly_pro_rata": false,
           "daysLeftInMonth": 0,
           "wallet_source": "external",
           "payment_source": {
               "data": {
                   "display_name": "Simpl",
                   "code": "simpl",
                   "show_balance": 1,
                   "linking_required": 1,
                   "otp_required_for_linking": 1,
                   "otp_required_for_checkout": 0,
                   "is_user_linked": true
               }
           }
       }
 */
public class PaymentMethod implements Comparable<PaymentMethod>, Serializable {

    long id = -1;
    String name;
    @SerializedName("display_name")
    String displayName;
    double amount = 0;
    @SerializedName("wallet_source")
    String walletSource;
    @SerializedName("payment_source")
    PaymentSource paymentSource;
    @SerializedName("company_wallet_id")
    long companyWalletId;
    @SerializedName("employee_can_recharge")
    int employeeCanRecharge;
    @SerializedName("user_can_link")
    int userCanLink;
    @SerializedName("payment_method")
    String paymentMethod = "";
    @SerializedName("priority")
    int priority;
    @SerializedName("payment_method_type")
    String paymentMethodType = "";
    @SerializedName("payment_details")
    PaymentDetailsResponse paymentDetailsResponse;
    @SerializedName("methods")
    PaymentMethodMethodResponse netBankingMethods;
    @SerializedName("payment_offer")
    PaymentOfferResponse paymentOfferResponse;
    @SerializedName("preferred")
    Integer preferred = 0;
    @SerializedName("logo")
    String logo;
    @SerializedName("otp_regex")
    String otpRegex = "";
    @SerializedName("default")
    int isDefault;
    @SerializedName("bin_id")
    String binId;
    @SerializedName("alert_message")
    String alertMessage;
    @SerializedName("upi_methods")
    PaymentUpiMethods paymentUpiMethods;
    boolean isCompulsory;
    String internalWalletsBalance = "";
    double nonRechargebleWalletBalance;
    private boolean selected;
    /**
     * This defines whether this can be selected in current scenario
     */
    private boolean canSelect = false;

    @SerializedName("convenience_fee")
    private double convenienceFee;

    @SerializedName("starting_amount")
    int defaultAmount;
    @SerializedName("show_reverse")
    int showReverse = 0;

    private boolean paymentTypeOthers = false;

    public boolean isPaymentTypeOthers() {
        return paymentTypeOthers;
    }

    public PaymentMethod setPaymentTypeOthers(boolean paymentTypeOthers) {
        this.paymentTypeOthers = paymentTypeOthers;
        return this;
    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public double getNonRechargebleWalletBalance() {
        return nonRechargebleWalletBalance;
    }

    public void setNonRechargebleWalletBalance(double nonRechargebleWalletBalance) {
        this.nonRechargebleWalletBalance = nonRechargebleWalletBalance;
    }

    public double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
    }

    public String getCard_company() {
        return card_company;
    }

    public void setCard_company(String card_company) {
        this.card_company = card_company;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    @SerializedName("card_company")
    String card_company = null;
    @SerializedName("card_type")
    String card_type = null;

    public String getInternalWalletsBalance() {
        return internalWalletsBalance;
    }

    public void setInternalWalletsBalance(String internalWalletsBalance) {
        this.internalWalletsBalance = internalWalletsBalance;
    }

    public boolean isCompulsory() {
        return isCompulsory;
    }

    public void setCompulsory(boolean compulsory) {
        isCompulsory = compulsory;
    }

    public String getOtpRegex() {
        return otpRegex;
    }

    public void setOtpRegex(String otpRegex) {
        this.otpRegex = otpRegex;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        if (displayName == null)
            return "";
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWalletSource() {
        return walletSource;
    }

    public void setWalletSource(String walletSource) {
        this.walletSource = walletSource;
    }

    public PaymentSource getPaymentSource() {
        return paymentSource;
    }

    public void setPaymentSource(PaymentSource paymentSource) {
        this.paymentSource = paymentSource;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getCompanyWalletId() {
        return companyWalletId;
    }

    public void setCompanyWalletId(long companyWalletId) {
        this.companyWalletId = companyWalletId;
    }

    public int getEmployeeCanRecharge() {
        return employeeCanRecharge;
    }

    public void setEmployeeCanRecharge(int employeeCanRecharge) {
        this.employeeCanRecharge = employeeCanRecharge;
    }

    public boolean employeeCanRecharge() {
        return getEmployeeCanRecharge() == 1;
    }

    public boolean isCanSelect() {
        return canSelect;
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }


    public boolean isInternal() {
        return getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL);
    }


    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PaymentMethod) {
            PaymentMethod other = (PaymentMethod) obj;
            return id == other.id;
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull PaymentMethod paymentMethod) {
        return paymentMethod.priority - priority;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public PaymentDetailsResponse getPaymentDetailsResponse() {
        return paymentDetailsResponse;
    }

    public PaymentUpiMethods getPaymentUpiMethods() {
        if(paymentUpiMethods==null)
            paymentUpiMethods = new PaymentUpiMethods();
        return paymentUpiMethods;
    }

    public void setPaymentUpiMethods(PaymentUpiMethods paymentUpiMethods) {
        this.paymentUpiMethods = paymentUpiMethods;
    }

    public void setPaymentDetailsResponse(PaymentDetailsResponse paymentDetailsResponse) {
        this.paymentDetailsResponse = paymentDetailsResponse;
    }

    public PaymentMethodMethodResponse getNetBankingMethods() {
        if (netBankingMethods == null)
            netBankingMethods = new PaymentMethodMethodResponse();
        return netBankingMethods;
    }

    public void setNetBankingMethods(PaymentMethodMethodResponse netBankingMethods) {
        this.netBankingMethods = netBankingMethods;
    }

    public boolean isWallet() {
        return paymentMethodType.equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_WALLET);
    }

    public boolean isDefault() {
        return isDefault > 0;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isLinkingAllowed() {
        if (userCanLink >= 1) {
            return true;
        } else if (getPaymentSource() != null) {
//            && getPaymentSource().getPaymentData().getCode().equalsIgnoreCase("simpl")
            if (getPaymentSource().getPaymentData().getLinkingRequired() == 1) {
                if (!getPaymentSource().getPaymentData().isUserLinked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getUserCanLink() {
        return userCanLink;
    }

    public void setUserCanLink(int userCanLink) {
        this.userCanLink = userCanLink;
    }

    public PaymentDetail getPaymentDetails() {
        if (paymentDetailsResponse == null || paymentDetailsResponse.paymentDetails == null) {
            return null;
        } else {
            return paymentDetailsResponse.paymentDetails;
        }
    }


    public boolean isUserLinked() {
        try {
            if (getPaymentDetails() != null) {
                return getPaymentDetails().isLinked();
            } else {
                return getPaymentSource().getPaymentData().isUserLinked();
            }
        } catch (NullPointerException ex) {
            return false;
        }

    }

    public PaymentOfferResponse getPaymentOfferResponse() {
        return paymentOfferResponse;
    }

    public void setPaymentOfferResponse(PaymentOfferResponse paymentOfferResponse) {
        this.paymentOfferResponse = paymentOfferResponse;
    }

    public String getPaymentOfferText() {
        if (paymentOfferResponse == null || paymentOfferResponse.paymentOffer == null) {
            return "";
        } else {
            return paymentOfferResponse.paymentOffer.getDesc();
        }
    }

    public int getPreferred() {
        if (preferred == null)
            preferred = 0;
        return preferred;
    }

    public void setPreferred(int preferred) {
        this.preferred = preferred;
    }

    public boolean isPreferred() {
        return this.getPreferred() >= 1;
    }

    public void setPreferred(Integer preferred) {
        this.preferred = preferred;
    }

    public boolean isSavedCard() {
        if (getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public String getLogo() {
        if (logo != null && !logo.isEmpty()) {
            return logo;
        }
        if (logo == null || logo.isEmpty()) {
            if (getPaymentSource() != null &&
                    getPaymentSource().getPaymentData() != null &&
                    getPaymentSource().getPaymentData().getPayment_logo() != null) {
                return getPaymentSource().getPaymentData().getPayment_logo();
            }
        }
        return "no image found";
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isDelinkingAllowed() {

        if (getPaymentSource() != null) {
//            && getPaymentSource().getPaymentData().getCode().equalsIgnoreCase("simpl")
            if (getPaymentSource().getPaymentData().getLinkingRequired() == 1) {
                if (getPaymentSource().getPaymentData().isUserLinked()) {
                    return true;
                }
            }
        } else if (getPaymentDetails() != null) {
            if (getPaymentDetails().isLinked()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    public int getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(int defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public int getShowReverse() {
        return showReverse;
    }

    public void setShowReverse(int showReverse) {
        this.showReverse = showReverse;
    }

    public boolean isShowReverse(){
        return this.showReverse==1;
    }

    public double getDisplayAmount(boolean isDisplayOnly) {
        double displayAmount = 0;
        if(isShowReverse() && isDisplayOnly){
            displayAmount = getDefaultAmount()-getAmount();
        }else{
            displayAmount = getAmount();
        }
        return displayAmount;
    }
}
