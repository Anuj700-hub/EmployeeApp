package com.hungerbox.customer.bluetooth;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    public TextView deviceName, deviceAddress, deviceTime;

    public DeviceViewHolder(View itemView) {

        super(itemView);
        this.deviceName = itemView.findViewById(R.id.device_name);
        this.deviceAddress = itemView.findViewById(R.id.device_address);
        this.deviceTime = itemView.findViewById(R.id.device_time);

    }
}
