package com.hungerbox.customer.order.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.DeskReferenceSetting;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.OrderSelectedTime;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserAddRequest;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.VendorProduct;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.GuestListQualcommAdapter;
import com.hungerbox.customer.order.adapter.OrderReviewAdapter;
import com.hungerbox.customer.order.adapter.RecommendedProductAdapter;
import com.hungerbox.customer.order.fragment.ExitFragment;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.order.fragment.GuestNamesFragment;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.GuestRemoveClickListener;
import com.hungerbox.customer.order.listeners.OrderReviewProductRefreshListener;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.service.AlarmService;
import com.hungerbox.customer.service.NotificationService;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class OrderReviewActivity extends ParentActivity implements OrderReviewProductRefreshListener {

    public static final int OTP_VERIFICATION_REQ_CODE = 987;
    RecyclerView rvOrderList, rvReccomendedItems;
    OrderReviewAdapter orderReviewAdapter;

    Toolbar toolbar;
    TextView tvTotal, tvTitle,
            tvOrderTotal,
            tvDeliveryCharge, tvContainerCharge, tvVat, tvServiceTax, tvCgst, tvSgst, tvTotalHeader,
            tvLocationName, tvlocationTime, tvLocationChange;
    LinearLayout llDeliveryContainer, llContainerContainer, llOrderDetailConatiner,
            llVat, llServiceTax, llCgst, llSgst, llLocationDensityContainer;

    TextInputLayout llOrderCommentContainer;
    RelativeLayout rlClosedAmountContainer, rlPaymentAmountContainer;
    Button btPay;
    ProgressBar progressBar;
    long vendorStartTime;
    TreeSet<User> users = new TreeSet<>();
    User user;
    String location, userName;
    long locationId;
    TimePickerDialog timePicker;
    Calendar orderSelectedTime;
    Order order;
    Vendor selectedOrderVendor;
    Ocassion orderOcassion;
    MainApplication mainApplication;
    boolean isRunning = false;
    private ExitFragment exitFragment;
    private TextView preOrderTime,tvEmpCount;
    private Cart cart;
    private LinearLayout llConvenienceFee, llRecommended;

    private ProgressBar pbPay;
    private double userExternalWalletCharge;
    private double internalWalletPayableAmount;
    private ArrayList orderInfoList;
    private Realm realm;
    private EditText tetComment;
    private ArrayList<Product> recommendedProducts = new ArrayList<>();
    private float locationCapacity;
    private double otherUserWallet = 0;
    private AlertDialog dialog;
    private TextInputEditText deskRefrence;
    private TextInputLayout txtInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review);

        Realm.init(getApplicationContext());

        toolbar = findViewById(R.id.tb_order_review);
        deskRefrence = findViewById(R.id.et_desk_reference);
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
        mainApplication = (MainApplication) getApplication();
        rvOrderList = findViewById(R.id.rv_order_review);
        rvOrderList.setLayoutManager(new CustomNonScrollingLayoutManager(this));
        tvEmpCount = findViewById(R.id.tv_emp_count);

        btPay = findViewById(R.id.bt_pay);
        btPay.setEnabled(false);
        pbPay = findViewById(R.id.pb_place_order);

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
        tetComment = findViewById(R.id.tet_comments);
        llOrderCommentContainer = findViewById(R.id.til_comments);

        tvCgst = findViewById(R.id.tv_cgst);
        tvSgst = findViewById(R.id.tv_sgst);

        tvServiceTax = findViewById(R.id.tv_st);
        tvTotal = findViewById(R.id.tv_total);
        tvTotalHeader = findViewById(R.id.tv_amount_header_total);
        progressBar = findViewById(R.id.pb_order_review);

        tvLocationChange = findViewById(R.id.tv_location_change);
        tvLocationName = findViewById(R.id.tv_order_location_name);
        tvlocationTime = findViewById(R.id.tv_location_time);
        llLocationDensityContainer = findViewById(R.id.ll_density);
        rvReccomendedItems = findViewById(R.id.rl_reccomended_items);
        llRecommended = findViewById(R.id.ll_reccomended);
        rvReccomendedItems.setLayoutManager(new CustomNonScrollingLayoutManager(this));
        isRunning = true;

        tvEmpCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGuestList();
            }
        });

        EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.SCREEN_OPEN_CART, EventUtil.SCREEN_HOME);

        try {

            String source = getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
            if (source == null) {
                source = "NA";
            }
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
            HBMixpanel.getInstance().addEvent(OrderReviewActivity.this, EventUtil.MixpanelEvent.SCREEN_OPEN_CART, jo);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        final MainApplication mainApplication = (MainApplication) getApplication();
        cart = mainApplication.getCart();
        guestOrderCheck(cart);
        userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        location = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        locationCapacity = SharedPrefUtil.getFloat(ApplicationConstants.LOCATION_CAPACITY, 0);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        CreateOrderListTask createOrderListTask = new CreateOrderListTask();
        createOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        if (cart.getVendor() == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }

        if(cart.getVendor().isRestaurant() && AppUtils.getConfig(OrderReviewActivity.this).getGroup_ordering() != null && AppUtils.getConfig(OrderReviewActivity.this).getGroup_ordering().isEnable()){
            findViewById(R.id.card_group_ordering).setVisibility(View.VISIBLE);
            ((TextInputLayout)findViewById(R.id.group_order_til)).setHint(AppUtils.getConfig(OrderReviewActivity.this).getGroup_ordering().getTitle());
            findViewById(R.id.group_order_til).requestFocus();
            findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((TextInputEditText)findViewById(R.id.group_order_tie)).getText().toString().isEmpty()){
                        AppUtils.showToast("Please enter valid user.", true, 2);
                    }else{
                        addUser(new UserAddRequest().setUsername(((TextInputEditText)findViewById(R.id.group_order_tie)).getText().toString()).setOccasionId(cart.getOccasionId()));
                    }
                }
            });
        }else{
            findViewById(R.id.card_group_ordering).setVisibility(View.GONE);
        }

        if (AppUtils.getConfig(this).isReccomendation_on_review()) {
            llRecommended.setVisibility(View.VISIBLE);
        } else {
            llRecommended.setVisibility(View.GONE);
        }

        if(AppUtils.getConfig(this).is_location_fixed())
        {
            tvLocationChange.setVisibility(View.GONE);
        }else{
            tvLocationChange.setVisibility(View.VISIBLE);
        }
        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_PLACE_ORDER_CLICK, EventUtil.SCREEN_CART);
                HBMixpanel.getInstance().addEvent(OrderReviewActivity.this, EventUtil.MixpanelEvent.CART_PLACE_ORDER_CLICK);
                if (guestOrderCheck(cart) &&
                        cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                    String title = "Please add the affiliations of your Guests to place guest Order";
                    GuestNamesFragment guestNamesFragment = GuestNamesFragment.newInstance(title,
                            "Submit", mainApplication.getGuestItemCount(),
                            new GuestNamesFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction(ArrayList guestList) {
                                    orderInfoList = guestList;
                                    order.orderGuestInfo = orderInfoList;
                                    order.setGuestType(cart.getGuestType());
                                    goToPayment();
                                }

                                @Override
                                public void onNegativeInteraction() {

                                }
                            });
                    guestNamesFragment.show(getSupportFragmentManager(), "Guest Names");
                } else if (AppUtils.getConfig(OrderReviewActivity.this).isNo_paid_orders() && order.getPrice() > 0) {
                    AppUtils.showToast("Paid items are not Allowed for your Company", true, 2);
                } else {
                    goToPayment();
                }
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

        if (!AppUtils.getConfig(this).isReview_comment()) {
            llOrderCommentContainer.setVisibility(View.GONE);
        }

        rlClosedAmountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAmountHeader();
            }
        });

        tvLocationChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try{
//                    HashMap map = new HashMap<>();
//                    map.put(CleverTapEvent.PropertiesNames.getScreen_name(), "Order Review");
//                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLocation_click(), map, OrderReviewActivity.this);
//                }catch (Exception exp){
//                    exp.printStackTrace();
//                }
                EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_CLICK_CHANGE_LOCATION, EventUtil.SCREEN_CART);
                showChangeLocationDialog();
            }
        });
        LogoutTask.updateTime();
    }

    private void addUser(UserAddRequest userAddRequest) {
        AppUtils.hideKeyboard(OrderReviewActivity.this, ((TextInputEditText)findViewById(R.id.group_order_tie)));
        String url = UrlConstant.ADD_USER_IN_GROUP;
        progressBar.setVisibility(View.VISIBLE);
        SimpleHttpAgent<UserReposne> addUserHttpAgent = new SimpleHttpAgent<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        progressBar.setVisibility(View.GONE);
                        if (responseObject.user.isAllowedInGroup()) {
                            users.add(responseObject.user);
                            ((TextInputEditText)findViewById(R.id.group_order_tie)).setText("");
                            updateOrderSummary(order);
                        } else {
                            FreeOrderErrorHandleDialog freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(null, "Unable to add this user.\nPlease try adding someone else", new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveButtonClicked() {

                                }

                                @Override
                                public void onNegativeButtonClicked() {

                                }
                            }, "OK");
                            freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "add_user");
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        progressBar.setVisibility(View.GONE);
                        FreeOrderErrorHandleDialog freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(null, error, new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveButtonClicked() {

                            }

                            @Override
                            public void onNegativeButtonClicked() {

                            }
                        }, "OK");
                        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "add_user");
                    }
                },
                UserReposne.class
        );
        addUserHttpAgent.post(userAddRequest, new HashMap<String, JsonSerializer>());
    }

    private void showChangeLocationDialog() {
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(
                "Your cart will get cleared if you change the location.",
                "CHANGE",
                "CANCEL",
                new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_CHANGE_LOCATION, EventUtil.SCREEN_CART);
                        clearLocalOrder();
                        Intent intent = new Intent();
                        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order Review");
                        intent.setAction(ApplicationConstants.ACTION_OPEN_LOCATION);
                        intent.putExtra(ApplicationConstants.ACTION, ApplicationConstants.ACTION_OPEN_LOCATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                }
        );

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "cart_clear")
                .commitAllowingStateLoss();
    }

    private void toggleAmountHeader() {
        EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_DETAILED_BILL, EventUtil.SCREEN_CART);
        HBMixpanel.getInstance().addEvent(OrderReviewActivity.this, EventUtil.MixpanelEvent.CART_DETAILED_BILL);
        rlClosedAmountContainer.setVisibility(View.GONE);
        rlPaymentAmountContainer.setVisibility(View.VISIBLE);
    }

    private void goToPayment() {

        if(deskRefrence.getVisibility() == View.VISIBLE){
            if(deskRefrence.getText().toString().trim().equals("")){
                AppUtils.showToast("Please enter "+AppUtils.getConfig(getApplicationContext()).getWorkstation_address(), true, 0);
            }else if(order.getDeskReference() != null && order.getDeskReference().trim().equals(deskRefrence.getText().toString().trim())){
                redirectToPayment();
            }else{
                DeskReferenceSetting userSettings = new DeskReferenceSetting();
                userSettings.setDeskReference(deskRefrence.getText().toString().trim());
                SimpleHttpAgent<DeskReferenceSetting> settingsHttpAgent = new SimpleHttpAgent<DeskReferenceSetting>(this,
                        UrlConstant.SET_USER_SETTINGS, new ResponseListener<DeskReferenceSetting>() {
                    @Override
                    public void response(DeskReferenceSetting responseObject) {

                        order.setDeskReference(deskRefrence.getText().toString().trim());
                        redirectToPayment();
                    }
                }, new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                        AppUtils.hideKeyboard(OrderReviewActivity.this,deskRefrence);
                        if(error != null && !error.equals("")){
                            AppUtils.showToast(error, true, 0);
                        }else{
                            AppUtils.showToast("Some error occured", true, 0);
                        }

                        if(order.getDeskReference() != null){
                            deskRefrence.setText(order.getDeskReference());
                            deskRefrence.setSelection(deskRefrence.getText().length());
                        }else{
                            deskRefrence.setText("");
                        }
                    }
                }, DeskReferenceSetting.class);
                settingsHttpAgent.post(userSettings,new HashMap<String, JsonSerializer>());
            }
        }else{
            redirectToPayment();
        }
    }

    void redirectToPayment(){
        order.setComment("" + tetComment.getText().toString());
        order.setDocId();
        if (order.getTotalPrice() > 0) {
            Intent paymentIntent = new Intent(OrderReviewActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("order", order);
            paymentIntent.putExtra("user", user);
            paymentIntent.putExtra("userExternalWalletCharge", userExternalWalletCharge);
            paymentIntent.putExtra("internalWalletPayableAmount", internalWalletPayableAmount);
            paymentIntent.putExtra("otherUserWallet", otherUserWallet);
            paymentIntent.putExtra("vendorId", selectedOrderVendor.getId());
            paymentIntent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            paymentIntent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order Review");
            startActivity(paymentIntent);
        } else {
            cleverTapPayClick(order);
            submitOrder(order);
        }
    }

    private void cleverTapPayClick(Order order){


        //cleverTap PayClick
//        try {
//
//            ArrayList<OrderProduct> products = order.getProducts();
//            String item_product = "";
//            for (OrderProduct orderProduct : products) {
//                item_product +="("+orderProduct.getName()+","+ orderProduct.getQuantity()+")";
//            }
//
//            //pay_click_response
//            HashMap<String, Object> pay_click_map = new HashMap<>();
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getSource(), "Payment");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getMethods_selected(), "company_paid");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getDefault_method(), "company_paid");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getCart_value(),order.getTotalPrice());
//            if(order.getComment()!=null && !order.getComment().trim().isEmpty())
//                pay_click_map.put(CleverTapEvent.PropertiesNames.getSpecial_instruction(), order.getComment());
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getItem_name_item_quantity(), item_product);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_method_change(), false);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_company_paid(),true);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getPay_click(), pay_click_map, OrderReviewActivity.this);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private void submitOrder(final Order order) {
        btPay.setText("Placing your order...");
        btPay.setEnabled(false);
//        EventUtil.logEvent(new CustomEvent("order_start").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
        String url = UrlConstant.POST_ORDER_URL;
        SimpleHttpAgent<OrderResponse> orderSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        pbPay.setVisibility(View.GONE);
//                        btPay.setText("Place Order");
                        if (responseObject != null && responseObject.order != null) {
                            handleResponse(responseObject, order);
                            if (responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)) {
//                                startOrderPayment(responseObject.order);
                            } else {
                                EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_ORDER_PLACED, EventUtil.SCREEN_PAYMENT);
                                HBMixpanel.getInstance().addEvent(OrderReviewActivity.this, EventUtil.MixpanelEvent.CART_ORDER_PLACED);
//                                EventUtil.logEvent(new CustomEvent("order_complete").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
                                order.setId(responseObject.order.getId());
                                order.setCreatedAt((DateTimeUtil.adjustCalender(OrderReviewActivity.this).getTimeInMillis() / 1000l));
                                if (user != null)
                                    updateOrderSummary(order);
                                clearLocalOrder();
                                order.setOrderStatus(responseObject.order.getOrderStatus());
                                startOrderView(responseObject.getOrder());
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        EventUtil.FbEventLog(OrderReviewActivity.this, EventUtil.CART_ORDER_ERROR, EventUtil.SCREEN_CART);

                        try {
                            String errorMessage = "NA";
                            if (errorResponse != null && errorResponse.message != null) {
                                errorMessage = errorResponse.message;
                            }
                            JSONObject jo = new JSONObject();
                            jo.put(EventUtil.MixpanelEvent.SubProperties.ERROR, errorMessage);
                            HBMixpanel.getInstance().addEvent(OrderReviewActivity.this, EventUtil.MixpanelEvent.CART_ORDER_ERROR, jo);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        LogoutTask.updateTime();
//                        EventUtil.logEvent(new CustomEvent("order_error").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
//                        btPay.setEnabled(true);
                        AppUtils.HbLog("dtap", "order error");

                        btPay.setText("Place Order");
                        pbPay.setVisibility(View.GONE);
                        btPay.setEnabled(true);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showOrderErrorDialog(null, "Sorry, there is some connectivity issue, Please try again", false);
                        } else if (errorResponse != null && errorResponse.getError().getOrderIds().size() > 0) {
                            order.setId(errorResponse.error.orderIds.get(0));
                            order.setCreatedAt((DateTimeUtil.adjustCalender(OrderReviewActivity.this).getTimeInMillis() / 1000l));
                            order.setOrderStatus(OrderUtil.NEW);
                            showOrderErrorDialog(order, errorResponse.message, true);
                        } else if (errorResponse != null && errorResponse.message != null) {
                            showOrderErrorDialog(null, errorResponse.message, false);
                        } else {
                            showOrderErrorDialog(null, "Oops! something went wrong. Please try again", true);
                        }
                    }
                },
                OrderResponse.class
        );
        orderSimpleHttpAgent.post(order, new HashMap<String, JsonSerializer>());
    }

    private void startOrderView(Order order) {
        Intent intent = null;
        if (selectedOrderVendor != null)
            order.setVendor(selectedOrderVendor);
        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            // perform operation for payment side
            //showPreOrderDialog(order);
            intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, true);
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra("message", "High Five! \nYour Order has been placed.\nIt is in Pre-Order");
            if (selectedOrderVendor != null)
                startOrderNotificatioService(order);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            clearLocalOrder();
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            startActivity(intent);
            clearLocalOrder();
            if (selectedOrderVendor != null)
                startOrderNotificatioService(order);
            finish();
        }

    }


    private void setupBottomBar() {

        btPay.setEnabled(true);
    }


    public boolean guestOrderCheck(Cart cart) {
        if (mainApplication.getGuestItemCount() != 0) {
            if (cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                return true;
            } else if (cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_PERSONAL)) {
                return true;
            } else {
                return false;
            }
        } else {
            cart.setGuestType("");
            return false;
        }
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

        MainApplication.bus.register(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        MainApplication.bus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT > 24){
            supportFinishAfterTransition();
        }else{
            finish();
        }

        try {
            if (getIntent().getBooleanExtra("anim", false)) {
                overridePendingTransition(R.anim.stay, R.anim.slide_out_up);
            }else{
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void getUserDetailsFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        String url = UrlConstant.USER_DETAIL + "?occasionId=" + orderOcassion.id + "&locationId=" + locationId;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {

                        if (responseObject == null) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getUserDetailsFromServer();
                                }
                            });
                        } else {
                            btPay.setEnabled(true);
                            updateUser(responseObject.user);
                            if (order != null)
                                updateOrderSummary(order);

                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getUserDetailsFromServer();
                                }
                            });
                        }
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }

    public void showNoNetFragment(RetryListener retryListener) {
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener);
        fragment.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, "exit")
                .commit();
    }

    private void updateUser(User responseObject) {
        user = responseObject;
        if(user.getDeskOrderingEnabled()==1){
            deskRefrence.setVisibility(View.VISIBLE);
            txtInputLayout.setVisibility(View.VISIBLE);
            if(user.getDeskReference()!=null && !user.getDeskReference().trim().isEmpty()){
                order.setDeskReference(user.getDeskReference());
                deskRefrence.setText(order.getDeskReference());
                deskRefrence.setSelection(deskRefrence.getText().length());
            }
            deskRefrence.requestFocus();
        }else{
            txtInputLayout.setVisibility(View.GONE);
            deskRefrence.setVisibility(View.GONE);
        }
    }


    private void startOrderNotificatioService(Order order) {
        boolean appNotoficationStting = SharedPrefUtil.getBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, false);
        if (appNotoficationStting && order != null) {
            Intent intent = new Intent(this, NotificationService.class);
            intent.putExtra(ApplicationConstants.ORDER, order);
            startService(intent);
        }
    }


    private void startOrderHeadService(Order order) {

        boolean isChatEnabled = SharedPrefUtil.getBoolean(ApplicationConstants.CHAT_HEAD_SETTING, false);
        if (isChatEnabled && AppUtils.getConfig(OrderReviewActivity.this).isChat_head()) {
            Intent intent = new Intent(this, AlarmService.class);
            intent.putExtra(ApplicationConstants.ORDER, order);
            startService(intent);
        }
    }


    private void clearLocalOrder() {
        MainApplication mainApplication = (MainApplication) getApplication();
        mainApplication.clearOrder();
    }


    @Override
    public void refreshOrderList(OrderProduct orderProduct, int position, boolean shouldRefresh) {

        if (shouldRefresh) {
            if (orderProduct.getQuantity() == 0) {
                try{
                    order.getProducts().remove(position);
                }catch (Exception exp){
                    exp.printStackTrace();
                }

                if (order.getProducts().size() <= 0) {
                    finish();
                }
            }
            if (orderReviewAdapter != null)
                orderReviewAdapter.notifyDataSetChanged();
        }
        if (user != null)
            updateOrderSummary(order);
        guestOrderCheck(cart);
    }

    private void setOcassion(Ocassion selectedOcasion) {
        this.orderOcassion = selectedOcasion;
        getUserDetailsFromServer();
    }

    private void setVendor(Vendor orderVendor) {

//        this.selectedOrderVendor = realm.where(Vendor.class).equalTo("id", orderVendor.getId()).findFirst();

        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }
        this.selectedOrderVendor = selectedOrderVendor.clone();

        long currentTime = DateTimeUtil.adjustCalender(OrderReviewActivity.this).getTimeInMillis();

        vendorStartTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getStartTime(), OrderReviewActivity.this);
        long vendorEndTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getEndTime(), OrderReviewActivity.this);
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

    private void updatePaymentDetails(Order order) {
        otherUserWallet = 0;
        order.setGroupOrder(false);
        if(order.users != null){
            order.users.clear();
        }
        order.users = new ArrayList<Long>();

        if (users != null) {
            for (User user : users) {
                otherUserWallet += user.getFirstWalletAmount();
                order.users.add(user.getId());
                order.setGroupOrder(true);
            }
        }

        if(users!=null && users.size()>0) {
            tvEmpCount.setVisibility(View.VISIBLE);
            tvEmpCount.setText(users.size() + " " + AppUtils.getConfig(OrderReviewActivity.this).getGroup_ordering().getCount_text());
        }else{
            tvEmpCount.setVisibility(View.GONE);
        }

        switch (cart.getGuestType()) {
            case ApplicationConstants.GUEST_TYPE_OFFICIAL:
                order.calculateGuestOrderPrice(cart.getGuestType());
                break;
            case ApplicationConstants.GUEST_TYPE_PERSONAL:
            default:
                order.calculateOrderProductPrice();
        }

        llConvenienceFee.setVisibility(View.GONE);
        double userRemainingBalance = (otherUserWallet + user.getCurrentWalletBalance()) - order.getTotalPrice();

        String payButtonString;
        if (userRemainingBalance < 0) {
            userExternalWalletCharge = order.getTotalPrice() - (otherUserWallet + user.getCurrentWalletBalance());
            internalWalletPayableAmount = (user.getCurrentWalletBalance() + otherUserWallet);
            sendNegativeWalletEvent(userRemainingBalance, order.getTotalPrice(), (users == null) ? false : true);
        } else {
            userExternalWalletCharge = 0;
            internalWalletPayableAmount = order.getTotalPrice();
        }

        if (order.getTotalPrice() > 0)
            payButtonString = "Select a Payment Method";
        else
            payButtonString = "Place Order";
        btPay.setText(payButtonString);
        setupBottomBar();
    }

    GuestListQualcommAdapter guestListQualcommAdapter;

    private void showGuestList(){

        RecyclerView rlGuestList;
        TextView tvGuestListTitle;
        LayoutInflater layoutInflater = getLayoutInflater();
        View alertlayout = layoutInflater.inflate(R.layout.guest_order_list,null,false);
        rlGuestList = alertlayout.findViewById(R.id.rl_guest_list);
        tvGuestListTitle = alertlayout.findViewById(R.id.tv_guest_list_title);


        final ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.addAll(users);


        guestListQualcommAdapter = new GuestListQualcommAdapter(OrderReviewActivity.this, userArrayList, new GuestRemoveClickListener() {
            @Override
            public void onRemoveClick(int position) {


                users.remove(userArrayList.get(position));
                userArrayList.remove(position);
                guestListQualcommAdapter.updateGuestList(userArrayList);
                AppUtils.showToast("User Removed Successfully.", true, 1);

                if(userArrayList.size() == 0){
                    dialog.dismiss();
                }
                updatePaymentDetails(order);

            }
        });
        AlertDialog.Builder guestListDialog = new AlertDialog.Builder(OrderReviewActivity.this);
        rlGuestList.setLayoutManager(new LinearLayoutManager(this));
        rlGuestList.setAdapter(guestListQualcommAdapter);

        tvGuestListTitle.setText(AppUtils.getConfig(OrderReviewActivity.this).getGroup_ordering().getCount_text());

        guestListDialog.setView(alertlayout);

        dialog = guestListDialog.create();
        dialog.show();

    }

    private void sendNegativeWalletEvent(double userRemaingBalance, double totalPrice, boolean isGroupOrder) {
        try {
            boolean walletEmpty = false;
            if (userRemaingBalance + totalPrice == 0)
                walletEmpty = true;
            else
                walletEmpty = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateOrderSummary(Order order) {

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

        if (user != null) {
            updatePaymentDetails(order);
        }


        tvOrderTotal.setText(String.format("₹ %.2f", order.getPrice()));

        tvTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));
        tvTotalHeader.setText(String.format("₹ %.2f", order.getTotalPrice()));

        if (order.getDeliveryCharge() <= 0 && order.getContainerCharge() <= 0
                && order.getServiceTax(selectedOrderVendor) <= 0
                && order.getVat(selectedOrderVendor) <= 0
                && order.getCgst(selectedOrderVendor) <= 0
                && order.getSgst(selectedOrderVendor) <= 0) {
            llOrderDetailConatiner.setVisibility(View.GONE);
        }

    }


    private void showOrderErrorDialog(Order order, String error, boolean clearOrder) {
        if (!isRunning)
            return;
        FreeOrderErrorHandleDialog freeOrderErrorHandleDialog = null;
        if (clearOrder) {
            clearLocalOrder();
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, null);
        } else {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                @Override
                public void onPositiveButtonClicked() {

                    Intent intent = AppUtils.getHomeNavigationIntent(OrderReviewActivity.this);
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

    private void handleResponse(OrderResponse responseObject, Order order) {
        btPay.setEnabled(true);
        this.order = order;
    }

    private void updateUiWith(Cart cart, Order order) {
        this.order = order;
        if (orderReviewAdapter == null) {
            orderReviewAdapter = new OrderReviewAdapter(this, order, cart, this);
            rvOrderList.setAdapter(orderReviewAdapter);
        } else {
            orderReviewAdapter.changeOrder(order, cart);
            orderReviewAdapter.notifyDataSetChanged();
        }
    }


    private void udateLocationInfo() {
        if (llLocationDensityContainer.getChildCount() >= 5)
            return;

        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }
        tvLocationName.setText(SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, ""));

        tvlocationTime.setText("" + selectedOrderVendor.getAvgOrderTime() + " mins");
        createImageViews(getLocationCapacityCount(locationCapacity), llLocationDensityContainer, this);

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

    public void findAndCreateRecommendation() {
        if (recommendedProducts != null && recommendedProducts.size() > 0)
            return;

        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }

        RealmQuery<Product> query = realm.where(Product.class);

        query.equalTo("recommeded", 1);
        query.equalTo("isFree", 0);
        query.equalTo("vendorId", selectedOrderVendor.getId());
        for (int i = 0; i < cart.getOrderProducts().size(); i++) {
            query.notEqualTo("id", cart.getOrderProducts().get(i).getId());
        }

        RealmResults<Product> realmResults = query.findAll().sort("sortOrder", Sort.DESCENDING);
        for (int i = 0; i < ((realmResults.size() > 5) ? 5 : realmResults.size()); i++) {
            recommendedProducts.add(realmResults.get(i).clone());
        }
        if (recommendedProducts.size() > 0) {
            //TODO remove items from cart
            createRecommendedList(recommendedProducts);
        } else {
            llRecommended.setVisibility(View.GONE);
        }
    }

    private void createRecommendedList(ArrayList<Product> recommendedProducts) {
        if (rvReccomendedItems.getAdapter() == null) {
            RecommendedProductAdapter recommendedProductAdapter = new RecommendedProductAdapter(
                    this,
                    order,
                    cart,
                    recommendedProducts,
                    new OrderReviewProductRefreshListener() {
                        @Override
                        public void refreshOrderList(OrderProduct orderProduct, int position, boolean shouldRefresh) {

                        }
                    }
            );
            rvReccomendedItems.setAdapter(recommendedProductAdapter);
        } else {
            if (rvReccomendedItems.getAdapter() instanceof RecommendedProductAdapter) {
                RecommendedProductAdapter recommendedProductAdapter = (RecommendedProductAdapter) rvReccomendedItems.getAdapter();
                recommendedProductAdapter.changeOrder(order, cart);
            }
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
        updateRecommendedList();
    }

    private void updateRecommendedList() {
        if (rvReccomendedItems.getAdapter() != null)
            rvReccomendedItems.getAdapter().notifyDataSetChanged();
    }

    private void initiateTimePicker(final Calendar calendarVendorStartTime, final Calendar calendarVendorEndTime) {

        timePicker = new TimePickerDialog(OrderReviewActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarSelectTime = Calendar.getInstance();
                calendarSelectTime.setTimeInMillis(DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis());
                calendarSelectTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarSelectTime.set(Calendar.MINUTE, minute);
                calendarSelectTime.set(Calendar.SECOND, 0);
                calendarSelectTime.set(Calendar.MILLISECOND, 0);

                if (calendarSelectTime.getTimeInMillis() >= calendarVendorStartTime.getTimeInMillis() && calendarSelectTime.getTimeInMillis() <= calendarVendorEndTime.getTimeInMillis()) {

                    vendorStartTime = calendarSelectTime.getTimeInMillis();
                    preOrderTime.setVisibility(View.VISIBLE);
                    if(minute < 10){
                        preOrderTime.setText(String.format(getString(R.string.selected_delivery_time), "" + hourOfDay, "0" + minute));
                    }else{
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
        ArrayList<VendorProduct> vendorProducts = new ArrayList<>();
        Cart cart;
        Order orderObject = new Order();
        Vendor orderVendor;
        Ocassion selectedOcasion;

        private boolean setCancel = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainApplication mainApplication = (MainApplication) getApplication();
//            order = mainApplication.getOrder();
//            productHashMap = mainApplication.getProductHashMap();
//            vendorHashMap = mainApplication.getVendorHashMap();
            cart = mainApplication.getCart();
            try {

                selectedOcasion = MainApplication.getOcassionForId(cart.getOccasionId());
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

            for (OrderProduct orderProduct : cart.getOrderProducts()) {
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
            findAndCreateRecommendation();
        }
    }
}
