package com.hungerbox.customer.bluetooth;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;

import java.util.ArrayList;

public class NearbyDeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    ArrayList<HBDevice> nearbyDevices;
    Activity activity;
    LayoutInflater layoutInflater;

    public NearbyDeviceAdapter(Activity activity, ArrayList<HBDevice> deviceList) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
        this.nearbyDevices = deviceList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(layoutInflater.inflate(R.layout.device_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        if(nearbyDevices.get(position).name == null){
            holder.deviceName.setText("Bluetooth Device");
        }
        else{
            holder.deviceName.setText(nearbyDevices.get(position).name);
        }
        holder.deviceAddress.setText(nearbyDevices.get(position).address);
        holder.deviceTime.setText(DateFormat.format("hh:mm:ss", nearbyDevices.get(position).time).toString());
    }

    @Override
    public int getItemCount() {
        return nearbyDevices.size();
    }
}
