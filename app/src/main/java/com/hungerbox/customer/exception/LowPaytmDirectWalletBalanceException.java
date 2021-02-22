package com.hungerbox.customer.exception;

public class LowPaytmDirectWalletBalanceException extends Exception {

    private double deficitAmount;
    private boolean isAddMoneyAllowed;

    public LowPaytmDirectWalletBalanceException(double deficitAmount,boolean isAddMoneyAllowed){
        this.deficitAmount = deficitAmount;
        this.isAddMoneyAllowed = isAddMoneyAllowed;
    }

    public double getDeficitAmount() {
        return deficitAmount;
    }

    public void setDeficitAmount(double deficitAmount) {
        this.deficitAmount = deficitAmount;
    }

    public boolean isAddMoneyAllowed() {
        return isAddMoneyAllowed;
    }

    public void setAddMoneyAllowed(boolean addMoneyAllowed) {
        isAddMoneyAllowed = addMoneyAllowed;
    }
}
