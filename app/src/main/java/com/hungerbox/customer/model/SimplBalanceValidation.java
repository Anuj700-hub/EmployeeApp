package com.hungerbox.customer.model;

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
public class SimplBalanceValidation implements Serializable {

    SimplBalanceValidationData data;

    public SimplBalanceValidationData getData() {
        return data;
    }

    public void setData(SimplBalanceValidationData data) {
        this.data = data;
    }
}
