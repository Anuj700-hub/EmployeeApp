package com.hungerbox.customer.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.Model.ViolationResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

public class BluetoothViolationActivity extends AppCompatActivity {

    ImageView ivBack;
    RecyclerView rvViolations;
    SwitchCompat swNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_violation);
        swNotification = findViewById(R.id.sw_notification);
        ivBack = findViewById(R.id.iv_back);
        rvViolations = findViewById(R.id.rv_violations);

        ivBack.setOnClickListener(v -> finish());
        swNotification.setChecked(SharedPrefUtil.getBoolean(Util.NOTIFICATION_SWITCH, true));
        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    SharedPrefUtil.putBoolean(Util.NOTIFICATION_SWITCH, true);
                } else {
                    SharedPrefUtil.putBoolean(Util.NOTIFICATION_SWITCH, false);
                }
            }
        });

        getViolationDataFromServer();
    }

    private void getViolationDataFromServer() {

        SimpleHttpAgent<ViolationResponse> violationResponse = new SimpleHttpAgent<ViolationResponse>(
                getApplicationContext(),
                UrlConstant.VIOLATION_LOG,
                new ResponseListener<ViolationResponse>() {
                    @Override
                    public void response(ViolationResponse responseObject) {
                        if (responseObject != null && responseObject.getViolationByDays().size() > 0) {
                            setViolationDataUI(responseObject);
                        } else {
                            AppUtils.showToast("No data found!", true, 0);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.showToast(error, true, 0);
                    }
                },
                ViolationResponse.class
        );
        violationResponse.get();

    }

    private void setViolationDataUI(ViolationResponse responseObject) {
        //Set data here
        BluetoothViolationAdapter bluetoothViolationAdapter = new BluetoothViolationAdapter(this, responseObject.getViolationByDays());
        rvViolations.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvViolations.setAdapter(bluetoothViolationAdapter);
        bluetoothViolationAdapter.notifyDataSetChanged();

    }
}
