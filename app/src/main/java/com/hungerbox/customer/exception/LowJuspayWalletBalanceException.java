package com.hungerbox.customer.exception;

/**
 * Created by sandipanmitra on 4/16/18.
 */

public class LowJuspayWalletBalanceException extends Exception {
    double extraAmount;


    public LowJuspayWalletBalanceException(double extraAmount) {
        this.extraAmount = extraAmount;
    }

    public LowJuspayWalletBalanceException(String message, double extraAmount) {
        super(message);
        this.extraAmount = extraAmount;
    }

    public LowJuspayWalletBalanceException(String message, Throwable cause, double extraAmount) {
        super(message, cause);
        this.extraAmount = extraAmount;
    }

    public LowJuspayWalletBalanceException(Throwable cause, double extraAmount) {
        super(cause);
        this.extraAmount = extraAmount;
    }

    public LowJuspayWalletBalanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, double extraAmount) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.extraAmount = extraAmount;
    }

    public double getExtraAmount() {
        return extraAmount;
    }

    public void setExtraAmount(double extraAmount) {
        this.extraAmount = extraAmount;
    }
}
