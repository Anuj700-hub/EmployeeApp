package com.hungerbox.customer.order.activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.order.fragment.OrderQrFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;

public class OrderQrActivity extends ParentActivity {

    protected Fragment fragment;
    public String qrEntryText, qrExitText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setApiTag(String.valueOf(System.currentTimeMillis()));

        qrEntryText = getIntent().getStringExtra(ApplicationConstants.QR_ENTRY_TEXT);
        qrExitText = getIntent().getStringExtra(ApplicationConstants.QR_EXIT_TEXT);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
       try{
           if(!AppUtils.getConfig(getApplicationContext()).isQrScreenShotAllowed()) {
               getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
           }

           if(AppUtils.getConfig(getApplicationContext()).isAllow_screen_on()){
               getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
           }

           if(AppUtils.getConfig(getApplicationContext()).isAllow_screen_brightness()) {
               WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
               layoutParams.screenBrightness = 1.0f;
               getWindow().setAttributes(layoutParams);
           }

       }catch (Exception exp){
           exp.printStackTrace();
       }
        setContentView(R.layout.order_qr_activity);

        //to avoid fragment recreation
        if(savedInstanceState==null) {
            createBaseContainer();
        }else{
 /*          fragment can be restored like this if we have to use
           either we can also do one thing that,we can restore and call the createBaseContainer() method,as there is check for fragment null*/
            // fragment = (OrderQrFragment)getSupportFragmentManager().findFragmentByTag("qr");
        }
    }

    protected void createBaseContainer() {

        if (fragment == null) {

            final OrderQrFragment fragment = OrderQrFragment.newInstance((Order) (getIntent().getSerializableExtra("order")), getIntent().getStringExtra("for"), getIntent().getBooleanExtra("fromOffline", false),
                    new OrderQrFragment.OnOrderQrFragmentListener() {
                        @Override
                        public void onClose() {

                            onBackPressed();
                        }
                    });
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, "qr")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, "qr")
                        .commit();
            }
        }
    }
}
