package com.hungerbox.customer.payment;

public interface OnUpiClickListener {

    void onUpiMethodSelected(int position,String pkgName,boolean isSelected);
}
