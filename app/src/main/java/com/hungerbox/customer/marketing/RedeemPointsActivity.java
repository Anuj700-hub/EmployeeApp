package com.hungerbox.customer.marketing;

/**
 * Created by manas on 5/4/18.
 */

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderPayment;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.RedeemMenuResponse;
import com.hungerbox.customer.model.RedeemProduct;
import com.hungerbox.customer.model.UserPoints;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.OrderSuccessActivity;
import com.hungerbox.customer.order.adapter.RedeemMenuListAdapter;
import com.hungerbox.customer.order.listeners.RedeemProductListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.ArrayList;
import java.util.HashMap;


public class RedeemPointsActivity extends ParentActivity {


    public UserPoints userPoints;
    private ImageView ivBack;
    private RecyclerView rvRedeemMenu;
    private RedeemMenuListAdapter menuListAdapter;
    private TextView tvPoints;
    private long occasionId = 0;
    private long locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_points);
        ivBack = findViewById(R.id.iv_back);
        rvRedeemMenu = findViewById(R.id.rv_redeem_menu);
        rvRedeemMenu.setLayoutManager(new LinearLayoutManager(RedeemPointsActivity.this));
        tvPoints = findViewById(R.id.tv_user_points);

        occasionId = MainApplication.selectedOcassionId;
        setClickListeners();
        updateUserPoints();
        updateRedeemMenu();

        LogoutTask.updateTime();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setClickListeners() {

    }

    private void updateUserPoints() {
        SimpleHttpAgent<UserPoints> userPointsHttpAgent = new SimpleHttpAgent<UserPoints>(RedeemPointsActivity.this,
                UrlConstant.GET_USER_POINTS, new ResponseListener<UserPoints>() {

            @Override
            public void response(UserPoints responseObject) {
                if (responseObject != null) {
                    tvPoints.setText("Hunger Points: " + ((int) responseObject.getPoints()));
                    userPoints = responseObject;
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        }, UserPoints.class);
        userPointsHttpAgent.get();
    }

    private void updateRedeemMenu() {
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
        String url = UrlConstant.GET_CONTEST_MENU + "?locationId=" + locationId + "&occasionId=" + occasionId;

        SimpleHttpAgent<RedeemMenuResponse> userPointsHttpAgent = new SimpleHttpAgent<RedeemMenuResponse>(RedeemPointsActivity.this,
                url, new ResponseListener<RedeemMenuResponse>() {
            @Override
            public void response(RedeemMenuResponse responseObject) {

                if (responseObject != null) {
                    if (menuListAdapter == null ||
                            rvRedeemMenu.getAdapter() == null) {
                        menuListAdapter = new RedeemMenuListAdapter(RedeemPointsActivity.this,
                                responseObject.getRedeemProductArrayList(), new RedeemProductListener() {
                            @Override
                            public void RedeemProduct(RedeemProduct product) {
                                if (product.getPrice() <= userPoints.getPoints())
                                    redeemProduct(product);
                                else {
                                    GenericPopUpFragment error = GenericPopUpFragment
                                            .newInstance("You do not have sufficient Hunger points to redeem the item. Play more to win.",
                                                    "OK", true,
                                                    new GenericPopUpFragment.OnFragmentInteractionListener() {
                                                        @Override
                                                        public void onPositiveInteraction() {

                                                        }

                                                        @Override
                                                        public void onNegativeInteraction() {

                                                        }
                                                    });
                                    error.show(getSupportFragmentManager(), "error");
                                }
//                                    Toast.makeText(RedeemPointsActivity.this, "You do not have sufficient points to Redeem this item", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (responseObject.getRedeemProductArrayList().size() == 0)
                            Toast.makeText(RedeemPointsActivity.this, "No Redeemable Products Available right now", Toast.LENGTH_SHORT).show();

                        rvRedeemMenu.setAdapter(menuListAdapter);
                    } else {
                        menuListAdapter.changeProducts(responseObject.getRedeemProductArrayList());
                        if (responseObject.getRedeemProductArrayList().size() == 0)
                            Toast.makeText(RedeemPointsActivity.this, "No Redeemable Products Available right now", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        }, RedeemMenuResponse.class);
        userPointsHttpAgent.get();
    }

    private void redeemProduct(RedeemProduct product) {
        EventUtil.FbEventLog(RedeemPointsActivity.this, EventUtil.REDEEM_PRODUCT_FINAL, product.getProduct());

        HBMixpanel.getInstance().addEvent(RedeemPointsActivity.this, EventUtil.MixpanelEvent.REDEEM_PRODUCT);
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(product.getProductId());
        orderProduct.setPrice(product.getPrice());
        orderProduct.setName(product.getProduct());
        orderProduct.setQuantity(1);
        ArrayList<OrderProduct> orderProductList = new ArrayList<>();
        orderProductList.add(orderProduct);

        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setWalletId(userPoints.getWalletId());
        orderPayment.setAmount(product.getPrice());
        Order tempOrder = new Order();
        tempOrder.setLocationId(locationId);
        tempOrder.setOccasionId(occasionId);
        tempOrder.setVendorId(product.getVendorId());
        tempOrder.setVendorName(product.getVendorName());
        tempOrder.setProducts(orderProductList);
        tempOrder.setInternalWalletUsed(false);
        tempOrder.setPaymentModes(orderPayment);

        final AlertDialog redeemAlert = new AlertDialog.Builder(RedeemPointsActivity.this)
                .setMessage("Placing your Order").setCancelable(false).setIcon(R.mipmap.ic_launcher).show();
        String url = UrlConstant.POST_ORDER_URL;
        SimpleHttpAgent<OrderResponse> orderSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        redeemAlert.dismiss();
                        if (responseObject != null) {
                            Intent intent = new Intent(RedeemPointsActivity.this, OrderSuccessActivity.class);
                            intent.putExtra(ApplicationConstants.BOOKING_ID, responseObject.getOrder().getId());
                            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
                            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
                            intent.putExtra("message", "High Five! \nYour Order has been placed.");
                            startActivity(intent);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        redeemAlert.dismiss();
                    }
                },
                OrderResponse.class
        );
        orderSimpleHttpAgent.post(tempOrder, new HashMap<String, JsonSerializer>());

    }
}

