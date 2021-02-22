package com.hungerbox.customer.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NearbyDeviceActivity extends AppCompatActivity {

    RecyclerView rvNearbyDevice;
    private ImageView ivBack;
    Switch nearbySwitch;
    ImageView btShareFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_device);

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());


        NearbyDeviceAdapter deviceListAdapter = new NearbyDeviceAdapter(this, getDevicesList());
        rvNearbyDevice = findViewById(R.id.rv_devices);
        nearbySwitch = findViewById(R.id.nearby_switch);
        btShareFile = findViewById(R.id.bt_share);

        rvNearbyDevice.setLayoutManager(new LinearLayoutManager(this));
        rvNearbyDevice.setAdapter(deviceListAdapter);

        nearbySwitch.setChecked(SharedPrefUtil.getBoolean(Util.NEARBY_SWITCH,true));

        nearbySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                AppUtils.showToast("Nearby Device alert is Turned On.", true, 0);
                SharedPrefUtil.putBoolean(Util.NEARBY_SWITCH, true);
            }
            else{
                AppUtils.showToast("Nearby Device alert is Turned Off.", true, 0);
                SharedPrefUtil.putBoolean(Util.NEARBY_SWITCH, false);
            }
        });

        btShareFile.setOnClickListener(v -> {
            try{
                String filePath =  android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HB/device_data.csv";

                File file = new File(filePath);
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/csv");
                if(!file.exists()){
                    AppUtils.showToast("File does no exist"+filePath, true, 0);
                }
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(NearbyDeviceActivity.this, getApplicationContext().getPackageName() + ".provider",file));
                startActivity(Intent.createChooser(shareIntent, "Share file using"));
            }catch (Exception exp){
                exp.printStackTrace();
            }
        });

    }

    private ArrayList<HBDevice> getDevicesList() {

        ArrayList list = new ArrayList();
        Comparator<Map.Entry<String, HBDevice>> valueComparator = (o1, o2) -> {
            long v1 = o1.getValue().time;
            long v2 = o2.getValue().time;
            return (int) (v2 - v1);
        };

        HashMap<String, HBDevice> map = Util.getHashMap("map", this);

        if (map != null){
            List<Map.Entry<String, HBDevice>> listOfEntries = new ArrayList<Map.Entry<String, HBDevice>>(map.entrySet());
            Collections.sort(listOfEntries, valueComparator);
            LinkedHashMap<String, HBDevice> sortedByValue = new LinkedHashMap<String, HBDevice>(listOfEntries.size());
            for(Map.Entry<String, HBDevice> entry : listOfEntries){
                sortedByValue.put(entry.getKey(), entry.getValue());
            }
            Iterator mapIterator = sortedByValue.entrySet().iterator();

            while (mapIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry)mapIterator.next();
                HBDevice device = (HBDevice)mapElement.getValue();
                list.add(device);
            }
        }

        return list;
    }
}
