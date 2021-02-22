package com.hungerbox.customer.order.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OrderCancellationEligibilityData;
import com.hungerbox.customer.model.OrderCancellationEligibilityResponse;
import com.hungerbox.customer.model.OrderCancellationResponse;
import com.hungerbox.customer.model.OrderListCancellationEligibilityResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderCancellationChatActivity extends AppCompatActivity {

    private Long userRef;
    private LinearLayout llHasOpenOrders, llYesNoOptions, llOrderOptions, llChatParent, llChatbox;
    private TextView tvItem, tvVendor, tvPrice, tvOrderId, yesNoAnswer, tvCancelConfirmation, tvAskForReason, tvOrderOptionDesc, tvHeader, tvReasonMsg;
    private EditText etChatbox;
    private Button btYesOption, btNoOption, btSend;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancellation_chat);

        tvHeader = findViewById(R.id.chat_header);
        tvHeader.setText(getIntent().getStringExtra("header"));
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(view -> finish());

        llChatParent = findViewById(R.id.chat_parent);
        tvItem = findViewById(R.id.msg_item);
        tvVendor = findViewById(R.id.msg_vendor);
        tvPrice = findViewById(R.id.msg_price);
        tvOrderId = findViewById(R.id.msg_order_id);
        etChatbox = findViewById(R.id.et_chatbox);
        llHasOpenOrders = findViewById(R.id.has_open_orders);
        llYesNoOptions = findViewById(R.id.ll_yes_no_input_options);
        btYesOption = findViewById(R.id.bt_yes);
        btNoOption = findViewById(R.id.bt_no);
        yesNoAnswer = findViewById(R.id.yes_no_option_answer);
        tvCancelConfirmation = findViewById(R.id.order_cancellation_confirm_msg);
        tvAskForReason = findViewById(R.id.ask_for_reason);
        btSend = findViewById(R.id.bt_send);
        llOrderOptions = findViewById(R.id.ll_order_options);
        tvOrderOptionDesc = findViewById(R.id.order_option_desc);
        llChatbox = findViewById(R.id.ll_chatbox);
        tvReasonMsg = findViewById(R.id.reason_msg);

        userRef = SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0);

        SimpleHttpAgent<OrderListCancellationEligibilityResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                UrlConstant.ORDER_LIST_CANCELLATION_ELIGIBILITY  + "?user_id=" + userRef,
                responseObject -> {
                    if(responseObject != null){

                        if(responseObject.hasAnyOrderToCancel()){

                            OrderCancellationEligibilityData recentOrder = responseObject.getRecentOrder();

                            tvItem.setText("Items : " + recentOrder.getOrderItems());
                            tvVendor.setText("Vendor : " + recentOrder.getVendorName());
                            tvPrice.setText("Price : " + recentOrder.getValue());
                            tvOrderId.setText("Order ID :" + recentOrder.getOrderRef());

                            llHasOpenOrders.setVisibility(View.VISIBLE);
                            etChatbox.setHint("Choose from below...");

                            btYesOption.setOnClickListener(v -> yesOptionOnClick(recentOrder));
                            btNoOption.setOnClickListener(v -> noOptionOnClick(responseObject));

                            llYesNoOptions.setVisibility(View.VISIBLE);
                        }
                        else{
                            showErrorInChat("Sorry, You don't have any open orders.");
                        }
                    }
                    else{
                        showErrorInChat("Oops! something went wrong. Please try again");
                    }
                },
                (errorCode, error, errorResponse) -> {

                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        showErrorInChat( "Sorry, there is some connectivity issue, Please try again");
                    }
                    else if (errorResponse != null && errorResponse.message != null) {
                        showErrorInChat(errorResponse.message);
                    }
                    else {
                        showErrorInChat("Oops! something went wrong. Please try again");
                    }
                },
                OrderListCancellationEligibilityResponse.class
        );

        objectSimpleHttpAgent.get();
    }

    private void showErrorInChat(String errorMessage){
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View chatError = inflater.inflate(R.layout.chat_error, null);
        TextView error = chatError.findViewById(R.id.chat_error);
        error.setText(errorMessage);
        llChatParent.addView(chatError);

        llYesNoOptions.setVisibility(View.GONE);
        llOrderOptions.setVisibility(View.GONE);
        llChatbox.setVisibility(View.GONE);
    }

    private void yesOptionOnClick(OrderCancellationEligibilityData order){

        llYesNoOptions.setVisibility(View.GONE);
        yesNoAnswer.setText("Yes");
        yesNoAnswer.setVisibility(View.VISIBLE);

        startOrderCancellation(order);
    }

    private void noOptionOnClick(OrderListCancellationEligibilityResponse orderListResponse){

        llYesNoOptions.setVisibility(View.GONE);
        yesNoAnswer.setText("No, Select another order!");
        yesNoAnswer.setVisibility(View.VISIBLE);
        llOrderOptions.setVisibility(View.VISIBLE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 20, 20, 20);

        ArrayList<OrderCancellationEligibilityData> orderList = orderListResponse.getAllOrders();

        for(OrderCancellationEligibilityData order : orderList){

            View orderOption = inflater.inflate(R.layout.order_option_layout, null);

            TextView item = orderOption.findViewById(R.id.order_option_item);
            TextView vendor = orderOption.findViewById(R.id.order_option_vendor);
            TextView price = orderOption.findViewById(R.id.order_option_price);
            TextView ref = orderOption.findViewById(R.id.order_option_ref);
            TextView status = orderOption.findViewById(R.id.order_option_status);

            item.setText("Items : " + order.getOrderItems());
            vendor.setText("Vendor : " + order.getVendorName());
            price.setText("Price : " + order.getValue());
            ref.setText("OrderId : " + order.getOrderRef());
            status.setText("Status : " + order.getStatus());

            orderOption.findViewById(R.id.cv_order_option).setOnClickListener(v -> {
                startOrderCancellation(order);
            });

            llOrderOptions.addView(orderOption, layoutParams);
        }

    }

    private void startOrderCancellation(OrderCancellationEligibilityData order){

        if(llOrderOptions.getVisibility() == View.VISIBLE){

            llOrderOptions.setVisibility(View.GONE);
            tvOrderOptionDesc.setText("Items : " + order.getOrderItems() + " Vendor : " + order.getVendorName());
            tvOrderOptionDesc.setVisibility(View.VISIBLE);

        }

        callOrderCancellationEligibility(order.getOrderRef(), new OrderCancellationEligibilityListener() {
            @Override
            public void onResponseReceived(OrderCancellationEligibilityResponse responseObject) {
                if(responseObject != null){
                    if(responseObject.isDirectCancellable()){

                        callOrderCancellation(order.getOrderRef(), "", new OrderCancellationListener() {
                            @Override
                            public void onResponseReceived(OrderCancellationResponse responseObj) {
                                if(responseObj != null){

                                    tvCancelConfirmation.setText(responseObj.getMessage());
                                    tvCancelConfirmation.setVisibility(View.VISIBLE);

                                }
                                else{
                                    showErrorInChat("Oops! something went wrong. Please try again");
                                }
                            }

                            @Override
                            public void onErrorOccurred(int errorCode, String error, ErrorResponse errorResponse) {

                                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                                    showErrorInChat( "Sorry, there is some connectivity issue, Please try again");
                                }
                                else if (errorResponse != null && errorResponse.message != null) {
                                    showErrorInChat(errorResponse.message);
                                }
                                else {
                                    showErrorInChat("Oops! something went wrong. Please try again");
                                }
                            }
                        });

                    }
                    else if(responseObject.askForReason()){

                        tvAskForReason.setVisibility(View.VISIBLE);
                        llChatbox.setVisibility(View.VISIBLE);
                        etChatbox.setHint("Type here...");

                        btSend.setOnClickListener(v -> sendButtonOnclick(order.getOrderRef()));
                    }
                    else{
                        showErrorInChat(responseObject.getCancellationMessage());
                    }
                }
                else{
                    showErrorInChat("Oops! something went wrong. Please try again");
                }
            }

            @Override
            public void onErrorOccurred(int errorCode, String error, ErrorResponse errorResponse) {

                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    showErrorInChat( "Sorry, there is some connectivity issue, Please try again");
                }
                else if (errorResponse != null && errorResponse.message != null) {
                    showErrorInChat(errorResponse.message);
                }
                else {
                    showErrorInChat("Oops! something went wrong. Please try again");
                }
            }
        });
    }

    private void sendButtonOnclick(String orderRef){

        String reason = etChatbox.getText().toString().trim();

        if(!reason.isEmpty()){

            llChatbox.setVisibility(View.GONE);
            tvReasonMsg.setVisibility(View.VISIBLE);
            tvReasonMsg.setText(reason);

            callOrderCancellation(orderRef, reason, new OrderCancellationListener(){

                @Override
                public void onResponseReceived(OrderCancellationResponse response) {
                    if(response != null){

                        tvCancelConfirmation.setText(response.getMessage());
                        tvCancelConfirmation.setVisibility(View.VISIBLE);

                    }
                    else{
                        showErrorInChat("Oops! something went wrong. Please try again");
                    }
                }

                @Override
                public void onErrorOccurred(int errorCode, String error, ErrorResponse errorResponse) {

                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        showErrorInChat( "Sorry, there is some connectivity issue, Please try again");
                    }
                    else if (errorResponse != null && errorResponse.message != null) {
                        showErrorInChat(errorResponse.message);
                    }
                    else {
                        showErrorInChat("Oops! something went wrong. Please try again");
                    }
                }
            });
        }
    }

    private void callOrderCancellationEligibility(String orderRef, OrderCancellationEligibilityListener listener){
        String cancellationEligibilityUrl = UrlConstant.ORDER_CANCELLATION_ELIGIBILITY + "?orderRef=" + orderRef + "&user_id=" + userRef;

        SimpleHttpAgent<OrderCancellationEligibilityResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                cancellationEligibilityUrl,
                responseObject -> {
                    listener.onResponseReceived(responseObject);
                },
                (errorCode, error, errorResponse) -> {
                    listener.onErrorOccurred(errorCode, error, errorResponse);
                },
                OrderCancellationEligibilityResponse.class
        );

        objectSimpleHttpAgent.get();
    }

    private void callOrderCancellation(String orderRef, String reason, OrderCancellationListener listener){

        SimpleHttpAgent<OrderCancellationResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                UrlConstant.ORDER_CANCELLATION,
                responseObject -> {
                    listener.onResponseReceived(responseObject);
                },
                (errorCode, error, errorResponse) -> {
                    listener.onErrorOccurred(errorCode, error, errorResponse);
                },
                OrderCancellationResponse.class
        );

        JSONObject orderCancellationInfo = new JSONObject();

        try {

            orderCancellationInfo.put("orderRef", orderRef);
            orderCancellationInfo.put("userId", SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0));
            if(!reason.isEmpty())
                orderCancellationInfo.put("reason", reason);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        objectSimpleHttpAgent.post(orderCancellationInfo.toString());
    }

    private interface OrderCancellationEligibilityListener {
        void onResponseReceived(OrderCancellationEligibilityResponse response);
        void onErrorOccurred(int errorCode, String error, ErrorResponse errorResponse);
    }

    private interface OrderCancellationListener {
        void onResponseReceived(OrderCancellationResponse response);
        void onErrorOccurred(int errorCode, String error, ErrorResponse errorResponse);
    }
}
