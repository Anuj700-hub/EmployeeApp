package com.hungerbox.customer.offline.activityOffline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.offline.fragmentOffline.OrderDetailFragmentOffline;
import com.hungerbox.customer.order.activity.OrderQrActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;

import org.json.JSONObject;


public class OrderDetailActvityOffline extends ParentActivity implements OrderDetailFragmentOffline.OnFragmentInteractionListenerOffline{


    public TextView tvTitle, orderCancellation;
    public RelativeLayout tvDone;
    public Handler handler;
    public Runnable runnable;
    public long activityStartTime = 0;
    protected Toolbar toolbar;
    long orderId;
    OrderDetailFragmentOffline fragment;
    Order order;
    private String historyType;
    private boolean isNewOrder = false;
    private boolean isRunning = false;
    private ImageView qr;
    private TextView tvOrderItemsCost;
    private ImageView ivBack;

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_detail_offline);

        isRunning = true;
        activityStartTime = System.currentTimeMillis();
        toolbar = findViewById(R.id.tb_global);

        tvTitle = toolbar.findViewById(R.id.tv_order_detail_title);
        qr = findViewById(R.id.qr);
        tvOrderItemsCost = toolbar.findViewById(R.id.tv_order_details_items_cost);
        orderCancellation = toolbar.findViewById(R.id.iv_cancel);
        ivBack = toolbar.findViewById(R.id.iv_back);
        tvDone = findViewById(R.id.tv_done);
        tvDone.setVisibility(View.INVISIBLE);
        orderId = getIntent().getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        isNewOrder = getIntent().getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        historyType = getIntent().getStringExtra("historyType");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order != null && isRunning) {
                    try {
                        String source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                        if (source == null) {
                            source = "NA";
                        }
                        JSONObject jo = new JSONObject();
                        jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        HBMixpanel.getInstance().addEvent(OrderDetailActvityOffline.this, EventUtil.MixpanelEvent.VIEW_QR_CODE, jo);

                        Bundle bundle = new Bundle();
                        bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                        EventUtil.FbEventLog(OrderDetailActvityOffline.this, EventUtil.MixpanelEvent.VIEW_QR_CODE, bundle);
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }

                    Intent intent = new Intent(OrderDetailActvityOffline.this, OrderQrActivity.class);
                    intent.putExtra("fromOffline", true);
                    intent.putExtra("order", order);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(OrderDetailActvityOffline.this, qr, "qr_code");

                    if(Build.VERSION.SDK_INT > 24){
                        startActivity(intent, options.toBundle());
                    }else{
                        startActivity(intent);
                    }
                }
            }
        });
        LogoutTask.updateTime();
        createBaseContainer();
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
                AppUtils.doLogout(this);
            } else {
                Intent intent = AppUtils.getHomeNavigationIntentOffline(this);
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
                            AppUtils.doLogout(OrderDetailActvityOffline.this);

                        }
                    });

                }
            }, 10000);
        }

    }




    protected void createBaseContainer() {
        if (fragment == null) {
            fragment = OrderDetailFragmentOffline.newInstance(OrderDetailActvityOffline.this, orderId, orderCancellation);
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
        tvTitle.setText("Order Detail");
        logoutAfterTimeout();
    }

    public void setOrder(Order order) {
        this.order = order;
        tvDone.setVisibility(View.VISIBLE);
        tvTitle.setText("Order #"+order.getOrderId());
        tvOrderItemsCost.setText("Items "+order.getProducts().size()+" | \u20B9"+String.format("%.2f", order.getPrice()));
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

    @Override
    public void onFragmentInteraction(int visibility) {

    }
}
