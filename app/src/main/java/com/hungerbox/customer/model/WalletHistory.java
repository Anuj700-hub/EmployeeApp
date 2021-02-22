package com.hungerbox.customer.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.ApplicationConstants;

public class WalletHistory implements java.io.Serializable {
    private static final long serialVersionUID = 325675260888960465L;
    @SerializedName("transaction_reference_id")
    String transactionId;
    @SerializedName("transaction_status")
    String status;
    @SerializedName("transaction_state")
    String transactionState;
    @SerializedName("wallet_source")
    String walletSource;
    private String change;
    private String reference_id;
    private String before_amount;
    private long created_at;
    private String user_id;
    private String comment;
    private String reference_type;
    private String after_amount ="";
    @SerializedName("wallet_name")
    private String walletName;

    public String getChange() {
        return this.change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getReference_id() {
        return this.reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getBefore_amount() {
        return this.before_amount;
    }

    public void setBefore_amount(String before_amount) {
        this.before_amount = before_amount;
    }

    public long getCreated_at() {
        return this.created_at * 1000;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReference_type() {
        return this.reference_type;
    }

    public void setReference_type(String reference_type) {
        this.reference_type = reference_type;
    }

    public String getAfter_amount() {
        return after_amount;
    }

    public void setAfter_amount(String after_amount) {
        this.after_amount = after_amount;
    }

    public String getWalletName() {
        if (walletName == null)
            walletName = "";
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getTransactionId() {
        if (transactionId == null)
            transactionId = "";
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        if (status == null)
            status = "";
        if (status.equalsIgnoreCase("1")) {
            return "success";
        } else if (status.equalsIgnoreCase("0")) {
            return "failure";
        } else {
            return "";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionState() {
        if (transactionState == null)
            transactionState = "";
        return transactionState;
    }

    public void setTransactionState(String transactionState) {
        this.transactionState = transactionState;
    }

    public String getWalletSource() {
        if (walletSource == null)
            walletSource = "";
        return walletSource;
    }

    public void setWalletSource(String walletSource) {
        this.walletSource = walletSource;
    }


    public int getWalletSatusTextColor(Context context) {
        switch (getTransactionState()) {
            case ApplicationConstants.PAYMENT_STATE_SUCCESS:
                return context.getResources().getColor(R.color.green);
            case ApplicationConstants.PAYMENT_STATE_FAILURE:
                return context.getResources().getColor(R.color.red);
            case ApplicationConstants.PAYMENT_STATE_PENDING:
                return context.getResources().getColor(R.color.colorAccent);
            case ApplicationConstants.PAYMENT_STATE_REVERSE:
                return context.getResources().getColor(R.color.green);
            default:
                return context.getResources().getColor(R.color.black);
        }
    }

    public String getAmountAfterDeduction() {
        try {
            double amountChange = Double.parseDouble(change);
            double beforeAmount = Double.parseDouble(before_amount);
            return String.format("₹ %.2f", (beforeAmount + amountChange));
        } catch (Exception e) {
            return this.before_amount;
        }
    }
}
