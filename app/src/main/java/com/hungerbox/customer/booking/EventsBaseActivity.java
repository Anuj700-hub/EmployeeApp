package com.hungerbox.customer.booking;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.OrderSuccessActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;


public class EventsBaseActivity extends GlobalActivity {


    boolean webCanGoBack = false;

    @Override
    protected void createBaseContainer() {

        String urlPart = getIntent().getStringExtra(ApplicationConstants.EVENT_URL);
        if (fragment == null) {
            fragment = BookingFragment.newInstance(urlPart);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            }
        }
        ivLogo.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.GONE);
        ivOcassions.setVisibility(View.INVISIBLE);
        spLocation.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Events");
    }


    public void toggleToolbar(final boolean active) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Your code to run in GUI thread here

                if (active) {
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    toolbar.setVisibility(View.GONE);
                }
                webCanGoBack = !active;
            }//public void run() {


        });


    }

    public void navigateToCheckoutView(final long vendorId, final long bookingId, final String type,
                                       final long locationId, final long productId, final int quantity,
                                       final double price, final String message) {


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                double totalPrice = (price * quantity);
                if (totalPrice == 0)
                    intent = new Intent(EventsBaseActivity.this, OrderSuccessActivity.class);
                else {
                    intent = new Intent(EventsBaseActivity.this, PaymentActivity.class);
                    Order order = new Order();
                    order.setVendorId(vendorId);
                    order.setLocationId(locationId);
                    order.setPrice(price * quantity);
                    order.setBookingId(bookingId);
                    order.setOrderType(type);
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setQuantity(quantity);
                    orderProduct.setId(productId);
                    orderProduct.setPrice(price);
                    order.getProducts().add(orderProduct);
                    intent.putExtra("order", order);
                    intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Events");

                }
                intent.putExtra("message", message);
                intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.PAYMENT_ORDER_TYPE_EVENT);
                goBackinwebView();
                startActivity(intent);
            }


        });

    }


    public void goBackinwebView() {
        if (fragment != null && fragment instanceof BookingFragment) {
            BookingFragment bookingFragment = (BookingFragment) fragment;
            bookingFragment.goBackInWebView();
        }
        toolbar.setVisibility(View.VISIBLE);
        webCanGoBack = false;
    }

    @Override
    public void onBackPressed() {

        if (webCanGoBack) {
            goBackinwebView();
        } else {
            finish();
        }
//        if (!(this instanceof MenuActivity)) {
//            showBackPressedMessage();
//        } else {
//            finish();
//        }
    }
}
