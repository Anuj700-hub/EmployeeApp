package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import com.hungerbox.customer.bluetooth.Util;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.TimeHMS;
import com.hungerbox.customer.order.fragment.OrderDetailFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import org.json.JSONObject;

import java.util.Calendar;


public class OrderDetailActvity extends ParentActivity implements OrderDetailFragment.OnFragmentInteractionListener {


    public TextView tvTitle, orderCancellation;
    public RelativeLayout tvDone;
    public Handler handler;
    public Runnable runnable;
    public long activityStartTime = 0;
    protected Toolbar toolbar;
    public long orderId;
    OrderDetailFragment fragment;
    Order order;
    private String historyType;
    private boolean isNewOrder = false;
    private boolean isArchived = false;
    private boolean isRunning = false;
    private ImageView qr;
    private ImageView ivBack;
    private TextView tvQrCode;
    GenericPopUpFragment errorHandleDialog;
    private ConstraintLayout timerLayout;
    private TextView tvTimer,tvVisit;
    protected CountDownTimer countDownTimer;
    public boolean forFeedback = false;
    public long notificationTime = 0;
    public String orderRef="";
    public String notifTitle,notifBody;
    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_detail);

        activityStartTime = System.currentTimeMillis();
        toolbar = findViewById(R.id.tb_global);

        tvTitle = toolbar.findViewById(R.id.tv_order_detail_title);
        qr = findViewById(R.id.qr);
        tvQrCode = findViewById(R.id.text);
        orderCancellation = toolbar.findViewById(R.id.iv_cancel);
        ivBack = toolbar.findViewById(R.id.iv_back);
        tvDone = findViewById(R.id.tv_done);
        timerLayout = findViewById(R.id.rv_visit_timer);
        tvTimer = findViewById(R.id.tv_timer);
        tvVisit = findViewById(R.id.tv_visit);
        tvDone.setVisibility(View.INVISIBLE);
        orderId = getIntent().getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        orderRef = getIntent().getStringExtra("order_ref");
        notificationTime = getIntent().getLongExtra("notification-time", 0L);
        forFeedback = getIntent().getBooleanExtra(ApplicationConstants.FOR_FEEDBACK, false);
        isNewOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        isArchived = getIntent().getBooleanExtra(ApplicationConstants.IS_ORDER_ARCHIVED, false);
        historyType = getIntent().getStringExtra("historyType");
        notifTitle = getIntent().getStringExtra("title");
        notifBody = getIntent().getStringExtra("body");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
        if (AppUtils.getConfig(this).isHide_qrcode(order.getLocation())){
            tvQrCode.setText("COLLECT ORDER");
            qr.setVisibility(View.GONE);
        }
        else {
            tvQrCode.setText("View QR Code");
            qr.setVisibility(View.VISIBLE);
        }

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQrClickListener();
            }
        });
        LogoutTask.updateTime();
        createBaseContainer();
    }

    private void viewQrClickListener(){
        if (order != null && isRunning) {
            if (AppUtils.isSocialDistancingActive(order.getLocation()) && order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
                showPopUp();
            } else if (AppUtils.getConfig(OrderDetailActvity.this).isHide_qrcode(order.getLocation()) &&
                    (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED))) {
                showPopUp();
            } else {
                try {
                    String source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                    if (source == null) {
                        source = "NA";
                    }
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                    HBMixpanel.getInstance().addEvent(OrderDetailActvity.this, EventUtil.MixpanelEvent.VIEW_QR_CODE, jo);

                    Bundle bundle = new Bundle();
                    bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                    EventUtil.FbEventLog(OrderDetailActvity.this, EventUtil.MixpanelEvent.VIEW_QR_CODE, bundle);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                Intent intent = new Intent(OrderDetailActvity.this, OrderQrActivity.class);
                intent.putExtra("order", order);
                ActivityOptionsCompat options;
                if (AppUtils.isSocialDistancingActive(order.getLocation())){
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(OrderDetailActvity.this, tvQrCode, "qr_code");
                } else {
                    options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(OrderDetailActvity.this, qr, "qr_code");
                }

                if (Build.VERSION.SDK_INT > 24) {
                    if (AppUtils.getConfig(OrderDetailActvity.this).isHide_qrcode(order.getLocation())) {
                        startActivity(intent);
                    } else {
                        startActivity(intent, options.toBundle());
                    }
                } else {
                    startActivity(intent);
                }
            }
        }
    }

    private void showPopUp(){
         errorHandleDialog = GenericPopUpFragment.newInstance("You will be able to collect the order \n only when the order is ready", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
               errorHandleDialog.dismiss();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        errorHandleDialog.setCancelable(false);
        errorHandleDialog.show(getSupportFragmentManager(), "order processed error");
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isRunning = false;
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack() {
        if (isNewOrder) {
            if (AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout()) {
                //AppUtils.showToast("order place logout",true,1);
                AppUtils.doLogout(this);
            } else {
                Intent intent = AppUtils.getHomeNavigationIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        finish();
    }

    private void logoutAfterTimeout() {
        if (isNewOrder &&
            AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.doLogout(OrderDetailActvity.this);

                        }
                    });

                }
            }, 10000);
        }

    }




    protected void createBaseContainer() {
        if (fragment == null) {
            fragment = OrderDetailFragment.newInstance(OrderDetailActvity.this, orderId, orderCancellation, isArchived);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.fl_container, fragment, "order_detail")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.fl_container, fragment, "order_detail")
                        .commit();
            }
        }

        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Order Details");
        logoutAfterTimeout();
    }

    public void updateTitle(String title) {
        tvTitle.setText(title);
    }

    public void setOrder(Order order) {
        this.order = order;
        tvDone.setVisibility(View.VISIBLE);
        if (AppUtils.getConfig(this).isHide_qrcode(order.getLocation())) {

            if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)){
                tvDone.setBackground(getResources().getDrawable(R.drawable.button_curved_border_primary));
            }
            else {
                tvDone.setBackground(getResources().getDrawable(R.drawable.button_curved_border_light));
            }

        }
        else {
            tvDone.setBackground(getResources().getDrawable(R.drawable.button_curved_border_primary));
        }
        adjustSocialDistancingVisibility(order);
    }

    @Override
    public void onFragmentInteraction(int visibility) {
//            tvDone.setVisibility(visibility);
    }

    public void showQr() {
        tvDone.performClick();
    }


    public void hideQrButton() {
        tvDone.setVisibility(View.GONE);
    }

    public void showQrButton() {
        tvDone.setVisibility(View.VISIBLE);
    }

    public void setQRText(Order order){
        if (order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)){
            tvQrCode.setText("View QR Code");
        } else{
            tvQrCode.setText("Visit Cafe");
        }
    }

    public void adjustSocialDistancingVisibility(Order order){
        if (AppUtils.isSocialDistancingActive(order.getLocation())){
            qr.setVisibility(View.GONE);
            setQRText(order);
            timerLayout.setOnClickListener((v)->{
                viewQrClickListener();
            });
            tvVisit.setOnClickListener((v)->{
                viewQrClickListener();
            });
            if (order!=null){
                if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
                    timeRemainingView();
                } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.CANCELLED)||order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)
                ||order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)
                        || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)|| order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)){
                    timerLayout.setVisibility(View.GONE);
                } else{
                    timeCompleteView();
                }
            }
        } else{
            qr.setVisibility(View.VISIBLE);
            tvQrCode.setText("View QR Code");
            timerLayout.setVisibility(View.GONE);
        }
    }

    public void timeRemainingView(){
        timerLayout.setVisibility(View.VISIBLE);
        tvVisit.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);
        timerLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccentLight)));
        if (order!=null && order.getPreOrderDeliveryTime()!=0) {
            startTimer(order.getPreOrderDeliveryTime());
        }
    }
    public void timeCompleteView(){
        tvVisit.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.GONE);
        timerLayout.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
    }

    public void startTimer(long time){
        Calendar now = DateTimeUtil.adjustCalender(MainApplication.appContext);
        Calendar estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);
        if (now.compareTo(estTime) < 0) {
            countDownTimer = new CountDownTimer(estTime.getTimeInMillis()-now.getTimeInMillis(), 1000) {

                public void onTick(long millisUntilFinished) {

                    tvTimer.setText(DateTimeUtil.getTimeLeftCustom(time).toString());
                }

                public void onFinish() {

                    if (fragment!=null){
                        fragment.getOrderDetail();
                    }
                    timeCompleteView();
                }
            }.start();
        }
    }

    public void endTimer(){
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        endTimer();
    }

    @Override
    public void finish() {
        try {
            handler.removeCallbacks(runnable);
            endTimer();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.finish();
    }
}
