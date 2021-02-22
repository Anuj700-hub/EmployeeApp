package com.hungerbox.customer.offline.activityOffline;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.OrderSelectedTime;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.adapterOffline.OrderReviewAdapterOffline;
import com.hungerbox.customer.offline.fragmentOffline.FreeOrderErrorHandleDialogOffline;
import com.hungerbox.customer.offline.listenersOffline.OrderReviewProductRefreshListenerOffline;
import com.hungerbox.customer.offline.modelOffline.Cart;
import com.hungerbox.customer.offline.modelOffline.OcassionOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.OrderProductOffline;
import com.hungerbox.customer.offline.modelOffline.OrderResponseOffline;
import com.hungerbox.customer.offline.modelOffline.ServerStatus;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.offline.modelOffline.VendorProductOffline;
import com.hungerbox.customer.order.fragment.ExitFragment;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.hungerbox.customer.util.view.HBTextViewBold;
import com.hungerbox.customer.util.view.HbTextView;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class OrderReviewActivtyOffline extends AppCompatActivity implements OrderReviewProductRefreshListenerOffline {

    public static final int OTP_VERIFICATION_REQ_CODE = 987;
    RecyclerView rvOrderList;
    OrderReviewAdapterOffline orderReviewAdapter;

    Toolbar toolbar;
    TextView tvTotal, tvTitle,
            tvOrderTotal,
            tvDeliveryCharge, tvContainerCharge, tvVat, tvServiceTax, tvCgst, tvSgst, tvTotalHeader;
    LinearLayout llDeliveryContainer, llContainerContainer, llOrderDetailConatiner,
            llVat, llServiceTax, llCgst, llSgst;


    RelativeLayout rlClosedAmountContainer, rlPaymentAmountContainer;
    Button btPay;
    ProgressBar progressBar;
    long vendorStartTime;
    User user;
    String location, userName;
    long locationId;
    TimePickerDialog timePicker;
    Calendar orderSelectedTime;
    OrderOffline order;
    VendorOffline selectedOrderVendor;
    OcassionOffline orderOcassion;

    boolean isRunning = false;
    private ExitFragment exitFragment;
    private TextView preOrderTime;
    private Cart cart;
    private LinearLayout llConvenienceFee;

    private TextInputLayout txtInputLayout;

    private HBTextViewBold amountTotal;
    private HbTextView payTextDetails;
    private ImageView downArrow;
    private boolean isDetailShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review_offline);


        toolbar = findViewById(R.id.tb_order_review);

        txtInputLayout = findViewById(R.id.txtInputLayout);
        txtInputLayout.setHint(AppUtils.getConfig(getApplicationContext()).getWorkstation_address());
        preOrderTime = findViewById(R.id.preorderTime);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        TextView spLocation = findViewById(R.id.tv_location);
        spLocation.setVisibility(View.GONE);
        ImageView ivOccasion = findViewById(R.id.iv_ocassion);
        ImageView ivSearch = findViewById(R.id.iv_search);
        ivOccasion.setVisibility(View.GONE);
        ivSearch.setVisibility(View.GONE);
        rvOrderList = findViewById(R.id.rv_order_review);
        rvOrderList.setLayoutManager(new CustomNonScrollingLayoutManager(this));


        btPay = findViewById(R.id.bt_pay);
        btPay.setEnabled(false);


        Intent intent = getIntent();
        selectedOrderVendor = (VendorOffline) intent.getSerializableExtra("vendorObject");


        llDeliveryContainer = findViewById(R.id.ll_delivery_container);
        llOrderDetailConatiner = findViewById(R.id.ll_order_details_container);
        llContainerContainer = findViewById(R.id.ll_container_charge_container);
        rlClosedAmountContainer = findViewById(R.id.rl_closed_amount_container);
        rlPaymentAmountContainer = findViewById(R.id.rl_payment_amount_container);
        llVat = findViewById(R.id.ll_vat_container);
        llServiceTax = findViewById(R.id.ll_st_container);
        llConvenienceFee = findViewById(R.id.ll_convenience_container);
        llCgst = findViewById(R.id.ll_cgst_container);
        llSgst = findViewById(R.id.ll_sgst_container);
        tvOrderTotal = findViewById(R.id.tv_total_order);
        tvDeliveryCharge = findViewById(R.id.tv_delivery);
        tvContainerCharge = findViewById(R.id.tv_container);
        tvVat = findViewById(R.id.tv_vat);


        tvCgst = findViewById(R.id.tv_cgst);
        tvSgst = findViewById(R.id.tv_sgst);

        tvServiceTax = findViewById(R.id.tv_st);
        tvTotal = findViewById(R.id.tv_total);
        tvTotalHeader = findViewById(R.id.tv_amount_header_total);
        progressBar = findViewById(R.id.pb_order_review);

        amountTotal = findViewById(R.id.tv_amount);
        payTextDetails = findViewById(R.id.tv_pay_details);
        downArrow = findViewById(R.id.iv_arrow);

        isRunning = true;


        try {

            String source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
            if (source == null) {
                source = "NA";
            }
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        cart = MainApplicationOffline.getCart(1);
        userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);


        CreateOrderListTask createOrderListTask = new CreateOrderListTask();
        createOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        if (cart.getVendor() == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }


        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startOrderView(order);
                checkServerStatus(order);
            }
        });

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTitle.setText("Cart");

        rlClosedAmountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAmountHeader();
            }
        });

        LogoutTask.updateTime();
    }

    private void toggleAmountHeader() {
        rlClosedAmountContainer.setVisibility(View.GONE);
        rlPaymentAmountContainer.setVisibility(View.VISIBLE);
    }

    private void showOnlineDialog(){
        GenericPopUpFragment popUpFragment = GenericPopUpFragment
                .newInstance("We are back online", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        Intent intent = new Intent(OrderReviewActivtyOffline.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        popUpFragment.setCancelable(false);
        popUpFragment.show(getSupportFragmentManager(), "hungerbox_online");
    }

    private void checkServerStatus(final OrderOffline order) {

        final String url = UrlConstant.CHECK_SERVER_STATUS;
        SimpleHttpAgent<ServerStatus> simpleHttpAgent = new SimpleHttpAgent<ServerStatus>(this
                , url,
                new ResponseListener<ServerStatus>() {
                    @Override
                    public void response(ServerStatus responseObject) {
                        if (responseObject != null ) {
                            if(!responseObject.getStatus()){//online
                                showOnlineDialog();
                            }else{//offline
                                startOrderView(order);
                            }


                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        startOrderView(order);
                    }
                }, ServerStatus.class);

        simpleHttpAgent.getWithoutHeader();

    }

    private void startOrderView(OrderOffline order) {
        Intent intent = null;
        if (selectedOrderVendor != null)
            order.setVendor(selectedOrderVendor);

        order.setCreatedAt(Calendar.getInstance().getTimeInMillis() / 1000);



        int cartQty = MainApplicationOffline.getTotalOrderCount(1);

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            // perform operation for payment side
            //showPreOrderDialog(order);
            intent = new Intent(this, OrderSuccessActivityOffline.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, true);
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra(ApplicationConstants.ORDER, order);
            intent.putExtra("cartQty", cartQty);
            intent.putExtra("message", "High Five! \nYour Order has been placed.\nIt is in Pre-Order");
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            clearLocalOrder();
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(this, OrderSuccessActivityOffline.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.ORDER, order);
            intent.putExtra("cartQty", cartQty);
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            startActivity(intent);
            clearLocalOrder();
            if (selectedOrderVendor != null)
                finish();
        }


    }

    private void setupBottomBar() {

        btPay.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cart != null)
            cart.setLastUpdatedTime(new Date().getTime());
        isRunning = true;

        MainApplicationOffline.bus.register(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        MainApplicationOffline.bus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT > 24) {
            supportFinishAfterTransition();
        } else {
            finish();
        }

        try {
            if (getIntent().getBooleanExtra("anim", false)) {
                overridePendingTransition(R.anim.stay, R.anim.slide_out_up);
            } else {
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void getUserDetailsFromServer() {

        user = new User();
        user.setName("Rnajeet");
        if (order != null)
            updateOrderSummary(order);


    }

    private void clearLocalOrder() {
        MainApplicationOffline.clearOrder(1);
    }

    @Override
    public void refreshOrderList(OrderProductOffline orderProduct, int position, boolean shouldRefresh) {

        if (shouldRefresh) {
            if (orderProduct.getQuantity() == 0) {
                try {
                    order.getProducts().remove(position);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

                if (order.getProducts().size() <= 0) {
                    finish();
                }
            }
            if (orderReviewAdapter != null)
                orderReviewAdapter.notifyDataSetChanged();
        }
        updateOrderSummary(order);
    }

    private void setOcassion(OcassionOffline selectedOcasion) {
        this.orderOcassion = selectedOcasion;
        getUserDetailsFromServer();
    }


    private void setVendor(VendorOffline orderVendor) {

        Intent intent = getIntent();
        selectedOrderVendor = (VendorOffline) intent.getSerializableExtra("vendorObject");
        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }
        this.selectedOrderVendor = selectedOrderVendor.clone();
        order.setVendorName(selectedOrderVendor.getVendorName());
        order.setVendor(selectedOrderVendor);

        long currentTime = Calendar.getInstance().getTimeInMillis();

        vendorStartTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getStartTime(), OrderReviewActivtyOffline.this);
        long vendorEndTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getEndTime(), OrderReviewActivtyOffline.this);
        if (currentTime > vendorStartTime) {
            vendorStartTime = currentTime;
            handleOrderTimeSelected(new OrderSelectedTime(vendorStartTime, false));
        } else {

            final Calendar calendarVendorStartTime = Calendar.getInstance();
            calendarVendorStartTime.setTimeInMillis(vendorStartTime);

            final Calendar calendarVendorEndTime = Calendar.getInstance();
            calendarVendorEndTime.setTimeInMillis(vendorEndTime);

            View.OnClickListener positiveListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitFragment.dismiss();
                    initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
                }
            };

            View.OnClickListener negativeListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            };

            exitFragment = ExitFragment.newInstance(String.format(getString(R.string.preorder_message), orderOcassion.name), "Continue", "Cancel", positiveListener, negativeListener);
            exitFragment.setCancelable(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(exitFragment, "exit")
                    .commitAllowingStateLoss();
        }
    }

    private void handleOrderTimeSelected(OrderSelectedTime time) {
        if (time != null) {
            Calendar orderCalendarTime = Calendar.getInstance();
            orderCalendarTime.setTimeInMillis(time.getTime());
            orderCalendarTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            order.setOrderTimeMiliis(orderCalendarTime.getTimeInMillis());
            Calendar calendarTime = Calendar.getInstance();
            calendarTime.setTimeInMillis(time.getTime());
            orderSelectedTime = calendarTime;
        }
    }

    private void updatePaymentDetails(OrderOffline order) {

        order.setGroupOrder(false);
        if (order.users != null) {
            order.users.clear();
        }
        order.users = new ArrayList<Long>();


        switch (cart.getGuestType()) {
            case ApplicationConstants.GUEST_TYPE_OFFICIAL:
                order.calculateGuestOrderPrice(cart.getGuestType());
                break;
            case ApplicationConstants.GUEST_TYPE_PERSONAL:
            default:
                order.calculateOrderProductPrice();
        }

        llConvenienceFee.setVisibility(View.GONE);

        String payButtonString;


        payButtonString = "PROCEED TO GET ORDER REQUEST";
        btPay.setText(payButtonString);
        setupBottomBar();
    }


    private void updateOrderSummary(OrderOffline order) {

        if (order.getDeliveryCharge() <= 0) {
            llDeliveryContainer.setVisibility(View.GONE);
        } else {
            llDeliveryContainer.setVisibility(View.VISIBLE);
            tvDeliveryCharge.setText(String.format("₹ %.2f", order.getDeliveryCharge()));
        }

        double containerCharge = order.getContainerCharge();

        if (containerCharge <= 0) {
            llContainerContainer.setVisibility(View.GONE);
        } else {
            llContainerContainer.setVisibility(View.VISIBLE);
            tvContainerCharge.setText(String.format("₹ %.2f", containerCharge));
        }


        llVat.setVisibility(View.GONE);
        llServiceTax.setVisibility(View.GONE);
        order.setTax(0);
        order.setServiceTax(0);


        double cgst = this.order.getCgst(selectedOrderVendor);
        if (cgst <= 0) {
            llCgst.setVisibility(View.GONE);
        } else {
            llCgst.setVisibility(View.VISIBLE);
            tvCgst.setText("₹ " + cgst);
        }

        double sgst = this.order.getSgst(selectedOrderVendor);
        if (sgst <= 0) {
            llSgst.setVisibility(View.GONE);
        } else {
            llSgst.setVisibility(View.VISIBLE);
            tvSgst.setText(String.format("₹ " + sgst));
        }

        updatePaymentDetails(order);


        tvOrderTotal.setText(String.format("₹ %.2f", order.getPrice()));

        tvTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));
        tvTotalHeader.setText(String.format("₹ %.2f", order.getTotalPrice()));
        amountTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDetailShowing){
                    downArrow.setImageDrawable(getResources().getDrawable(R.drawable.help_open));
                    isDetailShowing = !isDetailShowing;
                    payTextDetails.setText(getResources().getString(R.string.offline_order_short_text));
                }
                else{
                    downArrow.setImageDrawable(getResources().getDrawable(R.drawable.help_close));
                    isDetailShowing = !isDetailShowing;
                    payTextDetails.setText(getResources().getString(R.string.offline_order_long_text));
                }
            }
        });

        if (order.getDeliveryCharge() <= 0 && order.getContainerCharge() <= 0
                && order.getServiceTax(selectedOrderVendor) <= 0
                && order.getVat(selectedOrderVendor) <= 0
                && order.getCgst(selectedOrderVendor) <= 0
                && order.getSgst(selectedOrderVendor) <= 0) {
            llOrderDetailConatiner.setVisibility(View.GONE);
        }

        llOrderDetailConatiner.setVisibility(View.GONE);
        rlClosedAmountContainer.setVisibility(View.GONE);
        rlPaymentAmountContainer.setVisibility(View.GONE);


    }


    private void showOrderErrorDialog(OrderOffline order, String error, boolean clearOrder) {
        if (!isRunning)
            return;
        FreeOrderErrorHandleDialogOffline freeOrderErrorHandleDialog = null;
        if (clearOrder) {
            clearLocalOrder();
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(order, error, null);
        } else {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(order, error, new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                @Override
                public void onPositiveButtonClicked() {

                    Intent intent = AppUtils.getHomeNavigationIntentOffline(OrderReviewActivtyOffline.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNegativeButtonClicked() {

                }
            }, "cancel");
        }
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "free_order_error");
    }

    private void handleResponse(OrderResponseOffline responseObject, OrderOffline order) {
        btPay.setEnabled(true);
        this.order = order;
    }

    private void updateUiWith(Cart cart, OrderOffline order) {
        this.order = order;
        if (orderReviewAdapter == null) {
            orderReviewAdapter = new OrderReviewAdapterOffline(this, order, cart, this);
            rvOrderList.setAdapter(orderReviewAdapter);
        } else {
            orderReviewAdapter.changeOrder(order, cart);
            orderReviewAdapter.notifyDataSetChanged();
        }
    }


    private void udateLocationInfo() {


        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }
        order.setLocationName(SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, ""));

    }

    private void updateLocationDensity() {
        Random rand = new Random();
        int randomNum = 1 + rand.nextInt((5 - 1) + 1);
    }

    private int getLocationCapacityCount(float locationCapacity) {
        double capacity = locationCapacity;
        capacity = (capacity > 100) ? 100 : capacity;
        double count = 5.0 * capacity / 100.0;
        return (int) Math.ceil(count);
    }

    public void createImageViews(int count, LinearLayout parentView, Context context) {
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    50, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            ImageHandling.loadLocalImage(this, imageView, R.drawable.density_active);
            parentView.addView(imageView);
        }
        for (int i = 0; i < (5 - count); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    50, ViewGroup.LayoutParams.MATCH_PARENT
            ));

            ImageHandling.loadLocalImage(this, imageView, R.drawable.density_inactive);
            parentView.addView(imageView);
        }
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        modifyCart();
    }

    private void modifyCart() {
        new CreateOrderListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Subscribe
    public void onRemoveProductFromCart(RemoveProductFromCart removeProductFromCart) {
    }


    private void initiateTimePicker(final Calendar calendarVendorStartTime, final Calendar calendarVendorEndTime) {

        timePicker = new TimePickerDialog(OrderReviewActivtyOffline.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarSelectTime = Calendar.getInstance();
                calendarSelectTime.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
                calendarSelectTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarSelectTime.set(Calendar.MINUTE, minute);
                calendarSelectTime.set(Calendar.SECOND, 0);
                calendarSelectTime.set(Calendar.MILLISECOND, 0);

                if (calendarSelectTime.getTimeInMillis() >= calendarVendorStartTime.getTimeInMillis() && calendarSelectTime.getTimeInMillis() <= calendarVendorEndTime.getTimeInMillis()) {

                    vendorStartTime = calendarSelectTime.getTimeInMillis();
                    preOrderTime.setVisibility(View.VISIBLE);
                    if (minute < 10) {
                        preOrderTime.setText(String.format(getString(R.string.selected_delivery_time), "" + hourOfDay, "0" + minute));
                    } else {
                        preOrderTime.setText(String.format(getString(R.string.selected_delivery_time), "" + hourOfDay, "" + minute));
                    }

                    preOrderTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
                        }
                    });
                    handleOrderTimeSelected(new OrderSelectedTime(vendorStartTime, false));
                } else {
                    AppUtils.showToast(String.format(getString(R.string.delivery_time_selection_error_msg), selectedOrderVendor.getStartTime(), selectedOrderVendor.getEndTime()), false, 2, true);

                    initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
                }
            }
        }, calendarVendorStartTime.get(Calendar.HOUR_OF_DAY), calendarVendorStartTime.get(Calendar.MINUTE), true);
        timePicker.setCancelable(true);
        timePicker.setCanceledOnTouchOutside(false);
        timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if (preOrderTime.getVisibility() == View.GONE) {
                    finish();
                }
            }
        });
        timePicker.show();
    }


    class CreateOrderListTask extends AsyncTask<Void, Integer, Void> {
        //        private HashMap<Long, HashMap<Long, Integer>> order;
//        private HashMap<Long, Product> productHashMap;
//        private HashMap<Long, Vendor>  vendorHashMap;
        ArrayList<VendorProductOffline> vendorProducts = new ArrayList<>();
        Cart cart;
        OrderOffline orderObject = new OrderOffline();
        VendorOffline orderVendor;
        OcassionOffline selectedOcasion;

        private boolean setCancel = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            order = mainApplication.getOrder();
//            productHashMap = mainApplication.getProductHashMap();
//            vendorHashMap = mainApplication.getVendorHashMap();
            cart = MainApplicationOffline.getCart(1);
            try {

                selectedOcasion = MainApplicationOffline.getOcassionForId(cart.getOccasionId(), 1);
                orderObject.setOccasionId(selectedOcasion.id);
                orderObject.setLocationId(locationId);
                orderVendor = cart.getVendor();
                orderObject.setDeliveryCharge(orderVendor.getDeliveryCharge());
                orderObject.setContainerCharge(orderVendor.getConatainerCharge());


            } catch (NullPointerException e) {
                // error popup
                setCancel = true;
                showOrderErrorDialog(null, "Something went wrong while creating your order.Please try ordering again", true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

//            for(long vendorId: order.keySet()){
//                orderObject.setVendorId(vendorId).setProducts(new ArrayList<OrderProduct>());
//                orderVendor = vendorHashMap.get(vendorId);
//                orderObject.setDeliveryCharge(orderVendor.getDeliveryCharge());
//                orderObject.setContainerCharge(orderVendor.getConatainerCharge());
//                HashMap<Long, Integer> productCountMap = order.get(vendorId);
//                if(productCountMap!=null){
//                    for(long productId: productCountMap.keySet()){
//                        Product product = productHashMap.get(productId);
//                        VendorProduct vendorProduct = new VendorProduct();
//                        vendorProduct.vendorId = vendorId;
//                        vendorProduct.productId = productId;
//                        vendorProduct.count = productCountMap.get(productId);
//                        if(product.isFree()) {
//                            vendorProduct.price = 0;
//                        }else{
//                            vendorProduct.price = product.getPrice();
//                        }
//                        OrderProduct orderProduct = new OrderProduct();
//                        if(product.isFree()){
//                            orderProduct.price = 0;
//                        }else {
//                            orderProduct.price = vendorProduct.price;
//                        }
//
//                        orderProduct.quantity = vendorProduct.count;
//                        orderProduct.id = vendorProduct.productId;
//                        orderObject.getProducts().add(orderProduct);
//                        orderObject.addPrice(orderProduct.price*orderProduct.getQuantity());
//                        vendorProducts.add(vendorProduct);
//                    }
//                }
//            }
            if (setCancel)
                return null;

            for (OrderProductOffline orderProduct : cart.getOrderProducts()) {
                orderObject.getProducts().add(orderProduct);
            }
            orderObject.setVendorId(orderVendor.getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (setCancel)
                return;

            updateUiWith(cart, orderObject);
            setOcassion(selectedOcasion);
            setVendor(orderVendor);
            udateLocationInfo();
            updateOrderSummary(orderObject);

        }
    }


}
