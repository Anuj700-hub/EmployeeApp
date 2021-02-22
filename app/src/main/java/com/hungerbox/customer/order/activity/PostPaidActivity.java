package com.hungerbox.customer.order.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.JusPayPaymentItemModel;
import com.hungerbox.customer.model.OfflineInitiatePayment;
import com.hungerbox.customer.model.PaymentStatus;
import com.hungerbox.customer.model.PaymentStatusResposne;
import com.hungerbox.customer.model.PostPaidOrder;
import com.hungerbox.customer.model.PostPaidResponse;
import com.hungerbox.customer.navmenu.activity.SuccesFailActivity;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.PostPaidOrderAdapter;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.OfflineDuesDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.juspay.ec.sdk.api.Environment;
import in.juspay.ec.sdk.api.PaymentInstrument;
import in.juspay.ec.sdk.checkout.MobileWebCheckout;
import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;

public class PostPaidActivity extends AppCompatActivity {

    TextView tvDueAmount,tvTotalAmount;
    ImageView ivCancel;
    RecyclerView rvOrders;
    Button btnClearDues;
    PostPaidOrderAdapter postPaidOrderAdapter;
    String orderId;
    boolean ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_paid);
        tvDueAmount = findViewById(R.id.tv_dueAmount);
        ivCancel = findViewById(R.id.iv_cancel);
        tvTotalAmount = findViewById(R.id.tv_totalAmount);
        rvOrders = findViewById(R.id.rv_orders);
        btnClearDues =findViewById(R.id.btn_clear_dues);

        PostPaidResponse postPaidResponse = (PostPaidResponse) getIntent().getSerializableExtra("PostPaidRespone");
        ArrayList<PostPaidOrder> postPaidOrders = postPaidResponse.getTransactions().getPostPaidOrders();

        //AppUtils.HbLog("test","size: "+ postPaidOrders.size());

        postPaidOrderAdapter = new PostPaidOrderAdapter(postPaidOrders, new OfflineDuesDialog.ItemClick() {
            @Override
            public void onItemClick(@NotNull String orderID) {

            }
        });
        rvOrders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvOrders.setAdapter(postPaidOrderAdapter);

        tvDueAmount.setText("Your payment due is Rs "+ postPaidResponse.getAmount());
        tvTotalAmount.setText("Total Bill :  "+postPaidResponse.getAmount());
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnClearDues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOrderId();
                //performTransaction();
            }
        });


    }

    private void performTransaction() {
        PaymentInstrument paymentInstruments[] = null;

        if (AppUtils.getConfig(this).getJuspay_payment_option() == null || AppUtils.getConfig(this).getJuspay_payment_option().size() == 0 ) {
            paymentInstruments = new PaymentInstrument[]{
                    PaymentInstrument.CARD,
                    PaymentInstrument.NB,
            };
        } else {
            //todo juspay

            ArrayList<PaymentInstrument> paymentInstrumentsList = new ArrayList<>();
            for(JusPayPaymentItemModel juspay : AppUtils.getConfig(this).getJuspay_payment_option()){
                if(juspay.getKey().equals("NB")){
                    paymentInstrumentsList.add(PaymentInstrument.NB);
                }else if(juspay.getKey().equals("CARD")){
                    paymentInstrumentsList.add(PaymentInstrument.CARD);
                }else if(juspay.getKey().equals("WALLET")){
                    paymentInstrumentsList.add(PaymentInstrument.WALLET);
                }else if(juspay.getKey().equals("UPI")){
                    paymentInstrumentsList.add(PaymentInstrument.UPI);
                }else if(juspay.getKey().equals("SAVED_CARD")){
                    paymentInstrumentsList.add(PaymentInstrument.SAVED_CARD);
                }
            }

            paymentInstruments = paymentInstrumentsList.toArray(new PaymentInstrument[paymentInstrumentsList.size()]);
        }

        String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa") && targetEnv.equals("qa"))
            Environment.configure(Environment.SANDBOX, UrlConstant.JUSPAY_MERCHANT_ID);
        else
            Environment.configure(Environment.PRODUCTION, UrlConstant.JUSPAY_MERCHANT_ID);

        MobileWebCheckout checkout = new MobileWebCheckout(
                orderId,
                new String[]{
                        UrlConstant.PAYMENT_RESPONSE_REGEX
                },
                paymentInstruments,
                new HashMap<String, String>()
        );

        checkout.startPayment(this,
                new BrowserParams(),
                new BrowserCallback() {

                    @Override
                    public void endUrlReached(WebView webView, JSONObject jsonObject) {
                        if (!ignore) {
                            ignore = true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkOrderStatus(orderId);
                                }
                            });
                        }
                    }

                    @Override
                    public void onTransactionAborted(JSONObject jsonObject) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtils.showToast("aborted", false, 0);
                            }
                        });

                    }
                }
        );
    }

    private void checkOrderStatus(final String orderId) {
        String url = UrlConstant.PAYMENT_STATUS + orderId;

        SimpleHttpAgent<PaymentStatusResposne> simpleHttpAgent = new SimpleHttpAgent<PaymentStatusResposne>(
                this,
                url,
                new ResponseListener<PaymentStatusResposne>() {
                    @Override
                    public void response(final PaymentStatusResposne responseObject) {

                        navigateToSuccessFailView(responseObject.paymentStatus);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                        checkOrderStatus(orderId);
                    }
                },
                PaymentStatusResposne.class
        );

        simpleHttpAgent.get();
    }

    private void navigateToSuccessFailView(PaymentStatus paymentStatus) {

        Intent intent = new Intent(PostPaidActivity.this, SuccesFailActivity.class);
        intent.putExtra(ApplicationConstants.PAYMENT_STATUS, paymentStatus);
        startActivity(intent);
        finish();

    }

    private void getOrderId() {

        String url = UrlConstant.GET_ORDER_ID;
        SimpleHttpAgent<OfflineInitiatePayment> request = new SimpleHttpAgent<OfflineInitiatePayment>(
                this.getApplicationContext(),
                url,
                new ResponseListener<OfflineInitiatePayment>() {
                    @Override
                    public void response(OfflineInitiatePayment responseObject) {

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                OfflineInitiatePayment.class
        );

        request.post(new Object(), new HashMap<String, JsonSerializer>());

    }
}
