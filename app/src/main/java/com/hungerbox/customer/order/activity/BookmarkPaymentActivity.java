package com.hungerbox.customer.order.activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hungerbox.customer.BuildConfig;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.event.RemoveProductFromCart;
import com.hungerbox.customer.model.BookingGuest;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.DeskReferenceSetting;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Eta;
import com.hungerbox.customer.model.ListWallet;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderGuestInfo;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.OrderSelectedTime;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.PaymentStatus;
import com.hungerbox.customer.model.PostPaidResponse;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.ProjectCodeSetting;
import com.hungerbox.customer.model.TrendingMenuItem;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserAddRequest;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.VendorProduct;
import com.hungerbox.customer.model.WalletBreakupItem;
import com.hungerbox.customer.navmenu.UrlNavigationHandler;
import com.hungerbox.customer.navmenu.activity.SuccesFailActivity;
import com.hungerbox.customer.navmenu.fragment.WalletBreakupFragment;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.GuestListQualcommAdapter;
import com.hungerbox.customer.order.adapter.OrderReviewAdapter;
import com.hungerbox.customer.order.adapter.TrendingAdapter;
import com.hungerbox.customer.order.fragment.ExitFragment;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.order.fragment.GeneralDialogFragment;
import com.hungerbox.customer.order.fragment.GuestNamesFragment;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.CancelListener;
import com.hungerbox.customer.order.listeners.GuestRemoveClickListener;
import com.hungerbox.customer.order.listeners.MorePaymentOptionListener;
import com.hungerbox.customer.order.listeners.OnLoaderListener;
import com.hungerbox.customer.order.listeners.OnPaymentSelectLisntener;
import com.hungerbox.customer.order.listeners.OrderReviewProductRefreshListener;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.order.listeners.ViewDetailBillListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.service.NotificationService;
import com.hungerbox.customer.spaceBooking.adapter.SpaceGuestListCartAdapter;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.OfflineDuesDialog;
import com.hungerbox.customer.util.view.ViewDetailBillFragment;
import com.squareup.otto.Subscribe;
import com.takusemba.spotlight.Spotlight;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by ranjeet on 23,December,2018
 */
public class BookmarkPaymentActivity extends ParentActivity implements OrderReviewProductRefreshListener, OnPaymentSelectLisntener, MorePaymentOptionListener, OnLoaderListener, ViewDetailBillListener {

    public final int PAYMENT_SCREEN_REQUEST_CODE = 2064 ;
    public final static int PAYMENT_SCREEN_CODE = 5000;
    LinearLayout llDeliveryContainer, llContainerContainer, llOrderDetailConatiner, llVat, llServiceTax, llCgst, llSgst, llLocationDensityContainer, llConvenienceFee,llViewBill;

    private TextView preOrderTime, tvVendorName, tvTotal, tvTitle, tvOrderTime, tvOrderTotal, tvDeliveryCharge, tvContainerCharge, tvVat, tvServiceTax, tvCgst, tvSgst, tvTotalHeader, tvConvenienceFee,tvViewDetailBill,btGrandTotal;
    private double userExternalWalletCharge;
    private double internalWalletPayableAmount;

