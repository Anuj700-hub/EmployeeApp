package com.hungerbox.customer.order.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;

import com.hungerbox.customer.R;

public class ProductVegOnlySwitch extends RecyclerView.ViewHolder {
    public SwitchCompat switchVegOnly;
    public ProductVegOnlySwitch(@NonNull View itemView) {
        super(itemView);
        switchVegOnly = itemView.findViewById(R.id.sw_veg);
    }
}
