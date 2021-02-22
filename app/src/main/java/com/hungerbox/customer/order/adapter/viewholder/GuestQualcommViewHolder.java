package com.hungerbox.customer.order.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

public class GuestQualcommViewHolder extends RecyclerView.ViewHolder {

    public TextView tvGuestName;
    public ImageView ivClose;

    public GuestQualcommViewHolder(@NonNull View itemView) {
        super(itemView);
        tvGuestName = itemView.findViewById(R.id.tv_guest_name);
        ivClose = itemView.findViewById(R.id.iv_close);
    }
}
