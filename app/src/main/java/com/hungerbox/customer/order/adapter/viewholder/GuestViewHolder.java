package com.hungerbox.customer.order.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by peeyush on 28/6/16.
 */
public class GuestViewHolder extends RecyclerView.ViewHolder {

    public TextView tvGuestName, tvValidFrom, tvValidTill, tvStatus;
    public ImageView ivRemoveGuest;

    public GuestViewHolder(View itemView) {
        super(itemView);
        tvGuestName = itemView.findViewById(R.id.tv_guest_name);
        tvValidFrom = itemView.findViewById(R.id.tv_guest_valid_from);
        tvValidTill = itemView.findViewById(R.id.tv_guest_valid_till);
        tvStatus = itemView.findViewById(R.id.tv_status);
        ivRemoveGuest = itemView.findViewById(R.id.iv_remove_guest);
    }
}
