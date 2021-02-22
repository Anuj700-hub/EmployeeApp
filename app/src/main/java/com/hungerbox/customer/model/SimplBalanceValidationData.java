package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.model.serializer.CreditData;

import java.io.Serializable;

/**
 * Created by sandipanmitra on 6/3/17.
 */
//        {"data":
//            {"success":false,
//            "available_credit_in_paise":null,
//            "error_code":"unable_to_process",
//            "redirection_url":"https:\/\/sandbox.getsimpl.com\/subscriptions\/transactions\/limit_not_allowed?merchant_id=a7e6e4b8418ad9de1588745ea2bbd3f6&phone_number=8197926087&transaction_amount=138826","api_version":3
//            }
//        }
public class SimplBalanceValidationData implements Serializable {

    boolean success;
    @SerializedName("available_credit_in_paise")
    String availableCredit;
    @SerializedName("error_code")
    String errorCode;
    @SerializedName("redirection_url")
    String redirectionUrl;
    String message;

    @SerializedName("data")
    CreditData creditdata;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAvailableCredit() {

        return "" + Double.parseDouble(availableCredit) / 100;
    }

    public void setAvailableCredit(String availableCredit) {
        this.availableCredit = availableCredit;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreditData getCreditdata() {
        return creditdata;
    }

    public void setCreditdata(CreditData creditdata) {
        this.creditdata = creditdata;
    }
}
