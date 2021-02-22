package com.hungerbox.customer.navmenu.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.JusPayPaymentItemModel;
import com.hungerbox.customer.model.PaymentStatus;
import com.hungerbox.customer.model.PaymentStatusResposne;
import com.hungerbox.customer.model.Recharge;
import com.hungerbox.customer.model.RechargeResponse;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.payment.activity.JuspayWebview;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.juspay.ec.sdk.api.Environment;
import in.juspay.ec.sdk.api.PaymentInstrument;
import in.juspay.ec.sdk.checkout.MobileWebCheckout;

import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;

public class RechargeActivity extends ParentActivity {

    Button btRecharge, bt200, bt500, bt1000;
    EditText etAmount;
    View llOverlay;
    View pbRecharge;
    boolean ignore = false;
    boolean orderAfterRecharge;
    private TextView tvRechargeOverlay, currentBalance,tvRechargeHint;
    private ImageView ivBack;
    private View firstOverlay;
    private double amountValue;
    private boolean fromNavbar;
    private int JUSPAY_WEBVIEW_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        AppUtils.setupUI(findViewById(R.id.rechargeActivityParent), RechargeActivity.this);
        etAmount = findViewById(R.id.amountBox);
        firstOverlay = findViewById(R.id.firstOverlay);
        bt200 = findViewById(R.id.recharge_200);
        bt500 = findViewById(R.id.recharge_500);
        bt1000 = findViewById(R.id.recharge_1000);
        btRecharge = findViewById(R.id.buttonRecharge);
        pbRecharge = findViewById(R.id.pb_recharge);
        llOverlay = findViewById(R.id.ll_overlay);
        tvRechargeOverlay = findViewById(R.id.tv_recharge_overlay);
        ivBack = findViewById(R.id.iv_back);
        currentBalance = findViewById(R.id.currentBalance);
        tvRechargeHint = findViewById(R.id.tv_recharge_hint);


        try {
            String source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
            fromNavbar = getIntent().getBooleanExtra(CleverTapEvent.PropertiesNames.getSource(),false);
            if (source == null) {
                source = "NA";
            }
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
            HBMixpanel.getInstance().addEvent(RechargeActivity.this, EventUtil.MixpanelEvent.SCREEN_OPEN_RECHARGE, jo);
            Bundle bundle = new Bundle();
            bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
            EventUtil.FbEventLog(RechargeActivity.this, EventUtil.MixpanelEvent.SCREEN_OPEN_RECHARGE, bundle);
        } catch (Exception exp) {
            exp.printStackTrace();
        }


        bt200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.FbEventLog(RechargeActivity.this, EventUtil.RECHARGE_200, EventUtil.SCREEN_RECHARGE);