    private RelativeLayout rlDetailsPayment, rlContainer,rlCartEmpty,rlSpaceCheckbox;
    private ArrayList<Object> recommendedProducts = new ArrayList<>();
    private ArrayList<Object> updatedRecommendList = new ArrayList<>();
    private long locationId, vendorStartTime,spaceLocationId;
    private ProgressBar progressBar, pbPay;
    private OrderReviewAdapter orderReviewAdapter;
    private LinearLayout llRecommend,llbt;
    private ExitFragment exitFragment;
    private FrameLayout rlBaseContainer;
    private Ocassion orderOcassion;
    private RecyclerView rvOrderList, rvReccomendedItems,rvSpaceGuestList;
    private ArrayList orderInfoList;
    private Vendor selectedOrderVendor;
    TreeSet<User> users = new TreeSet<>();
    private Order order;
    private Realm realm;
    Calendar orderSelectedTime;
    TimePickerDialog timePicker;
    boolean isRunning = false;
    private Cart cart;
    MainApplication mainApplication;
    public Button btPay,btHome;
    User user;
    private TextView tvClearCart;
    private TrendingAdapter trendingListAdapter;
    public Fragment fragment;
    private String fragmentTag = "payment";
    private boolean initated = false;
    private ShimmerFrameLayout shLoading;
    private boolean fromBookmark = false;
    private boolean fromReorder = false;
    public boolean fromSpaceBooking,fromSpaceBookingReorder;
    public EditText tetComment;
    public TextInputLayout til_comments;
    public TextInputEditText deskReferenceEt, projectCodeEt;
    public TextInputLayout deskRefInputLayout,projectCodeInputLayout;
    public LinearLayout llDeskOptions;
    public CheckBox cbSelfPickup, cbDeliverToSeat,cbSpaceYes,cbSpaceNo;
    private Context context;
    private TextView tvLocationName,tvSpaceLocation,tvSpaceDate,tvSpaceTime,tvSpaceGuestTitle,tvSpaceName;
    private RelativeLayout groupOrdCardView;
    private TextView tvEmpCount;
    private AlertDialog dialog;
    public boolean isTimePickerShown = false;
    GuestListQualcommAdapter guestListQualcommAdapter;
    private  View.OnClickListener totalPriceClickListener;
    public OfflineDuesDialog duesDialog;
    public Order prevOrder;
    double otherUserWallet = 0;
    private boolean isPccEnabled = false;
    FreeOrderErrorHandleDialog orderErrorHandleDialog = null;
    private TextInputLayout txtInputLayout;
    private ConstraintLayout entryTimingLayout;
    private AppCompatImageView iText,ivEntryTick;
    private TextView tvEntryText,tvEntryTiming, tvWhyText;
    private long occasionId;
    private long etaTime;
    public TextView tvWallet;
    private boolean rechargeAllowed = false;
    private ImageView ivBottomOfTopCard,ivShoppingCart;
    private ConstraintLayout clSpaceGuestLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_bookmark_payment_activity);
        restoreFromSavedInstance(savedInstanceState);

        setApiTag(String.valueOf(System.currentTimeMillis()));

        Realm.init(getApplicationContext());
        context = this;
        rlContainer = findViewById(R.id.rl_container);
        rvReccomendedItems = findViewById(R.id.rl_reccomended_items);
        rlBaseContainer = findViewById(R.id.rl_base_container);
        rvOrderList = findViewById(R.id.rv_order_review);
        progressBar = findViewById(R.id.pb_order_review);
        tvVendorName = findViewById(R.id.tv_vendor_name);
        llRecommend = findViewById(R.id.ll_reccomended);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTotal = findViewById(R.id.tv_total_price);
        pbPay = findViewById(R.id.pb_place_order);
        btPay = findViewById(R.id.bt_pay);
        llbt = findViewById(R.id.ll_bt);

        shLoading = findViewById(R.id.shimmer_view_container);
        tvOrderTotal = findViewById(R.id.tv_total_order);
        tvDeliveryCharge = findViewById(R.id.tv_delivery);
        tvContainerCharge = findViewById(R.id.tv_container);
        tvVat = findViewById(R.id.tv_vat);
        tvCgst = findViewById(R.id.tv_cgst);
        tvSgst = findViewById(R.id.tv_sgst);
        tvServiceTax = findViewById(R.id.tv_st);
        tvConvenienceFee = findViewById(R.id.tv_convenience);
        deskReferenceEt = findViewById(R.id.et_desk_reference);
        deskRefInputLayout = findViewById(R.id.txtInputLayout);
        llDeskOptions = findViewById(R.id.ll_desk_options);
        cbSelfPickup = findViewById(R.id.cb_self_pickup);
        cbDeliverToSeat = findViewById(R.id.cb_deliver_to_seat);
        projectCodeEt = findViewById(R.id.et_project_code);
        projectCodeInputLayout = findViewById(R.id.til_project_code);
        tvLocationName = findViewById(R.id.tv_order_location_name);
        groupOrdCardView = findViewById(R.id.rl_group_ordering);
        tvViewDetailBill = findViewById(R.id.tv_view_detail_bill);
        llViewBill = findViewById(R.id.ll_view_bill);
        btGrandTotal = findViewById(R.id.bt_grand_total);
        ivBottomOfTopCard = findViewById(R.id.top_card_bottom_img);
        ivShoppingCart = findViewById(R.id.iv_shopping_cart);

        rlDetailsPayment = findViewById(R.id.rl_details_payment);
        llDeliveryContainer = findViewById(R.id.ll_delivery_container);
        llOrderDetailConatiner = findViewById(R.id.ll_order_details_container);
        llContainerContainer = findViewById(R.id.ll_container_charge_container);
        llVat = findViewById(R.id.ll_vat_container);
        llServiceTax = findViewById(R.id.ll_st_container);
        llConvenienceFee = findViewById(R.id.ll_convenience_container);
        llCgst = findViewById(R.id.ll_cgst_container);
        llSgst = findViewById(R.id.ll_sgst_container);
        preOrderTime = findViewById(R.id.preorderTime);
        tetComment = findViewById(R.id.tet_comments);
        til_comments = findViewById(R.id.til_comments);
        tvEmpCount = findViewById(R.id.tv_emp_count);
        rlCartEmpty = findViewById(R.id.rl_cart_empty);
        btHome = findViewById(R.id.bt_home);
        txtInputLayout = findViewById(R.id.txtInputLayout);
        txtInputLayout.setHint(AppUtils.getConfig(getApplicationContext()).getWorkstation_address());

        entryTimingLayout = findViewById(R.id.cl_entry_timing);
        iText = findViewById(R.id.iv_info);
        ivEntryTick = findViewById(R.id.iv_entry_allowed);
        tvEntryText = findViewById(R.id.tv_entry_text);
        tvEntryTiming = findViewById(R.id.tv_entry_time);
        tvWhyText = findViewById(R.id.tv_why);

        ImageView ivBack = findViewById(R.id.iv_back);
        tvWallet = findViewById(R.id.tv_wallet);

        tvClearCart = findViewById(R.id.tv_clear_cart);
        prevOrder = (Order) getIntent().getSerializableExtra("PrevOrder");
        fromBookmark = getIntent().hasExtra(ApplicationConstants.FROM_BOOKMARK);
        fromReorder = getIntent().hasExtra(ApplicationConstants.FROM_REORDER);
        fromSpaceBooking = getIntent().hasExtra(ApplicationConstants.FROM_SPACE_BOOKING);
        fromSpaceBookingReorder = getIntent().hasExtra(ApplicationConstants.FROM_SPACE_BOOKING_REORDER);
        spaceLocationId = getIntent().getLongExtra(ApplicationConstants.SPACE_LOCATION_ID,0);

        rlSpaceCheckbox = findViewById(R.id.rl_space_checkbox);
        cbSpaceYes = findViewById(R.id.cb_space_yes);
        cbSpaceNo = findViewById(R.id.cb_space_no);
        clSpaceGuestLayout = findViewById(R.id.cl_space_guest_layout);
        tvSpaceLocation = findViewById(R.id.tv_space_location);
        tvSpaceDate = findViewById(R.id.tv_space_date);
        tvSpaceTime = findViewById(R.id.tv_space_time);
        tvSpaceGuestTitle = findViewById(R.id.tv_space_guest_title);
        rvSpaceGuestList = findViewById(R.id.rv_space_guest_list);
        tvSpaceName = findViewById(R.id.tv_space_name);

        ivShoppingCart.setVisibility(View.VISIBLE);
        tvClearCart.setVisibility(View.VISIBLE);

        rlContainer.setVisibility(View.GONE);

        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        isRunning = true;

        rvReccomendedItems.setLayoutManager(new LinearLayoutManager(this));
        mainApplication = (MainApplication) getApplication();

        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });
        int cartQty = mainApplication.getTotalOrderCount();
        if(cartQty<=0){
            rlCartEmpty.setVisibility(View.VISIBLE);
            return;
        }else{
            cart = mainApplication.getCart();
            occasionId = cart.getOccasionId();
            guestOrderCheck(cart);
            CreateOrderListTask createOrderListTask = new CreateOrderListTask();
            createOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }



        totalPriceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePaymentHeader();
            }
        };

        tvLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLocationDialog();
            }
        });

        cbSelfPickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    deskRefInputLayout.setVisibility(View.GONE);
                    deskReferenceEt.setVisibility(View.GONE);
                    cbDeliverToSeat.setChecked(false);
                    showHideTimeLayout(true);
                }
                else{
                    deskRefInputLayout.setVisibility(View.VISIBLE);
                    deskReferenceEt.setVisibility(View.VISIBLE);
                    cbDeliverToSeat.setChecked(true);
                }
            }
        });

        cbDeliverToSeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked){
                    showHideTimeLayout(false);
                    deskRefInputLayout.setVisibility(View.VISIBLE);
                    deskReferenceEt.setVisibility(View.VISIBLE);
                    cbSelfPickup.setChecked(false);
                }
                else{
                    deskRefInputLayout.setVisibility(View.GONE);
                    deskReferenceEt.setVisibility(View.GONE);
                    cbSelfPickup.setChecked(true);
                }
            }
        });

        cbDeliverToSeat.setChecked(AppUtils.getConfig(this.getApplicationContext()).getDefault_delivery_option() == 1);

        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EventUtil.FbEventLog(BookmarkPaymentActivity.this, EventUtil.CART_PLACE_ORDER_CLICK, EventUtil.SCREEN_CART);
                HBMixpanel.getInstance().addEvent(BookmarkPaymentActivity.this, EventUtil.MixpanelEvent.CART_PLACE_ORDER_CLICK);
                if (selectedOrderVendor != null && order.getTotalPrice() < selectedOrderVendor.getMinimumOrderAmount()){
                    ErrorDialog(null,"Please order for minimum amount of ₹" + selectedOrderVendor.getMinimumOrderAmount(),false);
                }
                else if (guestOrderCheck(cart) && cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_OFFICIAL)) {
                    goToPayment();

                } else if (AppUtils.getConfig(BookmarkPaymentActivity.this).isNo_paid_orders() && order.getPrice() > 0) {
                    AppUtils.showToast("Paid items are not Allowed for your Company", true, 2);
                } else {
                    goToPayment();
                }
            }
        });

        tvClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackPressDialog();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTitle.setText("Cart");

        tvTotal.setOnClickListener(totalPriceClickListener);

        tvLocationName.setText(SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, ""));

        //Group ordering
        if (cart.getVendor()==null){
            Intent intent = AppUtils.getHomeNavigationIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if(cart.getVendor().isRestaurant() && AppUtils.getConfig(context).getGroup_ordering() != null && AppUtils.getConfig(context).getGroup_ordering().isEnable()){
            groupOrdCardView.setVisibility(View.VISIBLE);
            ((TextInputLayout)findViewById(R.id.group_order_til)).setHint(AppUtils.getConfig(context).getGroup_ordering().getTitle());
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
            groupOrdCardView.setVisibility(View.GONE);
        }

        //location change button
        //AppUtils.showToast("Is location fixed : "+AppUtils.getConfig(this).is_location_fixed(),true,1);

        if(AppUtils.getConfig(this).is_location_fixed())
        {
            tvLocationName.setEnabled(false);
        }else{
            tvLocationName.setEnabled(true);
        }
        //if group ordering enabled or desk reference enabled, then hide recommended items
//        if ((groupOrdCardView.getVisibility()==View.VISIBLE) ||(deskRefInputLayout.getVisibility()== View.VISIBLE)){
//            llRecommend.setVisibility(View.GONE);
//        }
        if (!AppUtils.getConfig(this).isReview_comment() || cart.getVendor().isVendingMachine()) {
            til_comments.setVisibility(View.GONE);
        } else{
            til_comments.setVisibility(View.VISIBLE);
        }
        tetComment.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (tetComment.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        tetComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AppUtils.hideKeyboard(BookmarkPaymentActivity.this, ((TextInputEditText)findViewById(R.id.tet_comments)));
                }
            }
        });

        deskReferenceEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    AppUtils.hideKeyboard(BookmarkPaymentActivity.this, ((TextInputEditText)findViewById(R.id.et_desk_reference)));
                }
            }
        });

        tvEmpCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGuestList();
            }
        });
        llViewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvViewDetailBill.getVisibility()==View.VISIBLE) {
                    viewDetailBillSelected();
                }
            }
        });
