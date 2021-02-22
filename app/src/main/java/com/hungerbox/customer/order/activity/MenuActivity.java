package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.fragment.MenuFragment;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;
import com.takusemba.spotlight.Spotlight;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by peeyush on 23/6/16.
 */
public class MenuActivity extends ParentActivity {

    public TextView tvCart, tvExtraCharges, tvCartAmount;
    public RelativeLayout flFloatingContainer;
    long vendorId;
    String vendorName;
    long ocasionId;
    String location;
    Fragment fragment;
    private Toolbar toolbar;
    private TextView tvTitle;
    private AppCompatButton btCheckout;
    MainApplication mainApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_menu);
        restoreFromSavedInstance(savedInstanceState);
        toolbar = findViewById(R.id.tb_global);

        setApiTag(String.valueOf(System.currentTimeMillis()));

        setSupportActionBar(toolbar);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        flFloatingContainer = findViewById(R.id.fl_cart_container);
        tvCart = findViewById(R.id.tv_cart);
        tvExtraCharges = findViewById(R.id.tv_extra_charge_label);
        tvCartAmount = findViewById(R.id.tv_order_amount);
        btCheckout = findViewById(R.id.bt_checkout);
        mainApplication = (MainApplication) getApplication();

        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.logBaseEvent(MenuActivity.this, EventUtil.CHECKOUT_CLICK);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    HBMixpanel.getInstance().addEvent(MenuActivity.this, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);

                    Bundle bundle = new Bundle();
                    bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    EventUtil.FbEventLog(MenuActivity.this, EventUtil.MixpanelEvent.MENU_CHECKOUT, bundle);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                navigateToOrderReview();
            }
        });
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.FbEventLog(MenuActivity.this, EventUtil.MENU_CHECKOUT, EventUtil.SCREEN_MENU);
                EventUtil.logBaseEvent(MenuActivity.this, EventUtil.CHECKOUT_CLICK);
                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
                    HBMixpanel.getInstance().addEvent(MenuActivity.this, EventUtil.MixpanelEvent.MENU_CHECKOUT, jo);

                    HashMap map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(), vendorId);
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getView_cart_click(), map, MenuActivity.this);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                navigateToOrderReview();
            }
        });
        createBaseContainer();
    }

    protected void createBaseContainer() {
        Intent intent = getIntent();
        if (intent != null) {
            vendorId = intent.getLongExtra(ApplicationConstants.VENDOR_ID, 0);
            ocasionId = intent.getLongExtra(ApplicationConstants.OCASSION_ID, 0);
            vendorName = intent.getStringExtra(ApplicationConstants.VENDOR_NAME);
            location = intent.getStringExtra(ApplicationConstants.LOCATION);
        }
        if (location == null)
            location = "Bangalore";

        fragment = MenuFragment.newInstance(vendorId, vendorName, ocasionId, this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.rl_base_container, fragment, "menu")
                .commit();
    }


    private void navigateToOrderReview() {
        LogoutTask.updateTime();
        clearTrayText();
        Intent intent = new Intent(MenuActivity.this, BookmarkPaymentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
        intent.putExtra("anim", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventUtil.FbEventLog(MenuActivity.this, EventUtil.MENU_BACK, EventUtil.SCREEN_MENU);
        HBMixpanel.getInstance().addEvent(MenuActivity.this, EventUtil.MixpanelEvent.MENU_CLICK_BACK);
        clearTrayText();
        try {
            Spotlight.with(this).closeSpotlight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCart();
        MainApplication.bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);
    }

    public void setUpCart() {
        MainApplication mainApplication = (MainApplication) MenuActivity.this.getApplication();
        int cartQty = mainApplication.getTotalOrderCount();
        double cardAmount = mainApplication.getTotalOrderAmount();
        Vendor vendor = mainApplication.getCart().getVendor();
        if (vendor != null) {
            if (vendor.getDeliveryCharge() > 0
                    || vendor.getConatainerCharge() > 0
                    || vendor.getServiceTax() > 0
                    || vendor.getTax() > 0
                    || vendor.getCgst() > 0
                    || vendor.getSgst() > 0
                    || AppUtils.getConfig(mainApplication).isConvenience_charge_applicable()) {
                tvExtraCharges.setVisibility(View.VISIBLE);
            } else {
                tvExtraCharges.setVisibility(View.GONE);
            }
        }
        if (cartQty <= 0) {
            if (flFloatingContainer.getVisibility() != View.GONE) {

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.cart_close);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        flFloatingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                flFloatingContainer.startAnimation(animation);
            }
        } else {
            if (flFloatingContainer.getVisibility() != View.VISIBLE) {
                flFloatingContainer.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.cart_open);
                flFloatingContainer.startAnimation(animation);
            }
            if (cartQty > 1) {
                tvCart.setText(String.format("%d Items  ", cartQty));
            } else {
                tvCart.setText(String.format("%d Item ", cartQty));
            }
            tvCartAmount.setText(String.format("₹ %.2f", cardAmount));

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            tvCartAmount.startAnimation(animation);
            tvCart.startAnimation(animation);
        }
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        setUpCart();
    }

    @Subscribe
    public void onOrderClearEvent(OrderClear orderClear) {
        if (flFloatingContainer.getVisibility() != View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.cart_close);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    flFloatingContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            flFloatingContainer.startAnimation(animation);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
                System.out.println("result");
            }
        }
    }

    private void clearTrayText() {
        try {
            if (fragment != null)
                ((MenuFragment) fragment).etTrayNo.getText().clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}