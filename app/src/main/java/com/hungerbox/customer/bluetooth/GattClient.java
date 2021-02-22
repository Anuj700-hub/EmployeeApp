package com.hungerbox.customer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.content.Context.BLUETOOTH_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GattClient {
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice;
    private String txPower = "";
    private int mRssi;
    private String txPowerLevel = "";
    private List<BluetoothGattCharacteristic> chars = new ArrayList<>();

    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                new Handler(Looper.getMainLooper()).post(() -> gatt.discoverServices());
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stopClient();
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                BluetoothGattService service = gatt.getService(ProximaConstants.SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic idCharacteristic = service.getCharacteristic(ProximaConstants.CHARACTERISTIC_UUID);
                    if (idCharacteristic != null) {
                        chars.add(idCharacteristic);
                    }
                }
                requestCharacteristics(gatt);
            }
        }

        public void requestCharacteristics(BluetoothGatt gatt) {
            if (!chars.isEmpty()) {
                gatt.readCharacteristic(chars.get(chars.size() - 1));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if (ProximaConstants.CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {

                byte[] data = characteristic.getValue();
                if(data != null){
                    String value = new String(data, StandardCharsets.UTF_8);

                    if(value.contains("HB")){
                        Util.logDevice(mDevice, (short) mRssi, mContext);
                    }
                    Log.d("GattClient", "Characteristic value - " + value);
                }
                else{
                    Log.d("GattClient", "This device does not support given descriptor type");
                }
            }
            if(chars.size() > 0){
                chars.remove(chars.get(chars.size() - 1));
            }

            if (chars.size() > 0) {
                requestCharacteristics(gatt);
            }
            else {
                gatt.disconnect();
            }
        }
    };

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startClient();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopClient();
                    break;
                default:
                    break;
            }
        }
    };

    public void startGattClient(Context context, ScanResult result) throws RuntimeException {

        mContext = context;
        mRssi = result.getRssi();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            txPower = String.valueOf(result.getTxPower());
        }
        if (result.getScanRecord() != null) {
            txPowerLevel = String.valueOf(result.getScanRecord().getTxPowerLevel());
        }

        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            registerReceiver();
            configureClient(result);
        }
    }

    private void configureClient(ScanResult result) {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        else {
            mDevice = mBluetoothAdapter.getRemoteDevice(result.getDevice().getAddress());
            startClient();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, filter);
    }

    private void startClient() {
        if (mDevice != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBluetoothGatt = mDevice.connectGatt(mContext, false, mGattCallback, TRANSPORT_LE);
                    } else {
                        mBluetoothGatt = mDevice.connectGatt(mContext, false, mGattCallback);
                    }
                }
                catch (Exception e){
                    Log.e("GattClient", "Error in connecting Gatt " + e.getMessage());
                }
            });
        }
    }

    private void stopClient() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void onDestroy() {
        if (mContext != null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter;
            if (mBluetoothManager != null) {
                bluetoothAdapter = mBluetoothManager.getAdapter();
                if (bluetoothAdapter.isEnabled()) {
                    stopClient();
                }
            }
        }
    }
}

