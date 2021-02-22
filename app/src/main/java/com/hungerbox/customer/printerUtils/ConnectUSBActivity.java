package com.hungerbox.customer.printerUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.db.DbHandler;
import com.lvrenyang.io.USBPrinting;
import com.lvrenyang.io.base.IOCallBack;
import com.lvrenyang.io.Pos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.hungerbox.customer.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ConnectUSBActivity extends Activity implements IOCallBack {

    ConnectUSBActivity mActivity;
    public static int nPrintWidth = 384;

    ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    USBPrinting mUsb = new USBPrinting();
    private PendingIntent mPermissionIntent;
    Order order = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectusb);

        mActivity = this;
        mPos.Set(mUsb);
        mUsb.SetCallBack(this);

        LogoutTask.updateTime();
        LogoutTask.getInstance(this).stopTimer();
        Intent intent = getIntent();
        order = (Order) getIntent().getSerializableExtra("order");
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(permissionReceiver, filter);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                Toast.makeText(ConnectUSBActivity.this, "Printer- Attached", Toast.LENGTH_SHORT).show();
                probe();

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(ConnectUSBActivity.this, "Printer - DeAttached", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            probe();
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mUsbReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        es.submit(new TaskClose(mUsb));
        unregisterReceiver(permissionReceiver);
    }

    private void probe() {
        final UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        boolean printerFound = false;
        if (deviceList.size() > 0) {
            while (deviceIterator.hasNext()) {
                final UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 1659) {
                    printerFound = true;
                    if (!mUsbManager.hasPermission(device)) {
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    } else {
                        //Toast.makeText(mActivity, "Connecting...", Toast.LENGTH_SHORT).show();
                        es.submit(new TaskOpen(mUsb, mUsbManager, device, mActivity));
                    }
                }

            }
        }

        if(!printerFound){
            Toast.makeText(getApplicationContext(),"No Printer Found",Toast.LENGTH_LONG).show();
            setBackPrintResult(-100);
        }


    }

    public class TaskOpen implements Runnable {
        USBPrinting usb = null;
        UsbManager usbManager = null;
        UsbDevice usbDevice = null;
        Context context = null;

        public TaskOpen(USBPrinting usb, UsbManager usbManager, UsbDevice usbDevice, Context context) {
            this.usb = usb;
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
            this.context = context;
        }

        @Override
        public void run() {
            usb.Open(usbManager, usbDevice, context);
        }
    }

    public class TaskPrint implements Runnable {
        Pos pos = null;

        public TaskPrint(Pos pos) {
            this.pos = pos;
        }

        @Override
        public void run() {
            final int bPrintResult = Prints.PrintTicket(order, getApplicationContext(), pos, nPrintWidth, true, false, true, 1, 1, 0);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBackPrintResult(bPrintResult);
                    //Toast.makeText(mActivity.getApplicationContext(), (bPrintResult >= 0) ? "Success" + " " + Prints.ResultCodeToString(bPrintResult) : "Fails" + " " + Prints.ResultCodeToString(bPrintResult), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void setBackPrintResult(int printerResult){
        Intent intent = new Intent();
        intent.putExtra("printer_result",printerResult);
        setResult(RESULT_OK,intent);
        finish();
    }

    public class TaskClose implements Runnable {
        USBPrinting usb = null;

        public TaskClose(USBPrinting usb) {
            this.usb = usb;
        }

        @Override
        public void run() {
            usb.Close();
        }
    }

    @Override
    public void OnOpen() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show();
                es.submit(new TaskPrint(mPos));

            }
        });
    }

    @Override
    public void OnOpenFailed() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mActivity, "Printer Open Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                //probe(); //If Close due to printer shutdown. Then you need to re-enumerate it here。
            }
        });
    }

}