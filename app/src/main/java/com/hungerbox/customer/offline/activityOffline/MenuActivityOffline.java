package com.hungerbox.customer.offline.activityOffline;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.eventOffline.CartItemAddedEventOffline;
import com.hungerbox.customer.offline.fragmentOffline.FreeOrderErrorHandleDialogOffline;
import com.hungerbox.customer.offline.fragmentOffline.MenuFragmentOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.squareup.otto.Subscribe;

public class MenuActivityOffline extends AppCompatActivity {

    public TextView tvCart, tvCartAmount;
    protected RelativeLayout flFloatingContainer;
    long vendorId;
    String vendorName;
    long ocasionId;
    String location;
    Fragment fragment;
    private Toolbar toolbar;
    private TextView tvTitle;
    private Button btCheckout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_menu_offline);
        toolbar = findViewById(R.id.tb_global);

        setSupportActionBar(toolbar);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        flFloatingContainer = findViewById(R.id.fl_cart_container);
        tvCart = findViewById(R.id.tv_cart);
        tvCartAmount = findViewById(R.id.tv_order_amount);
        btCheckout = findViewById(R.id.bt_checkout);

        flFloatingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToOrderReview();
            }
        });
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigateToOrderReview();
            }
        });
        createBaseContainer();
    }

    VendorOffline vendor;

    protected void createBaseContainer() {
        Intent intent = getIntent();
        vendor = new VendorOffline();
        if (intent != null) {
            vendorId = intent.getLongExtra(ApplicationConstants.VENDOR_ID, 0);
            ocasionId = intent.getLongExtra(ApplicationConstants.OCASSION_ID, 0);
            vendorName = intent.getStringExtra(ApplicationConstants.VENDOR_NAME);
            location = intent.getStringExtra(ApplicationConstants.LOCATION);
            vendor = (VendorOffline) intent.getSerializableExtra("vendorObject");
        }
        if (location == null)
            location = "Bangalore";

        fragment = MenuFragmentOffline.newInstance(vendorId, vendorName, ocasionId, this, vendor);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.rl_base_container, fragment, "menu")
                .commit();
    }


    private void navigateToOrderReview() {
        LogoutTask.updateTime();
        Intent intent = new Intent(MenuActivityOffline.this, OrderReviewActivtyOffline.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Menu");
        intent.putExtra("anim", true);
        intent.putExtra("vendorObject", vendor);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    public void onBackPressed() {
        showBackPressedMessage();
    }

    private void showBackPressedMessage() {

        int cartQty = MainApplicationOffline.getTotalOrderCount(1);

        if (cartQty == 0) {
            finish();
        } else {

            FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                    "Your cart will get cleared",
                    new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            MainApplicationOffline.clearOrder(1);
                            MainApplicationOffline.bus.post(new OrderClear());
                            finish();
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    }, "OK", "CANCEL");

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(orderErrorHandleDialog, "cart_clear")
                    .commitAllowingStateLoss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpCart();
        MainApplicationOffline.bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplicationOffline.bus.unregister(this);
    }

    public void setUpCart() {
        int cartQty = MainApplicationOffline.getTotalOrderCount(1);
        double cardAmount = MainApplicationOffline.getTotalOrderAmount(1);
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
            if (cartQty >= 10) {
                tvCart.setText(String.format("%d Items  ", cartQty));
            } else {
                tvCart.setText(String.format("  %d Items ", cartQty));
            }
            tvCartAmount.setText(String.format("₹ %.2f", cardAmount));

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            tvCartAmount.startAnimation(animation);
            tvCart.startAnimation(animation);
        }
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEventOffline cartItemAddedEvent) {
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
}
