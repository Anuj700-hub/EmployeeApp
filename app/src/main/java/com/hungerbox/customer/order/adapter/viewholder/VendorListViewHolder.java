package com.hungerbox.customer.order.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

public class VendorListViewHolder extends RecyclerView.ViewHolder {

    public RecyclerView rvVendorList, rvVendingList;
    public CardView cvVendorList, cvVendingList;
    public TextView tvVendorListTitle,tvVendingListTitle;

    public VendorListViewHolder(@NonNull View itemView) {
        super(itemView);

        rvVendorList = itemView.findViewById(R.id.rv_vendor_list);
        rvVendingList = itemView.findViewById(R.id.rv_vending_machine_list);
        cvVendorList = itemView.findViewById(R.id.cv_vendor);
        cvVendingList = itemView.findViewById(R.id.cv_vending);
        tvVendorListTitle = itemView.findViewById(R.id.tv_vendor_list_title);
        tvVendingListTitle = itemView.findViewById(R.id.tv_vending_list_title);

    }
}