                try{
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    amount = amount+200;
                    etAmount.setText(amount+"");
                }catch (Exception exp){
                    etAmount.setText("200");
                }
            }
        });


        bt500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.FbEventLog(RechargeActivity.this, EventUtil.RECHARGE_500, EventUtil.SCREEN_RECHARGE);
                try{
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    amount = amount+500;
                    etAmount.setText(amount+"");
                }catch (Exception exp){
                    etAmount.setText("500");
                }            }
        });


        bt1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventUtil.FbEventLog(RechargeActivity.this, EventUtil.RECHARGE_1000, EventUtil.SCREEN_RECHARGE);
                try{
                    int amount = Integer.parseInt(etAmount.getText().toString());
                    amount = amount+1000;
                    etAmount.setText(amount+"");
                }catch (Exception exp){
                    etAmount.setText("1000");
                }
            }
        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String amountToPay = getIntent().getStringExtra(ApplicationConstants.AMOUNT_TO_PAY);
        orderAfterRecharge = getIntent().getBooleanExtra(ApplicationConstants.ORDER_AFTER_RECHARGE, false);
        tvRechargeHint.setText(String.format("* Minimum recharge amount is %d", AppUtils.getConfig(getApplicationContext()).getMinimumRechargeAmount()));
        if (amountToPay != null) {
            amountValue = Math.ceil(Double.parseDouble(amountToPay));
            if (amountValue < AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount())
                etAmount.setText(AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount() + "");
            else
                etAmount.setText(amountToPay);
        }

        EventUtil.FbEventLog(RechargeActivity.this, EventUtil.SCREEN_OPEN_RECHARGE, EventUtil.SCREEN_HOME);

        btRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doRecharge();
            }
        });

        pbRecharge.setVisibility(View.VISIBLE);
        getUserDetailsFromServer();
    }

    private void doRecharge() {

        AppUtils.hideKeyboard(this,etAmount);

        try {
            if (AppUtils.getConfig(getApplicationContext()).isAuto_logout()) {
                LogoutTask.updateTime();
                LogoutTask.getInstance(getApplicationContext()).stopTimer();
            }

            String enteredAmount = etAmount.getText().toString();
            if(enteredAmount == null || enteredAmount.equals("")){
                AppUtils.showToast("Please enter valid amount!!", true, 2);
                return;
            }else if(Integer.parseInt(enteredAmount) < amountValue){
                if(amountValue < AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount()){
                    AppUtils.showToast("Please enter minimum "+AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount(), true, 2);
                }else{
                    AppUtils.showToast("Please enter minimum "+amountValue, true, 2);
                }
                return;
            }else if(Integer.parseInt(enteredAmount) < AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount()){
                AppUtils.showToast("Please enter minimum "+AppUtils.getConfig(RechargeActivity.this).getMinimumRechargeAmount(), true, 2);
                return;
            }else if(Integer.parseInt(enteredAmount) > AppUtils.getConfig(RechargeActivity.this).getMax_recharge()){
                AppUtils.showToast("Please enter maximum "+AppUtils.getConfig(RechargeActivity.this).getMax_recharge(), true, 2);
                return;
            }
            else{
                double amountToRecharge = Double.parseDouble(etAmount.getText().toString());
                JSONObject mixPanelValue = new JSONObject();
                mixPanelValue.put(EventUtil.MixpanelEvent.RechargeClick.RECHARGE_AMOUNT, amountToRecharge);
                HBMixpanel.getInstance().addEvent(RechargeActivity.this, EventUtil.MixpanelEvent.RechargeClick.EVENT_NAME, mixPanelValue);
                Bundle bundle = new Bundle();
                bundle.putDouble(EventUtil.MixpanelEvent.RechargeClick.RECHARGE_AMOUNT, amountToRecharge);
                EventUtil.FbEventLog(RechargeActivity.this, EventUtil.BANNER_BTN_CLICK, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(AppUtils.getConfig(RechargeActivity.this).getMeal_card_not_allowed_msg() == null || AppUtils.getConfig(RechargeActivity.this).getMeal_card_not_allowed_msg().equals("")){
            performRecharge();
        }else{
            GenericPopUpFragment linkPopUpFragment = GenericPopUpFragment.newInstance(AppUtils.getConfig(RechargeActivity.this).getMeal_card_not_allowed_msg(), "OK", new GenericPopUpFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    performRecharge();
                }

                @Override
                public void onNegativeInteraction() {
                    btRecharge.setEnabled(true);
                    pbRecharge.setVisibility(View.GONE);
                }
            });
            linkPopUpFragment.setCancelable(false);
            linkPopUpFragment.show(getSupportFragmentManager(), "recharge");
        }
    }

    private void performRecharge() {
        String url = UrlConstant.RECHARGE_URL;
        String amount = etAmount.getText().toString();
        EventUtil.FbEventLog(RechargeActivity.this, EventUtil.RECHARGE_VALUE, EventUtil.SCREEN_RECHARGE);

        pbRecharge.setVisibility(View.VISIBLE);
        SimpleHttpAgent<RechargeResponse> rechareHttAgent = new SimpleHttpAgent<RechargeResponse>(
                this,
                url,
                new ResponseListener<RechargeResponse>() {
                    @Override
                    public void response(RechargeResponse responseObject) {
                        doPaymentForOrder(responseObject);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbRecharge.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            AppUtils.showToast("Please check your network.", false, 0);
                        } else {
                            AppUtils.showToast("recharge failed", false, 0);
                        }

                    }
                },
                RechargeResponse.class
        );

        rechareHttAgent.post(new Recharge().setAmount(amount), new HashMap<String, JsonSerializer>());
    }



    private void getUserDetailsFromServer() {

        String url = UrlConstant.USER_DETAIL;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        pbRecharge.setVisibility(View.GONE);
                        if (responseObject != null) {
                            if (responseObject.user.getCurrentWallets().canEmployeeRecharge()) {
                                if (AppUtils.getConfig(RechargeActivity.this).is_recharge_allowed()){
                                    llOverlay.setVisibility(View.GONE);
                                    firstOverlay.setVisibility(View.VISIBLE);
                                }
                                else {
                                    firstOverlay.setVisibility(View.GONE);
                                    llOverlay.setVisibility(View.VISIBLE);
                                    tvRechargeOverlay.setText("Recharge is currently unavailiable. Please visit a helpdesk at your cafeteria.");
                                }
                            } else {
                                firstOverlay.setVisibility(View.GONE);
                                llOverlay.setVisibility(View.VISIBLE);
                                tvRechargeOverlay.setText("Recharge is currently unavailiable. Please visit a helpdesk at your cafeteria.");
                            }
                        }
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                pbRecharge.setVisibility(View.GONE);
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    AppUtils.showToast(error, true, 0);
                }
            }
        },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }

    private void doPaymentForOrder(final RechargeResponse order) {
        if (order == null || order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            AppUtils.showToast("unable to process your transaction", false, 0);
            pbRecharge.setVisibility(View.GONE);
            return;
        }

        pbRecharge.setVisibility(View.GONE);

        LogoutTask.updateTime();
        LogoutTask.getInstance(RechargeActivity.this).stopTimer();


        if(order.isUseUrl() && order.getRedirectUrl() != null && !order.getRedirectUrl().equals("")){

            Intent juspayWebviewActivity = new Intent(RechargeActivity.this, JuspayWebview.class);
            juspayWebviewActivity.putExtra(ApplicationConstants.URL, order.getRedirectUrl());
            juspayWebviewActivity.putExtra(ApplicationConstants.ORDER_ID, order.getOrderId());
            startActivityForResult(juspayWebviewActivity, JUSPAY_WEBVIEW_REQUEST_CODE);

        }else{
            PaymentInstrument paymentInstruments[] = null;

            if (AppUtils.getConfig(this).getJuspay_payment_option() == null || AppUtils.getConfig(this).getJuspay_payment_option().size() == 0 ) {
                paymentInstruments = new PaymentInstrument[]{
                        PaymentInstrument.CARD,
                        PaymentInstrument.NB,
                };
            } else {

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
                    order.getOrderId(),
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
                                LogoutTask.updateTime();
                                LogoutTask.getInstance(RechargeActivity.this).startTimer();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkOrderStatus(order);
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
                            if (AppUtils.getConfig(getApplicationContext()).isAuto_logout()) {
                                LogoutTask.updateTime();
                                LogoutTask.getInstance(getApplicationContext()).startTimer();
                            }
                        }
                    }
            );
        }
    }

    private void checkOrderStatus(final RechargeResponse order) {
        pbRecharge.setVisibility(View.VISIBLE);
        String url = UrlConstant.PAYMENT_STATUS + order.getOrderId();

        pbRecharge.setVisibility(View.VISIBLE);
        SimpleHttpAgent<PaymentStatusResposne> simpleHttpAgent = new SimpleHttpAgent<PaymentStatusResposne>(
                this,
                url,
                new ResponseListener<PaymentStatusResposne>() {
                    @Override
                    public void response(final PaymentStatusResposne responseObject) {

                        pbRecharge.setVisibility(View.GONE);
                        navigateToSuccessFailView(responseObject.paymentStatus);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbRecharge.setVisibility(View.GONE);
                        checkOrderStatus(order);
                    }
                },
                PaymentStatusResposne.class
        );

        simpleHttpAgent.get();
    }

    private void checkOrderStatus(final String orderId) {
        pbRecharge.setVisibility(View.VISIBLE);
        String url = UrlConstant.PAYMENT_STATUS + orderId;

        pbRecharge.setVisibility(View.VISIBLE);
        SimpleHttpAgent<PaymentStatusResposne> simpleHttpAgent = new SimpleHttpAgent<PaymentStatusResposne>(
                this,
                url,
                new ResponseListener<PaymentStatusResposne>() {
                    @Override
                    public void response(final PaymentStatusResposne responseObject) {

                        pbRecharge.setVisibility(View.GONE);
                        navigateToSuccessFailView(responseObject.paymentStatus);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbRecharge.setVisibility(View.GONE);
                        checkOrderStatus(orderId);
                    }
                },
                PaymentStatusResposne.class
        );

        simpleHttpAgent.get();
    }

    private void navigateToSuccessFailView(PaymentStatus paymentStatus) {
        if (AppUtils.getConfig(getApplicationContext()).isAuto_logout()) {
            LogoutTask.getInstance(getApplicationContext()).startTimer();
        }

//        try {
//            if(paymentStatus.getStatus().equalsIgnoreCase(ApplicationConstants.PAYEMT_SUCCESS)) {
//                HashMap<String, Object> map = new HashMap<>();
//                map.put(CleverTapEvent.PropertiesNames.getSource(), fromNavbar ? "Nav" : "Payment");
//                map.put(CleverTapEvent.PropertiesNames.getAmount(), Integer.parseInt(paymentStatus.getAmount()));
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getWallet_recharge(), map, getApplicationContext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        Intent intent = new Intent(RechargeActivity.this, SuccesFailActivity.class);
        intent.putExtra(ApplicationConstants.PAYMENT_STATUS, paymentStatus);
        intent.putExtra(ApplicationConstants.ORDER_AFTER_RECHARGE, orderAfterRecharge);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JUSPAY_WEBVIEW_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                LogoutTask.updateTime();
                LogoutTask.getInstance(RechargeActivity.this).startTimer();
                checkOrderStatus(data.getStringExtra(ApplicationConstants.ORDER_ID));
            } else {
                AppUtils.showToast("aborted", false, 0);
                if (AppUtils.getConfig(getApplicationContext()).isAuto_logout()) {
                    LogoutTask.updateTime();
                    LogoutTask.getInstance(getApplicationContext()).startTimer();
                }
            }
        }
    }
}
