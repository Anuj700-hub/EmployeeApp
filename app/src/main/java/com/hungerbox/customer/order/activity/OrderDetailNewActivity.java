package com.hungerbox.customer.order.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.bluetooth.BluetoothStopService;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.bluetooth.ViolationLogManager;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.EnterExitConfig;
import com.hungerbox.customer.model.FeedbackEta;
import com.hungerbox.customer.model.LinkedLocationsResponse;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderCancellationEligibilityResponse;
import com.hungerbox.customer.model.OrderCancellationResponse;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.OrderWallet;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.DetailedBillItemAdapter;
import com.hungerbox.customer.order.adapter.OrderWalletListAdapter;
import com.hungerbox.customer.order.adapter.PickupItemAdapter;
import com.hungerbox.customer.order.fragment.ApprovalPendingDialog;
import com.hungerbox.customer.order.fragment.OrderCancellationReasonDialog;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.spaceBooking.SpaceBookingActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericFragmentWithTitle;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderDetailNewActivity extends ParentActivity {

    private Toolbar toolbar;
    private RelativeLayout rlProgressBar;
    Button btMarkIn, btMarkOut, btCollectOrder, btBook;
    protected CountDownTimer countDownTimer;
    EnterExitConfig enterExitConfig;
    Config.SpaceType spaceType;

    private SwipeRefreshLayout refreshLayout;
    private ScrollView svOrderDetailRoot;
    private RecyclerView rvPickupItems, rvProductList, rlvOrderWalletList;
    private PickupItemAdapter pickupItemAdapter;
    private DetailedBillItemAdapter detailedBillItemAdapter;
    private boolean isAutoPopupShown = false;
    Config config;
    boolean isSpaceBookingOrder = false;

    private ConstraintLayout clOrderConfirmedCheckPoint, clEntryCafeCheckPoint, clPickupCheckPoint, clExitCafeCheckPoint;
    private View ivEnterCafeView, ivOrderConfirmView;

    private LinearLayout  llOrderAmountContainer, llServiceTax, llDeliveryCharge, llVat, llCgst, llSgst, llConvinenceCharge,  llContainerCharge;
    private TextView tvTotalOrderAmount, tvServiceTax, tvDeliveryCharge, tvVat, tvCgst, tvSgst, tvConvinenceCharge, tvContainerCharge, tvExitCafe, qtyTitle, itemTitle;

    CardView cvOrderStatus,cvDetailedBill;
    TextView tvOrderStatus, tvOrderStatusContent, deliveryTime, tvBookingHeader, tvBookingText, tvOrderPlaced, tvOrderPlacedText;
    ImageView ivStatus;

    private LinearLayout llOrderId, llOrderType, llOrderAddress, llGroupUsers, llProjectCode, llPickupButton, llBookingSection;
    private TextView tvOrderId, tvOrderType, tvOrderAddress, tvGroupUsers, tvOrderAddressLabel, tvProjectCodeLabel, tvProjectCode, tvVendorName;

    private ImageView ivBack, ivEnterQr, ivPickupQr, ivExitQr, ivEnterCafeTick, ivPickupCafeTick, ivExitCafeTick, ivOrderPlacedTick;
    private TextView tvScanPickupQrText, tvPickupOrder, tvEnterCafe;
    private TextView orderCancellation,
            tvEnterQrText, tvPickupQrText, tvExitQrText, tvScanQr, tvButtonMarkIn,
            tvTimer, tvTimerText, tvExitText, tvTime,
            tvCafeNameEntry,
            tvWelcomeCafe,
            tvOrderWalletListHeader, tvOrderAmount,
            tvOrderDetailTitle;
    View pickupLine;
    boolean isEntryQrEnabled = false, entryCheckpointActivated = false;

    public Handler handler, autoRedirectHandler;
    private Runnable runnable;
    private Order order;
    GenericPopUpFragment errorHandleDialog;

    public long activityStartTime = 0;
    private long orderId;
    private boolean isNewOrder = false, isArchived = false, isRunning = false, fromSlotBooking = false;
    public boolean forFeedback = false;
    public long notificationTime = 0;
    public String orderRef="";
    public String notifTitle, notifBody;
    private boolean shouldRedirectToSpaceBooking;
    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_order_detail_new);
        restoreFromSavedInstance(savedInstanceState);

        activityStartTime = System.currentTimeMillis();
        toolbar = findViewById(R.id.tb_global);

        setApiTag(String.valueOf(System.currentTimeMillis()));

        orderCancellation = toolbar.findViewById(R.id.iv_cancel);
        ivBack = toolbar.findViewById(R.id.iv_back);
        tvOrderDetailTitle = toolbar.findViewById(R.id.tv_order_detail_title);
        refreshLayout = findViewById(R.id.srl_order_detail);
        tvTimer = findViewById(R.id.tv_pre_order_timer);
        tvTimerText = findViewById(R.id.tv_timer_text);
        tvTime = findViewById(R.id.tv_time);
        tvWelcomeCafe = findViewById(R.id.tv_welcome_cafe);
        tvCafeNameEntry = findViewById(R.id.tv_cafe_name);
        ivEnterQr = findViewById(R.id.iv_enter_qr);
        ivExitQr = findViewById(R.id.iv_exit_qr);
        ivPickupQr = findViewById(R.id.iv_pickup_qr);
        ivEnterCafeTick = findViewById(R.id.iv_enter_cafe_checkpoint);
        ivOrderPlacedTick = findViewById(R.id.iv_order_confirm_checkpoint);
        tvScanPickupQrText = findViewById(R.id.tv_pickup_cafe);
        ivPickupCafeTick = findViewById(R.id.iv_pickup_order_checkpoint);
        ivExitCafeTick = findViewById(R.id.iv_exit_cafe_checkpoint);
        rlvOrderWalletList = findViewById(R.id.rlv_order_wallet_lists);
        pickupLine = findViewById(R.id.pickup_line);
        tvEnterQrText = findViewById(R.id.enter_tap_qr);
        tvScanQr = findViewById(R.id.tv_scan_qr);
        tvButtonMarkIn = findViewById(R.id.tv_button_mark_in);
        tvExitQrText = findViewById(R.id.exit_tap_qr);
        tvPickupQrText = findViewById(R.id.pickup_tap_qr);
        rlProgressBar = findViewById(R.id.rl_progress);
        svOrderDetailRoot = findViewById(R.id.order_detail_root);
        tvOrderWalletListHeader = findViewById(R.id.tv_payment_list_header);
        btMarkIn = findViewById(R.id.bt_mark_in);
        btMarkOut = findViewById(R.id.bt_mark_out);
        rvPickupItems = findViewById(R.id.rv_pickup_items);
        rvProductList = findViewById(R.id.rv_items_container);
        tvTotalOrderAmount = findViewById(R.id.tv_total);
        tvVat = findViewById(R.id.tv_vat);
        tvServiceTax = findViewById(R.id.tv_st);
        llVat = findViewById(R.id.ll_vat_container);
        llServiceTax = findViewById(R.id.ll_st_container);
        tvContainerCharge = findViewById(R.id.tv_container);
        llContainerCharge = findViewById(R.id.ll_container_charge_container);
        tvConvinenceCharge = findViewById(R.id.tv_convenience);
        llConvinenceCharge = findViewById(R.id.ll_convenience_container);
        llDeliveryCharge = findViewById(R.id.ll_delivery_container);
        tvDeliveryCharge = findViewById(R.id.tv_delivery);
        llSgst = findViewById(R.id.ll_sgst_container);
        tvSgst = findViewById(R.id.tv_sgst);
        llCgst = findViewById(R.id.ll_cgst_container);
        tvCgst = findViewById(R.id.tv_cgst);
        llOrderAmountContainer = findViewById(R.id.ll_total_order_container);
        tvOrderAmount = findViewById(R.id.tv_total_order);
        clOrderConfirmedCheckPoint = findViewById(R.id.cp_order_confirmed);
        clEntryCafeCheckPoint = findViewById(R.id.cp_enter_cafe);
        ivEnterCafeView = findViewById(R.id.iv_enter_cafe_line);
        ivOrderConfirmView = findViewById(R.id.iv_order_confirm_line);
        clPickupCheckPoint = findViewById(R.id.cp_pickup_order);
        clExitCafeCheckPoint = findViewById(R.id.cp_exit_cafe);
        cvDetailedBill = findViewById(R.id.cv_detailed_bill);
        cvOrderStatus = findViewById(R.id.cv_order_status);
        llOrderId = findViewById(R.id.ll_order_id);
        llOrderType = findViewById(R.id.ll_order_type);
        llOrderAddress = findViewById(R.id.ll_order_address);
        llGroupUsers = findViewById(R.id.ll_group_users);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderType = findViewById(R.id.tv_order_type);
        tvOrderAddress = findViewById(R.id.tv_order_address);
        tvOrderAddressLabel = findViewById(R.id.tv_order_address_label);
        tvProjectCodeLabel = findViewById(R.id.project_code_label);
        llProjectCode = findViewById(R.id.ll_project_code);
        tvProjectCode = findViewById(R.id.tv_project_code);
        tvGroupUsers = findViewById(R.id.tv_group_users);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        ivStatus = findViewById(R.id.iv_status);
        tvOrderStatusContent = findViewById(R.id.tv_order_status_content);
        tvPickupOrder = findViewById(R.id.tv_pickup_order);
        llPickupButton = findViewById(R.id.ll_pickup_card);
        btCollectOrder = findViewById(R.id.bt_collect_order);
        tvExitText = findViewById(R.id.exit_text);
        deliveryTime = findViewById(R.id.delivery_time);
        llBookingSection = findViewById(R.id.ll_booking_section);
        tvBookingHeader = findViewById(R.id.tv_booking_header);
        tvBookingText = findViewById(R.id.tv_booking_text);
        btBook = findViewById(R.id.bt_book);
        tvOrderPlaced = findViewById(R.id.tv_order_confirmed);
        tvOrderPlacedText = findViewById(R.id.tv_order_confirmed_text);
        tvVendorName = findViewById(R.id.tv_vendor_name);
        tvEnterCafe = findViewById(R.id.tv_enter_cafe);
        tvExitCafe = findViewById(R.id.tv_exit_cafe);
        itemTitle = findViewById(R.id.item_title);
        qtyTitle = findViewById(R.id.qty_title);

        config = AppUtils.getConfig(getApplicationContext());

        ivBack.setOnClickListener(v -> navigateBack());

        Intent intent = getIntent();
        orderId = intent.getLongExtra(ApplicationConstants.BOOKING_ID, 0L);
        orderRef = intent.getStringExtra("order_ref");
        notificationTime = intent.getLongExtra("notification-time", 0L);
        fromSlotBooking = intent.getBooleanExtra(ApplicationConstants.FROM_SLOT_BOOKING, false);
        forFeedback = intent.getBooleanExtra(ApplicationConstants.FOR_FEEDBACK, false);
        isNewOrder = intent.getBooleanExtra(ApplicationConstants.IS_NEW_ORDER, false);
        isArchived = intent.getBooleanExtra(ApplicationConstants.IS_ORDER_ARCHIVED, false);
        notifTitle = intent.getStringExtra("title");
        notifBody = intent.getStringExtra("body");
        shouldRedirectToSpaceBooking = intent.getBooleanExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        rlvOrderWalletList.setLayoutManager(gridLayoutManager);

        // todo : recyclerview nested scroll problem
        rvPickupItems.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            getOrderDetail();
        });

        runnable = () -> {
            try {

                refreshLayout.setProgressViewOffset(false, 0,
                        (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                                        .getDisplayMetrics()));

                refreshLayout.setRefreshing(true);
                getOrderDetail();

            }
            catch (Exception exp) {
                exp.printStackTrace();
            }
        };
        if (fromSlotBooking){
            hideComponentsSlotBooking();
        }
        showFeedbackDialog();
        LogoutTask.updateTime();
        logoutAfterTimeout();
    }
    public void hideComponentsSlotBooking(){
        cvDetailedBill.setVisibility(View.GONE);
        clPickupCheckPoint.setVisibility(View.GONE);
    }

    public void showFeedbackDialog(){

        if (forFeedback && notificationTime>0  && orderRef!=null && !orderRef.equalsIgnoreCase("")){
            String title = "Have you left the cafeteria?";
            String body = "This will help us in reducing congestion";
            if (notifTitle!=null && notifBody!=null){
                title = notifTitle;
                body = notifBody;
            }
            GenericFragmentWithTitle genericPopUpFragment = GenericFragmentWithTitle.newInstance(title,body, "Yes", "No", new GenericFragmentWithTitle.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    submitFeedback(orderRef,notificationTime,"yes");
                }

                @Override
                public void onNegativeInteraction() {
                    submitFeedback(orderRef,notificationTime,"no");
                }
            });
            genericPopUpFragment.show(this.getSupportFragmentManager(), "dialog");
        }
    }

    private void submitFeedback(String orderId, long notificationTime,String response) {
        String url = UrlConstant.FEEDBACK_ETA+"?order-id="+orderId+"&notification-time="+notificationTime
                +"&response-time="+ DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis()
                +"&response="+response;

        SimpleHttpAgent<FeedbackEta> feedbackRatingReasonResponseSimpleHttpAgent = new SimpleHttpAgent<FeedbackEta>(
                MainApplication.appContext,
                url,
                responseObject -> {
                    if (responseObject != null && responseObject.getSuccess()) {
                        Log.d(TAG, "response: feedback response submitted successfully");
                    }
                    else{
                        Log.d(TAG, "response: Error in submitting feedback response via notification");
                    }
                },
                (errorCode, error, errorResponse) -> Log.d(TAG, "handleError: Error in submitting feedback response via notification"),
                FeedbackEta.class
        );

        feedbackRatingReasonResponseSimpleHttpAgent.get(getApiTag());
    }

    private void getOrderDetail() {

        String url;
        if(isArchived)
            url = UrlConstant.ARCHIVED_ORDER_DETAIL + orderId + "/1/1/0/1";
        else
            url = UrlConstant.ORDER_DETAIL + orderId + "/1/1/0/1";

        SimpleHttpAgent<OrderResponse> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
                this,
                url,
                responseObject -> {

                    refreshLayout.setRefreshing(false);
                    rlProgressBar.setVisibility(View.GONE);

                    if (responseObject == null) {
                            AppUtils.showToast("Unable to fetch Your Order", false, 0);
                    }
                    else {
                        this.order = responseObject.order;

                        isSpaceBookingOrder = order.isSpaceBookingOrder();

                        if(isSpaceBookingOrder){
                            tvOrderDetailTitle.setText("Booking Details");
                        }

                        svOrderDetailRoot.setVisibility(View.VISIBLE);
                        setTitle("#" + order.getOrderId());

                        enterExitConfig = new EnterExitConfig(responseObject.order.getLocation().getEnforcedCapacity());
                        checkForOrderCancellation();
                        isEntryQrEnabled = false;
                        UpdateOrderDetailUI();

                        if(order.getUserExitTime()!=-1 && (SharedPrefUtil.getLong(Util.BLUETOOTH_LAST_ORDER_ID,0) ==this.order.getId())  && (Util.isBluetoothDistancingActive(OrderDetailNewActivity.this, order) && !ViolationLogManager.Companion.isBluetoothAllDayEnabled(OrderDetailNewActivity.this))){
                            SharedPrefUtil.putBoolean(Util.IS_USER_EXIT,true);
                        }
                    }

                },
                (errorCode, error, errorResponse) -> {
                    refreshLayout.setRefreshing(false);
                    rlProgressBar.setVisibility(View.GONE);
                    AppUtils.showToast("Unable to fetch Your Order", false, 0);
                },
                OrderResponse.class
        );
        orderResponseSimpleHttpAgent.get(getApiTag());
    }

    private void UpdateOrderDetailUI(){

        if(showingFinalUI()){
            UpdateFinalStateUI();
        }
        else{
            showIntermediateUI();
        }
        if (order!=null && order.vendor!=null && order.vendor.vendor!=null && order.vendor.vendor.isSlotBookingVendor()){
            hideComponentsSlotBooking();
        } else {
            updateBillSection();
        }
    }

    private boolean showingFinalUI(){
        return (hasExitedCafe() && (OrderUtil.isFinalState(order.getOrderStatus()) || (!AppUtils.isSocialDistancingActive(order.getLocation()) && order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER))));
    }

    private boolean hasExitedCafe(){
        if(!enterExitConfig.isExitCafeActive()){
            return true;
        }
        if(!(order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)) && (order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED) ) && (order.getUserExitTime() == -1)){
            return false;
        }
        else{
            return true;
        }
    }

    private void showIntermediateUI(){

        cvOrderStatus.setVisibility(View.GONE);

        clOrderConfirmedCheckPoint.setVisibility(View.VISIBLE);
        ivEnterCafeView.setVisibility(View.VISIBLE);

        updateBookingSection();
        updateOrderPlacedCheckPoint();
        updateEntryCafeCheckPoint();
        if(isSpaceBookingOrder){
            clPickupCheckPoint.setVisibility(View.GONE);
            updateExitCafeCheckPoint();
            showAutoPopup();
        }
        else{
            clPickupCheckPoint.setVisibility(View.VISIBLE);
            updatePickupCheckPoint();
        }
    }

    private void updateBookingSection(){
        if(isSpaceBookingOrder){

            spaceType = AppUtils.getSpaceType(this, order.getLocation().getType());

            if(spaceType != null && spaceType.isIs_food_allowed()){
                llBookingSection.setVisibility(View.VISIBLE);
                tvBookingHeader.setText(spaceType.getMeal_section_header());
                tvBookingText.setText(spaceType.getMeal_section_text());
                btBook.setOnClickListener(v -> navigateForBooking());
            }
            else{
                llBookingSection.setVisibility(View.GONE);
            }
        }
        else{
            if(config.space_management != null && config.space_management.getSpace_booking_in_order_detail() != null && !config.space_management.getSpace_booking_in_order_detail().isEmpty()){

                spaceType = AppUtils.getSpaceType(this, config.space_management.getSpace_booking_in_order_detail());

                if(spaceType != null){
                    llBookingSection.setVisibility(View.VISIBLE);
                    tvBookingHeader.setText(spaceType.getSpace_section_header());
                    tvBookingText.setText(spaceType.getSpace_section_text());
                    btBook.setOnClickListener(v -> navigateForBooking());
                    if(shouldRedirectToSpaceBooking){
                        autoRedirectHandler = new Handler();
                        autoRedirectHandler.postDelayed(() -> runOnUiThread(() -> btBook.performClick()), 3000);
                        shouldRedirectToSpaceBooking = false;
                    }
                }
                else{
                    llBookingSection.setVisibility(View.GONE);
                }
            }
            else{
                llBookingSection.setVisibility(View.GONE);
            }
        }
    }

    private void navigateToSpaceBooking(Location autoSelectedLocation){
        Intent intent = new Intent(this, SpaceBookingActivity.class);
        intent.putExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE , config.space_management.getSpace_booking_in_order_detail());
        intent.putExtra(ApplicationConstants.SpaceBooking.AUTO_SELECT_LOCATION_ID, autoSelectedLocation.id);
        startActivity(intent);
    }

    private void navigateToLocationScreen(ArrayList<Location> linkedLocations){

        Intent intent = new Intent(this, LocationChangeActivity.class);
        intent.putExtra("bookingLocations", linkedLocations);
        startActivity(intent);
    }

    private void navigateForBooking(){
        String url = UrlConstant.LIST_LINKED_LOCATIONS + (isSpaceBookingOrder? "?location_id=": "?cafe_id=") + order.getLocationId() + (isSpaceBookingOrder? "": ("&type=" + config.space_management.getSpace_booking_in_order_detail()));

        SimpleHttpAgent<LinkedLocationsResponse> linkedLocationsSimpleHttpAgent = new SimpleHttpAgent<LinkedLocationsResponse>(
                this,
                url,
                responseObject -> {
                    if(responseObject != null && responseObject.getLinkedLocationsList().size() > 0){
                        if(isSpaceBookingOrder){
                            navigateToLocationScreen(responseObject.getLinkedLocationsList());
                        }
                        else{
                            navigateToSpaceBooking(responseObject.getLinkedLocationsList().get(0));
                        }
                    }
                    else{
                        if(isSpaceBookingOrder) {
                            AppUtils.showToast("Meal service is not available in this Space", true, 2);
                        }
                        else{
                            AppUtils.showToast("No locations available for " + spaceType.getSpace_name() + " booking", true, 2);
                        }
                    }
                },
                (errorCode, error, errorResponse) -> {
                    if(isSpaceBookingOrder) {
                        AppUtils.showToast("Error in Booking a meal", true, 2);
                    }
                    else{
                        AppUtils.showToast("Error in Booking a " + spaceType.getSpace_name(), true, 2);
                    }
                },
                LinkedLocationsResponse.class
        );
        linkedLocationsSimpleHttpAgent.get(getApiTag());
    }

    private void updateOrderPlacedCheckPoint(){
        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
            ivOrderPlacedTick.setImageResource(isSpaceBookingOrder? R.drawable.blue_tick : R.drawable.hollow_tick);
        }
        else if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)){
            ivOrderPlacedTick.setImageResource(R.drawable.hollow_tick);
        }
        else{
            ivOrderPlacedTick.setImageResource(R.drawable.blue_tick);
        }

        if(isSpaceBookingOrder){
            tvOrderPlaced.setText(order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)? "Approval Pending" : "Booking Confirmed");
            tvOrderPlacedText.setVisibility(View.GONE);
            tvVendorName.setVisibility(View.VISIBLE);
            if(order.getProducts().size() > 0){
                tvVendorName.setText(order.getProducts().get(0).getName());
            }
            ivOrderConfirmView.setVisibility(enterExitConfig.isEntryCafeActive()? View.VISIBLE : View.GONE);
        }
    }


    private boolean isEntryEnabled(){
        //preorder check
        if( ((order.getUserEntryTime() == -1) && (order.getUserExitTime() == -1)) || ((order.getUserEntryTime() != -1) && (order.getUserExitTime() != -1)))
            return true;
        else
            return false;
    }

    private void updateEntryCafeCheckPoint(){

        if(order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY) || !enterExitConfig.isEntryCafeActive()){
            clEntryCafeCheckPoint.setVisibility(View.GONE);
        }
        else{
            clEntryCafeCheckPoint.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.GONE);

            if(isSpaceBookingOrder && spaceType != null){
                tvEnterCafe.setText("Enter " + spaceType.getFacility_text());
            }

            if(enterExitConfig.isEntryCafeShowingQr()){
                ivEnterQr.setVisibility(View.VISIBLE);
                btMarkIn.setVisibility(View.GONE);
            }
            else{
                ivEnterQr.setVisibility(View.GONE);
                btMarkIn.setVisibility(View.VISIBLE);
            }

            if (AppUtils.isSocialDistancingActive(order.getLocation())){

                if(isSpaceBookingOrder){
                    if((order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)) && order.getPreOrderDeliveryTime() != 0){
                        if(order.getPreOrderDeliveryTime() * 1000 - DateTimeUtil.adjustCalender(getApplicationContext()).getTimeInMillis() >= 2 * 60 * 60 * 1000){
                            tvTimerText.setText("QR code available on");
                            tvTime.setVisibility(View.VISIBLE);
                            tvTime.setText(DateTimeUtil.getDateStringCustom(order.getPreOrderDeliveryTime() * 1000, "dd MMM yyyy, hh:mm a"));
                            tvTimer.setVisibility(View.GONE);
                        }
                        else{
                            tvTimerText.setText("QR code available in");
                            tvTimer.setVisibility(View.VISIBLE);
                            startTimer(order.getPreOrderDeliveryTime());
                        }
                    }
                    else{
                        activateEnterCafe();
                    }

                    ivEnterCafeView.setVisibility(enterExitConfig.isExitCafeActive()? View.VISIBLE : View.GONE);
                }
                else{
                    if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) && order.getPreOrderDeliveryTime() != 0){
                        startTimer(order.getPreOrderDeliveryTime());
                    }
                    else{
                        activateEnterCafe();
                    }
                }
            }
            else{
                activateEnterCafe();
            }
        }
    }

    void enableButton(Boolean forEntry){

        if(forEntry){

            tvButtonMarkIn.setVisibility(View.VISIBLE);
            tvWelcomeCafe.setVisibility(View.GONE);
            tvCafeNameEntry.setVisibility(View.GONE);

            if(isSpaceBookingOrder){
                tvButtonMarkIn.setText("You can enter the " + (spaceType != null? spaceType.getFacility_text() : " cafeteria") );
            }

            btMarkIn.setEnabled(true);
            btMarkIn.setBackgroundResource(R.drawable.button_collect_order_enabled);
            btMarkIn.setOnClickListener(v -> enterExitClickApiCall(true));
        }
        else{
            btMarkOut.setEnabled(true);
            btMarkOut.setBackgroundResource(R.drawable.button_collect_order_enabled);
            btMarkOut.setOnClickListener(v -> enterExitClickApiCall(false));
        }
    }

    void disableButton(Boolean forEntry){
        if(forEntry){
            tvButtonMarkIn.setVisibility(View.GONE);

            tvWelcomeCafe.setVisibility(View.VISIBLE);
            tvCafeNameEntry.setVisibility(View.VISIBLE);

            if(isSpaceBookingOrder){
                tvCafeNameEntry.setText("The " + (spaceType != null? spaceType.getFacility_text() : "Cafe"));
            }
            else{
                tvCafeNameEntry.setText(order.getLocation().getName());
            }

            btMarkIn.setEnabled(false);
            btMarkIn.setBackgroundResource(R.drawable.button_collect_order);
            btMarkIn.setOnClickListener(null);
        }
        else{
            btMarkOut.setEnabled(false);
            btMarkOut.setBackgroundResource(R.drawable.button_collect_order);
            btMarkOut.setOnClickListener(null);
        }
    }

    void enterExitClickApiCall(boolean forEntry){
        String url = UrlConstant.ENTER_EXIT_URL;

        String type = "";
        if(forEntry)
            type = "entry";
        else
            type = "exit";

        url += type;

        SimpleHttpAgent<FeedbackEta> paymentDetailsResponseSimpleHttpAgent = new SimpleHttpAgent<FeedbackEta>(
                this,
                url,
                responseObject -> {
                    if (responseObject != null && responseObject.getSuccess()) {
                        if(forEntry){
                            AppUtils.showToast("Marked In", true, 2);
                        }
                        else{
                            AppUtils.showToast("Marked Out", true, 2);
                        }
                        getOrderDetail();
                    }
                },
                (errorCode, error, errorResponse) -> Log.d(TAG, "handleError: Error submitting enter exit api call"),
                FeedbackEta.class
        );

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("order_id", order.getId());

        paymentDetailsResponseSimpleHttpAgent.post(payload, new HashMap<String, JsonSerializer>(), getApiTag());
    }

    private void enableQr(boolean forEntry){

        if(forEntry){
            tvScanQr.setVisibility(View.VISIBLE);
            isEntryQrEnabled = true;

            tvWelcomeCafe.setVisibility(View.GONE);
            tvCafeNameEntry.setVisibility(View.GONE);

//            tvWelcomeCafe.setVisibility(View.VISIBLE);
//            tvCafeNameEntry.setVisibility(View.VISIBLE);
//            tvCafeNameEntry.setText("The " + (spaceType != null ? spaceType.getFacility_text() : "Cafe"));

            if(isSpaceBookingOrder){
                tvScanQr.setText("Scan the QR now to enter the " + (spaceType != null ? spaceType.getFacility_text() : "cafeteria."));
            }

            tvEnterQrText.setVisibility(View.VISIBLE);
            tvEnterQrText.setOnClickListener(v -> viewQrClickListener(ivEnterQr,"enter_qr",true ));
            ivEnterQr.setVisibility(View.VISIBLE);
            ivEnterQr.setImageResource(R.drawable.qr_enabled);
            ivEnterQr.setOnClickListener(v -> viewQrClickListener(ivEnterQr, "enter_qr",true ));
        }
        else{
            tvExitQrText.setVisibility(View.VISIBLE);
            tvExitQrText.setOnClickListener(v -> viewQrClickListener(ivExitQr, "exit_qr",true ));
            ivExitQr.setVisibility(View.VISIBLE);
            ivExitQr.setImageResource(R.drawable.qr_enabled);
            ivExitQr.setOnClickListener(v -> viewQrClickListener(ivExitQr,"exit_qr",true ));
        }
    }

    private void disableQr(boolean forEntry){
        if(forEntry){

            tvScanQr.setVisibility(View.GONE);
            isEntryQrEnabled = false;

            tvWelcomeCafe.setVisibility(View.VISIBLE);
            tvCafeNameEntry.setVisibility(View.VISIBLE);

            if(isSpaceBookingOrder){
                tvCafeNameEntry.setText("The " + (spaceType != null ? spaceType.getFacility_text() : "Cafe"));
            }
            else{
                tvCafeNameEntry.setText(order.getLocation().getName());
            }

            tvEnterQrText.setVisibility(View.INVISIBLE);
            tvEnterQrText.setOnClickListener(null);
            ivEnterQr.setVisibility(View.VISIBLE);
            ivEnterQr.setImageResource(R.drawable.qr_disabled);
            ivEnterQr.setOnClickListener(null);
        }
        else{
            tvExitQrText.setVisibility(View.INVISIBLE);
            tvExitQrText.setOnClickListener(null);
            ivExitQr.setVisibility(View.VISIBLE);
            ivExitQr.setImageResource(R.drawable.qr_disabled);
            ivExitQr.setOnClickListener(null);
        }
    }

    private void activateEnterCafe(){

        ivEnterCafeTick.setImageResource(R.drawable.blue_tick);
        entryCheckpointActivated = true;

        tvTimer.setVisibility(View.GONE);
        tvTimerText.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);

        if(enterExitConfig.isEntryCafeShowingQr()){

            if(isEntryEnabled()){
                enableQr(true);
            }
            else{
                disableQr(true);
            }

        }
        else{
            if(isEntryEnabled()){
                enableButton(true);
            }
            else{
                disableButton(true);
            }

        }
    }

    private boolean checkForPickupUIActivation(){
        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.AGENT_PICKED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.ATTENTION_REQUIRED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_CONFIRMED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)  ){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean checkForOrderQrActivation(){
        if(AppUtils.isKitchenSystemEnabled(order)){
            if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.AGENT_PICKED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.ATTENTION_REQUIRED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_CONFIRMED)){
                return false;
            }
            else{
                return true;
            }
        }
        else{
            if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
                return false;
            }
            else{
                return true;
            }
        }
    }

    private void updatePickupCheckPoint(){

        if(order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY) || !enterExitConfig.isExitCafeActive()){
            pickupLine.setVisibility(View.GONE);
        }

        if(order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)){
            tvPickupOrder.setText("Order Pickup");
        }

        if(AppUtils.getConfig(this).isHide_qrcode(order.getLocation())){

            ivPickupQr.setVisibility(View.GONE);
            tvPickupQrText.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP;
            llPickupButton.setLayoutParams(params);

            btCollectOrder.setVisibility(View.VISIBLE);
        }
        else{
            ivPickupQr.setVisibility(View.VISIBLE);
            tvPickupQrText.setVisibility(View.VISIBLE);
            btCollectOrder.setVisibility(View.GONE);
        }



        if(checkForPickupUIActivation()){
            ivPickupCafeTick.setImageResource(R.drawable.blue_tick);
        }
        else{
            ivPickupCafeTick.setImageResource(R.drawable.hollow_tick);
        }

        if(checkForOrderQrActivation()){
            enableOrderQr();
        }
        else{
            disableOrderQr();
        }

        Spannable spanText = new SpannableString("Scan QR to collect Ready Items");
        spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvScanPickupQrText.setText(spanText);

        rvPickupItems.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rvPickupItems.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        updateExitCafeCheckPoint();

                        showAutoPopup();
                    }
                });

        refreshItemList();
    }

    private void showAutoPopup(){
        if(!isSpaceBookingOrder && checkForPickupUIActivation() && !(order.vendor.vendor.isSlotBookingVendor() && (order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)||order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)||order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)))){
            if(!isAutoPopupShown){
                viewQrClickListener(ivPickupQr,"", false);
            }
            scrollToView(clPickupCheckPoint);
        }
        else if(!isAutoPopupShown && order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING) && spaceType != null){
            ApprovalPendingDialog dialog = new ApprovalPendingDialog(spaceType.getApproval_pending_title(), spaceType.getApproval_pending_desc(), () -> {
            });
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(dialog, "approval_pending_dialog")
                    .commitAllowingStateLoss();
        }
        else if(isEntryQrEnabled){
            if(!isAutoPopupShown){
                viewQrClickListener(ivEnterQr, "enter_qr", false );
            }
            scrollToView(clEntryCafeCheckPoint);
        }

        isAutoPopupShown = true;
    }

    private void showPopUp() {
        errorHandleDialog = GenericPopUpFragment.newInstance("You will be able to collect the order \n only when the order is ready", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                errorHandleDialog.dismiss();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        errorHandleDialog.setCancelable(false);
        errorHandleDialog.show(getSupportFragmentManager(), "order processed error");
    }

    private void disableOrderQr(){
        if(AppUtils.getConfig(this).isHide_qrcode(order.getLocation())){

            btCollectOrder.setEnabled(false);
            btCollectOrder.setBackgroundResource(R.drawable.button_collect_order);
            btCollectOrder.setOnClickListener(null);

        }
        else{
            tvPickupQrText.setVisibility(View.GONE);

            ivPickupQr.setImageResource(R.drawable.qr_disabled);
            ivPickupQr.setOnClickListener(null);
            tvPickupQrText.setOnClickListener(null);
        }
    }

    private void enableOrderQr(){
        if(AppUtils.getConfig(this).isHide_qrcode(order.getLocation())){

            if(!AppUtils.isKitchenSystemEnabled(order) &&
                    (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.AGENT_PICKED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.ATTENTION_REQUIRED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED) ||
                    order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_CONFIRMED))){

                btCollectOrder.setEnabled(true);
                btCollectOrder.setBackgroundResource(R.drawable.button_collect_order_enabled);
                btCollectOrder.setOnClickListener(v -> showPopUp());
            }
            else{
                btCollectOrder.setEnabled(true);
                btCollectOrder.setBackgroundResource(R.drawable.button_collect_order_enabled);
                btCollectOrder.setOnClickListener(v -> viewQrClickListener(ivPickupQr,"",false ));
            }
        }
        else{

            ivPickupQr.setImageResource(R.drawable.qr_enabled);
            ivPickupQr.setOnClickListener(v -> viewQrClickListener(ivPickupQr,"",true ));
            tvPickupQrText.setOnClickListener(v -> viewQrClickListener(ivPickupQr, "",true ));
        }
    }

    private void updateExitCafeCheckPoint(){

        if(order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY) || !enterExitConfig.isExitCafeActive()){
            if(order.vendor.vendor.isSlotBookingVendor()) {
                ivEnterCafeView.setVisibility(View.GONE);
            }
            clExitCafeCheckPoint.setVisibility(View.GONE);
        }
        else{
            clExitCafeCheckPoint.setVisibility(View.VISIBLE);
            ivEnterCafeView.setVisibility(View.VISIBLE);

            if(isSpaceBookingOrder && spaceType != null){
                tvExitCafe.setText("Exit " + spaceType.getFacility_text());
            }

            if(enterExitConfig.isExitCafeShowingQr()){
                ivExitQr.setVisibility(View.VISIBLE);
                tvExitQrText.setVisibility(View.VISIBLE);
                btMarkOut.setVisibility(View.GONE);
                tvExitText.setText("Scan the QR to exit the " + ((isSpaceBookingOrder && spaceType != null)? spaceType.getFacility_text(): "cafeteria."));
            }
            else{
                ivExitQr.setVisibility(View.GONE);
                tvExitQrText.setVisibility(View.GONE);
                btMarkOut.setVisibility(View.VISIBLE);
                tvExitText.setText("You can exit the " + ((isSpaceBookingOrder && spaceType != null)? spaceType.getFacility_text(): "Cafeteria."));
            }

            if(enterExitConfig.isExitCafeShowingQr()){

                if(isExitEnabled()){
                    enableQr(false);
                }
                else{
                    disableQr(false);
                }

            }
            else{

                if(isExitEnabled()){
                    enableButton(false);
                }
                else{
                    disableButton(false);
                }

            }

            if(checkForPickupUIActivation()){
                ivExitCafeTick.setImageResource(R.drawable.blue_tick);
            }
        }
    }

    public boolean isExitEnabled(){
        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
            return false;
        }
        return !isEntryEnabled();
    }

    public void startTimer(long time){

        Calendar now = DateTimeUtil.adjustCalender(MainApplication.appContext);
        if(time * 1000 < now.getTimeInMillis()){
            tvTimer.setVisibility(View.GONE);
            tvTimerText.setVisibility(View.GONE);
            if(enterExitConfig.isEntryCafeShowingQr()){
                disableQr(true);
            }
            else{
                disableButton(true);
            }
            return;
        }
        Calendar estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);

        if (now.compareTo(estTime) < 0) {
            countDownTimer = new CountDownTimer(estTime.getTimeInMillis() - now.getTimeInMillis(), 1000) {

                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(DateTimeUtil.getTimeLeftCustom(time).toString());
                }

                public void onFinish() {
                    getOrderDetail();
                }
            }.start();
        }
    }

    private void scrollToView(final View view) {
        Point childOffset = new Point();
        getDeepChildOffset(svOrderDetailRoot, view.getParent(), view, childOffset);
        ObjectAnimator.ofInt(svOrderDetailRoot, "scrollY",  childOffset.y).setDuration(200).start();

    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    private void refreshItemList(){
        if(pickupItemAdapter == null){
            pickupItemAdapter = new PickupItemAdapter(this, order.getProducts());
            rvPickupItems.setAdapter(pickupItemAdapter);
        }
        else{
            pickupItemAdapter.changeProducts(order.getProducts());
        }
    }

    private void updateBillSection(){
        updateDetailedSection();
        updatePaymentSection();
    }

    private void updateDetailedSection(){
        updateOrderInfo();
        updateBillInfo();
        startUpdateTimer();
    }

    private void updateOrderInfo(){

        llOrderId.setVisibility(View.VISIBLE);
        tvOrderId.setText(order.getOrderId());

        if(order.getOrderPickType() != null && order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)){
            llOrderType.setVisibility(View.VISIBLE);
            tvOrderType.setText(order.getOrderPickType());
        }
        else{
            llOrderType.setVisibility(View.GONE);
        }

        if(order.getDeskReference() != null && !order.getDeskReference().trim().isEmpty()){
            llOrderAddress.setVisibility(View.VISIBLE);
            tvOrderAddressLabel.setText(AppUtils.getConfig(getApplicationContext()).getWorkstation_address());
            tvOrderAddress.setText(order.getDeskReference());
        }
        else{
            llOrderAddress.setVisibility(View.GONE);
        }

        String groupUsers = getGroupUsers();
        if(groupUsers.isEmpty()){
            llGroupUsers.setVisibility(View.GONE);
        }
        else{
            llGroupUsers.setVisibility(View.VISIBLE);
            tvGroupUsers.setText(groupUsers);
        }

        if (order.getProjectCode() != null && !order.getProjectCode().trim().isEmpty()){
            llProjectCode.setVisibility(View.VISIBLE);
            tvProjectCodeLabel.setText(AppUtils.getConfig(getApplicationContext()).getProjectcode_place_holder());
            tvProjectCode.setText(order.getProjectCode());
        }
        else{
            llProjectCode.setVisibility(View.GONE);
        }
    }

    private void updateBillInfo() {

        DbHandler.getDbHandler(this).createOrder(order);

        if(isSpaceBookingOrder){
            qtyTitle.setText("Slots");
            itemTitle.setText("Facility");
        }
        else{
            qtyTitle.setText("Qty.");
            itemTitle.setText("Item");
        }

        tvTotalOrderAmount.setText(String.format("\u20B9 %.2f", order.getPrice()));
        order.setOrderFromServer(true);

        if (order.getServiceTax() > 0) {
            llServiceTax.setVisibility(View.VISIBLE);
            tvServiceTax.setText(String.format("\u20B9 %.2f", order.getServiceTax()));
        }
        else
            llServiceTax.setVisibility(View.GONE);
        if (order.getTax() > 0) {
            llVat.setVisibility(View.VISIBLE);
            tvVat.setText(String.format("\u20B9 %.2f", order.getTax()));
        }
        else
            llVat.setVisibility(View.GONE);

        if (order.getAbsContainerCharge() > 0) {
            llContainerCharge.setVisibility(View.VISIBLE);
            tvContainerCharge.setText(String.format("\u20B9 %.2f", order.getAbsContainerCharge()));
        }
        else
            llContainerCharge.setVisibility(View.GONE);

        if (order.getDeliveryCharge() > 0) {
            llDeliveryCharge.setVisibility(View.VISIBLE);
            tvDeliveryCharge.setText(String.format("\u20B9 %.2f",order.getDeliveryCharge()));
        }
        else
            llDeliveryCharge.setVisibility(View.GONE);

        if (order.getConvenienceFee() > 0) {
            llConvinenceCharge.setVisibility(View.VISIBLE);
            tvConvinenceCharge.setText(String.format("\u20B9 %.2f", order.getConvenienceFee()));
        }
        else
            llConvinenceCharge.setVisibility(View.GONE);

        if (order.getCgst() > 0) {
            llCgst.setVisibility(View.VISIBLE);
            tvCgst.setText(String.format("\u20B9 %.2f", order.getCgst()));
        }
        else
            llCgst.setVisibility(View.GONE);

        if (order.getSgst() > 0) {
            llSgst.setVisibility(View.VISIBLE);
            tvSgst.setText(String.format("\u20B9 %.2f", order.getSgst()));
        }
        else {
            llSgst.setVisibility(View.GONE);
        }
        if (!order.getVendor().getVendor().isRestaurant() && AppUtils.getConfig(this).isShow_gst() && order.getVendor().getVendor().getCustomerGst()>0){
            llCgst.setVisibility(View.VISIBLE);
            llSgst.setVisibility(View.VISIBLE);
            double customerGst = order.getVendor().getVendor().getCustomerGst();
            double orderAmount;
            if (order.getConvenienceFee()>0) {
                orderAmount = order.getPrice() - order.getConvenienceFee();
            }
            else {
                orderAmount = order.getPrice();
            }
            tvSgst.setText(String.format("\u20B9 %.2f", (orderAmount*customerGst)/(2.0*(100+customerGst))));
            tvCgst.setText(String.format("\u20B9 %.2f", (orderAmount*customerGst)/(2.0*(100+customerGst))));
        }

        if (order.getPrice() > 0) {
            llOrderAmountContainer.setVisibility(View.VISIBLE);
            if (!order.getVendor().getVendor().isRestaurant() && AppUtils.getConfig(this).isShow_gst()&& order.getVendor().getVendor().getCustomerGst()>0){
                double customerGst = order.getVendor().getVendor().getCustomerGst();
                double orderAmount;
                if (order.getConvenienceFee()>0) {
                    orderAmount = order.getPrice() - order.getConvenienceFee();
                }
                else {
                    orderAmount = order.getPrice();
                }
                double roundedPrice = Double.parseDouble(String.format("%.2f",((orderAmount*customerGst)/(2.0*(100+customerGst)))));
                tvOrderAmount.setText(String.format("\u20B9 %.2f", orderAmount-roundedPrice-roundedPrice));
            }
            else {
                tvOrderAmount.setText(String.format("\u20B9 %.2f", order.getCalculateOrderProductPrice()));
            }
        } else {
            llOrderAmountContainer.setVisibility(View.GONE);
        }

        updateProductList();
    }

    private void checkForOrderCancellation(){

        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_CONFIRMED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_PROCESSED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PARTIALLY_DELIVERED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.ATTENTION_REQUIRED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.AGENT_PICKED)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)
                || order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING)) {

            if (AppUtils.getConfig(this).getDirect_cancellation() != null && !AppUtils.getConfig(this).getDirect_cancellation().isEmpty()) {

                orderCancellation.setVisibility(View.VISIBLE);

                orderCancellation.setOnClickListener(v -> {

                    GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(
                            "Are you sure do you want to cancel the order ?",
                            "Confirm",
                            "Cancel",
                            new GenericPopUpFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    startOrderCancellation();
                                }

                                @Override
                                public void onNegativeInteraction() {

                                }
                            }
                    );

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(genericPopUpFragment, "cancel_confirmation")
                            .commitAllowingStateLoss();
                });
            }
            else {
                orderCancellation.setVisibility(View.GONE);
            }
        }
        else{
            orderCancellation.setVisibility(View.GONE);
        }
    }

    private void UpdateFinalStateUI(){

        cvOrderStatus.setVisibility(View.VISIBLE);

        clOrderConfirmedCheckPoint.setVisibility(View.GONE);
        clEntryCafeCheckPoint.setVisibility(View.GONE);
        clPickupCheckPoint.setVisibility(View.GONE);
        clExitCafeCheckPoint.setVisibility(View.GONE);
        ivEnterCafeView.setVisibility(View.GONE);
        llBookingSection.setVisibility(View.GONE);

        svOrderDetailRoot.smoothScrollTo(0,0);

        activateOrderStatusCard();

        if(!AppUtils.isSocialDistancingActive(order.getLocation()) && order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
            updateBookingSection();
        }
    }

    private void activateOrderStatusCard(){
        deliveryTime.setVisibility(View.GONE);
        if(OrderUtil.getOrderStatusImageIdForOrderDetail(order.getOrderStatus()) != 0) {

            ivStatus.setImageResource(OrderUtil.getOrderStatusImageIdForOrderDetail(order.getOrderStatus()));

            switch (order.getOrderStatus()){
                case OrderUtil.DELIVERED:
                    tvOrderStatus.setText("Delivered");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case OrderUtil.HANDED_OVER:
                    tvOrderStatus.setText("Delivered");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case OrderUtil.CANCELLED:
                    tvOrderStatus.setText("Cancelled");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.REJECTED:
                    tvOrderStatus.setText("Cancelled");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.NOT_COLLECTED:
                    tvOrderStatus.setText("Not Collected");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.not_collected_color));
                    break;
                case OrderUtil.PRE_ORDER:
                    tvOrderStatus.setText("Pre-Order");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                    break;
                case OrderUtil.PAYMENT_FAILED:
                    tvOrderStatus.setText("Payment Failed");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.PAYMENT_PENDING:
                    tvOrderStatus.setText("Payment Pending");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.FULFILLED:
                    tvOrderStatus.setText("Fulfilled");
                    tvOrderStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                    break;
                default:
                    cvOrderStatus.setVisibility(View.GONE);
            }
        }
        else {
            ivStatus.setVisibility(View.GONE);
        }


        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)|| order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)) {

            String rejectReason = order.getRejectMessage();
            if(rejectReason != null){
                String message = "Your order has been cancelled";

                if (rejectReason.length() > 0) {
                    message += "\n(" + rejectReason + ")";
                }

                tvOrderStatusContent.setText(message);
            }
            else{
                tvOrderStatusContent.setVisibility(View.GONE);
            }
        }
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            tvOrderStatusContent.setText("Your order will be ready on");
            deliveryTime.setVisibility(View.VISIBLE);
            deliveryTime.setText(DateTimeUtil.getDateStringCustom(order.getPreOrderDeliveryTime() * 1000, "dd MMM yyyy|hh:mm aa").replace("|", ", at "));
        }
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)){
            tvOrderStatusContent.setText("Your order has been collected");
        }
        else if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER)){
            tvOrderStatusContent.setText("Your order has been handed Over");
        }
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)) {
            tvOrderStatusContent.setText("Your order was not collected");
        }
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED)) {
            tvOrderStatusContent.setText("Your "+ (isSpaceBookingOrder ? "booking" : "order")+" is fulfilled.");
        }
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)) {
            tvOrderStatusContent.setText("Your Payment is pending.");
        }
        else{
            tvOrderStatusContent.setVisibility(View.GONE);
        }

    }

    private String getGroupUsers(){
        if(order!=null && order.groupUser!=null && order.groupUser.getUsers()!=null&& order.groupUser.getUsers().size()>0){

            StringBuilder groupUser = new StringBuilder("Group : ");

            for(int i = 0; i < order.groupUser.getUsers().size(); i++){
                if(i > 0){
                    groupUser.append(", ");
                }
                groupUser.append(order.groupUser.getUsers().get(i).getName());
            }
            return groupUser.toString();

        }
        else{
            return "";
        }
    }

    private void navigateBack() {
        if (isNewOrder) {
            if (AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout()) {
                AppUtils.doLogout(this);
            }
            else {
                Intent intent = AppUtils.getHomeNavigationIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        finish();
    }

    private void logoutAfterTimeout() {
        if (isNewOrder && AppUtils.isCafeApp() && AppUtils.getConfig(this).isAuto_logout()) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.doLogout(OrderDetailNewActivity.this);

                        }
                    });

                }
            }, 10000);
        }

    }

    private void startOrderCancellation(){

        String cancellationEligibilityUrl = UrlConstant.ORDER_CANCELLATION_ELIGIBILITY + "?orderRef=" + order.getOrderId() + "&user_id="+ SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0);

        SimpleHttpAgent<OrderCancellationEligibilityResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                cancellationEligibilityUrl,
                responseObject -> {
                    if(responseObject != null){

                        if(responseObject.isDirectCancellable()){
                            callOrderCancellation(order.getOrderId(), "");
                        }
                        else if(responseObject.askForReason()){
                            askForReasonThenCancelOrder(order.getOrderId());
                        }
                        else{
                            showErrorInCancellationDialog(responseObject.getCancellationMessage());
                        }

                    }
                    else{
                        showErrorInCancellationDialog(getString(R.string.try_again));
                    }
                },
                (errorCode, error, errorResponse) -> {

                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        showErrorInCancellationDialog( "Sorry, there is some connectivity issue, Please try again");
                    }
                    else if (errorResponse != null && errorResponse.message != null) {
                        showErrorInCancellationDialog(errorResponse.message);
                    }
                    else {
                        showErrorInCancellationDialog(getString(R.string.try_again));
                    }

                },
                OrderCancellationEligibilityResponse.class
        );

        objectSimpleHttpAgent.get(getApiTag());
    }

    private void askForReasonThenCancelOrder(String orderRef){

        OrderCancellationReasonDialog orderCancellationReasonDialog = OrderCancellationReasonDialog.newInstance(reason -> callOrderCancellation(orderRef, reason));

        orderCancellationReasonDialog.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(orderCancellationReasonDialog, "order_cancellation_reason")
                .commitAllowingStateLoss();
    }

    private  void callOrderCancellation(String orderRef, String reason){

        SimpleHttpAgent<OrderCancellationResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                UrlConstant.ORDER_CANCELLATION,
                responseObject -> {
                    if(responseObject != null) {
                        SharedPrefUtil.putBoolean(ApplicationConstants.SHOULD_REFRESH_FROM_CHAT, true);
                        AppUtils.showToast(responseObject.getMessage(), true, 0);
                        getOrderDetail();
                    }
                    else{
                        showErrorInCancellationDialog(getString(R.string.try_again));
                    }
                },
                (errorCode, error, errorResponse) -> {

                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        showErrorInCancellationDialog( "Sorry, there is some connectivity issue, Please try again");
                    }
                    else if (errorResponse != null && errorResponse.message != null) {
                        showErrorInCancellationDialog(errorResponse.message);
                    }
                    else {
                        showErrorInCancellationDialog(getString(R.string.try_again));
                    }

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

        objectSimpleHttpAgent.post(orderCancellationInfo.toString(), getApiTag());
    }

    private void showErrorInCancellationDialog(String errorMessage){
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(errorMessage, "Ok", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {

            }

            @Override
            public void onNegativeInteraction() {

            }
        });

        genericPopUpFragment.show(this.getSupportFragmentManager(), "order_cancellation_dialog");
    }

    private void updateProductList() {

        ArrayList<OrderProduct> products = order.getProducts();

        if(detailedBillItemAdapter == null){
            detailedBillItemAdapter = new DetailedBillItemAdapter(this, products);
            rvProductList.setAdapter(detailedBillItemAdapter);
        }
        else{
            detailedBillItemAdapter.changeProducts(products);
        }
    }

    private void updatePaymentSection(){

        ArrayList<OrderWallet> wallets = order.getWallets().getOrderWallets();
        if (wallets.size() == 0) {
            tvOrderWalletListHeader.setText("No Payment Method Available");
        }
        else {
            tvOrderWalletListHeader.setText("Payment ");
        }

        if (rlvOrderWalletList.getAdapter() == null) {
            OrderWalletListAdapter orderWalletListAdapter = new OrderWalletListAdapter(this, wallets);
            rlvOrderWalletList.setAdapter(orderWalletListAdapter);
        }
        else {
            OrderWalletListAdapter orderWalletListAdapter = (OrderWalletListAdapter) rlvOrderWalletList.getAdapter();
            orderWalletListAdapter.updateWallets(wallets);
            orderWalletListAdapter.notifyDataSetChanged();
        }
    }

    private void viewQrClickListener(ImageView sourceQr, String designFor, Boolean showAnimation){
        if (order != null && isRunning) {

            Intent intent = new Intent(OrderDetailNewActivity.this, OrderQrActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("order", order);
            if(!designFor.isEmpty()){
                intent.putExtra("for", designFor);
            }
            if(isSpaceBookingOrder && spaceType != null){
                intent.putExtra(ApplicationConstants.QR_ENTRY_TEXT, spaceType.getQr_code_entry_text());
                intent.putExtra(ApplicationConstants.QR_EXIT_TEXT, spaceType.getQr_code_exit_text());
            }

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(OrderDetailNewActivity.this, sourceQr, "qr_code");

            if (Build.VERSION.SDK_INT > 24) {

                if(showAnimation){
                    startActivity(intent, options.toBundle());
                }
                else{
                    startActivity(intent);
                }

            }
            else {
                startActivity(intent);
            }
        }
    }

    public void endTimer(){
        try{
            if (countDownTimer != null){
                countDownTimer.cancel();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        endTimer();
    }

    @Override
    public void finish() {
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
            handler = null;
            endTimer();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.finish();
    }


    @Override
    protected void onDestroy() {

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        if(autoRedirectHandler != null){
            autoRedirectHandler.removeCallbacksAndMessages(null);
        }
        handler = null;
        endTimer();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isRunning = true;

        try {

            refreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                                    .getDisplayMetrics()));

            refreshLayout.setRefreshing(true);
            getOrderDetail();

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isRunning = false;
    }

    private void startUpdateTimer() {

        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }

        try {

            long totalTime = System.currentTimeMillis() - activityStartTime;
            if (AppUtils.getConfig(this).getOrder_detail_refresh_time() != 0 && totalTime < 30 * 60 * 1000) {
                handler = new Handler();
                handler.postDelayed(runnable, AppUtils.getConfig(this).getOrder_detail_refresh_time());
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
