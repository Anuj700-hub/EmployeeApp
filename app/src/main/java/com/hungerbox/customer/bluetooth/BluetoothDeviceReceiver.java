package com.hungerbox.customer.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BluetoothDeviceReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (device.getName() != null && (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE || device.getName().contains("HB"))) {
                    Util.logDevice(device, rssi, context);
                }

            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
