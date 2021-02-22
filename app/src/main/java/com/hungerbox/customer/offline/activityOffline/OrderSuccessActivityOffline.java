package com.hungerbox.customer.offline.activityOffline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;

import com.hungerbox.customer.offline.modelOffline.OrderOffline;

import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONObject;

public class OrderSuccessActivityOffline extends AppCompatActivity {

    long bookingId;
    boolean isNewOrder;
    boolean isPreOrder;
    String orderType;
    private TextView tvMessage;
    private Button btDone;
    private ImageView ivFive;
    OrderOffline orderOffline;
    int cartQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        tvMessage = findViewById(R.id.tv_message);
        ivFive = findViewById(R.id.iv_high);

        try {
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.STATUS, "Success");
            HBMixpanel.getInstance().addEvent(OrderSuccessActivityOffline.this, EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME, jo);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        bookingId = getIntent().getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        isNewOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        orderType = getIntent().getStringExtra(ApplicationConstants.PAYMENT_ORDER_TYPE);
        isPreOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, false);
        orderOffline = (OrderOffline) getIntent().getSerializableExtra(ApplicationConstants.ORDER);
        cartQty = getIntent().getIntExtra("cartQty",0);
        Animation sgAnimation = AnimationUtils.loadAnimation(OrderSuccessActivityOffline.this, R.anim.high_five);


        sgAnimation.setRepeatMode(2);
        sgAnimation.setRepeatCount(3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivFive.setAnimation(sgAnimation);
        } else if (!AppUtils.getConfig(getApplicationContext()).isStop_animation_below_lollipop()) {
            ivFive.setAnimation(sgAnimation);
        }

        String msg = getIntent().getStringExtra("message");
        if (msg == null || msg.length() == 0)
            msg = "High Five! \nYour Booking was a Success.";
        tvMessage.setText(msg);
        btDone = findViewById(R.id.bt_done);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppUtils.getHomeNavigationIntentOffline(OrderSuccessActivityOffline.this);
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

                try {
                    Config.Rating rating = AppUtils.getConfig(OrderSuccessActivityOffline.this).getRating();
                    int deliveryCount = SharedPrefUtil.getInt(ApplicationConstants.DELIVERED_COUNT, 0);

                    navigate();
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
        }, 1000L);
    }

    public void navigate() {

        Intent intent = null;
        if (isPreOrder) {
            intent = AppUtils.getHomeNavigationIntentOffline(OrderSuccessActivityOffline.this);
            intent.putExtra(ApplicationConstants.ORDER,orderOffline);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(OrderSuccessActivityOffline.this, OrderQrCodeActivityOffline.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Payment Success");
            intent.putExtra(ApplicationConstants.BOOKING_ID, bookingId);
            intent.putExtra("cartQty",cartQty);
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.ORDER,orderOffline);
        }

        startActivity(intent);
        finish();
    }
}


