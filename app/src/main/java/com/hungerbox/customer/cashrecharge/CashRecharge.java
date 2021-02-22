package com.hungerbox.customer.cashrecharge;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.OnWalletUpdate;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.Recharge;
import com.hungerbox.customer.model.RechargeResponse;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

import device.itl.sspcoms.BarCodeReader;
import device.itl.sspcoms.DeviceEvent;
import device.itl.sspcoms.SSPDevice;
import device.itl.sspcoms.SSPDeviceType;
import device.itl.sspcoms.SSPSystem;
import device.itl.sspcoms.SSPUpdate;

public class CashRecharge extends AppCompatActivity {

    private static SSPDevice sspDevice;
    int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    int OPEN_SETTING = 2;
    private static ITLDeviceCom deviceCom;
    private static D2xxManager ftD2xx = null;
    private static FT_Device ftDev = null;
    private static CashRecharge instance = null;
    private View pb;
    private RelativeLayout initiateLayout, successRechargeLayout;
    private TextView currentBalance,currentBalance1, textLoading;
    
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_recharge);
        instance = this;

        pb = findViewById(R.id.pb);
        initiateLayout = findViewById(R.id.initiateLayout);
        textLoading = findViewById(R.id.text_loading);
        successRechargeLayout = findViewById(R.id.successRechargeLayout);
        currentBalance = findViewById(R.id.currentBalance);
        currentBalance1 = findViewById(R.id.currentBalance1);
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.doLogout(CashRecharge.this);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Toolbar toolbar = findViewById(R.id.tb_pay);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserDetailsFromServer();
    }


    public void getUserDetailsFromServer() {
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String url = UrlConstant.USER_DETAIL + "?occasionId=" + "&locationId=" + locationId;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        if (responseObject != null) {
                            MainApplication.bus.post(new OnWalletUpdate(responseObject.user));

                            if(successRechargeLayout.getVisibility() == View.VISIBLE){
                                currentBalance1.setText("Rs "+ String.format("%.2f", responseObject.user.getCurrentWalletBalance()));
                            }else{
                                currentBalance.setText("Rs "+ String.format("%.2f", responseObject.user.getCurrentWalletBalance()));
                                initialTask();
                            }
                        }else{
                            Toast.makeText(instance, "Current Balance Api Fail", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                            Toast.makeText(instance, "Please check your network connection.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(error != null && !error.equals("")){
                            Toast.makeText(instance, error, Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(instance, "Current Balance Api Fail", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }

    private void doRecharge(String amount) {

        String url = UrlConstant.RECHARGE_URL;

        SimpleHttpAgent<RechargeResponse> rechareHttAgent = new SimpleHttpAgent<RechargeResponse>(
                this,
                url,
                new ResponseListener<RechargeResponse>() {
                    @Override
                    public void response(RechargeResponse responseObject) {

                        deviceCom.SetEscrowAction(SSPSystem.BillAction.Accept);
                        instance.pb.setVisibility(View.GONE);
                        instance.successRechargeLayout.setVisibility(View.VISIBLE);

                        getUserDetailsFromServer();
                        LogoutTask.updateTime();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        deviceCom.SetEscrowAction(SSPSystem.BillAction.Reject);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                            Toast.makeText(instance, "Please check your network connection.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(error != null && !error.equals("")){
                            Toast.makeText(instance, error, Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(instance, "Unable To Recharge", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                RechargeResponse.class
        );

        rechareHttAgent.post(new Recharge().setAmount(amount), new HashMap<String, JsonSerializer>());
    }

    private void setUP(){

        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException ex) {
            Log.e("SSP FTManager", ex.toString());
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);


        UsbManager usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        if(usbManager.getDeviceList().size() == 0){
            closeDevice();
            Toast.makeText(instance, "Card Accepter Machine Not Connected.", Toast.LENGTH_SHORT).show();
        }else{
            boolean permissionGranted = false;
            for ( Map.Entry<String, UsbDevice> entry : usbManager.getDeviceList().entrySet()) {
                UsbDevice usbDevice = entry.getValue();
                if(usbManager.hasPermission(usbDevice)){
                    permissionGranted = true;
                }
            }

            if(!permissionGranted){
                closeDevice();
                Toast.makeText(instance, "Card Accepter Machine Not Connected.", Toast.LENGTH_SHORT).show();
            }
        }

        deviceCom = new ITLDeviceCom();

        instance.pb.setVisibility(View.VISIBLE);
        openDevice();
    }

    private void initialTask(){
        LogoutTask.updateTime();

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }else{
            setUP();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            setUP();
        }else{
            boolean foreverDenied = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        foreverDenied = true;
                    }
                }
            }

            final View snackBar = findViewById(R.id.crdl);
            final String content = "Open settings, allow Hungerbox all the permissions to start recharge.";
            if (foreverDenied) {

                FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                        content,
                        new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                startActivityForResult(intent, OPEN_SETTING);
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                                Snackbar.make(snackBar, content, Snackbar.LENGTH_INDEFINITE).setAction("ALLOW", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        startActivityForResult(intent, OPEN_SETTING);
                                    }
                                }).show();
                            }
                        }, "ALLOW", "CANCEL");
                orderErrorHandleDialog.setCancelable(false);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(orderErrorHandleDialog, "")
                        .commitAllowingStateLoss();

            } else {

                FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                        content,
                        new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                initialTask();
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                                Snackbar.make(snackBar, content, Snackbar.LENGTH_INDEFINITE).setAction("ALLOW", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        startActivityForResult(intent, OPEN_SETTING);
                                    }
                                }).show();
                            }
                        }, "RETRY", "CANCEL");

                orderErrorHandleDialog.setCancelable(false);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(orderErrorHandleDialog, "")
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING) {
            initialTask();
        }
    }

    private void openDevice() {


        if (ftDev != null) {
            if (ftDev.isOpen()) {
                // if open and run thread is stopped, start thread
                SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                return;
            }
        }

        int devCount = 0;

        if (ftD2xx != null) {
            // Get the connected USB FTDI devoces
            devCount = ftD2xx.createDeviceInfoList(this);
        } else {
            return;
        }

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);
        // none connected
        if (devCount <= 0) {
            return;
        }
        if (ftDev == null) {
            ftDev = ftD2xx.openByIndex(this, 0);
        } else {
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(this, 0);
            }
        }
        // run thread
        if (ftDev.isOpen()) {
            SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }

        if (ftDev != null) {
            pb.setVisibility(View.VISIBLE);
            deviceCom.setup(ftDev, 0, false, false, 0);
            deviceCom.SetEscrowMode(true);
            deviceCom.start();
        } else {
            final View snackBar = findViewById(R.id.crdl);
            Snackbar.make(snackBar, "No USB connection detected!", Snackbar.LENGTH_INDEFINITE).setAction("Connect?", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDevice();
                }
            }).show();
            Toast.makeText(CashRecharge.this, "No USB connection detected!", Toast.LENGTH_SHORT).show();
        }
    }


    private static void closeDevice() {

        instance.finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (!ftDev.isOpen()) {
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }

    @Override
    public void finish() {
        try{
            if (ftDev != null) {
                deviceCom.Stop();
                ftDev.close();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }


    public static CashRecharge getInstance(){

        return instance;
    }

    public static void DisplaySetUp(SSPDevice dev)
    {

        sspDevice = dev;
        
        instance.pb.setVisibility(View.GONE);

        // check for type comapable
        if(dev.type != SSPDeviceType.BillValidator){
            AlertDialog.Builder builder = new AlertDialog.Builder(CashRecharge.getInstance());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Connected device is not BNV (" + dev.type.toString() + ")")
                    .setTitle("BNV");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getInstance().finish();
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            // 4. Show the dialog
            dialog.show();// show error
            return;

        }
//
//        downloadFileSelect.setEnabled(true);

        /* device details  */
//        txtFirmware.append(" " + dev.firmwareVersion);
//        txtDevice.append(" " + dev.headerType.toString());
//        txtSerial.append(" " + String.valueOf(dev.serialNumber));
//        txtDataset.append(dev.datasetVersion);

        /* display the channel info */
//        channelValues.clear();
//        for (ItlCurrency itlCurrency : dev.currency) {
//            String v = itlCurrency.country + " " + String.format("%.2f", itlCurrency.realvalue);
//            channelValues.add(v);
//        }
//
//        adapterChannels.notifyDataSetChanged();


        // if device has barcode hardware
        if (dev.barCodeReader.hardWareConfig != SSPDevice.BarCodeStatus.None) {
            // send new configuration
            BarCodeReader cfg = new BarCodeReader();
            cfg.barcodeReadEnabled = true;
            cfg.billReadEnabled = true;
            cfg.numberOfCharacters = 18;
            cfg.format = SSPDevice.BarCodeFormat.Interleaved2of5;
            cfg.enabledConfig = SSPDevice.BarCodeStatus.Both;
            deviceCom.SetBarcocdeConfig(cfg);
        }
    }


    public static void DisplayEvents(final DeviceEvent ev) {

        
        String eventValues0, eventValues1;
        switch (ev.event) {
            case CommunicationsFailure:

                break;
            case Ready:
                LogoutTask.updateTime();
                instance.pb.setVisibility(View.GONE);
                instance.initiateLayout.setVisibility(View.VISIBLE);
                eventValues0 = "Ready";
                eventValues1 = "";
                break;

            case BillRead:
                LogoutTask.updateTime();
                instance.initiateLayout.setVisibility(View.GONE);
                instance.successRechargeLayout.setVisibility(View.GONE);
                instance.textLoading.setText("READING CASH");
                instance.pb.setVisibility(View.VISIBLE);
                eventValues0 = "Reading";
                eventValues1 = "";
                break;

            case BillEscrow:
                LogoutTask.updateTime();
                instance.doRecharge(ev.value+"");

                eventValues0 = "Bill Escrow";

                eventValues1 = ev.currency + " " +
                        String.format("%.2f", ev.value);
              
                break;
            case BillStacked:

                break;
            case BillReject:
//                instance.dorecharge.setText("doRecharge");
                Toast.makeText(instance, ev.event.name() +" "+ev.currency, Toast.LENGTH_SHORT).show();
                eventValues0 = "Bill Reject";
                eventValues1= "";
                break;
            case BillJammed:
                eventValues0 = "Bill jammed";
                eventValues1 = "";
                break;
            case BillFraud:
                eventValues0 = "Bill Fraud";
                eventValues1 = ev.currency + " " +
                        String.format("%.2f", ev.value);
                break;
            case BillCredit:
//                instance.dorecharge.setText("doRecharge");
                Toast.makeText(instance, ev.event.name() +" "+ev.value, Toast.LENGTH_SHORT).show();
                eventValues0 = "Bill Credit";
                eventValues1 = ev.currency + " " +
                        String.format("%.2f", ev.value);
                break;
            case Full:
                eventValues0 = "Bill Cashbox full";
                eventValues1 = "";
                break;
            case Initialising:

                break;
            case Disabled:
                eventValues0 = "Disabled";
                eventValues0 = "";
                break;
            case SoftwareError:
                eventValues0 = "Software error";
                eventValues1 = "";
                break;
            case AllDisabled:
                eventValues0 = "All channels disabled";
                eventValues1 = "";
                break;
            case CashboxRemoved:
                eventValues0 = "Cashbox removed";
                eventValues1 = "";
                break;
            case CashboxReplaced:
                eventValues0 = "Cashbox replaced";
                eventValues1 = "";
                break;
            case NotePathOpen:
                eventValues0 = "Note path open";
                eventValues1 = "";
                break;
            case BarCodeTicketEscrow:
                eventValues0 = "Barcode ticket escrow:";
                eventValues1 = ev.currency;
                break;
            case BarCodeTicketStacked:
                eventValues0 = "Barcode ticket stacked";
                eventValues1 = "";
                break;
        }
    }


    public static void DeviceDisconnected(SSPDevice dev) {

        Toast.makeText(instance, "disconnected", Toast.LENGTH_SHORT).show();
    }
    
    public static void UpdateFileDownload(SSPUpdate update){
        
    }
}