//        tvViewDetailBill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                viewDetailBillSelected();
//            }
//        });

        updatePccVariable(); //match project code occasion with cart occasion

        showHidePcField();

        adjustSocialDistancingFeature();

        adjustTableCheckFeature();

        if(fromSpaceBooking){
            adjustSpaceBookingFeature();
        }

        ivBottomOfTopCard.setVisibility(shouldTopCardVisible()? View.VISIBLE: View.GONE);

    }

    private boolean shouldTopCardVisible(){
        return llDeskOptions.getVisibility() == View.VISIBLE || txtInputLayout.getVisibility() == View.VISIBLE || entryTimingLayout.getVisibility() == View.VISIBLE || iText.getVisibility() == View.VISIBLE || tvWhyText.getVisibility() == View.VISIBLE || projectCodeInputLayout.getVisibility() == View.VISIBLE;
    }

    //below methods showHidePcField(),isProjectCodeFieldEmpty(),updatePccVariable()
    // and updatePccVariable() is related to Project Code feature for ZS


    public void affilationPopUp(){
        String title = "Please add the affiliations of your Guests to place Guest Order";
        GuestNamesFragment guestNamesFragment = GuestNamesFragment.newInstance(title, "Submit", mainApplication.getGuestItemCount(), new GuestNamesFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(ArrayList guestList) {
                orderInfoList = guestList;
                order.orderGuestInfo = orderInfoList;
                order.setGuestType(cart.getGuestType());
//                                    btPay.setEnabled(false);
                //TODO take user to payment
                //validateAndSubmit();
                redirectToPayment(false);
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        guestNamesFragment.show(getSupportFragmentManager(), "Guest Names");
    }

    private void showHidePcField(){
        if (isPccEnabled && cartContainsFreeProducts()){
            projectCodeInputLayout.setVisibility(View.VISIBLE);
            if(AppUtils.getConfig(this).getProjectcode_place_holder()!=null){
                projectCodeInputLayout.setHint(AppUtils.getConfig(this).getProjectcode_place_holder());
            }

        } else{
            projectCodeInputLayout.setVisibility(View.GONE);
        }
    }
    public boolean isProjectCodeEnabled(){
        if ((projectCodeInputLayout.getVisibility() == View.VISIBLE) &&(projectCodeEt.getVisibility()==View.VISIBLE)){
            return true;
        } else{
            return false;
        }
    }
    public boolean isProjectCodeFieldEmpty(){
        if ((projectCodeInputLayout.getVisibility() == View.VISIBLE) &&(projectCodeEt.getText().toString().isEmpty())){
            return true;
        } else{
            return false;
        }
    }


    private boolean cartContainsFreeProducts(){
        if (cart!=null) {
            for (OrderProduct orderProduct : cart.getOrderProducts()){
                if (orderProduct.isFree()){
                    return true;
                }
            }
        }
        return false;
    }

    private void updatePccVariable(){
        if(fromSpaceBooking)
            return;

        if (AppUtils.getConfig(this).getProject_code_array() != null && AppUtils.getConfig(this).getProject_code_array().size()>0){
            if (cart!=null){
                if(AppUtils.getConfig(this).getProject_code_array().contains(cart.getOccasionId())) {
                    isPccEnabled = true;
                }
            }
        }
    }

    private void checkForOfflineDues() {
        String url = UrlConstant.CHECK_POSTPAID_DUES;
        SimpleHttpAgent<PostPaidResponse> postPaidResponseSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<PostPaidResponse>() {
                    @Override
                    public void response(PostPaidResponse responseObject) {
                        try {
                            if (responseObject.getStatus().equalsIgnoreCase("due")) {
                                showDialogForDues(responseObject);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
                , new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                AppUtils.showToast(error,false,0);

            }
        },PostPaidResponse.class);

        postPaidResponseSimpleHttpAgent.get(getApiTag());

    }

    private void showDialogForDues(PostPaidResponse response){
        if (duesDialog!=null && duesDialog.isVisible()){
            duesDialog.dismiss();
        }
        duesDialog = OfflineDuesDialog.Companion.newInstance(response, new PaymentCallBack() {
            @Override
            public void onSuccess(PaymentStatus paymentStatus) {
                Intent intent = new Intent(BookmarkPaymentActivity.this, SuccesFailActivity.class);
                intent.putExtra(ApplicationConstants.PAYMENT_STATUS, paymentStatus);
                startActivityForResult(intent,PAYMENT_SCREEN_CODE);
            }

            @Override
            public void onAborted() {
                duesDialog.dismiss();
            }
        },response.isForcePayment(),this);
        if (response.isForcePayment()){

        }
        duesDialog.setCancelable(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        duesDialog.show(fragmentManager,"Dues Dialog");
//        fragmentManager.beginTransaction()
//                .add(duesDialog, "dues")
//                .commitAllowingStateLoss();
    }

    private void showGuestList(){

        RecyclerView rlGuestList;
        TextView tvGuestListTitle;
        LayoutInflater layoutInflater = getLayoutInflater();
        View alertlayout = layoutInflater.inflate(R.layout.guest_order_list,null,false);
        rlGuestList = alertlayout.findViewById(R.id.rl_guest_list);
        tvGuestListTitle = alertlayout.findViewById(R.id.tv_guest_list_title);
        Button okButton = alertlayout.findViewById(R.id.bt_ok_guest);


        final ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.addAll(users);


        guestListQualcommAdapter = new GuestListQualcommAdapter(BookmarkPaymentActivity.this, userArrayList, new GuestRemoveClickListener() {
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
        AlertDialog.Builder guestListDialog = new AlertDialog.Builder(BookmarkPaymentActivity.this);
        rlGuestList.setLayoutManager(new LinearLayoutManager(this));
        rlGuestList.setAdapter(guestListQualcommAdapter);

        tvGuestListTitle.setText(AppUtils.getConfig(BookmarkPaymentActivity.this).getGroup_ordering().getCount_text());

        guestListDialog.setView(alertlayout);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(dialog!=null) {
                    dialog.dismiss();
                }
            }
        });

        dialog = guestListDialog.create();
        dialog.show();

    }

    private void addUser(UserAddRequest userAddRequest) {
        AppUtils.hideKeyboard(BookmarkPaymentActivity.this, ((TextInputEditText)findViewById(R.id.group_order_tie)));
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
                        ErrorPopFragment freeOrderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(error, "OK", true, ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {

                                    }

                                    @Override
                                    public void onNegativeInteraction() {

                                    }
                                });
                                freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "add_user");
                    }
                },
                UserReposne.class
        );
        addUserHttpAgent.post(userAddRequest, new HashMap<String, JsonSerializer>(), getApiTag());
    }

    private void showChangeLocationDialog() {
        FreeOrderErrorHandleDialog genericPopUpFragment = FreeOrderErrorHandleDialog.newInstance(null,
                "Your cart will get cleared if you change the location."
                ,
                new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        EventUtil.FbEventLog(context, EventUtil.CART_CHANGE_LOCATION, EventUtil.SCREEN_CART);
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
                    public void onNegativeButtonClicked() {

                    }
                },
                "CHANGE",
                "CANCEL"
        );

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "cart_clear")
                .commitAllowingStateLoss();
    }

    private void goToHome(){
        Intent intent = new Intent(getApplicationContext(),GlobalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void togglePaymentHeader() {
        if (rlDetailsPayment.getVisibility() == View.VISIBLE) {
            rlDetailsPayment.setVisibility(View.GONE);
            tvTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);

        } else {
            tvTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
            rlDetailsPayment.setVisibility(View.VISIBLE);
        }
    }


    private void getUserDetailsFromServer() {
        try {

            if (user != null) {
                updateUser(user);
                if (user.getHasDueBills()) {
                    checkForOfflineDues();
                } else {
                    if (duesDialog != null && duesDialog.isVisible()) {
                        duesDialog.dismiss();
                    }
                }
                return;
            }

            shLoading.setVisibility(View.VISIBLE);
            String url = UrlConstant.USER_DETAIL + "?occasionId=" + orderOcassion.id + "&locationId=" + locationId;
            SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(this, url, new ResponseListener<UserReposne>() {
                @Override
                public void response(UserReposne responseObject) {

                    if (responseObject == null) {
                        showNoNetFragment(new RetryListener() {
                            @Override
                            public void onRetry() {
                                getUserDetailsFromServer();
                            }
                        }, new CancelListener() {
                            @Override
                            public void onCancel() {
                                clearLocalOrder();
                                finish();
                            }
                        });
                    } else {
                        updateUser(responseObject.user);
                        if (order != null) updateOrderSummary(order);
                        rlContainer.setVisibility(View.VISIBLE);
                        shLoading.setVisibility(View.GONE);
                        if (responseObject.user.getHasDueBills()) {
                            checkForOfflineDues();
                        }
                    }
                }
            }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    shLoading.setVisibility(View.GONE);
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {

                        ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance(ApplicationConstants.NO_NET_STRING, "RETRY", true, ApplicationConstants.NO_INTERNET_IMAGE,
                                new ErrorPopFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {
                                        getUserDetailsFromServer();
                                    }

                                    @Override
                                    public void onNegativeInteraction() {
                                        //clearLocalOrder();
                                        finish();
                                    }
                                });

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        genericPopUpFragment.setCancelable(false);
                        fragmentManager.beginTransaction()
                                .add(genericPopUpFragment, "error")
                                .commitAllowingStateLoss();
                    }
                }
            }, UserReposne.class);
            userSimpleHttpAgent.get(getApiTag());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateProjectCode(){
        ProjectCodeSetting projectCodeSetting = new ProjectCodeSetting();
        projectCodeSetting.setProjectCode(projectCodeEt.getText().toString());
        SimpleHttpAgent<ProjectCodeSetting> projectCodeSettingSimpleHttpAgent = new SimpleHttpAgent<ProjectCodeSetting>(this, UrlConstant.SET_USER_SETTINGS,
                new ResponseListener<ProjectCodeSetting>() {
                    @Override
                    public void response(ProjectCodeSetting responseObject) {

                        order.setProjectCode(projectCodeEt.getText().toString());

                        if(deskReferenceEt.getVisibility() == View.VISIBLE){
                            checkDeskReference();
                        }else{
                            redirectToPayment(true);
                        }
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                order.setProjectCode(null);
                AppUtils.showToast(error,true,0);
            }
        },ProjectCodeSetting.class);
        projectCodeSettingSimpleHttpAgent.post(projectCodeSetting,new HashMap<String,JsonSerializer>(), getApiTag());
    }

    public void checkDeskReference(){

        if(deskReferenceEt.getText().toString().trim().equals("")){
            AppUtils.showToast("Please enter "+AppUtils.getConfig(getApplicationContext()).getWorkstation_address(), true, 0);
        }else if(order.getDeskReference() != null && order.getDeskReference().trim().equals(deskReferenceEt.getText().toString().trim())){
            redirectToPayment(true);
        }else{
            DeskReferenceSetting userSettings = new DeskReferenceSetting();
            userSettings.setDeskReference(deskReferenceEt.getText().toString().trim());
            SimpleHttpAgent<DeskReferenceSetting> settingsHttpAgent = new SimpleHttpAgent<DeskReferenceSetting>(this,
                    UrlConstant.SET_USER_SETTINGS, new ResponseListener<DeskReferenceSetting>() {
                @Override
                public void response(DeskReferenceSetting responseObject) {
                    AppUtils.resetCurrentUserDataClass(BookmarkPaymentActivity.this.getApplicationContext());
                    order.setDeskReference(deskReferenceEt.getText().toString().trim());
                    user.setDeskReference(deskReferenceEt.getText().toString().trim());
                    redirectToPayment(true);
                }
            }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    AppUtils.hideKeyboard(BookmarkPaymentActivity.this,deskReferenceEt);
                    if(error != null && !error.equals("")){
                        AppUtils.showToast(error, true, 0);
                    }else{
                        AppUtils.showToast("Some error occurred", true, 0);
                    }

                    if(order.getDeskReference() != null){
                        deskReferenceEt.setText(order.getDeskReference());
                        deskReferenceEt.setSelection(deskReferenceEt.getText().length());
                    }else{
                        deskReferenceEt.setText("");
                    }
                }
            }, DeskReferenceSetting.class);
            settingsHttpAgent.post(userSettings,new HashMap<String, JsonSerializer>(), getApiTag());
        }

    }

    private void goToPayment() {
        if (isProjectCodeEnabled()){
            if(isProjectCodeFieldEmpty()){
                AppUtils.showToast(AppUtils.getConfig(getApplicationContext()).getProject_code_message(), true, 0);
                return;
            } else {

                updateProjectCode();
            }
        } else{
            if(deskReferenceEt.getVisibility() == View.VISIBLE){
                checkDeskReference();
            }else{
                redirectToPayment(true);
            }
        }

    }

    void redirectToPayment(boolean callAffilationPopUp){
        if (!tetComment.getText().toString().equals("")) {
            order.setComment("" + tetComment.getText().toString());
        }

        if (guestOrderCheck(cart) && cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_OFFICIAL) && callAffilationPopUp){
            affilationPopUp();
            return;
        }

        order.setDocId();
        if(otherUserWallet >= order.getTotalPrice()){
            cleverTapPayClick(order);
            submitOrder(order);
        } else if (order.getTotalPrice() > 0) {
            Gson gson = new Gson();
            Intent paymentIntent = new Intent(BookmarkPaymentActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("order", gson.toJson(order));
            paymentIntent.putExtra("user", user);
            paymentIntent.putExtra("userExternalWalletCharge", userExternalWalletCharge);
            paymentIntent.putExtra(ApplicationConstants.SPACE_BOOKING, fromSpaceBooking);
            paymentIntent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, cbSpaceYes.isChecked());
            paymentIntent.putExtra("internalWalletPayableAmount", internalWalletPayableAmount);
            paymentIntent.putExtra("vendorId", selectedOrderVendor.getId());
            paymentIntent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            paymentIntent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Express Checkout");
            paymentIntent.putExtra(ApplicationConstants.FROM_BOOKMARK,fromBookmark);
            paymentIntent.putExtra(ApplicationConstants.FROM_REORDER,fromReorder);
            paymentIntent.putExtra("prevOrder",prevOrder);
            paymentIntent.putExtra("otherUserWallet",otherUserWallet);
            startActivityForResult(paymentIntent,PAYMENT_SCREEN_REQUEST_CODE);
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
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
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getSource(), "Exp");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getMethods_selected(), "company_paid");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getDefault_method(), "company_paid");
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getCart_value(),order.getTotalPrice());
//            if(order.getComment()!=null && !order.getComment().trim().isEmpty())
//                pay_click_map.put(CleverTapEvent.PropertiesNames.getSpecial_instruction(), order.getComment());
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getItem_name_item_quantity(), item_product);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_method_change(), false);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_company_paid(),true);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getPay_click(), pay_click_map, BookmarkPaymentActivity.this);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private void submitOrder(final Order order) {
        btPay.setText("Placing your order...");
        btPay.setEnabled(false);
        progressBar.setVisibility(View.GONE);
//        EventUtil.logEvent(new CustomEvent("order_start").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
        String url = UrlConstant.POST_ORDER_URL;
        SimpleHttpAgent<OrderResponse> orderSimpleHttpAgent = new SimpleHttpAgent<>(this, url, new ResponseListener<OrderResponse>() {
            @Override
            public void response(OrderResponse responseObject) {
                progressBar.setVisibility(View.GONE);
//                        btPay.setText("Place Order");
                if (responseObject != null && responseObject.order != null) {
                    handleResponse(responseObject, order);
                    if (responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)) {
//                                startOrderPayment(responseObject.order);
                    } else {
                        EventUtil.FbEventLog(BookmarkPaymentActivity.this, EventUtil.CART_ORDER_PLACED, EventUtil.SCREEN_PAYMENT);
                        HBMixpanel.getInstance().addEvent(BookmarkPaymentActivity.this, EventUtil.MixpanelEvent.CART_ORDER_PLACED);
//                                EventUtil.logEvent(new CustomEvent("order_complete").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
                        order.setId(responseObject.order.getId());
                        order.setCreatedAt((DateTimeUtil.adjustCalender(BookmarkPaymentActivity.this).getTimeInMillis() / 1000l));
                        if (user != null) updateOrderSummary(order);
                        cleverTapPlaceOrder();
                        clearLocalOrder();
                        order.setOrderStatus(responseObject.order.getOrderStatus());
                        startOrderView(responseObject.getOrder());
                    }
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                EventUtil.FbEventLog(BookmarkPaymentActivity.this, EventUtil.CART_ORDER_ERROR, EventUtil.SCREEN_CART);

                try {
                    String errorMessage = "NA";
                    if (errorResponse != null && errorResponse.message != null) {
                        errorMessage = errorResponse.message;
                    }
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.ERROR, errorMessage);
                    HBMixpanel.getInstance().addEvent(BookmarkPaymentActivity.this, EventUtil.MixpanelEvent.CART_ORDER_ERROR, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                LogoutTask.updateTime();
//                        EventUtil.logEvent(new CustomEvent("order_error").putCustomAttribute("vendor_id", selectedOrderVendor.getId()));
//                        btPay.setEnabled(true);
                AppUtils.HbLog("dtap", "order error");

                btPay.setText("Place Order");
                progressBar.setVisibility(View.GONE);
                btPay.setEnabled(true);
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    showOrderErrorDialog(null, "Sorry, there is some connectivity issue, Please try again", false);
                } else if (errorResponse != null && errorResponse.getError().getOrderIds().size() > 0) {
                    order.setId(errorResponse.error.orderIds.get(0));
                    order.setCreatedAt((DateTimeUtil.adjustCalender(BookmarkPaymentActivity.this).getTimeInMillis() / 1000l));
                    order.setOrderStatus(OrderUtil.NEW);
                    showOrderErrorDialog(order, errorResponse.message, true);
                } else if (errorResponse != null && errorResponse.message != null) {
                    showOrderErrorDialog(null, errorResponse.message, true);
                } else {
                    showOrderErrorDialog(null, "Oops! something went wrong. Please try again", true);
                }
            }
        }, OrderResponse.class);
        orderSimpleHttpAgent.post(order, new HashMap<String, JsonSerializer>(), getApiTag());
    }

    private void startOrderView(Order order) {
        Intent intent = null;
        if (selectedOrderVendor != null) order.setVendor(selectedOrderVendor);
        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            // perform operation for payment side
            //showPreOrderDialog(order);
            intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, true);
            if(fromSpaceBooking){
                intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
            }
            else{
                intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, cbSpaceYes.isChecked());
            }
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra("message", "High Five! \nYour Order has been placed.\nIt is in Pre-Order");
            if (selectedOrderVendor != null) startOrderNotificatioService(order);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            clearLocalOrder();
            startActivity(intent);
            //notification handling case
            setFromNotification(false);
            finish();
        } else {
            intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            if(fromSpaceBooking){
                intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
                intent.putExtra(ApplicationConstants.IS_APPROVAL_PENDING, order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING));
            }
            else{
                intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, cbSpaceYes.isChecked());
            }
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            startActivity(intent);
            clearLocalOrder();
            if (selectedOrderVendor != null) startOrderNotificatioService(order);
            //notification handling case
            setFromNotification(false);
            finish();
        }

    }

    public void cleverTapPlaceOrder()
    {
        try{
            String verName = AppUtils.getVersionName();
            verName = verName.replace(".","");
            int versionName = (int)Integer.parseInt(verName);

            String methodSelected = "company_paid";
            HashMap<String,Object> place_order_map = new HashMap<>();

            if(order.getDeskReference()!=null && order.getDeskReference().length()>0){
                place_order_map.put(CleverTapEvent.PropertiesNames.is_desk_order(),"true");
            }else{
                place_order_map.put(CleverTapEvent.PropertiesNames.is_desk_order(),"false");
            }

            place_order_map.put(CleverTapEvent.PropertiesNames.getSource(),fromBookmark?ApplicationConstants.ADD_ITEM_SOURCE_EXP:ApplicationConstants.ADD_ITEM_SOURCE_NORMAL);
            place_order_map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
            place_order_map.put(CleverTapEvent.PropertiesNames.getVendor_name(),selectedOrderVendor.getVendorName());
            place_order_map.put(CleverTapEvent.PropertiesNames.is_method_change(), "false");
            place_order_map.put(CleverTapEvent.PropertiesNames.getVendor_id(),selectedOrderVendor.getId());
            place_order_map.put(CleverTapEvent.PropertiesNames.getOs(),"Android");

            place_order_map.put(CleverTapEvent.PropertiesNames.getOrder_status(),"payment_success");
            place_order_map.put(CleverTapEvent.PropertiesNames.getPayment_method_name(),methodSelected);
            place_order_map.put(CleverTapEvent.PropertiesNames.getAmount(),order.getTotalPrice());
            place_order_map.put(CleverTapEvent.PropertiesNames.getLocation_id(),order.getLocationId());
            place_order_map.put(CleverTapEvent.PropertiesNames.getVersion_name(),versionName);
            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getPlace_order(),place_order_map,getApplicationContext());

        }catch (Exception e){
            e.printStackTrace();
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


    private void handleResponse(OrderResponse responseObject, Order order) {
        btPay.setEnabled(true);
        this.order = order;
    }

    private void updateUser(User responseObject) {

        int deskOrderingEnabled = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
        user = responseObject;

        if(fromSpaceBooking){
            return;
        }

        if(user!= null && user.getDeskReference()!=null && deskOrderingEnabled == 1){
            order.setDeskReference(user.getDeskReference());
        }
        if((user!= null && deskOrderingEnabled == 1) || (user!= null && deskOrderingEnabled == 2 && selectedOrderVendor.getDeskOrderingEnabled() == 1)){
            deskRefInputLayout.setVisibility(View.VISIBLE);
            deskReferenceEt.setVisibility(View.VISIBLE);
            autoFillDeskRef();
        }
        else if(user!= null && deskOrderingEnabled == 2 && selectedOrderVendor.getDeskOrderingEnabled() == 2){
            llDeskOptions.setVisibility(View.VISIBLE);
            deskRefInputLayout.setVisibility(View.VISIBLE);
            deskReferenceEt.setVisibility(View.VISIBLE);

            if(cbSelfPickup.isChecked()){
                deskRefInputLayout.setVisibility(View.GONE);
                deskReferenceEt.setVisibility(View.GONE);
            } else if (cbDeliverToSeat.isChecked()){
                showHideTimeLayout(false);
            }

            autoFillDeskRef();
        }
        else{
            deskRefInputLayout.setVisibility(View.GONE);
            deskReferenceEt.setVisibility(View.GONE);
        }
        adjustSocialDistancingFeature();
    }

    private void autoFillDeskRef(){
        if(user!= null && user.getDeskReference()!=null){
            deskReferenceEt.setText(user.getDeskReference());
        }
    }

    private void setOcassion(Ocassion selectedOcasion) {
        this.orderOcassion = selectedOcasion;
        getUserDetailsFromServer();
    }

    private void updateOrderSummary(Order order) {

        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }

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
        if (!selectedOrderVendor.isRestaurant() && AppUtils.getConfig(this).isShow_gst() && selectedOrderVendor.getCustomerGst()>0){
            llCgst.setVisibility(View.VISIBLE);
            llSgst.setVisibility(View.VISIBLE);
            double customerGst = selectedOrderVendor.getCustomerGst();
            tvSgst.setText(String.format("\u20B9 %.2f", (order.getPrice()*customerGst)/(2.0*(100+customerGst))));
            tvCgst.setText(String.format("\u20B9 %.2f", (order.getPrice()*customerGst)/(2.0*(100+customerGst))));
        }

        if (!selectedOrderVendor.isRestaurant() && AppUtils.getConfig(this).isShow_gst()&& selectedOrderVendor.getCustomerGst()>0){
            double customerGst = selectedOrderVendor.getCustomerGst();
            double roundedPrice = Double.parseDouble(String.format("%.2f",((order.getPrice()*customerGst)/(2.0*(100+customerGst)))));
            tvOrderTotal.setText(String.format("₹ %.2f", order.getPrice()-roundedPrice-roundedPrice));
            tvTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));
        }
        else {
            tvOrderTotal.setText(String.format("₹ %.2f", order.getPrice()));
            tvTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));
        }

        if (order.getConvenienceFee()<=0 && order.getDeliveryCharge() <= 0 && order.getContainerCharge() <= 0 && order.getServiceTax(selectedOrderVendor) <= 0 && order.getVat(selectedOrderVendor) <= 0 && order.getCgst(selectedOrderVendor) <= 0 && order.getSgst(selectedOrderVendor) <= 0 && !AppUtils.getConfig(this).isShow_gst()) {
            llOrderDetailConatiner.setVisibility(View.GONE);
            tvTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvTotal.setOnClickListener(null);
            tvViewDetailBill.setVisibility(View.GONE);

        } else {
            tvTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
            tvTotal.setOnClickListener(totalPriceClickListener);
            tvViewDetailBill.setVisibility(View.VISIBLE);
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
            tvEmpCount.setPaintFlags(preOrderTime.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvEmpCount.setText(users.size() + " " + AppUtils.getConfig(BookmarkPaymentActivity.this).getGroup_ordering().getCount_text());
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

        double convenienceFee = user.getCurrentWallets().getConvenienceFee(order.getPrice());

        if (convenienceFee > 0) {
            llConvenienceFee.setVisibility(View.VISIBLE);
            tvConvenienceFee.setText("₹ " + Math.round(convenienceFee * 100) / 100.00);
            order.setConvenienceFee(convenienceFee);
        } else llConvenienceFee.setVisibility(View.GONE);

        double userRemainingBalance = (otherUserWallet + user.getCurrentWalletBalance()) - order.getTotalPrice();

        if (userRemainingBalance < 0) {
            userExternalWalletCharge = order.getTotalPrice() - (otherUserWallet + user.getCurrentWalletBalance());
            internalWalletPayableAmount = (user.getCurrentWalletBalance() + otherUserWallet);
        } else {
            userExternalWalletCharge = 0;
            internalWalletPayableAmount = order.getTotalPrice();
        }
        // Qualcomm wallet breakup
        if(AppUtils.getConfig(this).showWallet_breakup()){
            getUserPaymentDetails(cart.getVendorId(),order.getTotalPrice());
        }else{
            tvWallet.setVisibility(View.GONE);
        }
        if (order.getTotalPrice() > 0) {
            rlBaseContainer.setVisibility(View.VISIBLE);
            createBaseContainer();
        } else {
            rlBaseContainer.setVisibility(View.GONE);
            llbt.setVisibility(View.VISIBLE);
            btPay.setText("Place Order");
            btGrandTotal.setVisibility(View.VISIBLE);
            btGrandTotal.setText(String.format("\u20B9 %.2f", 0.00));
        }

        if(otherUserWallet > 0) {
            handleOtherUserWallet();
        }

        if(fromSpaceBooking){
            adjustSpaceBookingFeature();
        }

    }

    private void handleOtherUserWallet(){
        if(otherUserWallet >= order.getTotalPrice()) {
            rlBaseContainer.setVisibility(View.GONE);
            llbt.setVisibility(View.VISIBLE);
            btPay.setText("Place Order");
            btGrandTotal.setVisibility(View.VISIBLE);
            btGrandTotal.setText(String.format("\u20B9 %.2f", order.getTotalPrice()));
        }
    }


    private void updateUiWith(Cart cart, Order order) {
        this.order = order;
        if (orderReviewAdapter == null) {
            orderReviewAdapter = new OrderReviewAdapter(this, order, cart, this);
            rvOrderList.setLayoutManager(new LinearLayoutManager(this));
            rvOrderList.setAdapter(orderReviewAdapter);
        } else {
            orderReviewAdapter.changeOrder(order, cart);
            orderReviewAdapter.notifyDataSetChanged();
        }

    }

    protected void createBaseContainer() {

        if (!isRunning) return;
        llbt.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        if (fromReorder) {
            bundle.putSerializable("prevOrder",prevOrder);
            bundle.putBoolean("fromReorder",fromReorder);
        }
        if (fragment == null) {
            fragment = PaymentFragment.newInstance(order, ApplicationConstants.ORDER_TYPE_FOOD, user, userExternalWalletCharge, internalWalletPayableAmount, locationId, true, this, this,this,0, cbSpaceYes.isChecked(), fromSpaceBooking);
            fragment.setArguments(bundle);
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction().add(R.id.rl_base_container, fragment, fragmentTag).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.rl_base_container, fragment, fragmentTag).commit();
            }
        } else {
            PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            paymentFragment.setArguments(bundle);
            transaction.detach(paymentFragment);
            transaction.attach(paymentFragment);
            if (initated) {
                paymentFragment.updateOrder(order, userExternalWalletCharge, internalWalletPayableAmount,otherUserWallet);
            }
        }
    }

    @Override
    public void handlePaymentMethodSelected(PaymentMethod paymentMethod, boolean selected) {
        final PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        paymentFragment.handlePaymentMethodSelected(paymentMethod, selected);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode==PAYMENT_SCREEN_REQUEST_CODE && resultCode ==PaymentFragment.WALLET_LINK_CODE){
                if (fragment!=null && fragment instanceof PaymentFragment){
                    ((PaymentFragment) fragment).getUserDetailsFromServer();
                }
            } else{
                user = null;
                getUserDetailsFromServer();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }

    public void findAndCreateRecommendation() {
        if (recommendedProducts != null && recommendedProducts.size() > 0) {
            return;
        }
        if (selectedOrderVendor==null){
            return;
        }
        if (!fromBookmark){
            RealmQuery<Product> query = realm.where(Product.class);
            //query.equalTo("vendorId", selectedOrderVendor.getId());
            query.equalTo("vendorId", selectedOrderVendor.getId()).equalTo("recommeded", 1);
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                query.notEqualTo("id", cart.getOrderProducts().get(i).getId());
            }

            RealmResults<Product> realmResults = query.findAll().sort("sortOrder", Sort.DESCENDING);
            for (int i = 0; i < realmResults.size(); i++) {
                Product recommendedProduct = realmResults.get(i).clone();
                recommendedProduct.setRecommendationType(ApplicationConstants.RecommendationType.CART_TRENDING);
                recommendedProducts.add(recommendedProduct);
            }
            if (recommendedProducts.size() > 0) {
                llRecommend.setVisibility(View.VISIBLE);
                createRecommendedList(recommendedProducts);
            } else {
                llRecommend.setVisibility(View.GONE);
            }
        } else{
            RealmQuery<TrendingMenuItem> query = realm.where(TrendingMenuItem.class);
            query.equalTo("vendorId", selectedOrderVendor.getId());
            for (int i = 0; i < cart.getOrderProducts().size(); i++) {
                query.notEqualTo("id", cart.getOrderProducts().get(i).getId());
            }

            RealmResults<TrendingMenuItem> realmResults = query.findAll().sort("sortOrder", Sort.DESCENDING);
            for (int i = 0; i < realmResults.size(); i++) {
                Product recommendedProduct = realmResults.get(i).copy();
                recommendedProduct.setRecommendationType(ApplicationConstants.RecommendationType.CART_TRENDING);
                recommendedProducts.add(recommendedProduct);
            }
            if (recommendedProducts.size() > 0) {
                llRecommend.setVisibility(View.VISIBLE);
                createRecommendedList(recommendedProducts);
            } else {
                llRecommend.setVisibility(View.GONE);
            }
        }
    }

    private void createRecommendedList(ArrayList<Object> recommendedProducts) {
        Ocassion selectedOcasion = MainApplication.getOcassionForId(cart.getOccasionId());
        Vendor vendor = AppUtils.getVendorById(getApplicationContext(), selectedOrderVendor.getId());
        if (rvReccomendedItems.getAdapter() == null) {
//            trendingListAdapter = new ListAdapter(this, recommendedProducts, vendor, selectedOcasion.id, false, 1);
            trendingListAdapter = new TrendingAdapter(this, recommendedProducts, vendor, selectedOcasion.id, false, 1);
            rvReccomendedItems.setAdapter(trendingListAdapter);
        } else {
            trendingListAdapter.changeProducts(recommendedProducts, vendor);
            trendingListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cart != null) cart.setLastUpdatedTime(new Date().getTime());
        isRunning = true;

        MainApplication.bus.register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        MainApplication.bus.unregister(this);
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        updateRecommendeList();
        modifyCart();
    }

    private void modifyCart() {
        new CreateOrderListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void onRemoveProductFromCart(RemoveProductFromCart removeProductFromCart) {
        updateRecommendeList();
    }

    public void updateRecommendeList() {

        cart = mainApplication.getCart();
        updatedRecommendList.clear();
        ArrayList<Long> cartItemId = new ArrayList<>();
        for (int i = 0; i < cart.getOrderProducts().size(); i++) {
            cartItemId.add(cart.getOrderProducts().get(i).getId());
        }
        for (Object product : recommendedProducts) {
            if (product instanceof Product) {
                Product convertedProduct = (Product) product;

                if (!cartItemId.contains(convertedProduct.getId())) {
                    updatedRecommendList.add(product);
                }
            }
        }
        if (updatedRecommendList.size() > 0) {
            llRecommend.setVisibility(View.VISIBLE);
            createRecommendedList(updatedRecommendList);
        } else {
            llRecommend.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshOrderList(OrderProduct orderProduct, int position, boolean shouldRefresh) {
        if (shouldRefresh) {
            if (orderProduct.getQuantity() == 0) {
                order.getProducts().remove(position);
                if (order.getProducts().size() <= 0) {
                    finish();
                }
            }
            if (orderReviewAdapter != null) orderReviewAdapter.notifyDataSetChanged();
        }
        if (order != null && order.getProducts().size() > 0) modifyCart();
        if (user != null) updateOrderSummary(order);
        showHidePcField();
    }

    @Override
    public void onBackPressed() {
        if (fromBookmark || fromReorder || fromSpaceBookingReorder) {
            showBackPressDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showOrderErrorDialog(Order order, String error, boolean clearOrder) {
        if (!isRunning) return;
        ErrorPopFragment freeOrderErrorHandleDialog = null;
        if (clearOrder) {
            clearLocalOrder();
        }
        freeOrderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(error,"Go To Home",ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {

                    finish();
                }

                @Override
                public void onNegativeInteraction() {

                }});
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "free_order_error");
    }

    public void ErrorDialog(Order order, String error, boolean clearOrder) {

        ErrorPopFragment freeOrderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(error, "OK", ApplicationConstants.PAYMENT_FAILED_IMAGE, new ErrorPopFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {

            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "free_order_error");
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
            //cart.setGuestType("");
            return false;
        }
    }

    private void setVendor(Vendor orderVendor) {
        this.selectedOrderVendor = AppUtils.getVendorById(getApplicationContext(), orderVendor.getId());

        if (selectedOrderVendor == null) {
            AppUtils.showToast("Something went wrong.", true, 0);
            finish();
            return;
        }

        this.selectedOrderVendor = selectedOrderVendor.clone();

        if (fromSpaceBooking){
            String type = getIntent().getStringExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE);
            Config.SpaceType spaceType =  AppUtils.getSpaceType(this,type);
            if(spaceType!=null) {
                tvSpaceName.setText(spaceType.getName());
                ImageHandling.loadRemoteImage(spaceType.getIcon_url(), ivShoppingCart, R.drawable.ic_table_booking_icon_big, R.drawable.ic_table_booking_icon_big, this);

            }
            if(cart!=null && cart.getOrderProducts().size()>0){
                tvVendorName.setText(cart.getOrderProducts().get(0).getName());
                handleOrderTimeSelected(new OrderSelectedTime(DateTimeUtil.getTimeStampFromTime(cart.getOrderProducts().get(0).getDate()+" "+cart.getOrderProducts().get(0).getSlotStartTime()),false));
            }

            return;
        }

        tvVendorName.setText(selectedOrderVendor.getVendorName());
        tvVendorName.setSelected(true);
        tvLocationName.setSelected(true);
        long currentTime = DateTimeUtil.adjustCalender(BookmarkPaymentActivity.this).getTimeInMillis();

        vendorStartTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getStartTime(), BookmarkPaymentActivity.this);
        long vendorEndTime = DateTimeUtil.getTodaysTimeFromStringNew(selectedOrderVendor.getEndTime(), BookmarkPaymentActivity.this);
        if (currentTime > vendorStartTime && (orderOcassion.getPreOrderDay() == Ocassion.PreOrderDayType.TODAY)) {
            vendorStartTime = currentTime;
            handleOrderTimeSelected(new OrderSelectedTime(vendorStartTime, false));
        } else if (!isTimePickerShown){
            if(orderOcassion.getPreOrderDay() == Ocassion.PreOrderDayType.TODAY){

                if(AppUtils.isSocialDistancingActive(null)){

                    preOrderTime.setVisibility(View.VISIBLE);
                    preOrderTime.setPaintFlags(preOrderTime.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    preOrderTime.setText("Delivery Time - " + DateTimeUtil.getDateStringCustom(vendorStartTime, "HH:mm"));
                    preOrderTime.setOnClickListener(null);
                    handleOrderTimeSelected(new OrderSelectedTime(vendorStartTime, false));
                }
                else {
                    final Calendar calendarVendorStartTime = Calendar.getInstance();
                    calendarVendorStartTime.setTimeInMillis(vendorStartTime);

                    final Calendar calendarVendorEndTime = Calendar.getInstance();
                    calendarVendorEndTime.setTimeInMillis(vendorEndTime);

                    View.OnClickListener positiveListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exitFragment.dismiss();
                            isTimePickerShown = true;
                            initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
                        }
                    };

                    View.OnClickListener negativeListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    };

                    exitFragment = ExitFragment.newInstance(String.format(getString(R.string.preorder_message), orderOcassion == null ? " " : orderOcassion.name), "Continue", "Cancel", positiveListener, negativeListener);
                    exitFragment.setCancelable(false);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(exitFragment, "exit").commitAllowingStateLoss();
                }
            }
            else{
                preOrderTime.setVisibility(View.VISIBLE);
                preOrderTime.setPaintFlags(preOrderTime.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                long nextDayTime = AppUtils.getNextWorkingDayTime(orderOcassion.getPreOrderDay(), vendorStartTime);
                preOrderTime.setText("Delivery Time - " + DateTimeUtil.getDateStringCustom(nextDayTime, "dd MMM yyyy, hh:mm aa"));
                preOrderTime.setOnClickListener(null);
                handleOrderTimeSelected(new OrderSelectedTime(nextDayTime, false));
            }
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

    private void clearLocalOrder() {
        if (getApplication() != null) {
            MainApplication mainApplication = (MainApplication) getApplication();
            mainApplication.clearOrder();
        }
    }

    private void initiateTimePicker(final Calendar calendarVendorStartTime, final Calendar calendarVendorEndTime) {

        timePicker = new TimePickerDialog(BookmarkPaymentActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    preOrderTime.setPaintFlags(preOrderTime.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    if(minute < 10){
                        preOrderTime.setText(String.format(getString(R.string.preorder_delivery_time), "" + hourOfDay, "0" + minute));
                    }else{
                        preOrderTime.setText(String.format(getString(R.string.preorder_delivery_time), "" + hourOfDay, "" + minute));
                    }

                    preOrderTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
                        }
                    });
//                    preOrderTime.setVisibility(View.VISIBLE);
//                    preOrderTime.setText(String.format(getString(R.string.selected_delivery_time), "" + hourOfDay, "" + minute));
//
//                    preOrderTime.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            initiateTimePicker(calendarVendorStartTime, calendarVendorEndTime);
//                        }
//                    });
                    handleOrderTimeSelected(new OrderSelectedTime(vendorStartTime, false));
                } else {
                    AppUtils.showToast(String.format(getString(R.string.delivery_time_selection_error_msg), selectedOrderVendor.getStartTime(), selectedOrderVendor.getEndTime()), true, 2, true);
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

    @Override
    public void handleMoreOptionSelected() {
        if (order != null) {
            goToPayment();
        }
    }

    @Override
    public void onProgressLoad(boolean state) {
        if (!isRunning) return;

        if (state) {
            initated = false;
        } else {
            initated = true;
            if (fragment != null) {
                PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
                paymentFragment.updateOrder(order, userExternalWalletCharge, internalWalletPayableAmount, otherUserWallet);
            }
        }

    }

    @Override
    public void viewDetailBillSelected() {

        ViewDetailBillFragment genericPopUpFragment = ViewDetailBillFragment.Companion.newInstance(user, order, selectedOrderVendor,
                new ViewDetailBillFragment.OnFragmentInteractionListener() {
            @Override
            public void positiveClick() {

            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "detail")
                .commitAllowingStateLoss();
    }

    class CreateOrderListTask extends AsyncTask<Void, Integer, Void> {
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
            cart = mainApplication.getCart();
            try {

                selectedOcasion = MainApplication.getOcassionForId(cart.getOccasionId());
                orderObject.setOccasionId(selectedOcasion.id);
                if(fromSpaceBooking) {
                    orderObject.setLocationId(spaceLocationId);
                }else{
                    orderObject.setLocationId(locationId);
                }
                orderVendor = cart.getVendor();
                orderObject.setDeliveryCharge(orderVendor.getDeliveryCharge());
                orderObject.setContainerCharge(orderVendor.getConatainerCharge());

                if(order != null && order.getOrderTime() != 0 && (DateTimeUtil.adjustCalender(BookmarkPaymentActivity.this).getTimeInMillis() < vendorStartTime))
                    orderObject.setOrderTime(order.getOrderTime());

            } catch (NullPointerException e) {
                // error popup
                setCancel = true;
                showOrderErrorDialog(null, "Something went wrong while creating your order.Please try ordering again", true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (setCancel) return null;

            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                orderObject.getProducts().add(orderProduct);
            }
            orderObject.setVendorId(orderVendor.getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (setCancel) return;
            updateUiWith(cart, orderObject);
            setOcassion(selectedOcasion);
            setVendor(orderVendor);
            updateOrderSummary(orderObject);
            findAndCreateRecommendation();
            updateGuestUser();

        }
    }

    public void showNoNetFragment(RetryListener retryListener, CancelListener cancelListener) {
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener, cancelListener);
        fragment.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(fragment, "exit").commit();
    }

    public void showBackPressDialog() {
        if (!isRunning) return;
        String title = "Remove cart item ?\n";
        String msg = "Your cart contains dishes from " + this.selectedOrderVendor.getVendorName() + ". Do you want to discard the selection and go back?";

        if(fromSpaceBooking){
            title = "Clear Selection";
            msg = "Do you want to discard the selection and go back?";
        }
        GeneralDialogFragment generalDialogFragment = GeneralDialogFragment.newInstance(title,msg , new GeneralDialogFragment.OnDialogFragmentClickListener() {
            @Override
            public void onPositiveInteraction(GeneralDialogFragment dialog) {
                clearLocalOrder();

                if(fromSpaceBooking){
                    SharedPrefUtil.putBoolean(ApplicationConstants.BACK_TO_SPACE_BOOKING, true);
                }
                try {
                    Spotlight.with(BookmarkPaymentActivity.this).closeSpotlight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }

            @Override
            public void onNegativeInteraction(GeneralDialogFragment dialog) {

            }
        });
        generalDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    public interface PaymentCallBack{
        void onSuccess(PaymentStatus paymentStatus);
        void onAborted();
    }

    private void adjustSocialDistancingFeature(){
        if (fromSpaceBooking) {
            showHideTimeLayout(false);
            return;
        }

        if (AppUtils.isSocialDistancingActive(null)&& isCurrentOccasionOrder()){
            getEta();
            entryTimingLayout.setVisibility(View.VISIBLE);
            tvWhyText.setVisibility(View.VISIBLE);
            iText.setVisibility(View.VISIBLE);

            ivEntryTick.setVisibility(View.GONE);
            tvEntryText.setText(AppUtils.getConfig(this).getSocial_distancing().getEta_text());
            tvEntryTiming.setVisibility(View.VISIBLE);
            tvEntryTiming.setText("");//the time
            setSocialDistInfoClickListener();
            if (deskRefInputLayout.getVisibility()==View.VISIBLE){
                showHideTimeLayout(false);
            }
        } else{
            showHideTimeLayout(false);
        }
    }

    private void adjustSpaceBookingFeature(){
        rvOrderList.setVisibility(View.GONE);
        til_comments.setVisibility(View.GONE);
        tvLocationName.setVisibility(View.GONE);
        clSpaceGuestLayout.setVisibility(View.VISIBLE);
        deskReferenceEt.setVisibility(View.GONE);

        String startTime = DateTimeUtil.getDateStringFromDateString(cart.getOrderProducts().get(0).getSlotStartTime(), "hh:mm:ss", "hh:mm a");
        String endTime = DateTimeUtil.getDateStringFromDateString(cart.getOrderProducts().get(0).getSlotEndTime(), "hh:mm:ss", "hh:mm a");

        if(order!=null && order.getTotalPrice()>0) {
            tvTitle.setText("Confirm & Pay");
        }else{
            tvTitle.setText("Confirm");
            btPay.setText("Book");
            llViewBill.setVisibility(View.GONE);
        }
        tvSpaceTime.setText(startTime+" - "+endTime);
        tvSpaceDate.setText(DateTimeUtil.getDateStringFromDateString(cart.getOrderProducts().get(0).getDate(),"yyyy-MM-dd","dd MMM YYYY"));
        tvSpaceLocation.setText(getIntent().getStringExtra(ApplicationConstants.SPACE_LOCATION_NAME));

    }

    private void adjustTableCheckFeature(){
        if(fromSpaceBooking)
            return;
        if (AppUtils.getSpaceType(this,ApplicationConstants.SPACE_TYPE_TABLE)!=null &&
                SharedPrefUtil.getStringArrayList(ApplicationConstants.OTHER_TYPE_LOCATION).contains(ApplicationConstants.SPACE_TYPE_TABLE)){
            rlSpaceCheckbox.setVisibility(View.VISIBLE);

            cbSpaceYes.setOnCheckedChangeListener((buttonView, isChecked) -> cbSpaceNo.setChecked(!isChecked));

            cbSpaceNo.setOnCheckedChangeListener((buttonView, isChecked) -> cbSpaceYes.setChecked(!isChecked));

            cbSpaceNo.setChecked(true);
        }
    }

    private void showHideTimeLayout(boolean show){
        if (show) {
            if (AppUtils.isSocialDistancingActive(null) && isCurrentOccasionOrder()){
                entryTimingLayout.setVisibility(View.VISIBLE);
                tvWhyText.setVisibility(View.VISIBLE);
                iText.setVisibility(View.VISIBLE);

                ivEntryTick.setVisibility(View.GONE);
                tvEntryText.setText(AppUtils.getConfig(this).getSocial_distancing().getEta_text());
                tvEntryTiming.setVisibility(View.VISIBLE);
                tvEntryTiming.setText(new SimpleDateFormat("hh:mm a").format(new Date(etaTime * 1000)));
            }
        } else{
            entryTimingLayout.setVisibility(View.GONE);
            tvWhyText.setVisibility(View.GONE);
            iText.setVisibility(View.GONE);
        }
    }

    private boolean isCurrentOccasionOrder(){

        long currentTime = DateTimeUtil.adjustCalender(BookmarkPaymentActivity.this).getTimeInMillis();
        Ocassion currentOccasion = MainApplication.getOcassionForId(cart.getOccasionId());

        return currentOccasion!=null && cart != null && cart.getVendor() != null &&
                currentTime > DateTimeUtil.getTodaysTimeFromStringNew(cart.getVendor().getStartTime(), BookmarkPaymentActivity.this) &&
                (currentOccasion.getPreOrderDay() == Ocassion.PreOrderDayType.TODAY);
    }

    private void waitIsOver(){
        ivEntryTick.setVisibility(View.VISIBLE);
        tvEntryText.setText(AppUtils.getConfig(this).getSocial_distancing().getEta_text());
        tvEntryTiming.setVisibility(View.GONE);
    }

    private void getEta(){
        if (locationId!=0 && occasionId!=0){

            String url = UrlConstant.ORDER_ETA+"?occasion_id="+occasionId+"&location_id="+locationId;
            SimpleHttpAgent<Eta> postPaidResponseSimpleHttpAgent = new SimpleHttpAgent<>(
                    this,
                    url,
                    new ResponseListener<Eta>() {
                        @Override
                        public void response(Eta responseObject) {
                            try {
                            if (responseObject.getData()!=null){
                                etaTime = responseObject.getData().getEta();
                                tvEntryTiming.setText(new SimpleDateFormat("hh:mm a").format(new Date(etaTime * 1000)));
                                if (responseObject.getData().getEta()<1000000){
                                    waitIsOver();
                                }
                            }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                    , new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    AppUtils.showToast(error,false,0);
                    showHideTimeLayout(false);

                }
            },Eta.class);

            postPaidResponseSimpleHttpAgent.get(getApiTag());
        }
    }

    private void setSocialDistInfoClickListener(){
        iText.setOnClickListener((v)->{showInfoWebView();});
        tvWhyText.setOnClickListener((v)->{showInfoWebView();});
    }
    private void showInfoWebView(){

        if (AppUtils.getConfig(this).getSocial_distancing() != null && AppUtils.getConfig(this).getSocial_distancing().getInfo_url() !=null && !AppUtils.getConfig(this).getSocial_distancing().getInfo_url().trim().equalsIgnoreCase("")) {
            UrlNavigationHandler urlNavigationHandler = new UrlNavigationHandler(this);
            Intent intent = urlNavigationHandler.getUrlNavPath(AppUtils.getConfig(this).getSocial_distancing().getInfo_url());
            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    public void getUserPaymentDetails(long vendorId, double cartValue) {

        String url = UrlConstant.WALLET_LIST_V2 + "?occasionId=" + cart.getOccasionId() +
                "&locationId=" + locationId + "&transactionAmount=" + cartValue +
                "&vendorId=" + vendorId
                + "&showMswipe=" + 0 + "&showPineLabs=" + 0;


        SimpleHttpAgent<ListWallet> userWalletsSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                new ResponseListener<ListWallet>() {
                    @Override
                    public void response(ListWallet responseObject) {

                        if (responseObject != null) {
                            calculateAndShowWallet(responseObject);
                        }else{
                            tvWallet.setVisibility(View.GONE);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        tvWallet.setVisibility(View.GONE);
                    }
                },
                ListWallet.class
        );

        userWalletsSimpleHttpAgent.post("", new HashMap<String, JsonSerializer>(), getApiTag());
    }
    public void calculateAndShowWallet(ListWallet listWallet){
        try {
            if (listWallet != null) {

                ArrayList<WalletBreakupItem> walletList = new ArrayList<>();
                double balance = 0;
                rechargeAllowed = false;
                for (PaymentMethod paymentMethod : listWallet.getPaymentMethods()){
                    if (paymentMethod.getWalletSource().equalsIgnoreCase("internal")){
                        if(paymentMethod.isInternal() && paymentMethod.employeeCanRecharge()){
                            rechargeAllowed = true;
                        }
                        balance += paymentMethod.getAmount();
                        walletList.add(new WalletBreakupItem(paymentMethod.getDisplayName(),Double.toString(paymentMethod.getAmount())));
                    }
                }
                if (walletList.size() > 0) {
                    tvWallet.setVisibility(View.VISIBLE);
                    tvWallet.setText("Rs." + balance);
                } else {
                    tvWallet.setVisibility(View.GONE);
                }
                tvWallet.setOnClickListener(v -> showWalletBreakPopUp(walletList,rechargeAllowed));
            }
        } catch (Exception e){
            e.printStackTrace();
            tvWallet.setVisibility(View.GONE);
        }
    }

    public void showWalletBreakPopUp(ArrayList<WalletBreakupItem> walletList,boolean rechargeAllowed){
        if(walletList.size() > 0){
            WalletBreakupFragment wbFragment = WalletBreakupFragment.Companion.newInstance(walletList,rechargeAllowed);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(wbFragment, "wallet_breakup")
                    .commitAllowingStateLoss();
        }
    }


    public void updateGuestUser(){

        //Adding Space Guest User In Order

        if(fromSpaceBooking) {

            if (mainApplication.getCart().getSpaceGuests().size() > 0) {
                ArrayList<OrderGuestInfo> orderGuestInfos = new ArrayList<>();
                for (BookingGuest bookingGuest : mainApplication.getCart().getSpaceGuests()) {
                    orderGuestInfos.add(new OrderGuestInfo(bookingGuest.getName(), bookingGuest.getEmail(), bookingGuest.getPhoneNo(), bookingGuest.isNotification()));
                }
                order.orderGuestInfo = orderGuestInfos;
                tvSpaceGuestTitle.setText("Guest List (" + orderGuestInfos.size() + ")");
                SpaceGuestListCartAdapter adapter = new SpaceGuestListCartAdapter(this, orderGuestInfos);
                rvSpaceGuestList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }else{
                if(cart.getOrderProducts().get(0).getQuantity()>1) {
                    tvSpaceGuestTitle.setText("Total Guest :  " + (cart.getOrderProducts().get(0).getQuantity() - 1));
                }
            }
        }
    }

}