package com.hungerbox.customer.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.hungerbox.customer.R;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.Arrays;
import java.util.UUID;

public class BluetoothDeviceCheckActivity extends ParentActivity {
    BluetoothAdapter bluetoothAdapter;
    int ENABLE_REQUEST = 0;
    int PERMISSION_REQUEST = 1;
    int DISCOVERABLE_REQUEST = 2;
    int LOCATION_REQUEST = 3;
    int PERMISSION_DENIED_REQUEST = 4;
    int OPTIMISATION_REQUEST = 5;
    public static int BLUETOOTH_DISCOVERABLE_TIME = 3600;
    private static int BLUETOOTH_INFO_REQUEST = 5015;
    String optimisationMessage = "Please allow app to run in background";
    String[] permission;

    BluetoothGattService mBluetoothGattService;
    AdvertiseCallback advertiseCallback;
    BluetoothLeAdvertiser bluetoothLeAdvertiser;
    BluetoothGattServer mGattServer;
    BluetoothManager mBluetoothManager;
    BluetoothGattCharacteristic mBatteryLevelCharacteristic;

    // UUID's for Gatt
    UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    static UUID CHARACTERISTIC_USER_DESCRIPTION_UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    static UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    String BATTERY_LEVEL_DESCRIPTION = "The current charge level of a " + "battery. 100% represents fully charged while 0% represents fully discharged.";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset,null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            if (offset != 0) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_INVALID_OFFSET, offset, null);
                return;
            }
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

            int status = BluetoothGatt.GATT_SUCCESS;

            if (descriptor.getUuid() == CLIENT_CHARACTERISTIC_CONFIGURATION_UUID) {

                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                boolean supportsNotifications = (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
                boolean supportsIndications = (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0;

                if (!(supportsNotifications || supportsIndications)) {
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                }
                else if (value.length != 2) {
                    status = BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH;
                }
                else if (Arrays.equals(value, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) || (supportsNotifications && Arrays.equals(value, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) || (supportsIndications && Arrays.equals(value, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE))) {
                    status = BluetoothGatt.GATT_SUCCESS;
                    descriptor.setValue(value);
                }
                else {
                    status = BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED;
                }
            } else {
                status = BluetoothGatt.GATT_SUCCESS;
                descriptor.setValue(value);
            }
            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, status,0,null);
            }
        }
    };
    private boolean bluetooth_for_first_time = true;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_bluetooth_device_check);

        bluetooth_for_first_time = SharedPrefUtil.getBoolean(Util.BLUETOOTH_INIT_FOR_FIRST_TIME, true);

        if(bluetooth_for_first_time){

            Intent intent = new Intent(this, BluetoothInformationActivity.class);
            startActivityForResult(intent, BLUETOOTH_INFO_REQUEST);

        }
        else{
            checkForBluetoothPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkForBluetoothPermissions(){
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();

        long userId = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID, 0);
        if(userId != 0)
            bluetoothAdapter.setName("HB-"+ userId);

        checkForBatteryOptimisation();
    }

    private void enableBluetooth() {
        Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBT, ENABLE_REQUEST);
    }

    private void manageDiscoverable() {

        if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABLE_TIME);
            startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST);
        }else{
            startDiscovery();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ENABLE_REQUEST){
            if(resultCode == RESULT_OK){
                managePermission();
            }
            else{
                if(checkToExit()){
                    exitPopupFlow(true);
                }
                else{
                    enableBluetooth();
                }
            }
        }
        else if(requestCode == PERMISSION_REQUEST){
            askForGps();
        } else if(requestCode == DISCOVERABLE_REQUEST){

            if(resultCode == BLUETOOTH_DISCOVERABLE_TIME){
                startDiscovery();
            }else{
                if(checkToExit()){
                    exitPopupFlow(true);
                }
                else{
                    manageDiscoverable();
                }
            }
        }else if(requestCode == LOCATION_REQUEST){
            if(checkToExit()){
                if(resultCode == RESULT_OK){
                    askForGps();
                }
                else{
                    exitPopupFlow(true);
                }
            }
            else{
                askForGps();
            }
        }else if(requestCode == PERMISSION_DENIED_REQUEST){
            managePermission();
        }
        else if(requestCode == OPTIMISATION_REQUEST) {
            afterCheckingOptimisationSetting();
        }
        else if(requestCode == BLUETOOTH_INFO_REQUEST){
            SharedPrefUtil.putBoolean(Util.BLUETOOTH_INIT_FOR_FIRST_TIME, false);
            checkForBluetoothPermissions();
        }
    }

    private boolean checkToExit(){
        if(AppUtils.getConfig(this.getApplicationContext()).getBluetooth_distancing() != null && ViolationLogManager.Companion.isBluetoothAllDayEnabled(this.getApplicationContext()))
            return true;
        else
            return false;
    }

    private void startDiscovery() {

        startAdvertising();

        Intent intent = BluetoothAlertService.startService(getApplicationContext());
        if (Util.isForegroundCompatible(this)) {
            ContextCompat.startForegroundService(this, intent);
        } else {
            startService(intent);
        }

        if(bluetooth_for_first_time && AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)){

            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(
                    "Please Enable Autostart Permission to run app in background.",
                    "ALLOW",
                    "CANCEL",
                    new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            requestPremisionForAutoStart();
                        }

                        @Override
                        public void onNegativeInteraction() {
                            if(checkToExit()){
                                exitPopupFlow(false);
                            }
                            else{
                                finish();
                            }
                        }
                    }
            );

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "autostart_permission")
                    .commitAllowingStateLoss();

        }else{
            if(checkToExit()){
                exitPopupFlow(false);
            }
            else{
                finish();
            }
        }
    }

    private void requestPremisionForAutoStart(){

        try {
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
            Toast.makeText(BluetoothDeviceCheckActivity.this, "Please give autostart permission for Hungerbox app.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("AutoStartPermission", "Error occurred in Auto Start Permission Request : " + e.getMessage() );
        }
        if(checkToExit()){
            exitPopupFlow(false);
        }
        else{
            finish();
        }

    }

    void checkForBatteryOptimisation(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(this.getPackageName())) {

                try{
                    Intent viewIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    Toast.makeText(BluetoothDeviceCheckActivity.this, optimisationMessage, Toast.LENGTH_SHORT).show();
                    viewIntent.setData(Uri.parse("package:" + this.getPackageName()));
                    startActivityForResult(viewIntent, OPTIMISATION_REQUEST);
                }
                catch (ActivityNotFoundException e){
                    afterCheckingOptimisationSetting();
                }

            }
            else{
                afterCheckingOptimisationSetting();
            }
        }else{
            afterCheckingOptimisationSetting();
        }
    }

    void exitPopupFlow(Boolean shouldResetSwitch){
        if(shouldResetSwitch){
            SharedPrefUtil.putBoolean(ApplicationConstants.BLE_PROXIMITY_ENABLED, false);
        }
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    void afterCheckingOptimisationSetting(){
        if (!bluetoothAdapter.isEnabled()) {
            enableBluetooth();
        }
        else{
            managePermission();
        }
    }

    private void managePermission(){

        if(
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED))
        ){
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(AppUtils.getConfig(this).getLocation_permission_info(), "Ok", true, true, "Permission Request" ,new GenericPopUpFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    requestingLocationPermission();
                }

                @Override
                public void onNegativeInteraction() {

                }
            });

            genericPopUpFragment.setCancelable(false);
            genericPopUpFragment.show(this.getSupportFragmentManager(), "location_permission");
        }
        else{
            requestingLocationPermission();
        }
    }

    private void requestingLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permission = new String[]{ Manifest.permission.BLUETOOTH,

                    Manifest.permission.BLUETOOTH_ADMIN,

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        }else{
            permission = new String[]{ Manifest.permission.BLUETOOTH,

                    Manifest.permission.BLUETOOTH_ADMIN,

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_COARSE_LOCATION};
        }


        ActivityCompat.requestPermissions(this,

                permission,

                PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i =0; i<permissions.length; i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[i])){

                    ErrorPopFragment errorHandleDialog = ErrorPopFragment.Companion.newInstance("Provide Location Permission via Phone Setting", "OK", false, ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {

                            try{
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, PERMISSION_DENIED_REQUEST);
                            }catch (Exception exp){
                               exp.printStackTrace();
                                Toast.makeText(BluetoothDeviceCheckActivity.this, "Provide Location Permission via Phone Setting", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNegativeInteraction() {
                            finish();
                        }
                    });

                    errorHandleDialog.setCancelable(false);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(errorHandleDialog, "no_internet")
                            .commitAllowingStateLoss();
                    return;
                }else{
                    if(checkToExit()){
                        if (permissions[i].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Please allow Hungerbox App to access location all the time by clicking on 'Allow all the time' to enable Bluetooth Proxima.", "Ok", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    exitPopupFlow(true);
                                }

                                @Override
                                public void onNegativeInteraction() {

                                }
                            });

                            genericPopUpFragment.setCancelable(false);
                            genericPopUpFragment.show(this.getSupportFragmentManager(), "background_permission_popup");
                        }
                        else{
                            exitPopupFlow(true);
                        }
                    }
                    else{
                        managePermission();
                    }
                }
                return;
            }
        }
        askForGps();
    }

    private void askForGps() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(statusOfGPS){
            manageDiscoverable();
        }
        else{
            LocationRequest locationRequest;
            LocationSettingsRequest mLocationSettingsRequest;
            SettingsClient mSettingsClient;
            mSettingsClient = LocationServices.getSettingsClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            mLocationSettingsRequest = builder.build();
            builder.setAlwaysShow(true);

            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener(this, locationSettingsResponse -> {
                        manageDiscoverable();
                    })
                    .addOnFailureListener(this, e -> {
                        int statusCode = ((ApiException) e).getStatusCode();

                        switch (statusCode) {

                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {

                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(this, LOCATION_REQUEST);
                                } catch (IntentSender.SendIntentException sie) {

                                }
                                break;

                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(viewIntent, LOCATION_REQUEST);
                                break;
                        }
                    });
        }
    }


    private void startAdvertising() {
      try{
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
              bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
              advertiseCallback = new AdvertiseCallback() {
                  @Override
                  public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                      Log.d("GATT", "BLE advertisement added successfully");
                  }

                  @Override
                  public void onStartFailure(int errorCode) {
                      Log.e("GATT", "Failed to add BLE advertisement, reason: " + errorCode);
                  }
              };

              try {
                  bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
              } catch (Exception exp) {
                  exp.printStackTrace();
              }

          AdvertiseSettings settings = new AdvertiseSettings.Builder()
                  .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                  .setConnectable(true)
                  .setTimeout(0)
                  .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                  .build();

              AdvertiseData data = new AdvertiseData.Builder()
                      .setIncludeDeviceName(true)
                      .setIncludeTxPowerLevel(false)
                      .addServiceUuid(new ParcelUuid(BATTERY_SERVICE_UUID))
                      .build();

              if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                  bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
              }


              mBatteryLevelCharacteristic = new BluetoothGattCharacteristic(BATTERY_LEVEL_UUID, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);

              mBatteryLevelCharacteristic.addDescriptor(getClientCharacteristicConfigurationDescriptor());
              mBatteryLevelCharacteristic.addDescriptor(getCharacteristicUserDescriptionDescriptor(BATTERY_LEVEL_DESCRIPTION));
              mBatteryLevelCharacteristic.setValue(55, BluetoothGattCharacteristic.FORMAT_UINT8, 0);

              BluetoothGattService mBatteryService = new BluetoothGattService(BATTERY_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
              mBatteryService.addCharacteristic(mBatteryLevelCharacteristic);
              mBluetoothGattService = mBatteryService;

              mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
              mGattServer.addService(mBluetoothGattService);

//              AppUtils.showToast("Gatt Service started", true, 2);
          }
      }catch (Exception exp){
          exp.printStackTrace();
      }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothGattDescriptor getClientCharacteristicConfigurationDescriptor() {
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID,(BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE));
        descriptor.setValue(new byte[]{0, 0});
        return descriptor;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothGattDescriptor getCharacteristicUserDescriptionDescriptor(String defaultValue) {
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(CHARACTERISTIC_USER_DESCRIPTION_UUID,(BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE));
        try {
            descriptor.setValue(defaultValue.getBytes("UTF-8"));
        } finally {
            return descriptor;
        }
    }
}
