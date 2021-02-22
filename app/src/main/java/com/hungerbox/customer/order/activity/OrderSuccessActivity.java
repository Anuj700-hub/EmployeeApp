package com.hungerbox.customer.order.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderSuccessActivity extends ParentActivity {

    long bookingId;
    boolean isNewOrder;
    boolean isPreOrder;
    boolean redirectSpaceBooking;
    String orderType;
    private TextView tvMessage;
    private Button btDone;
    private ImageView ivFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        tvMessage = findViewById(R.id.tv_message);
        ivFive = findViewById(R.id.iv_high);


        try {
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.STATUS, "Success");
            HBMixpanel.getInstance().addEvent(OrderSuccessActivity.this, EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME, jo);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        bookingId = getIntent().getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        isNewOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        orderType = getIntent().getStringExtra(ApplicationConstants.PAYMENT_ORDER_TYPE);
        isPreOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, false);
        redirectSpaceBooking = getIntent().getBooleanExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, false);

        Animation sgAnimation = AnimationUtils.loadAnimation(OrderSuccessActivity.this, R.anim.high_five);


        sgAnimation.setRepeatMode(2);
        sgAnimation.setRepeatCount(3);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ivFive.setAnimation(sgAnimation);
        }else if(!AppUtils.getConfig(getApplicationContext()).isStop_animation_below_lollipop()){
            ivFive.setAnimation(sgAnimation);
        }

        String msg = getIntent().getStringExtra("message");

        if(getIntent().getBooleanExtra(ApplicationConstants.FOR_SPACE_BOOKING, false)){
            tvMessage.setText(getIntent().getBooleanExtra(ApplicationConstants.IS_APPROVAL_PENDING, false)? "Booking Sent for Approval." : "Booking Successful.");
        }
        else{
            if (msg == null || msg.length() == 0)
                msg = "High Five! \nYour order has been placed.";
            tvMessage.setText(msg);
        }
        btDone = findViewById(R.id.bt_done);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppUtils.getHomeNavigationIntent(OrderSuccessActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        if (orderType != null && orderType.equalsIgnoreCase(ApplicationConstants.ORDER_TYPE_FOOD)) {
            navigateToOrderDetailAfterTime();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(orderType!=null && orderType.equalsIgnoreCase(ApplicationConstants.ORDER_TYPE_FOOD)){
//            navigateToOrderDetailAfterTime();
//        }
    }

    private void navigateToOrderDetailAfterTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)){
                    navigate();
                }else {

                    try {
                        Config.Rating rating = AppUtils.getConfig(OrderSuccessActivity.this).getRating();
                        int deliveryCount = SharedPrefUtil.getInt(ApplicationConstants.DELIVERED_COUNT, 0);
                        if (rating != null && new ArrayList<String>(Arrays.asList(rating.getStep().split("_"))).contains(deliveryCount + "")) {
                            AppUtils.showRatingDialog(OrderSuccessActivity.this, rating.getTitle(), rating.getDesc(), rating.feedback_title, rating.getFeedback_desc(), deliveryCount, OrderSuccessActivity.this);
                        } else {
                            navigate();
                        }

                    } catch (Exception exp) {
                        exp.printStackTrace();
                        navigate();
                    }

                    try {
                        int deliveryCount = SharedPrefUtil.getInt(ApplicationConstants.DELIVERED_COUNT, 0);
                        SharedPrefUtil.putInt(ApplicationConstants.DELIVERED_COUNT, deliveryCount + 1);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }
        }, 1000L);
    }

    public void navigate() {

        SharedPrefUtil.putBoolean(Util.IS_USER_EXIT,false);
        SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_ID, bookingId);

        Intent intent = null;

        if(SharedPrefUtil.getBoolean(ApplicationConstants.IS_GUEST_USER)){
            intent = new Intent(OrderSuccessActivity.this, GuestOrderSuccess.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Payment Success");
            intent.putExtra(ApplicationConstants.BOOKING_ID, bookingId);
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
        }else {
            intent = new Intent(OrderSuccessActivity.this, OrderDetailNewActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Payment Success");
            intent.putExtra(ApplicationConstants.BOOKING_ID, bookingId);
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            if(redirectSpaceBooking){
                intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, true);
            }
        }

        startActivity(intent);
        finish();
    }
}
