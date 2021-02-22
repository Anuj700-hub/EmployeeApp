package com.hungerbox.customer.order.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.order.listeners.MorePaymentOptionListener;
import com.hungerbox.customer.order.listeners.OnLoaderListener;
import com.hungerbox.customer.order.listeners.OnPaymentSelectLisntener;
import com.hungerbox.customer.order.listeners.ViewDetailBillListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;

public class PaymentActivity extends ParentActivity implements OnPaymentSelectLisntener,MorePaymentOptionListener , ViewDetailBillListener,OnLoaderListener {

    public Fragment fragment;
    private String fragmentTag = "payment";
    private boolean fromBookmark;
    private boolean fromNavBar;
    private ConstraintLayout constraintLayoutParent;
    private boolean fromBigBasket;
    private boolean fromReorder;
    private Order prevOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setApiTag(String.valueOf(System.currentTimeMillis()));

        fromBookmark = getIntent().getBooleanExtra(ApplicationConstants.FROM_BOOKMARK,false);
        fromBigBasket = getIntent().getBooleanExtra(ApplicationConstants.BIG_BASKET,false);
        fromNavBar = getIntent().getBooleanExtra(ApplicationConstants.FROM_NAVBAR,false);
        if (fromNavBar || fromBigBasket){
            setContentView(R.layout.payment_activity_new);
        } else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.payment_activity_half);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {//setting the the transparent part to black transparent and adding the notification bar again
                @Override
                public void run() {
                    ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.black_transparent));
                    findViewById(R.id.cl_parent).setBackground(colorDrawable);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
            }, 700);

            constraintLayoutParent = findViewById(R.id.cl_parent);
            constraintLayoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        createBaseContainer();
    }

    protected void createBaseContainer() {
        Order order = new Gson().fromJson((String) getIntent().getSerializableExtra("order"),Order.class);
        String orderPaymentType = getIntent().getStringExtra(ApplicationConstants.PAYMENT_ORDER_TYPE);
        User user = (User) getIntent().getSerializableExtra("user");
        double userExternalWalletCharge = getIntent().getDoubleExtra("userExternalWalletCharge", -1);
        double otherUserWallet = getIntent().getDoubleExtra("otherUserWallet", 0);
        double internalWalletPayableAmount = getIntent().getDoubleExtra("internalWalletPayableAmount", -1);
        long locationId = getIntent().getLongExtra(ApplicationConstants.LOCATION_ID, 2);

        prevOrder = (Order) getIntent().getSerializableExtra("prevOrder");
        fromReorder = getIntent().getBooleanExtra(ApplicationConstants.FROM_REORDER,false);
        Bundle bundle = new Bundle();
        if (fromReorder) {
            bundle.putSerializable("prevOrder",prevOrder);
            bundle.putBoolean("fromReorder",fromReorder);
        }
        if (fragment == null) {
            fragment = PaymentFragment.newInstance(order,orderPaymentType,user,
                    userExternalWalletCharge,internalWalletPayableAmount,
                    locationId,false,this,this, this,otherUserWallet, getIntent().getBooleanExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, false), getIntent().getBooleanExtra(ApplicationConstants.SPACE_BOOKING, false));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment.setArguments(bundle);
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, fragmentTag)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, fragmentTag)
                        .commit();
            }
        }
    }

//    @Override
//    public void onEnterAnimationComplete() {
//        super.onEnterAnimationComplete();
//        if (!fromBookmark){
//            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.black_transparent));
//            findViewById(R.id.cl_parent).setBackground(colorDrawable);
//            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!fromBookmark){
//            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.black_transparent));
//            findViewById(R.id.cl_parent).setBackground(colorDrawable);
//        }
//    }

    @Override
    public void onBackPressed() {

        final PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        paymentFragment.checkAndCloseActivty();
        super.onBackPressed();

    }

    @Override
    public void handlePaymentMethodSelected(PaymentMethod paymentMethod, boolean selected) {
        final PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        paymentFragment.handlePaymentMethodSelected(paymentMethod, selected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void handleMoreOptionSelected() {

    }

    @Override
    public void onProgressLoad(boolean state) {

    }

    @Override
    public void viewDetailBillSelected() {

    }
}
