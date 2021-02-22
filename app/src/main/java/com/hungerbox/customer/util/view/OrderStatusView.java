package com.hungerbox.customer.util.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.order.activity.OrderQrActivity;
import com.hungerbox.customer.util.OrderUtil;

public class OrderStatusView extends LinearLayout {


    private final View currentOrderQrImage;
    private final RelativeLayout rlOrderStatusView;
    private RelativeLayout rlQRLayout;
    private Order currentOrder;
    private View currentOrderView;
    private View currentOrderLine1;
    private View currentOrderLine2;
    private TextView currentOrderText1, currentOrderText2, currentOrderText3;
    private AppCompatImageView currentOrderOnlyDot1, currentOrderOnlyDot2, currentOrderOnlyDot3, currentOrderOnlyTick1, currentOrderOnlyTick2, currentOrderOnlyTick3;
    private boolean openQR = false;

    public OrderStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.order_status_view, this, true);
        rlOrderStatusView = findViewById(R.id.rl_order_status_view);
        rlQRLayout = findViewById(R.id.rl_qr);
        currentOrderView = findViewById(R.id.currentOrder);
        currentOrderLine1 = findViewById(R.id.currentOrderLine1);
        currentOrderLine2 = findViewById(R.id.currentOrderLine2);
        currentOrderText1 = findViewById(R.id.currentOrderText1);
        currentOrderText2 = findViewById(R.id.currentOrderText2);
        currentOrderText3 = findViewById(R.id.currentOrderText3);
        currentOrderOnlyDot1 = findViewById(R.id.currentOrderOnlyDot1);
        currentOrderOnlyDot2 = findViewById(R.id.currentOrderOnlyDot2);
        currentOrderOnlyDot3 = findViewById(R.id.currentOrderOnlyDot3);
        currentOrderOnlyTick1 = findViewById(R.id.currentOrderOnlyTick1);
        currentOrderOnlyTick2 = findViewById(R.id.currentOrderOnlyTick2);
        currentOrderOnlyTick3 = findViewById(R.id.currentOrderOnlyTick3);
        currentOrderQrImage = findViewById(R.id.iv_order_detail_qr);

    }

    public void setOrder(Order order, final Activity activity,  boolean showQR){

        currentOrder = order;
        if(showQR)
        {
            rlQRLayout.setVisibility(VISIBLE);
            currentOrderQrImage.setVisibility(VISIBLE);
            currentOrderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (openQR) {
                        Intent intent = new Intent(activity, OrderQrActivity.class);
                        intent.putExtra("order", currentOrder);

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(activity, currentOrderQrImage, "qr_code");
                        if(Build.VERSION.SDK_INT > 24){
                            activity.startActivity(intent, options.toBundle());
                        }else{
                            activity.startActivity(intent);
                        }
                        openQR =true;
                    }
                }
            });
        }
        else{
            rlQRLayout.setVisibility(GONE);
            currentOrderQrImage.setVisibility(GONE);
            rlOrderStatusView.setLayoutParams(new
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 100f));
        }

        currentOrderOnlyDot1.clearAnimation();
        currentOrderOnlyDot2.clearAnimation();
        currentOrderOnlyDot3.clearAnimation();
        if (currentOrder.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW)|| currentOrder.getOrderStatus().equalsIgnoreCase(OrderUtil.AGENT_PICKED)) {
            currentOrderView.setVisibility(View.VISIBLE);
            //currentOrderText1.setTypeface(null, Typeface.BOLD);
            //currentOrderText2.setTypeface(null, Typeface.NORMAL);
            //currentOrderText3.setTypeface(null, Typeface.NORMAL);
           // currentOrderText1.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
//            currentOrderText2.setTextColor(ContextCompat.getColor(activity, R.color.background_grey));
//            currentOrderText3.setTextColor(ContextCompat.getColor(activity, R.color.background_grey));
            currentOrderOnlyDot1.setColorFilter(ContextCompat.getColor(activity, R.color.green), PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot2.setColorFilter(ContextCompat.getColor(activity, R.color.background_grey), android.graphics.PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot3.setColorFilter(ContextCompat.getColor(activity, R.color.background_grey), android.graphics.PorterDuff.Mode.SRC_IN);
            ViewCompat.setBackgroundTintList(currentOrderLine1, ColorStateList.valueOf(getResources().getColor(R.color.background_grey)));
            ViewCompat.setBackgroundTintList(currentOrderLine2, ColorStateList.valueOf(getResources().getColor(R.color.background_grey)));
            currentOrderOnlyTick1.setVisibility(View.VISIBLE);
            currentOrderOnlyTick2.setVisibility(View.GONE);
            currentOrderOnlyTick3.setVisibility(View.GONE);
            Animation animation = new AlphaAnimation(1, 0.5f);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            //currentOrderOnlyDot1.startAnimation(animation);
        } else if (currentOrder.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)) {
            currentOrderView.setVisibility(View.VISIBLE);
//            currentOrderText1.setTypeface(null, Typeface.BOLD);
//            currentOrderText2.setTypeface(null, Typeface.BOLD);
//            currentOrderText3.setTypeface(null, Typeface.NORMAL);
//            currentOrderText1.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
//            currentOrderText2.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
//            currentOrderText3.setTextColor(ContextCompat.getColor(activity, R.color.background_grey));
            currentOrderOnlyDot1.setColorFilter(ContextCompat.getColor(activity, R.color.green), PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot2.setColorFilter(ContextCompat.getColor(activity, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot3.setColorFilter(ContextCompat.getColor(activity, R.color.background_grey), android.graphics.PorterDuff.Mode.SRC_IN);
            ViewCompat.setBackgroundTintList(currentOrderLine1, ColorStateList.valueOf(getResources().getColor(R.color.green)));
            ViewCompat.setBackgroundTintList(currentOrderLine2, ColorStateList.valueOf(getResources().getColor(R.color.background_grey)));
            currentOrderOnlyTick1.setVisibility(View.VISIBLE);
            currentOrderOnlyTick2.setVisibility(View.VISIBLE);
            currentOrderOnlyTick3.setVisibility(View.GONE);
            Animation animation = new AlphaAnimation(1, 0.5f);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
           // currentOrderOnlyDot2.startAnimation(animation);
        } else if (currentOrder.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {
            currentOrderView.setVisibility(View.VISIBLE);
//            currentOrderText1.setTypeface(null, Typeface.BOLD);
//            currentOrderText2.setTypeface(null, Typeface.BOLD);
//            currentOrderText3.setTypeface(null, Typeface.BOLD);
//            currentOrderText1.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
//            currentOrderText2.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
//            currentOrderText3.setTextColor(ContextCompat.getColor(activity, R.color.text_dark));
            currentOrderOnlyDot1.setColorFilter(ContextCompat.getColor(activity, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot2.setColorFilter(ContextCompat.getColor(activity, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            currentOrderOnlyDot3.setColorFilter(ContextCompat.getColor(activity, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            ViewCompat.setBackgroundTintList(currentOrderLine1, ColorStateList.valueOf(getResources().getColor(R.color.green)));
            ViewCompat.setBackgroundTintList(currentOrderLine2, ColorStateList.valueOf(getResources().getColor(R.color.green)));
            currentOrderOnlyTick1.setVisibility(View.VISIBLE);
            currentOrderOnlyTick2.setVisibility(View.VISIBLE);
            currentOrderOnlyTick3.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(1, 0.5f);
            animation.setDuration(500);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            //currentOrderOnlyDot3.startAnimation(animation);
        }else{
            currentOrderView.setVisibility(View.GONE);
        }

    }
}
