package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manas on 7/12/18.
 */
public class EmailVerification {
    @SerializedName("data")
    EmailResponse data;

    public EmailResponse getData() {
        return data;
    }

    public void setData(EmailResponse data) {
        this.data = data;
    }

   public class EmailResponse {
        @SerializedName("success")
        boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

}
