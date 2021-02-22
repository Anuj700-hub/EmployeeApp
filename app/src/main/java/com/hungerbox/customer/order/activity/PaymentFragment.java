package com.hungerbox.customer.order.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.bigbasket.bbinstant.bb_sdk_interfaces.BBSDKParam;
import com.bigbasket.bbinstant.bb_sdk_interfaces.InitializeException;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.exception.LowBalanceException;
import com.hungerbox.customer.exception.LowJuspayWalletBalanceException;
import com.hungerbox.customer.exception.LowPaytmDirectWalletBalanceException;
import com.hungerbox.customer.exception.MinimumOrderAmountException;
import com.hungerbox.customer.exception.PhoneNumberAbsentException;
import com.hungerbox.customer.exception.SimplEligibiltyTestException;
import com.hungerbox.customer.model.BigBasketGenerateSession;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.DeskReferenceSetting;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.JPWalletRecharge;
import com.hungerbox.customer.model.JPWalletRechargeResponse;
import com.hungerbox.customer.model.ListWallet;
import com.hungerbox.customer.model.MoreOptionHeader;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderPayment;
import com.hungerbox.customer.model.OrderPaymentData;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.model.PaymentMethodHeader;
import com.hungerbox.customer.model.PaymentMethodMethod;
import com.hungerbox.customer.model.PaymentUpiMethods;
import com.hungerbox.customer.model.ProjectCodeSetting;
import com.hungerbox.customer.model.SavedCardData;
import com.hungerbox.customer.model.SimplBalanceValidation;
import com.hungerbox.customer.model.UpiMethod;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.WalletBreakupItem;
import com.hungerbox.customer.model.WalletOtpVerification;
import com.hungerbox.customer.model.ZetaCertificate;
import com.hungerbox.customer.model.ZetaRequest;
import com.hungerbox.customer.model.ZetaRequestBase;
import com.hungerbox.customer.navmenu.activity.RechargeActivity;
import com.hungerbox.customer.navmenu.activity.WalletHistoryActivity;
import com.hungerbox.customer.navmenu.fragment.AccountDetailsUpdateDialog;
import com.hungerbox.customer.navmenu.fragment.WalletBreakupFragment;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.PaymentMethodsAdapterV2;
import com.hungerbox.customer.order.fragment.GuestNamesFragment;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.fragment.SimplWebViewFragment;
import com.hungerbox.customer.order.listeners.MorePaymentOptionListener;
import com.hungerbox.customer.order.listeners.OnLoaderListener;
import com.hungerbox.customer.order.listeners.OnPaymentSelectLisntener;
import com.hungerbox.customer.order.listeners.PaymentMethodsInterface;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.order.listeners.ViewDetailBillListener;
import com.hungerbox.customer.payment.JPPaymentController;
import com.hungerbox.customer.payment.OnPaymentStatusChangeListener;
import com.hungerbox.customer.payment.activity.JPOtpverificationActivity;
import com.hungerbox.customer.payment.activity.NetbankingActivity;
import com.hungerbox.customer.prelogin.activity.OtpVerificationActivity;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.service.NotificationService;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.hungerbox.customer.util.view.LoaderFragment;
import com.hungerbox.customer.util.view.RoundedRectangle;
import com.phonepe.android.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import com.phonepe.intent.sdk.api.TransactionRequest;
import com.phonepe.intent.sdk.api.TransactionRequestBuilder;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.target.SimpleTarget;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import in.juspay.ec.sdk.api.Environment;
import in.juspay.ec.sdk.api.ExpressCheckoutService;
import in.juspay.ec.sdk.api.core.AbstractPayment;
import in.juspay.ec.sdk.api.core.CardPayment;
import in.juspay.ec.sdk.api.core.NetBankingPayment;
import in.juspay.ec.sdk.api.core.SavedCardPayment;
import in.juspay.ec.sdk.api.core.WalletPayment;
import in.juspay.ec.sdk.netutils.JuspayHttpResponse;
import in.juspay.ec.sdk.netutils.NetUtils;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.hungerbox.customer.order.activity.OrderReviewActivity.OTP_VERIFICATION_REQ_CODE;
import static com.hungerbox.customer.util.HBConstants.RequestCodeConstants.PHONE_PE_SDK_REQUEST_CODE;

public class PaymentFragment extends Fragment implements OnPaymentSelectLisntener {

    public static final int ORDER_PAYMENT_WEB_REQUEST = 33294,PINE_LBAS_REQUEST=2020,DYNAMIC_QR_PAYTM_REQUEST = 2021;
    private static final int ZETA_REQUEST_CODE = 890;
    private static final int JUSPAY_OTP_LINK_CODE = 891;
    private static final int JUSPAY_NETBANK_SELECT = 892;
    private static final int JUSPAY_WALLET_RECAHRGE = 893;
    private static final int GET_NEW_CARD = 2010;
    private static final int SAVE_NEW_CARD = 2012;
    private static final int PERSONAL_WALLET_RECHARGE = 894;
    public static final int PAYTM_DIRECT_RECHARGE_REQUEST = 9876;
    public static final int PAYTM_INTENT_UPI = 9877;
    public static final int RESULT_ERROR = 111;
    final int EDIT_CARD = 2011;
    public static final int WALLET_LINK_CODE = 888;
    public TextView tvTitle,tvViewHistory;
    Toolbar toolbar;
    boolean isRunning = false;
    LoaderFragment loaderFragment = null;
    public RecyclerView rvPaymentMethods;
    private TextView tvPay,btGrandTotal,tvViewDetailBill;
    private LinearLayout llViewBill;
    public Button btPay;
    private MainApplication mainApplication;
    public Cart cart;
    public Order order;
    private double userExternalWalletCharge;
    private double internalWalletPayableAmount;
    private Realm realm;
    private Vendor selectedOrderVendor;
    private long selectedVendorId;
    private SimplBalanceValidation simplEligibilityResponse;
    public User user;
    public ProgressBar pbPay;
    private PaymentMethodsAdapterV2 dataAdapter;
    private ArrayList internalWalletsArray;
    private ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
    private ArrayList<Object> paymentMethodsForDisplay = new ArrayList<>();
    private PaymentMethod selectedPaymentMethod;
    private boolean fromNavbar;
    private long locationId;
    private double totalPrice;
    private long vendorId;
    private String message;
    private String orderPaymentType = "";
    public Order tempOrder;
    private boolean isZetaTried = false;
    private CardPayment cardPaymentObject;
    private JuspayHttpResponse response;
    private SavedCardPayment savedCardPaymentObject;
    private RelativeLayout pbWalletList;
    private AlertDialog redirectionDialog;
    public boolean startTransaction = false;
    public boolean showMswipe = false , fromExpressCheckout = false,checkPostpaidEligibilty = false;
    double convenienceFeeOnInternalWallets=0, convenienceFeeOnExternalWallets=0;
    private String cardBin;
    private MorePaymentOptionListener morePaymentOptionListener;
    public double amountToPay = 0;
    OnLoaderListener loaderListener;
    private double otherUserWallet;
    private boolean fromBookmark = false;
    private RelativeLayout parentRL;
    private String source = "NA";
    private boolean fromBigBasket;
    private double bb_minimum_balance = 0;
    private boolean show_others_expanded = false;
    ViewDetailBillListener viewDetailBillListener;
    private boolean rechargePopup = false, redirectSpaceBooking = false, forSpaceBooking = false;
    String tagForApiRequest = "";

    ArrayList<WalletBreakupItem> walletBreakupItemList;
    ArrayList<PaymentMethod>  walletBreakupItemArrayList;

    public Order prevOrder;
    boolean fromReorder;
    ErrorPopFragment priceChangeErrorHandleDialog;
    public PaymentFragment() {
    }

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    public static PaymentFragment newInstance(Order order, String orderPaymentType,
                                              User user, double userExternalWalletCharge,
                                              double internalWalletPayableAmount,
                                              long locationId, boolean fromExpressCheckout,
                                              MorePaymentOptionListener morePaymentOptionListener,
                                              OnLoaderListener loaderListener, ViewDetailBillListener viewDetailBillListener,double otherUserWallet, boolean redirectSpaceBooking, boolean forSpaceBooking
    ){
        PaymentFragment fragment = new PaymentFragment();
        fragment.order = order;
        fragment.orderPaymentType = orderPaymentType;
        fragment.user = user;
        fragment.userExternalWalletCharge = userExternalWalletCharge;
        fragment.internalWalletPayableAmount = internalWalletPayableAmount;
        fragment.locationId = locationId;
        fragment.fromExpressCheckout = fromExpressCheckout;
        fragment.morePaymentOptionListener = morePaymentOptionListener;
        fragment.viewDetailBillListener = viewDetailBillListener;
        fragment.loaderListener = loaderListener;
        fragment.redirectSpaceBooking = redirectSpaceBooking;
        fragment.forSpaceBooking = forSpaceBooking;
        if(order == null){
            fragment.amountToPay = 0;
        }else{
            fragment.amountToPay = order.getTotalPrice() - otherUserWallet;
        }
        if(fragment.amountToPay<0)
            fragment.amountToPay=0;
        fragment.otherUserWallet = otherUserWallet;
        return fragment;
    }

    PaymentMethod currentPaymentMethod;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        Bundle bundle = this.getArguments();
        if (bundle !=null){
            prevOrder = (Order) bundle.getSerializable("prevOrder");
            fromReorder = bundle.getBoolean("fromReorder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getActivity() != null){
            if(getActivity() instanceof BookmarkPaymentActivity){
                tagForApiRequest = ((BookmarkPaymentActivity) getActivity()).getApiTag();
            }
            else if(getActivity() instanceof PaymentActivity){
                tagForApiRequest = ((PaymentActivity) getActivity()).getApiTag();
            }
        }

        View view;
        if(fromExpressCheckout)
            view = inflater.inflate(R.layout.activity_payment_bookmark, container, false);
        else
            view = inflater.inflate(R.layout.activity_payment, container, false);


        HBMixpanel.getInstance().timeTrack(getActivity(), EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME);

        toolbar = view.findViewById(R.id.tb_pay);
        rvPaymentMethods = view.findViewById(R.id.rv_payment);

        tvTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        tvPay = view.findViewById(R.id.tv_pay);
        btPay = view.findViewById(R.id.bt_pay);

        btPay.setVisibility(View.GONE);
        pbPay = view.findViewById(R.id.pb_pay);
        pbWalletList = view.findViewById(R.id.pb_wallet_list);
        parentRL = view.findViewById(R.id.rl_parent);
        tvViewHistory = view.findViewById(R.id.tv_view_history);

        tvPay.setCompoundDrawables(null,null,null,null);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ImageView ivBack = view.findViewById(R.id.iv_back);
        fromNavbar = getActivity().getIntent().getBooleanExtra(ApplicationConstants.FROM_NAVBAR, false);
        fromBigBasket = getActivity().getIntent().getBooleanExtra(ApplicationConstants.BIG_BASKET, false);

        EventUtil.FbEventLog(getActivity(), EventUtil.SCREEN_OPEN_PAYMENT, EventUtil.SCREEN_HOME);
        fromBookmark = getActivity().getIntent().getBooleanExtra(ApplicationConstants.FROM_BOOKMARK,false);
        if (!fromNavbar){
            toolbar.setVisibility(View.GONE);
        }

        if(fromBigBasket){
            toolbar.setVisibility(View.VISIBLE);
            btPay.setText("Select");
            SharedPrefUtil.putBoolean(ApplicationConstants.BB_SDK_OPEN,false);
        }

        try {
            String source = getActivity().getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
            if (source == null) {
                source = "NA";
            }
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.SCREEN_OPEN_PAYMENT, jo);
            Bundle bundle = new Bundle();
            bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
            EventUtil.FbEventLog(getActivity(), EventUtil.MixpanelEvent.SCREEN_OPEN_PAYMENT, bundle);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject jo = new JSONObject();
                    jo.put(EventUtil.MixpanelEvent.SubProperties.STATUS, "Failed");
                    HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME, jo);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                checkAndCloseActivty();
            }
        });



        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getActivity() != null && getActivity() instanceof BookmarkPaymentActivity && ((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getVisibility() == View.VISIBLE) {
                    if(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().toString().trim().equals("")){
                        AppUtils.showToast("Please enter "+AppUtils.getConfig(getActivity()).getWorkstation_address(), true, 0);
                        return;
                    }else if(order.getDeskReference() != null && order.getDeskReference().trim().equals(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().toString().trim())){
                        paymentBtnClick();
                    }else{
                        DeskReferenceSetting userSettings = new DeskReferenceSetting();
                        userSettings.setDeskReference(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().toString().trim());
                        SimpleHttpAgent<DeskReferenceSetting> settingsHttpAgent = new SimpleHttpAgent<DeskReferenceSetting>(getContext(),
                                UrlConstant.SET_USER_SETTINGS, new ResponseListener<DeskReferenceSetting>() {
                            @Override
                            public void response(DeskReferenceSetting responseObject) {
                                AppUtils.resetCurrentUserDataClass(getActivity().getApplicationContext());
                                order.setDeskReference(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().toString().trim());
                                user.setDeskReference(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().toString().trim());
                                paymentBtnClick();
                            }
                        }, new ContextErrorListener() {
                            @Override
                            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                                AppUtils.hideKeyboard(getActivity(),((BookmarkPaymentActivity) getActivity()).deskReferenceEt);
                                if(error != null && !error.equals("")){
                                    AppUtils.showToast(error, true, 0);
                                }else{
                                    AppUtils.showToast("Some error occurred", true, 0);
                                }

                                if(order.getDeskReference() != null){
                                    ((BookmarkPaymentActivity) getActivity()).deskReferenceEt.setText(order.getDeskReference());
                                    ((BookmarkPaymentActivity) getActivity()).deskReferenceEt.setSelection(((BookmarkPaymentActivity) getActivity()).deskReferenceEt.getText().length());
                                }else{
                                    ((BookmarkPaymentActivity) getActivity()).deskReferenceEt.setText("");
                                }
                            }
                        }, DeskReferenceSetting.class);
                        settingsHttpAgent.post(userSettings,new HashMap<String, JsonSerializer>(), tagForApiRequest);
                    }

                }else{
                    paymentBtnClick();
                }
            }
        });

        tvPay.setVisibility(View.VISIBLE);
        if (fromNavbar) {
            tvPay.setVisibility(View.GONE);
            tvViewHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WalletHistoryActivity.class);
                    intent.putExtra("rechargePopup", rechargePopup);
                    startActivity(intent);
                }
            });
        }

        tvTitle.setText("Payment Options");
        mainApplication = (MainApplication) getActivity().getApplication();
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        cart = mainApplication.getCart();

        if(fromExpressCheckout){
            toolbar.setVisibility(View.GONE);
            btGrandTotal = view.findViewById(R.id.bt_grand_total);
            tvViewDetailBill = view.findViewById(R.id.tv_view_detail_bill);
            llViewBill = view.findViewById(R.id.ll_view_bill);
            setViewDetailVisibility();
        }

        if (!fromNavbar && order!=null) {
            locationId = order.getLocationId();
            totalPrice = order.getTotalPrice();
            vendorId = order.getVendorId();
            getUserDetailsFromServer();
            selectedVendorId = order.getVendorId();
            tvPay.setText(String.format("To Pay : ₹ %.2f", amountToPay));

            selectedOrderVendor = AppUtils.getVendorById(getActivity(), selectedVendorId);
            if (selectedOrderVendor == null) {
                selectedOrderVendor = new Vendor();
                selectedOrderVendor.setId(selectedVendorId);
            }
            message = getActivity().getIntent().getStringExtra("message");
            redirectionDialog = new AlertDialog.Builder(getActivity())
                    .setMessage("You are being redirected for Order Payment").setCancelable(false).create();
        } else {

            btPay.setVisibility(View.GONE);

            if(fromBigBasket){
                tvPay.setVisibility(View.GONE);
                tvViewHistory.setVisibility(View.GONE);
                btPay.setVisibility(View.VISIBLE);
                getBBMinimumBalance();
            }else{
                getUserDetailsFromServer();
            }
            totalPrice = 0;
            vendorId = 0;
        }
        parentRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //empty listener
            }
        });
        LogoutTask.updateTime();

        return view;
    }



    private void paymentBtnClick(){


        if(getActivity() != null && getActivity() instanceof BookmarkPaymentActivity) {
            if (((BookmarkPaymentActivity) getActivity()).isProjectCodeEnabled()){
                if (((BookmarkPaymentActivity) getActivity()).isProjectCodeFieldEmpty()) {
                    AppUtils.showToast(AppUtils.getConfig(getContext()).getProject_code_message(), true, 0);
                }else{
                    sendProjectCodeSettings();
                }
                return;
            }
        }

        preValidate();
    }

    private void preValidate(){
        LogoutTask.updateTime();
        EventUtil.FbEventLog(getActivity(), EventUtil.PAYMENT_CLICK_PAY, EventUtil.SCREEN_CART);
        HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.PAYMENT_PAY_CLICK);
        btPay.setEnabled(false);
        AppUtils.HbLog("dtap", "btpay click disabled on click");
        if (getActivity()!=null && getActivity() instanceof BookmarkPaymentActivity) {
            if (order != null) {
                order.setComment(((BookmarkPaymentActivity) getActivity()).tetComment.getText().toString());
            }
        }
        cleverTapPayClick();


        if (fromReorder && checkPrevOrder()){
            if (prevOrder.getPrice()!=order.getPrice()){
                showOrderPricePopUp(order.getOrderId());
            }else{
                validateAndSubmit();
            }
        }else{
            validateAndSubmit();
        }
    }

    public void sendProjectCodeSettings(){
        if (getActivity()!=null && getActivity() instanceof BookmarkPaymentActivity) {

            ProjectCodeSetting projectCodeSetting = new ProjectCodeSetting();
            projectCodeSetting.setProjectCode(((BookmarkPaymentActivity) getActivity()).projectCodeEt.getText().toString());
            SimpleHttpAgent<ProjectCodeSetting> projectCodeSettingSimpleHttpAgent = new SimpleHttpAgent<ProjectCodeSetting>(getActivity(), UrlConstant.SET_USER_SETTINGS,
                    new ResponseListener<ProjectCodeSetting>() {
                        @Override
                        public void response(ProjectCodeSetting responseObject) {

                            order.setProjectCode(((BookmarkPaymentActivity) getActivity()).projectCodeEt.getText().toString());

                            preValidate();
                        }
                    }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    order.setProjectCode(null);
                    AppUtils.showToast(error, true, 0);
                }
            }, ProjectCodeSetting.class);
            projectCodeSettingSimpleHttpAgent.post(projectCodeSetting,new HashMap<String,JsonSerializer>(), tagForApiRequest);
        }
    }

    public void setViewDetailVisibility(){
        if(order == null)
            return;

        try {
            Vendor selectedOrderVendor = AppUtils.getVendorById(getActivity(), order.getVendorId());
            if (order.getConvenienceFee()<=0 && order.getDeliveryCharge() <= 0 && order.getContainerCharge() <= 0 &&
                    order.getServiceTax(selectedOrderVendor) <= 0 && order.getVat(selectedOrderVendor) <= 0 &&
                    order.getCgst(selectedOrderVendor) <= 0 && order.getSgst(selectedOrderVendor) <= 0 && !AppUtils.getConfig(getActivity()).isShow_gst()) {
                tvViewDetailBill.setVisibility(View.GONE);
            }else{
                tvViewDetailBill.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        llViewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvViewDetailBill.getVisibility()==View.VISIBLE){
                    viewDetailBillListener.viewDetailBillSelected();
                }
            }
        });
//        tvViewDetailBill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(viewDetailBillListener!=null){
//                    viewDetailBillListener.viewDetailBillSelected();
//                }
//            }
//        });
    }

    private boolean checkPrevOrder(){
        ArrayList<OrderProduct> prevOrderProducts = prevOrder.getProducts();
        ArrayList<OrderProduct> currOrderProducts = order.getProducts();
        for (OrderProduct orderProduct: currOrderProducts){
            boolean check = false;
            for (OrderProduct orderProduct1: prevOrderProducts){
                if (orderProduct.getId()==orderProduct1.getId()){
                    check =true;
                    if (! (orderProduct.getQuantity()==orderProduct1.getQuantity())){
                        return false;
                    }
                }
            }
            if (!check){
                return  false;
            }
        }

        return true;
    }

    private void showOrderPricePopUp(String orderId) {
        //event
//        try{
//            HashMap map = new HashMap<>();
//            map.put(CleverTapEvent.PropertiesNames.getOrderId(), orderId);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getReordering_price_change(),map,getActivity().getApplicationContext());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        priceChangeErrorHandleDialog = ErrorPopFragment.Companion.newInstance( "Order Amount has been changed from previous order.\n" +
                "        Do you want to place order? ", "OK", true,ApplicationConstants.GENERAL_ERROR, new ErrorPopFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                priceChangeErrorHandleDialog.dismiss();
               validateAndSubmit();
            }

            @Override
            public void onNegativeInteraction() {
                btPay.setEnabled(true);
               priceChangeErrorHandleDialog.dismiss();

            }
        });
        priceChangeErrorHandleDialog.setCancelable(false);
        priceChangeErrorHandleDialog.show(getActivity().getSupportFragmentManager(), "price change");
    }

    public void checkAndCloseActivty() {
        if(getActivity()==null)
            return;
        if (orderPaymentType != null && orderPaymentType.equals(ApplicationConstants.PAYMENT_ORDER_TYPE_EVENT)) {
            onEventBackDialog();
        } else {

            try {
                JSONObject jo = new JSONObject();
                jo.put(EventUtil.MixpanelEvent.SubProperties.STATUS, "Failed");
                HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME, jo);
            } catch (Exception exp) {
                exp.printStackTrace();
            }

            getActivity().finish();
        }
    }
    private void getBBMinimumBalance(){

        String url = UrlConstant.BB_MINIMUM_BALANCE;
        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {

                        if(responseObject!=null){
                            try {
                                bb_minimum_balance = (Double) ((LinkedTreeMap) responseObject).get("minimumBalance");
                            } catch (Exception e) {
                                bb_minimum_balance = AppUtils.getConfig(getContext()).getBb_minimum_balance();
                                e.printStackTrace();
                            }


                        }else{
                            bb_minimum_balance = AppUtils.getConfig(getContext()).getBb_minimum_balance();
                        }
                        getUserDetailsFromServer();

                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                getUserDetailsFromServer();
                bb_minimum_balance = AppUtils.getConfig(getContext()).getBb_minimum_balance();

            }
        },Object.class);

        objectSimpleHttpAgent.get(tagForApiRequest);


    }

    private void cancelBooking() {
        String url = UrlConstant.EVENTS_BOOKING_CANCEL + order.getBookingId();

        SimpleHttpAgent<Object> objectSimpleHttpAgent = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {

                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        },
                Object.class
        );

        objectSimpleHttpAgent.put("{}", tagForApiRequest);
    }

    private void combineInternalWallets() {
        PaymentMethod internalWallets = new PaymentMethod();
        internalWallets.setId(new Date().getTime());
        internalWallets.setDisplayName("Hungerbox Wallet");
        internalWallets.setWalletSource("internal");
        internalWallets.setName("Hungerbox Wallet");
        internalWallets.setPriority(Integer.MAX_VALUE);
        if (AppUtils.getConfig(getActivity()).isHb_wallet_compulsory()) {
            internalWallets.setCompulsory(true);
        }

        double amount = 0;
        internalWalletsArray = new ArrayList();
        String walletBreakup = "";
        boolean is_company_wallet = false;
        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.getWalletSource().equalsIgnoreCase("internal")) {
                if (paymentMethod.isDefault()) {
                    internalWallets.setIsDefault(1);
                }
                if(paymentMethod.getConvenienceFee()>0)
                    internalWallets.setConvenienceFee(paymentMethod.getConvenienceFee());
                internalWalletsArray.add(paymentMethod);
                amount += paymentMethod.getAmount();
                if (paymentMethod.getEmployeeCanRecharge() == 1)
                {
                    internalWallets.setEmployeeCanRecharge(1);
                }else
                {   is_company_wallet = true;
                    internalWallets.setNonRechargebleWalletBalance
                            (internalWallets.getNonRechargebleWalletBalance()+paymentMethod.getAmount());
                }
                walletBreakup = walletBreakup + "\n" + paymentMethod.getDisplayName();
                if(paymentMethod.isShowReverse())
                    walletBreakup = walletBreakup +" Consumed" + String.format(" ₹ %.2f",paymentMethod.getDisplayAmount(true)) + " ";
                else
                    walletBreakup = walletBreakup +" "+ String.format(" ₹ %.2f",paymentMethod.getDisplayAmount(true)) + " ";

            }
        }


        internalWallets.setAmount(amount);
        internalWallets.setInternalWalletsBalance(walletBreakup);
        internalWallets.setDisplayName(internalWallets.getDisplayName());

        paymentMethods.removeAll(internalWalletsArray);
        paymentMethods.add(0, internalWallets);
    }

    public void getUserDetailsFromServer() {
        btPay.setEnabled(false);
        String url = UrlConstant.USER_DETAIL + "?locationId=" + locationId;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        btPay.setEnabled(true);
                        AppUtils.HbLog("dtap","userDetails");
                        user = responseObject.user;
                        if(getActivity()!=null) {
                            String payload = SharedPrefUtil.getString(ApplicationConstants.PAYLOAD, null);
                            if (AppUtils.getConfig(getActivity()).isShow_mswipe()) {
                                startTransaction = false;
                                ((ParentActivity) getActivity()).LoginMswipe(PaymentFragment.this);
                            } else {
                                getUserPaymentDetails();
                            }
                        }
                        if(!fromNavbar)
                            setDeskReferenceInOrder(responseObject.user);
                        AppUtils.HbLog("dtap", "userDetails");
                        user = responseObject.user;
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if(getActivity()==null)
                            return;
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(false);
                        }
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get(tagForApiRequest);
    }

    private void setDeskReferenceInOrder(User user){
        if(order!=null){
            if(SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0) == 1){
                if(user.getDeskReference()!=null && !user.getDeskReference().trim().isEmpty()){
                    order.setDeskReference(user.getDeskReference());
                }
            }
        }
    }

    private void linkUserPayment(final PaymentMethod externalPaymentMethod) {
        startOtpVerification(externalPaymentMethod);

    }

    private void startOtpVerification(PaymentMethod externalPaymentMethod) {
        LogoutTask.updateTime();
        LogoutTask.getInstance(getActivity()).stopTimer();
        Intent otpVerification = new Intent(getActivity(), OtpVerificationActivity.class);
        otpVerification.putExtra("forResult", true);
        //otpVerification.putExtra("verification_code", walletOtpVerification);
        otpVerification.putExtra("wallet", externalPaymentMethod.getPaymentSource().getPaymentData().getCode());
        otpVerification.putExtra("wallet_name", externalPaymentMethod.getPaymentSource().getPaymentData().getDisplayName());
        otpVerification.putExtra("otp_regex", externalPaymentMethod.getOtpRegex());
        otpVerification.putExtra("name", OtpVerificationActivity.PAYMENT_ACTIVITY);
        startActivityForResult(otpVerification, OTP_VERIFICATION_REQ_CODE);
    }

    private boolean validatePaymentSourcesAndBalance(PaymentMethod externalPaymentMethod)
            throws LowBalanceException, SimplEligibiltyTestException,
            MinimumOrderAmountException, LowJuspayWalletBalanceException, PhoneNumberAbsentException, LowPaytmDirectWalletBalanceException {
        if (internalWalletPayableAmount + userExternalWalletCharge + otherUserWallet < order.getTotalPrice()) {
            throw new LowBalanceException();
        }

        if(externalPaymentMethod != null &&
                externalPaymentMethod.getPaymentMethodType()
                        .equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_WALLET))
        {
            if(user.getPhoneNumber().isEmpty()){
                throw new PhoneNumberAbsentException();
            }
        }else if (externalPaymentMethod != null && externalPaymentMethod.getPaymentSource() != null
                && externalPaymentMethod.getPaymentSource().getPaymentData() != null &&
                externalPaymentMethod.getPaymentSource().getPaymentData().getLinkingRequired() == 1) {

            if(user.getPhoneNumber().isEmpty()){
                throw new PhoneNumberAbsentException();
            }
        }

        if (externalPaymentMethod != null && externalPaymentMethod.getPaymentSource() != null && externalPaymentMethod.getPaymentSource().getPaymentData() != null) {
            if (externalPaymentMethod.getWalletSource().equalsIgnoreCase("external")) {
                if (!externalPaymentMethod.getPaymentSource().getPaymentData().isUserLinked()) {
                    linkUserPayment(externalPaymentMethod);
                    return false;
                } else if (externalPaymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase("simpl")){
                    throw new SimplEligibiltyTestException();
                }
            }
        }

        if (externalPaymentMethod != null &&
                externalPaymentMethod.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_WALLET)
                && externalPaymentMethod.isLinkingAllowed() && externalPaymentMethod.getPaymentDetails() != null && externalPaymentMethod.getPaymentDetails().isLinked()) {
            if (userExternalWalletCharge > externalPaymentMethod.getPaymentDetails().getCurretBalance())
                throw new LowJuspayWalletBalanceException(userExternalWalletCharge - externalPaymentMethod.getPaymentDetails().getCurretBalance());
        }

        if(externalPaymentMethod!=null && externalPaymentMethod.getPaymentSource()!=null
                && externalPaymentMethod.getPaymentSource().getPaymentData()!=null &&
                (externalPaymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_DIRECT_PAYTM_WALLET)
                        || externalPaymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_DIRECT_PAYTM_POSTPAID))
                && externalPaymentMethod.getPaymentSource().getPaymentData().getLinkingRequired()==1 && externalPaymentMethod.getPaymentSource().getPaymentData().isUserLinked()
        ){
            if(userExternalWalletCharge > externalWalletBalance){
                throw new LowPaytmDirectWalletBalanceException(userExternalWalletCharge-externalWalletBalance,externalPaymentMethod.getPaymentSource().getPaymentData().getAddMoneyAllowed()==1);
            }
        }

        if (selectedOrderVendor != null && order.getTotalPrice() < selectedOrderVendor.getMinimumOrderAmount()) {
            throw new MinimumOrderAmountException();
        }
        return true;
    }

    private void validateAndSubmit() {

        OrderPayment paymentMode;
        PaymentMethod externalPaymentMethod = null;
        cart.getPaymentMethods().clear();
        setupCartPaymentmethods(paymentMethods);
        if (cart.getPaymentMethods().size() > 0) {
            externalPaymentMethod = calculateWalletAmounts();
            cart.setPaymentMethod(externalPaymentMethod);
            if (externalPaymentMethod != null) {
                order.setPaymentModes(new OrderPayment(externalPaymentMethod));
                order.setPaymentMethods(externalPaymentMethod);
                if (externalPaymentMethod.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)){
                    order.getPaymentModes().setBinId(cardBin);
                }
            } else {
                order.setPaymentModes(null);
                order.setPaymentMethods(null);
            }
        } else {
            try {
                if (cart != null && cart.getPaymentMethods() != null && cart.getPaymentMethods().size() == 0) {
                    AppUtils.showToast("Please select at least one payment method", true, 2);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            btPay.setEnabled(true);
            return;
        }
        if (cart.containsAndGetInternalPaymentMethod() == null) {
            order.setInternalWalletUsed(false);
        } else {
            order.setInternalWalletUsed(true);
        }

        try {
            if (validatePaymentSourcesAndBalance(externalPaymentMethod)) {
                AppUtils.HbLog("dtap", "inside validateOrSubmit" + btPay.isEnabled());
                checkPaymentSourcesPaymentAndSubmitOrder(order);
            }
        } catch (LowBalanceException e) {
            showLowBalanceAlert();
        } catch (SimplEligibiltyTestException e) {
            isSimplEligible(order);
        }catch (MinimumOrderAmountException e) {
            showOrderErrorDialog(null, "Please order for minimum amount of ₹" + selectedOrderVendor.getMinimumOrderAmount(), false);
        } catch (LowJuspayWalletBalanceException e) {
            goForRechargeOfJuspayWallet(externalPaymentMethod, e.getExtraAmount());
        } catch (PhoneNumberAbsentException e) {
            AccountDetailsUpdateDialog updateDialog = AccountDetailsUpdateDialog.newInstance(new AccountDetailsUpdateDialog.OnFieldChangeListener() {
                @Override
                public void onPasswordChanged(String mobileNum) {
                    user.setPhoneNumber(mobileNum);
                    getUserPaymentDetails();
                }

                @Override
                public void onDismiss() {
                    btPay.setEnabled(true);
                }
            }, ApplicationConstants.PHONE_NUMBER, user.getId());

            updateDialog.show(getActivity().getSupportFragmentManager(),"update_phone");
        }catch (LowPaytmDirectWalletBalanceException e){
            if(e.isAddMoneyAllowed()){
                Intent recharge = new Intent(getContext(),PaytmRechargeActivity.class);
                recharge.putExtra("amount",e.getDeficitAmount());
                recharge.putExtra(ApplicationConstants.PAYMENT_METHOD,externalPaymentMethod);
                recharge.putExtra(ApplicationConstants.CURRENT_EXTRENAL_WALLET_BALANCE,externalWalletBalance);
                startActivityForResult(recharge,PAYTM_DIRECT_RECHARGE_REQUEST);
            }else {
                showLowPaytmBalanceAlert();
            }
        }

        LogoutTask.updateTime();
    }

    private void showLowPaytmBalanceAlert(){

        if(getActivity()==null)
            return;

        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(AppUtils.getConfig(getContext()).getLow_paytm_balance_msg(), "OK", true,
                new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        btPay.setEnabled(true);
                        getUserDetailsFromServer();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                }
        );

        genericPopUpFragment.setCancelable(false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error popup")
                .commitAllowingStateLoss();
    }

    private void goForRechargeOfJuspayWallet(final PaymentMethod externalPaymentMethod, final double extraAmount) {
        if(AppUtils.getConfig(getActivity()).isNew_juspay_recharge()){
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).stopTimer();
            Intent intent = new Intent(getActivity(), JPWebViewActivityNew.class);
            intent.putExtra("amountId",  Math.ceil(extraAmount));
            intent.putExtra("walletId",  externalPaymentMethod.getPaymentDetails().getId());
            intent.putExtra("walletName", externalPaymentMethod.getPaymentMethod());
            intent.putExtra("walletCurrentBalance", externalPaymentMethod.getPaymentDetails().getCurretBalance());
            intent.putExtra(ApplicationConstants.PAYMENT_METHOD, externalPaymentMethod);
            startActivityForResult(intent, JUSPAY_WALLET_RECAHRGE);
        }else{
            HashMap<String, Object> reqPayload = new HashMap<>();
            reqPayload.put("walletId", externalPaymentMethod.getPaymentDetails().getId());
            reqPayload.put("walletName", externalPaymentMethod.getPaymentMethod());
            reqPayload.put("amount", extraAmount);
            final String walletName = externalPaymentMethod.getPaymentMethod();

            SimpleHttpAgent<JPWalletRechargeResponse> objectSimpleHttpAgent = new SimpleHttpAgent<JPWalletRechargeResponse>(
                    getActivity(),
                    UrlConstant.JUSPAY_WALLET_RECHARGE,
                    new ResponseListener<JPWalletRechargeResponse>() {
                        @Override
                        public void response(JPWalletRechargeResponse responseObject) {
                            if(getActivity()==null)
                                return;
                            if (responseObject != null) {
                                startJuspayWalletRecharge(responseObject.getJpWalletRecharges().get(0), extraAmount, walletName, externalPaymentMethod);
                            }
                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            if(getActivity()==null)
                                return;
                            showOrderErrorDialog(order, "Unable to proceed with wallet topup. Please try again", true);
                        }
                    },
                    JPWalletRechargeResponse.class
            );

            objectSimpleHttpAgent.post(reqPayload, new HashMap<String, JsonSerializer>(), tagForApiRequest);
        }
    }

    private void startJuspayWalletRecharge(JPWalletRecharge jpWalletRecharge, double amount, String walletName, PaymentMethod externalPaymentMethod) {
        if (jpWalletRecharge.getPaymentUrl().isEmpty()) {
            showOrderErrorDialog(order, "Unable to proceed with wallet topup", false);
        } else {
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).stopTimer();
            Intent intent = new Intent(getActivity(), JPWebViewActivityNew.class);
            intent.putExtra(ApplicationConstants.PAYMENT_URL, jpWalletRecharge.getPaymentUrl());
            intent.putExtra(ApplicationConstants.RETURN_URL, jpWalletRecharge.getReturnUrl());
            intent.putExtra("transactionId", jpWalletRecharge.getTransactionId());
            intent.putExtra("amountId", amount + "");
            intent.putExtra("walletName",walletName);
            intent.putExtra(ApplicationConstants.PAYMENT_METHOD, externalPaymentMethod);
            startActivityForResult(intent, JUSPAY_WALLET_RECAHRGE);
        }

    }

    double externalWalletBalance = 0;
    private PaymentMethod calculateWalletAmounts() {
        PaymentMethod internalPaymentMethod = null, externalPaymentMehtod = null;
        externalWalletBalance = 0;
        for (PaymentMethod paymentMethod : cart.getPaymentMethods()) {
            if (paymentMethod.getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL)) {
                internalPaymentMethod = paymentMethod;
            } else {
                externalPaymentMehtod = paymentMethod;
            }
        }

        if (internalPaymentMethod != null) {
            if (internalPaymentMethod.getAmount() >= order.getTotalPrice()) {
                internalWalletPayableAmount = order.getTotalPrice();
            } else {
                internalWalletPayableAmount = internalPaymentMethod.getAmount();
            }
        } else {
            internalWalletPayableAmount = 0;
        }

        if (externalPaymentMehtod != null) {
            if (internalWalletPayableAmount == order.getTotalPrice()) {
                userExternalWalletCharge = 0;
            } else {
                userExternalWalletCharge = order.getTotalPrice() - internalWalletPayableAmount;
            }
            externalWalletBalance = externalPaymentMehtod.getAmount();
            AppUtils.HbLog("External_Wallet",externalWalletBalance+"");
            externalPaymentMehtod.setAmount(userExternalWalletCharge);
        } else {
            userExternalWalletCharge = 0;
        }

        return externalPaymentMehtod;
    }

    private void setupCartPaymentmethods(ArrayList<PaymentMethod> paymentMethods) {
        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.isSelected()) {
                cart.getPaymentMethods().add(paymentMethod);
            }
        }
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

    private void showLowBalanceAlert() {
        if(rechargePopup){
            String message = "Your wallet balance is too low to place order. Click on Recharge to proceed.";
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(message,
                    "RECHARGE", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            btPay.setEnabled(true);
                            AppUtils.HbLog("dtap", "balance alert");
                            navigateToRecharge();
                        }

                        @Override
                        public void onNegativeInteraction() {
                            btPay.setEnabled(true);
                            AppUtils.HbLog("dtap", "userDetails");
                        }
                    });

            genericPopUpFragment.setCancelable(false);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();
        }
        else{
            String message = AppUtils.getConfig(getActivity()).getMinimum_balance_error();
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(message,
                    "Ok", true , new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            btPay.setEnabled(true);
                            AppUtils.HbLog("dtap", "low balance popup");
                        }

                        @Override
                        public void onNegativeInteraction() {
                            btPay.setEnabled(true);
                        }
                    });

            genericPopUpFragment.setCancelable(false);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();
        }
    }

    private void navigateToRecharge() {
        Intent intent = new Intent(getActivity(), RechargeActivity.class);
        intent.putExtra(ApplicationConstants.AMOUNT_TO_PAY, "" + Math.ceil(amountToPay - internalWalletPayableAmount));
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Payment Alert");
        intent.putExtra(ApplicationConstants.ORDER_AFTER_RECHARGE, true);
        startActivityForResult(intent, PERSONAL_WALLET_RECHARGE);
    }

    public void showOrderErrorDialog(Order order, String error, boolean clearOrder) {
        //return in case of activity being closed
        try{
            if (!isRunning)
                return;
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).startTimer();
            if (clearOrder) {
                clearLocalOrder();
            }
            btPay.setText("Place Order");
            pbPay.setVisibility(View.GONE);

            if(!error.isEmpty()){
                ErrorPopFragment freeOrderErrorHandleDialog = ErrorPopFragment.Companion.newInstance(error, "OK", false,ApplicationConstants.PAYMENT_FAILED_IMAGE, new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        afterErrorPopup();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
                freeOrderErrorHandleDialog.setCancelable(false);
                freeOrderErrorHandleDialog.show(getActivity().getSupportFragmentManager(), "free_order_error");
            }
            else{
                afterErrorPopup();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void afterErrorPopup(){
        btPay.setEnabled(true);
        pbWalletList.setVisibility(View.GONE);
        getUserDetailsFromServer();
    }

    private void clearLocalOrder() {
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        mainApplication.clearOrder();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        if (cart != null)
            cart.setLastUpdatedTime(new Date().getTime());

        if(fromBigBasket) {

            if(SharedPrefUtil.getBoolean(ApplicationConstants.BB_SDK_OPEN,false)){
                SharedPrefUtil.putBoolean(ApplicationConstants.BB_SDK_OPEN,false);
                showBBdMsg("Thank you for using BigBasket!");
            }
        }

    }

    private void showBBdMsg(String reason) {
        pbPay.setVisibility(View.GONE);
        GenericPopUpFragment genericPopUpFragment =
                GenericPopUpFragment.newInstance(reason,
                        "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveInteraction() {
                              try{
                                  getActivity().finish();
                              }catch (Exception exp){
                                  exp.printStackTrace();
                              }
                            }

                            @Override
                            public void onNegativeInteraction() {

                            }
                        });

        genericPopUpFragment.setCancelable(false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error popup")
                .commitAllowingStateLoss();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    private void isSimplEligible(final Order order) {
        if (simplEligibilityResponse == null) {
            simplPaymentValidation(true, order);
            return;
        }
        if (simplEligibilityResponse.getData().isSuccess()) {
            SharedPrefUtil.putBoolean(ApplicationConstants.HAS_USED_SIMPL, true);
            checkDeskRefAndSubmit(order);
        } else if (simplEligibilityResponse.getData().getErrorCode() == null) {

            ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance("Something went wrong\n\n" +
                            "Please choose another payment source.",
                    "Try Another Method",ApplicationConstants.PAYMENT_FAILED_IMAGE, new ErrorPopFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            btPay.setEnabled(true);

                        }

                        @Override
                        public void onNegativeInteraction() {
                            btPay.setEnabled(true);

                        }
                    });

            genericPopUpFragment.setCancelable(false);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();
        } else if (simplEligibilityResponse.getData().getErrorCode().equalsIgnoreCase("unable_to_process")) {
            AppUtils.HbLog("dtap", "simpl");

            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Unable to Process this transaction\n\n" +
                            "You are trying to make a transaction exceeding your credit limit.",
                    "Try Another Method", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            btPay.setEnabled(true);

                        }

                        @Override
                        public void onNegativeInteraction() {
                            btPay.setEnabled(true);

                        }
                    });

            genericPopUpFragment.setCancelable(false);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();

        } else if (simplEligibilityResponse.getData().getErrorCode().equalsIgnoreCase("insufficient_credit")) {
            btPay.setEnabled(true);
            AppUtils.HbLog("dtap", "simpl insufficient");
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance("Insufficient Credit\n\n"
                            + "Available Credit - " + simplEligibilityResponse.getData().getAvailableCredit() +
                            "\n\nYou don't have sufficient balance to make this transaction. Please click on Pay to pay your previous bill."
                    , "Pay", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {

                            String urlEncoder = "";
                            try {
                                urlEncoder = URLEncoder.encode("{\"company_id\":" + AppUtils.getConfig(getActivity()).getCompany_id() + "}", "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String redirectUrl = simplEligibilityResponse.getData().getRedirectionUrl() + "&merchant_payload=" + urlEncoder;
                            SimplWebViewFragment simplWebViewFragment = SimplWebViewFragment.newInstance(redirectUrl, new SimplWebViewFragment.OnRedirectListener() {
                                @Override
                                public void onRedirect(String url, boolean isSuccess, String reason) {
                                    if (isSuccess) {
                                    } else {
                                        showPaymentErrorAlert(reason);
                                    }
                                }

                                @Override
                                public void onBackClicked(Map<String, String> postData) {
                                    paymentRejectedbyBackRegisterfor(order);
                                }
                            });
                            simplWebViewFragment.show(getActivity().getSupportFragmentManager(), "Payment_WebView");
                        }

                        @Override
                        public void onNegativeInteraction() {

                        }
                    });

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();
        } else if (simplEligibilityResponse.getData().getErrorCode().equalsIgnoreCase("pending_dues")) {
            btPay.setEnabled(true);
            AppUtils.HbLog("dtap", "pending dues");
            GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(
                    "Your payment is due. Please click on Pay to settle your Simpl bill"
                    , "Pay", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {

                            String urlEncoder = "";
                            try {
                                urlEncoder = URLEncoder.encode("{\"company_id\":" + AppUtils.getConfig(getActivity()).getCompany_id()
                                        + ",\"source\":android" + "}", "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String redirectUrl = simplEligibilityResponse.getData().getRedirectionUrl() + "&merchant_payload=" + urlEncoder;
                            SimplWebViewFragment simplWebViewFragment = SimplWebViewFragment.newInstance(redirectUrl, new SimplWebViewFragment.OnRedirectListener() {
                                @Override
                                public void onRedirect(String url, boolean isSuccess, String reason) {
                                    if (isSuccess) {
                                    } else
                                        showPaymentErrorAlert(reason);
                                }

                                @Override
                                public void onBackClicked(Map<String, String> postData) {
                                    paymentRejectedbyBackRegisterfor(order);
                                }
                            });
                            simplWebViewFragment.show(getActivity().getSupportFragmentManager(), "Payment_WebView");
                        }

                        @Override
                        public void onNegativeInteraction() {

                        }
                    });

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(genericPopUpFragment, "error popup")
                    .commitAllowingStateLoss();

        }
    }

    private void isPhonePeEligible(final Order order) {

        HashMap<String, String> map= order.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload();

        TransactionRequest transactionRequest2 = new TransactionRequestBuilder()
                .setData(map.get("base64Body"))
                .setChecksum(map.get("checksum"))
                .setUrl(map.get("apiEndPoint"))
                .build();

        try {
            startActivityForResult(PhonePe.getTransactionIntent(getActivity(), transactionRequest2), PHONE_PE_SDK_REQUEST_CODE);
        } catch (PhonePeInitException e) {
            e.printStackTrace();
        }
    }

    private void showPaymentErrorAlert(String reason) {
        if (reason.length() == 0) {
            reason = "Oops! Your Payment Failed.\n"
                    + "Please try again later or recharge your HungerBox Wallet to Place the order";
        }
        btPay.setText("Place Order");
        pbPay.setVisibility(View.GONE);
        ErrorPopFragment genericPopUpFragment =
                ErrorPopFragment.Companion.newInstance(reason,
                        "OK",false, ApplicationConstants.PAYMENT_FAILED_IMAGE, new ErrorPopFragment.OnFragmentInteractionListener() {
                            @Override
                            public void onPositiveInteraction() {
                                btPay.setEnabled(true);
                            }

                            @Override
                            public void onNegativeInteraction() {

                            }
                        });

        genericPopUpFragment.setCancelable(false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error popup")
                .commitAllowingStateLoss();
    }

    private void simplPaymentValidation(final boolean placeOrder, final Order order) {

        String payload = SharedPrefUtil.getString(ApplicationConstants.PAYLOAD, null);

        PayloadData payloadData = new PayloadData();
        if (payload != null && !AppUtils.getConfig(getActivity()).isStop_simple_payload()) {
            payloadData.setSimplPayload(payload);
        }

        String url = UrlConstant.SIMPL_TRANSACTION_VALIDATION
                + "?code=" + "simpl&transactionAmount=" + userExternalWalletCharge+"&vendorId="+vendorId;
        SimpleHttpAgent simpleHttpAgent = new SimpleHttpAgent(getActivity(), url,
                new ResponseListener<SimplBalanceValidation>() {
                    @Override
                    public void response(final SimplBalanceValidation responseObject) {
                        if(getActivity()==null)
                            return;
                        simplEligibilityResponse = responseObject;
                        if (placeOrder)
                            isSimplEligible(order);
                    }
                }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                simplEligibilityResponse = null;
            }
        }, SimplBalanceValidation.class);
        simplEligibilityResponse = null;
        simpleHttpAgent.post(payloadData, new HashMap<String, JsonSerializer>(), tagForApiRequest);

    }

    private void checkPaymentSourcesPaymentAndSubmitOrder(Order order) {
        PaymentMethod paymentMethodToUse = null;
        if (order.getPaymentModes() != null)
            paymentMethodToUse = order.getPaymentMethod();

        if (paymentMethodToUse != null) {
            if (paymentMethodToUse.isLinkingAllowed() &&
                    (paymentMethodToUse.getPaymentDetails() == null
                            || !paymentMethodToUse.getPaymentDetails().isLinked())) {
                startJuspayWalletLinking(paymentMethodToUse);
                return;
            }

            if (!paymentMethodToUse.isLinkingAllowed() &&
                    paymentMethodToUse.getPaymentDetails() != null) {
                order.getPaymentModes().setPaymentMethodId("");
            }

            if (paymentMethodToUse.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_NETBANKING)) {
                startNetBankingSelection(paymentMethodToUse);
                return;
            }
            if (paymentMethodToUse.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
                    && paymentMethodToUse.getPaymentDetails() == null) {
                if (cardPaymentObject == null) {
                    handleCardPayment(paymentMethodToUse);
                    return;
                }
            } else if (paymentMethodToUse.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
                    && paymentMethodToUse.getPaymentDetails() != null) {
                if (savedCardPaymentObject == null) {
                    handleCardPayment(paymentMethodToUse);
                    return;
                }
            }
        }

        if(paymentMethodToUse!=null && checkIfPaytmUpi(paymentMethodToUse)){
            UpiMethod upiMethod =  selectedUpiMethod();
            //Sending payment method in case of Upi from the app with which payment is done
            if (upiMethod != null) {
                order.getPaymentModes().setPaymentMethodType(upiMethod.getMethod());
            }
        }

        checkDeskRefAndSubmit(order);
    }

    private void startNetBankingSelection(PaymentMethod paymentMethodToUse) {
        Intent intent = new Intent(getActivity(), NetbankingActivity.class);
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToUse);
        startActivityForResult(intent, JUSPAY_NETBANK_SELECT);
    }

    private void startJuspayWalletLinking(PaymentMethod paymentMethodToUse) {
        Intent intent = new Intent(getActivity(), JPOtpverificationActivity.class);
        intent.putExtra(ApplicationConstants.OTP_USER, user);
        intent.putExtra(ApplicationConstants.PAYMENT_METHOD, paymentMethodToUse);
        startActivityForResult(intent, JUSPAY_OTP_LINK_CODE);
    }

    private void checkDeskRefAndSubmit(final Order order) {
        try {

            if (getActivity() != null && getActivity() instanceof BookmarkPaymentActivity) {

                if (guestOrderCheck(cart) && cart.getGuestType().equals(ApplicationConstants.GUEST_TYPE_OFFICIAL)){
                    affilationPopUp(order);
                }
                else {
                    submitOrder(order);
                }
            } else {
                submitOrder(order);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    private ArrayList orderInfoList;
    public void affilationPopUp(Order order){
        String title = "Please add the affiliations of your Guests to place guest Order";
        GuestNamesFragment guestNamesFragment = GuestNamesFragment.newInstance(title, "Submit", mainApplication.getGuestItemCount(), new GuestNamesFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction(ArrayList guestList) {
                orderInfoList = guestList;
                order.orderGuestInfo = orderInfoList;
                order.setGuestType(cart.getGuestType());
//                                    btPay.setEnabled(false);
                //TODO take user to payment
                //validateAndSubmit();
                submitOrder(order);

            }

            @Override
            public void onNegativeInteraction() {
                submitOrder(order);
            }
        });
        guestNamesFragment.show(getActivity().getSupportFragmentManager(), "Guest Names");
    }

    private void submitOrder(final Order order) {


        if(fromBigBasket){

            HashMap<String, Object> reqPayload = new HashMap<>();
            if(cart.getExternalPaymentMethod()!=null && cart.getExternalPaymentMethod().getPaymentSource()!=null
                    && cart.getExternalPaymentMethod().getPaymentSource().getPaymentData()!=null ) {
                reqPayload.put("payment_method", cart.getExternalPaymentMethod().getPaymentSource().getPaymentData().getCode());
            }
            if(cart.containsAndGetInternalPaymentMethod()!=null) {
                reqPayload.put("internal_wallet_used", "1");
            }else{
                reqPayload.put("internal_wallet_used", "0");
            }

            reqPayload.put("location_id", SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID,0)+"");
            reqPayload.put("occasion_id", getActivity().getIntent().getLongExtra(ApplicationConstants.OCASSION_ID,0)+"");

            pbPay.setVisibility(View.VISIBLE);
            SimpleHttpAgent<BigBasketGenerateSession> objectSimpleHttpAgent = new SimpleHttpAgent<BigBasketGenerateSession>(
                    getActivity(),
                    UrlConstant.BIG_BASKET,
                    new ResponseListener<BigBasketGenerateSession>() {
                        @Override
                        public void response(BigBasketGenerateSession responseObject) {
                            pbPay.setVisibility(View.GONE);
                            if(getActivity()==null)
                                return;
                            Log.i("bbinstant", responseObject+"");
                            BBSDKParam param = null;
                            try {
                                SharedPrefUtil.putBoolean(ApplicationConstants.BB_SDK_OPEN,true);
                                param = new BBSDKParam.SDKBuilder()
                                        .withUserName(SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME,"")) //Optional
                                        .withEmail(SharedPrefUtil.getString(ApplicationConstants.PREF_USER_EMAIL,""))	  //Optional
                                        .withMerchantId(ApplicationConstants.BIG_BASKET_MID)
                                        .withLocationId(SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID,0)+"")
                                        .withBalanceAvailable(true)
                                        .withAppType(ApplicationConstants.BIG_BASKET_MERCHANT_TYPE)
                                        .withUserToken(responseObject.getUser_token())
                                        .withPhoneNum(AppUtils.getConfig(getActivity()).getCountry_code() + ""+SharedPrefUtil.getString(ApplicationConstants.USER_PHONE,""))
                                        .build();
                            } catch (InitializeException e) {
                                SharedPrefUtil.putBoolean(ApplicationConstants.BB_SDK_OPEN,false);
                                e.printStackTrace();
                            }


                            param.startBBApp(getActivity());
                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            pbPay.setVisibility(View.GONE);
                            if(getActivity()==null)
                                return;
                            showOrderErrorDialog(order, "Unable to proceed with wallet topup. Please try again", true);
                        }
                    },
                    BigBasketGenerateSession.class
            );

            objectSimpleHttpAgent.post(reqPayload, new HashMap<String, JsonSerializer>(),tagForApiRequest);

            // add tagAPIrequest in next build

            LogoutTask.updateTime();
            return;
        }

        LogoutTask.updateTime();
        btPay.setText("Placing your order...");
        pbPay.setVisibility(View.VISIBLE);
        String url = UrlConstant.POST_ORDER_URL;
        SimpleHttpAgent<OrderResponse> orderSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        pbPay.setVisibility(View.GONE);
                        if(getActivity()==null) {
                            return;
                        }
                        if (responseObject != null && responseObject.order != null) {
                            handleResponse(responseObject, order);
                            if (responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)) {
                                startOrderPayment(responseObject.order);
                            } else if(responseObject.order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)){
                                cleverTapPlaceOrder("payment_fail");
                                showOrderErrorDialog(null, "Oops! something went wrong. Please try again", true);
                            } else {

                                EventUtil.FbEventLog(getActivity(), EventUtil.CART_ORDER_PLACED, EventUtil.SCREEN_PAYMENT);
                                HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.CART_ORDER_PLACED);
                                order.setId(responseObject.order.getId());
                                order.setCreatedAt((DateTimeUtil.adjustCalender(getActivity()).getTimeInMillis() / 1000l));
                                if (user != null)
                                    updateOrderSummary(order);
                                clearLocalOrder();
                                order.setOrderStatus(responseObject.order.getOrderStatus());
                                startOrderView(responseObject.getOrder());
                            }
                            updateLocationUpdate();
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if(getActivity()==null)
                            return;
                        try {
                            String errorMessage = "NA";
                            if (errorResponse != null && errorResponse.message != null) {
                                errorMessage = errorResponse.message;
                            }
                            JSONObject jo = new JSONObject();
                            jo.put(EventUtil.MixpanelEvent.SubProperties.ERROR, errorMessage);
                            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.CART_ORDER_ERROR,jo);

                        }catch (Exception exp){
                            exp.printStackTrace();
                        }
                        LogoutTask.updateTime();
                        AppUtils.HbLog("dtap", "order error");
                        btPay.setText("Place Order");
                        pbPay.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showOrderErrorDialog(null, "Sorry, there is some connectivity issue, Please try again", false);
                        } else if (errorResponse != null && errorResponse.getError().getOrderIds().size() > 0) {
                            order.setId(errorResponse.error.orderIds.get(0));
                            order.setCreatedAt((DateTimeUtil.adjustCalender(getActivity()).getTimeInMillis() / 1000l));
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
        orderSimpleHttpAgent.post(order, new HashMap<String, JsonSerializer>(), tagForApiRequest);
    }



    private void handleResponse(OrderResponse responseObject, Order order) {
        this.order.setId(responseObject.getOrder().getId());
        this.order.setOrderStatus(responseObject.getOrder().getOrderStatus());
//        this.order.setOrderFromServer(true);
    }

    private void updateLocationUpdate() {
    }

    private boolean checkZetaAppInstalled(Order order) {
        if (!AppUtils.getConfig(getActivity()).isNew_zeta_enabled() || isZetaTried)
            return false;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("zetapay://payAuth"));
        PackageManager packageManager = getActivity().getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {

            String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

            if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa") && targetEnv.equals("qa")) {
                intent.setPackage("in.zeta.android.debug");
            } else {
                intent.setPackage("in.zeta.android");
            }

            ZetaRequest zetaRequest = new ZetaRequest();
            ZetaRequestBase requestBase = new ZetaRequestBase();

            if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("qa") && targetEnv.equals("qa")) {
                intent.setPackage("in.zeta.android.debug");
                zetaRequest.setOrderId("APP2APP_" + order.getOrderId());
            } else {
                intent.setPackage("in.zeta.android");
                zetaRequest.setOrderId("PWZA2A_" + order.getOrderId());
                requestBase.setZetaCertificate(new ZetaCertificate("PROD"));
            }

            zetaRequest.setAmountValue(String.format("%.2f", (userExternalWalletCharge)));
            requestBase.setZetaRequest(zetaRequest);
            intent.putExtra("data", new Gson().toJson(requestBase));
            try {
                isZetaTried = true;
                startActivityForResult(intent, ZETA_REQUEST_CODE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private void startOrderPayment(final Order orderForPayment) {
        if (!isRunning)
            return;
        LogoutTask.updateTime();
        LogoutTask.getInstance(getActivity()).stopTimer();

        PaymentMethod externalPaymentMethodUsed = cart.getExternalPaymentMethod();

        if (externalPaymentMethodUsed == null) {
            cleverTapPlaceOrder("payment_fail");
            showOrderErrorDialog(order, "You modified the payment method while processing the payment", true);
            return;
        }

        tempOrder = orderForPayment;

        if(externalPaymentMethodUsed!=null && externalPaymentMethodUsed.getPaymentSource()!=null && externalPaymentMethodUsed.getPaymentSource().getPaymentData() != null &&
                externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode() != null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PINELBAS) ){
            startPineLabsTransaction(orderForPayment,externalPaymentMethodUsed);

        }else if(externalPaymentMethodUsed!=null && externalPaymentMethodUsed.getPaymentSource()!=null && externalPaymentMethodUsed.getPaymentSource().getPaymentData() != null &&
                externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode() != null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_DYNAMIC_QR_PAYTM)){
            startDynamicQrPaytmTrasnaction(orderForPayment,externalPaymentMethodUsed);
        } else if (externalPaymentMethodUsed.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_WALLET) && !externalPaymentMethodUsed.isLinkingAllowed()) {
            startPaymentForWallet(externalPaymentMethodUsed, orderForPayment, order);
        } else if (externalPaymentMethodUsed.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_WALLET) &&
                (orderForPayment.getOrderPaymentResponse() == null || orderForPayment.getOrderPaymentResponse().getOrderPaymentData() == null ||
                        orderForPayment.getOrderPaymentResponse().getOrderPaymentData().getAction().isEmpty()
                )) {
            cleverTapPlaceOrder("payment_fail");
            showOrderErrorDialog(order, "Oops! something went wrong. Please try again", true);
            return;
        }
        else if (externalPaymentMethodUsed.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)) {
            if (externalPaymentMethodUsed.getPaymentDetails() == null)
                cardPaymentObject.setOrderId(orderForPayment.getOrderId());
            else
                savedCardPaymentObject.setOrderId(orderForPayment.getOrderId());
            startCardPayment(externalPaymentMethodUsed, orderForPayment);
        }
        else if (externalPaymentMethodUsed.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_NETBANKING)) {
            startNBPayment(externalPaymentMethodUsed, orderForPayment, order);
        } else if (externalPaymentMethodUsed!=null && externalPaymentMethodUsed.getPaymentSource()!=null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.MSWIPE_SODEXO)) {
            ((ParentActivity)getActivity()).startMswipeTransaction(PaymentFragment.this);
            return;
        } else if (externalPaymentMethodUsed!=null && externalPaymentMethodUsed.getPaymentSource()!=null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.MSWIPE_CREDIT_DEBIT)) {
            ((ParentActivity)getActivity()).startMswipeTransaction(PaymentFragment.this);
            return;
        } else if(externalPaymentMethodUsed != null && externalPaymentMethodUsed.getPaymentSource() != null && externalPaymentMethodUsed.getPaymentSource().getPaymentData() != null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode() != null && externalPaymentMethodUsed.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase("Phonepeupi")){
            isPhonePeEligible(orderForPayment);
        }
        else if(checkIfPaytmUpi(externalPaymentMethodUsed)){
            startPaytmUpiPayment(orderForPayment);
        }
        else {
            if (orderForPayment.getOrderPaymentResponse() == null || orderForPayment.getOrderPaymentResponse().getOrderPaymentData() == null) {
                showOrderErrorDialog(order, "Error in initiating the payment", true);
                return;
            }
            OrderPaymentData orderPaymentData = orderForPayment.getOrderPaymentResponse().getOrderPaymentData();
            orderPaymentData.setOrderId(orderForPayment.getId());
            orderPaymentData.setOrderRef(orderForPayment.getOrderId());
            orderPaymentData.getPaymentPayload().put("order_id", String.valueOf(orderForPayment.getId()));
            orderPaymentData.getPaymentPayload().put("order_ref", orderForPayment.getOrderId());

            if (!orderPaymentData.getAction().contains("sodexo") && orderPaymentData.getAction().contains("zeta") && !isZetaTried ) {
                if(!checkZetaAppInstalled(orderForPayment)){
//                    paymentRejectedbyBackRegisterfor(orderForPayment,true,true);
                    Intent intent = new Intent(getActivity(), OrderWebPaymentActivity.class);
                    intent.putExtra(ApplicationConstants.URL, orderPaymentData.getAction());
                    intent.putExtra(ApplicationConstants.HTTP_METHOD, orderPaymentData.getMethod());
                    intent.putExtra(ApplicationConstants.POST_DATA, orderPaymentData.getPaymentPayload());
                    intent.putExtra(ApplicationConstants.ORDER, orderForPayment);
                    startActivityForResult(intent, ORDER_PAYMENT_WEB_REQUEST);
                }

            } else {
                Intent intent = new Intent(getActivity(), OrderWebPaymentActivity.class);
                intent.putExtra(ApplicationConstants.URL, orderPaymentData.getAction());
                intent.putExtra(ApplicationConstants.HTTP_METHOD, orderPaymentData.getMethod());
                intent.putExtra(ApplicationConstants.POST_DATA, orderPaymentData.getPaymentPayload());
                intent.putExtra(ApplicationConstants.ORDER, orderForPayment);
                startActivityForResult(intent, ORDER_PAYMENT_WEB_REQUEST);
            }
        }

    }

    private void startPineLabsTransaction(Order order,PaymentMethod paymentMethod){

        if(getActivity()==null)
            return;
        Intent intent = new Intent(getActivity(),PineLabTransactionStatus.class);
        intent.putExtra("order",order);
        intent.putExtra("payment",paymentMethod);
        startActivityForResult(intent,PINE_LBAS_REQUEST);

    }

    private void startDynamicQrPaytmTrasnaction(Order order , PaymentMethod paymentMethod){
        if(getActivity()==null)
            return;


        Intent intent = null;
        if(order.getOrderPaymentResponse() != null && order.getOrderPaymentResponse().getOrderPaymentData() != null &&
                order.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload() != null &&
                order.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload().get("qrType")!=null &&
                order.getOrderPaymentResponse().getOrderPaymentData().getPaymentPayload().get("qrType").equalsIgnoreCase(ApplicationConstants.QR_PAYTM)){
            intent = new Intent(getActivity(),DynamicQrPaytmTransaction.class);
        }else{
            intent = new Intent(getActivity(),DynamicQrPaytmTransactionUpi.class);
        }
        intent.putExtra("order",order);
        intent.putExtra("payment",paymentMethod);
        startActivityForResult(intent,DYNAMIC_QR_PAYTM_REQUEST);
    }

    private void startPaytmUpiPayment(Order orderPayment){
        if(getActivity()==null)
            return;

        UpiMethod selectedUpi = selectedUpiMethod();
        if(selectedUpi!=null) {
            Intent ins = new Intent(Intent.ACTION_VIEW);
            ComponentName name = new ComponentName(selectedUpi.getAppInfoPkgName(), selectedUpi.getAppInfoActivityName());
            ins.setComponent(name);
            ins.setData(Uri.parse(URLDecoder.decode(orderPayment.getOrderPaymentResponse().getOrderPaymentData().getAction())));
            startActivityForResult(ins, PAYTM_INTENT_UPI);

        }else{
            showOrderErrorDialog(null,"Oops! something went wrong. Please try again",true);
        }
    }


    private void startNBPayment(PaymentMethod externalPaymentMethodUsed, Order orderForPayment, Order order) {
        if (redirectionDialog != null)
            redirectionDialog.dismiss();
        NetBankingPayment nbPayment = new NetBankingPayment();
        nbPayment.setOrderId(orderForPayment.getOrderId());
        nbPayment.setBank(externalPaymentMethodUsed.getNetBankingMethods().getNetBankingMethods().get(0).getMethod());
        startPaymentOnJuspay(externalPaymentMethodUsed, nbPayment);
    }

    private void startCardPayment(PaymentMethod externalPaymentMethodUsed, Order order) {

        if (externalPaymentMethodUsed.getPaymentDetails() == null) {
            CardPayment tempCardPayment = new CardPayment();
            tempCardPayment = cardPaymentObject;
            startPaymentOnJuspay(externalPaymentMethodUsed, tempCardPayment);
        } else
            startPaymentOnJuspay(externalPaymentMethodUsed, savedCardPaymentObject);
    }

    private void saveCardToJusPay(final CardPayment cardPaymentObject, final Order tempOrder) {

        btPay.setText("Validating Your Card..");
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int connectTimeout = 3000;
                    int readTimeout = 3000;

                    NetUtils nu = new NetUtils(connectTimeout, readTimeout);
                    try {

                        String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

                        if (BuildConfig.BUILD_TYPE.contains("qa") && targetEnv.equals("qa"))
                            Environment.configure(Environment.SANDBOX, UrlConstant.JUSPAY_MERCHANT_ID);
                        else
                            Environment.configure(Environment.PRODUCTION, UrlConstant.JUSPAY_MERCHANT_ID);
                        response = ExpressCheckoutService.tokenize(cardPaymentObject, nu);
                        SimpleHttpAgent<SavedCardData> addCard = new SimpleHttpAgent<SavedCardData>
                                (getActivity(), UrlConstant.JUSPAY_ADD_CARD,
                                        new ResponseListener<SavedCardData>() {
                                            @Override
                                            public void response(SavedCardData responseObject) {
                                                if(getActivity()==null)
                                                    return;
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Snackbar.make(rvPaymentMethods, "Card Saved Successfully", Snackbar.LENGTH_SHORT).show();

//                                                        try {
//                                                            HashMap<String, Object> map = new HashMap<>();
//                                                            map.put(CleverTapEvent.PropertiesNames.getSource(), source);
//                                                            map.put(CleverTapEvent.PropertiesNames.getUser_action(), "add");
//                                                            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getAdd_card(), map, getActivity());
//                                                        }catch (Exception e){
//                                                            e.printStackTrace();
//                                                        }


                                                        if(!fromNavbar)
                                                        {
                                                            validateAndSubmit();
                                                        }else{
                                                            getUserPaymentDetails();
                                                        }
                                                    }
                                                });
                                                AppUtils.HbLog("card_saved_response", responseObject.getToken());
                                            }
                                        }, new ContextErrorListener() {
                                    @Override
                                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                                        AppUtils.HbLog("card_save_error", error);
                                        savedCardPaymentObject = null;
                                        PaymentFragment.this.cardPaymentObject = null;
                                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                                            showNoNetFragment(true);
                                        }else {
                                            if(error!=null && !error.equals("")) {
                                                showOrderErrorDialog(null, error, false);
                                            }else{
                                                showOrderErrorDialog(null,"Unable to verify your card",false);
                                            }
                                        }
                                        //Snackbar.make(rvPaymentMethods, error, Snackbar.LENGTH_SHORT).show();
                                    }
                                }, SavedCardData.class);
                        SavedCardData cardData = new SavedCardData();
                        cardData.setNameOnCard(cardPaymentObject.getProperties().get("name_on_card"));
                        JSONObject token = new JSONObject(response.responsePayload);
                        cardData.setToken(token.getString("token"));
                        cardData.setBinId(cardBin);
                        addCard.post(cardData, new HashMap<String, JsonSerializer>(), tagForApiRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showOrderErrorDialog(null,"Unable to verify your card",false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showOrderErrorDialog(null,"Unable to verify your card",false);
                }
            }
        });
        thread.start();
    }

    private void startPaymentOnJuspay(final PaymentMethod externalPaymentMethodToUse, final AbstractPayment payment) {
        LogoutTask.updateTime();
        LogoutTask.getInstance(getActivity()).stopTimer();
        JPPaymentController jpPaymentController = new JPPaymentController(externalPaymentMethodToUse);
        jpPaymentController.setJPPayment(payment);
        try {
            jpPaymentController.startPayment(getActivity(), new OnPaymentStatusChangeListener() {
                @Override
                public void onPaymentStatusInit() {
                }

                @Override
                public void onPaymentComplete(boolean status) {
                    if(getActivity()==null)
                        return;
                    LogoutTask.updateTime();
                    LogoutTask.getInstance(getActivity()).startTimer();

                    if (status) {
                        if (externalPaymentMethodToUse.getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
                                && externalPaymentMethodToUse.getPaymentDetails() == null) {
                            CardPayment temp = cardPaymentObject;
                            tempOrder.setOrderStatus(OrderUtil.NEW);
                            startOrderView(tempOrder);
                            cardPaymentObject = null;
                            return;
                        }
                        tempOrder.setOrderStatus(OrderUtil.NEW);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startOrderView(tempOrder);
                            }
                        });
                    } else {

                        cardPaymentObject = null;
                        cleverTapPlaceOrder("payment_fail");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showOrderErrorDialog(order, "Your order could not be placed. In case money has been deducted from your account, it will be refunded in 5 to 15 days.", false);
                                AppUtils.HbLog("dtap", "jusPay payment error");
                            }
                        }, 500);
                    }
                }

                @Override
                public void onPaymentAborted() {
                    if(getActivity()==null)
                        return;
                    LogoutTask.updateTime();
                    LogoutTask.getInstance(getActivity()).startTimer();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtils.HbLog("dtap", "juspay payment aborted");
                            paymentRejectedbyBackRegisterfor(order);
                        }
                    });
                }
            });
        } catch (Exception e) {
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).startTimer();
            AppUtils.getConfig(getActivity());
            showOrderErrorDialog(order, "There is some problem with the payment system", false);
        }
    }

    private void startPaymentForWallet(PaymentMethod externalPaymentMethodUsed, Order orderForPayment, Order order) {
        if (!externalPaymentMethodUsed.isLinkingAllowed()) {
            WalletPayment myPayment = new WalletPayment();
            myPayment.setWallet(externalPaymentMethodUsed.getPaymentMethod());
            myPayment.setOrderId(orderForPayment.getOrderId());
            startPaymentOnJuspay(externalPaymentMethodUsed, myPayment);
        } else {
            cleverTapPlaceOrder("payment_fail");
            showOrderErrorDialog(order,
                    String.format("There is some error while paying through your %s account", externalPaymentMethodUsed.getDisplayName()),
                    false);
        }
    }

    public void paymentRejectedbyBackRegisterfor(final Order order) {
        pbPay.setVisibility(View.VISIBLE);

        String url = UrlConstant.WEB_PAYMENT_REJECTED_BACK + order.getId();
        SimpleHttpAgent<Object> paymentRejectedBackHandler = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        if(getActivity()==null)
                            return;
                        cleverTapPlaceOrder("payment_fail");
                        showOrderErrorDialog(order, "You cancelled the payment", false);
                        pbPay.setVisibility(View.GONE);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbPay.setVisibility(View.GONE);
                        AppUtils.HbLog("dtap", " payment  cancel api " + order.getOrderId());
                    }
                },
                Object.class
        );
        paymentRejectedBackHandler.get(tagForApiRequest);
    }


    private void updatePhonePeTransactionStatus(final Order order) {
        if (order==null){
            AppUtils.showToast("Something went wrong", true, 0);
            if (getActivity()!=null) {
                getActivity().finish();
            }
            return;
        }
        pbPay.setVisibility(View.VISIBLE);
        btPay.setText("Verifying payment status");

        String url = UrlConstant.UPDATE_EXTERNAL_PAYMENT + order.getId();
        SimpleHttpAgent<OrderResponse> paymentRejectedBackHandler = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        btPay.setEnabled(true);
                        pbPay.setVisibility(View.GONE);
                        if(getActivity()==null)
                            return;
                        if(responseObject.order.getOrderStatus().equals(OrderUtil.PAYMENT_FAILED)){
                            cleverTapPlaceOrder("payment_fail");
                            showOrderErrorDialog(responseObject.order, responseObject.order.getRejectMessage(), false);
                        }else{
                            startOrderView(responseObject.order);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbPay.setVisibility(View.GONE);
                        AppUtils.HbLog("dtap", " payment  cancel api " + order.getOrderId());
                    }
                },
                OrderResponse.class
        );
        paymentRejectedBackHandler.get(tagForApiRequest);
    }

    private void cleverTapPayClick(){


        //cleverTap PayClick
//        try {
//
//            //payment method selected
//            String methodSelected = "";
//            for (PaymentMethod paymentMethod : paymentMethods) {
//                if (paymentMethod.isSelected()) {
//                    methodSelected += paymentMethod.getDisplayName() + " ,";
//                }
//            }
//
//            //first default payment
//            String defaultMethod = "";
//            boolean isMethodChange = false;
//            for (PaymentMethod paymentMethod : paymentMethods) {
//                if (paymentMethod.isDefault()) {
//                    defaultMethod = paymentMethod.getDisplayName();
//                    isMethodChange = !paymentMethod.isSelected();
//                    break;
//                }
//            }
//
//            ArrayList<OrderProduct> products = order.getProducts();
//            String item_product = "";
//            for (OrderProduct orderProduct : products) {
//                item_product +="("+orderProduct.getName()+","+ orderProduct.getQuantity()+")";
//            }
//
//            //pay_click_response
//            HashMap<String, Object> pay_click_map = new HashMap<>();
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getSource(), source);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getMethods_selected(), methodSelected);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getDefault_method(), defaultMethod);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getCart_value(),order.getTotalPrice());
//            if(order.getComment()!=null && !order.getComment().trim().isEmpty())
//                pay_click_map.put(CleverTapEvent.PropertiesNames.getSpecial_instruction(), order.getComment());
//            pay_click_map.put(CleverTapEvent.PropertiesNames.getItem_name_item_quantity(), item_product);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_method_change(), isMethodChange);
//            pay_click_map.put(CleverTapEvent.PropertiesNames.is_company_paid(),false);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getPay_click(), pay_click_map, getActivity());
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void cleverTapPlaceOrder(String status)
    {
        try{
            String methodSelected = "";
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.isSelected()) {
                    methodSelected += paymentMethod.getDisplayName() + " ,";

                    if(checkIfPaytmUpi(paymentMethod)){
                        cleverTapUpi(paymentMethod,status);
                    }
                }
            }

            String defaultMethod = "";
            boolean isMethodChange = false;
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.isDefault()) {
                    isMethodChange = !paymentMethod.isSelected();
                    break;
                }
            }

            //place_order_Status
            HashMap<String,Object> place_order_map = new HashMap<>();

            if(order.getDeskReference()!=null && order.getDeskReference().length()>0){
                place_order_map.put(CleverTapEvent.PropertiesNames.is_desk_order(),"true");
            }else{
                place_order_map.put(CleverTapEvent.PropertiesNames.is_desk_order(),"false");
            }

            String verName = AppUtils.getVersionName();
            verName = verName.replace(".","");
            int versionName = (int)Integer.parseInt(verName);

            place_order_map.put(CleverTapEvent.PropertiesNames.getSource(),source);
            place_order_map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
            place_order_map.put(CleverTapEvent.PropertiesNames.getVendor_name(),selectedOrderVendor.getVendorName());
            place_order_map.put(CleverTapEvent.PropertiesNames.is_method_change(), isMethodChange+"");
            place_order_map.put(CleverTapEvent.PropertiesNames.getVendor_id(),selectedOrderVendor.getId());
            place_order_map.put(CleverTapEvent.PropertiesNames.getOs(),"Android");
            place_order_map.put(CleverTapEvent.PropertiesNames.getOrder_status(),status);
            place_order_map.put(CleverTapEvent.PropertiesNames.getPayment_method_name(),methodSelected);
            place_order_map.put(CleverTapEvent.PropertiesNames.getAmount(),order.getTotalPrice());
            place_order_map.put(CleverTapEvent.PropertiesNames.getLocation_id(),order.getLocationId());
            place_order_map.put(CleverTapEvent.PropertiesNames.getVersion_name(),versionName);
            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getPlace_order(),place_order_map,getActivity());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cleverTapUpi(PaymentMethod paymentMethod,String status){

//        try{
//
//            UpiMethod upiMethod = selectedUpiMethod();
//            if(upiMethod!=null) {
//                HashMap<String, Object> upi_method_map = new HashMap<>();
//                upi_method_map.put(CleverTapEvent.PropertiesNames.getSource(),source);
//                upi_method_map.put(CleverTapEvent.PropertiesNames.getOrder_status(),status);
//                upi_method_map.put(CleverTapEvent.PropertiesNames.getAmount(),order.getTotalPrice());
//                upi_method_map.put(CleverTapEvent.PropertiesNames.getUpi_app_name(), upiMethod.getMethod());
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getUpi_method(),upi_method_map,getActivity());
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void startOrderView(Order order) {

        Intent intent = null;
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        selectedOrderVendor = AppUtils.getVendorById(getActivity(), selectedVendorId);

        if (selectedOrderVendor != null)
            order.setVendor(selectedOrderVendor);
        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING))
            doPaymentForOrder(order);
        else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            intent = new Intent(getActivity(), OrderSuccessActivity.class);
            intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
            intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
            intent.putExtra(ApplicationConstants.IS_ORDER_PRE_ORDER, true);
            intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, orderPaymentType);

            if(getActivity() instanceof BookmarkPaymentActivity){
                BookmarkPaymentActivity activity = (BookmarkPaymentActivity) getActivity();
                if(activity.fromSpaceBooking){
                    intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
                }
                else{
                    intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, activity.cbSpaceYes.isChecked());
                }
            }
            else if (getActivity() instanceof PaymentActivity){
                if(forSpaceBooking){
                    intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
                }
                else{
                    intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, redirectSpaceBooking);
                }
            }

            intent.putExtra("message", "High Five! \nYour Order has been placed.\nIt is in Pre-Order");
            if (selectedOrderVendor != null)
                startOrderNotificatioService(order);
            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            clearLocalOrder();
            startActivity(intent);
            disableNotificationFlag();
            getActivity().finish();
        } else {
            if (orderPaymentType.equalsIgnoreCase(ApplicationConstants.ORDER_TYPE_FOOD)) {
                intent = new Intent(getActivity(), OrderSuccessActivity.class);
                intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
                intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
                intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, orderPaymentType);

                if(getActivity() instanceof BookmarkPaymentActivity){
                    BookmarkPaymentActivity activity = (BookmarkPaymentActivity) getActivity();
                    if(activity.fromSpaceBooking){
                        intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
                        intent.putExtra(ApplicationConstants.IS_APPROVAL_PENDING, order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING));
                    }
                    else{
                        intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, activity.cbSpaceYes.isChecked());
                    }
                }
                else if(getActivity() instanceof PaymentActivity){
                    if(forSpaceBooking){
                        intent.putExtra(ApplicationConstants.FOR_SPACE_BOOKING, true);
                        intent.putExtra(ApplicationConstants.IS_APPROVAL_PENDING, order.getOrderStatus().equalsIgnoreCase(OrderUtil.APPROVAL_PENDING));
                    }
                    else{
                        intent.putExtra(ApplicationConstants.REDIRECT_SPACE_BOOKING, redirectSpaceBooking);
                    }
                }

                cleverTapPlaceOrder("payment_success");

            } else {
                intent = new Intent(getActivity(), OrderSuccessActivity.class);
                if (message != null)
                    intent.putExtra("message", message);
                else
                    intent.putExtra("message", "High Five! \nYour Order has been placed.");
            }

            intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
            startActivity(intent);
            clearLocalOrder();
            if (selectedOrderVendor != null)
                startOrderNotificatioService(order);
            disableNotificationFlag();
            getActivity().finish();
        }

    }

    private void disableNotificationFlag(){
        if (getActivity()!=null && getActivity() instanceof BookmarkPaymentActivity){
            ((BookmarkPaymentActivity)getActivity()).setFromNotification(false);
        }
    }

    private void doPaymentForOrder(Order order) {

    }

    private void startOrderNotificatioService(Order order) {

        boolean appNotoficationStting = SharedPrefUtil.getBoolean(ApplicationConstants.APP_NOTIFICATION_SETTING, false);
        if (appNotoficationStting && order != null) {
            Intent intent = new Intent(getActivity(), NotificationService.class);
            intent.putExtra(ApplicationConstants.ORDER, order);
            getActivity().startService(intent);
        }
    }

    private void updateOrderSummary(Order order) {
        order.setTax(0);
        order.setServiceTax(0);

        if (user != null) {
            updatePaymentDetails(order);
        }
    }

    private void updatePaymentDetails(Order order) {

//        switch (cart.getGuestType()) {
//            case ApplicationConstants.GUEST_TYPE_OFFICIAL:
//                order.calculateGuestOrderPrice(cart.getGuestType());
//                break;
//            case ApplicationConstants.GUEST_TYPE_PERSONAL:
//            default:
//                order.calculateOrderProductPrice();
//        }

        double userRemainingBalance = (user.getCurrentWalletBalance()) - order.getTotalPrice();

        String payButtonString;
        if (userRemainingBalance < 0) {
            userExternalWalletCharge = order.getTotalPrice() - (user.getCurrentWalletBalance());
            internalWalletPayableAmount = (user.getCurrentWalletBalance());
        } else {
            userExternalWalletCharge = 0;
            internalWalletPayableAmount = order.getTotalPrice();
        }

        amountToPay = order.getTotalPrice() - otherUserWallet;
        if(fromExpressCheckout){
                btGrandTotal.setVisibility(View.VISIBLE);
                btGrandTotal.setText(String.format("₹ %.2f", order.getTotalPrice()));
                setViewDetailVisibility();
        }

        if(amountToPay<0)
            amountToPay=0;

        if(order.getConvenienceFee()>0)
        {
            tvPay.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_info),null);
            tvPay.setText(String.format("To Pay : ₹ %.2f", amountToPay));
            tvPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConvenienceFeePopup();
                }
            });
        }else{
            tvPay.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            tvPay.setText(String.format("To Pay : ₹ %.2f", amountToPay));
            tvPay.setOnClickListener(null);
        }

    }

    private void showConvenienceFeePopup() {
        String message = "Order Value : "+String.format("₹ %.2f", order.getPrice())
                + "\n" + "Convenience fee charged : "+ String.format("₹ %.2f", order.getConvenienceFee())
                + "\n" + "Total Order Amount : " + String.format("₹ %.2f", order.getTotalPrice()) ;
        GenericPopUpFragment convenieceFeePopup = GenericPopUpFragment.newInstance(message, "Okay",
                true, Gravity.RIGHT ,new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {

                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        convenieceFeePopup.show(getActivity().getSupportFragmentManager(),"conveniece_fee");
    }

    public void getUserPaymentDetails() {

        if(getActivity()==null)
            return;
        String payload = SharedPrefUtil.getString(ApplicationConstants.PAYLOAD, null);

        final MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        cart = mainApplication.getCart();
        pbWalletList.setVisibility(View.VISIBLE);
        if(loaderListener!=null)
            loaderListener.onProgressLoad(true);

        String url;

        PayloadData payloadData = new PayloadData();
        if (payload != null && !AppUtils.getConfig(getActivity()).isStop_simple_payload()) {
            payloadData.setSimplPayload(payload);
        }
        if(fromExpressCheckout){
            if (fromBookmark) {
                url = UrlConstant.EXPRESS_CHECKOUT_WALLET + "?occasionId=" + cart.getOccasionId() +
                        "&locationId=" + locationId + "&transactionAmount=" + totalPrice
                        + "&vendorId=" + vendorId + "&showMswipe=" + getShowMswipe()+ "&showPineLabs=" + showPineLabs();

                source = ApplicationConstants.ADD_ITEM_SOURCE_EXP;

            } else{
                url = UrlConstant.EXPRESS_CHECKOUT_WALLET + "?occasionId=" + cart.getOccasionId() +
                        "&locationId=" + locationId + "&transactionAmount=" + totalPrice
                        + "&vendorId=" + vendorId + "&showMswipe=" + getShowMswipe() + "&requestSource=menu"+ "&showPineLabs=" + showPineLabs();
                source = ApplicationConstants.ADD_ITEM_SOURCE_NORMAL;
            }
        }else {
            url = UrlConstant.WALLET_LIST_V2 + "?occasionId=" + cart.getOccasionId() +
                    "&locationId=" + locationId + "&transactionAmount=" + totalPrice +
                    "&vendorId=" + vendorId + "&showMswipe=" + getShowMswipe() + "&showPineLabs=" + showPineLabs();

            source = ApplicationConstants.ADD_ITEM_SOURCE_NORMAL;
        }

        if(fromNavbar) {
            url = UrlConstant.WALLET_LIST_V2 + "?occasionId=" + MainApplication.selectedOcassionId +
                    "&locationId=" + locationId + "&transactionAmount=" + totalPrice
                    + "&vendorId=" + vendorId + "&is_payment_screen=true" + "&showMswipe=" + getShowMswipe();
            source = ApplicationConstants.ADD_ITEM_SOURCE_NORMAL;
        }

        if(fromBigBasket){
            url = UrlConstant.EXPRESS_CHECKOUT_WALLET + "?occasionId=" + MainApplication.selectedOcassionId +
                    "&locationId=" + locationId + "&transactionAmount=" + totalPrice
                    + "&vendorId=" + vendorId + "&is_payment_screen=true" + "&showMswipe=" + getShowMswipe() + "&requestSource=bigbasket";

            source = "BB Instant";
        }

        SimpleHttpAgent<ListWallet> userWalletsSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<ListWallet>() {
                    @Override
                    public void response(ListWallet responseObject) {
                        if(getActivity()==null)
                            return;

                        if (responseObject != null) {
                            pbWalletList.setVisibility(View.GONE);
                            paymentMethods.clear();
                            paymentMethods.addAll(responseObject.getPaymentMethods());
                            if (!fromNavbar) {
                                if(AppUtils.getConfig(getContext()).isCheck_internal_wallet()) {
                                    if (checkForPersonalWallets()) {
                                        combineInternalWallets();
                                    }
                                }else{
                                    combineInternalWallets();
                                }
                            }
                            Collections.sort(paymentMethods);
                            ArrayList<PaymentMethod> paymentMethodsCopy = new ArrayList<>();
                            if (paymentMethods != null) {
                                paymentMethodsCopy.addAll(paymentMethods);
                                for (PaymentMethod paymentMethod : paymentMethods) {
                                    if(paymentMethod.isInternal() && paymentMethod.employeeCanRecharge()){
                                        rechargePopup = true;
                                        break;
                                    }
                                }
                            }
                            populateDisplayList(paymentMethodsCopy);

                            if(fromBigBasket){
                                createDummyOrder();
                            }

                            selectDefault();
//                            if(!fromBigBasket){
//                                selectDefault();
//                            }

                            if (!fromNavbar)
                            {
                                if(order==null)
                                    return;

                                btPay.setVisibility(View.VISIBLE);
                                computeConvenienceCharge();
                                updateOrderSummary(order);
                            }

                            if(checkPostpaidEligibilty && responseObject.getPaymentMethods()!=null){
                                checkForPaytmPostpaid(responseObject.getPaymentMethods());
                            }

                            if (dataAdapter == null) {

                                dataAdapter = new PaymentMethodsAdapterV2(getActivity(), paymentMethods,
                                        paymentMethodsForDisplay,
                                        fromNavbar,fromExpressCheckout, userExternalWalletCharge, new PaymentMethodsInterface() {
                                    @Override
                                    public void delinkPayment(PaymentMethod paymentData, String operation) {
                                        if (operation.equalsIgnoreCase("delink"))
                                            delinkPaymentMethod(paymentData);
                                        else
                                            deleteCard(paymentData);
                                    }

                                    @Override
                                    public void linkPayment(final PaymentMethod paymentData) {
                                        if (!fromNavbar) {
                                            Snackbar.make(rvPaymentMethods, "Your wallet will be linked when you proceed for Payment", Snackbar.LENGTH_LONG).show();
                                            return;
                                        }
                                        if(!user.getPhoneNumber().isEmpty()){
                                            if (paymentData.getPaymentSource() != null
                                                    && paymentData.getPaymentSource() != null) {
                                                linkUserPayment(paymentData);
                                            } else {
                                                startJuspayWalletLinking(paymentData);
                                            }
                                        }else{
                                            AccountDetailsUpdateDialog updateDialog = AccountDetailsUpdateDialog.newInstance(new AccountDetailsUpdateDialog.OnFieldChangeListener() {
                                                @Override
                                                public void onPasswordChanged(String mobileNum) {
                                                    user.setPhoneNumber(mobileNum);
                                                    getUserPaymentDetails();

                                                }
                                                @Override
                                                public void onDismiss() {
                                                    btPay.setEnabled(true);
                                                }
                                            }, ApplicationConstants.PHONE_NUMBER, user.getId());
                                            updateDialog.show(getActivity().getSupportFragmentManager(),"update_phone");
                                        }
                                    }

                                    @Override
                                    public void rechargeWallet(PaymentMethod paymentMethod) {
                                        if (paymentMethod.isInternal()) {
                                            try {
                                                String source = getActivity().getIntent().getStringExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE);
                                                if (source == null) {
                                                    source = "NA";
                                                }
                                                JSONObject jo = new JSONObject();
                                                jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                                                HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.PAYMENT_RECHARGE_CLICK, jo);

                                                Bundle bundle = new Bundle();
                                                bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, source);
                                                EventUtil.FbEventLog(getActivity(), EventUtil.MixpanelEvent.PAYMENT_RECHARGE_CLICK, bundle);

                                            } catch (Exception exp) {
                                                exp.printStackTrace();
                                            }

                                            Intent intent = new Intent(getActivity(), RechargeActivity.class);
                                            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Payment Recharge");
                                            intent.putExtra(ApplicationConstants.ORDER_AFTER_RECHARGE, false);
                                            intent.putExtra(CleverTapEvent.PropertiesNames.getSource(), source);
                                            startActivityForResult(intent, PERSONAL_WALLET_RECHARGE);
                                        } else {
                                        }

                                    }

                                    @Override
                                    public void setSelected(boolean checkedState, int position) {
                                        for (PaymentMethod paymentMethod : paymentMethods) {
                                            paymentMethod.setSelected(false);
                                        }
                                        paymentMethods.get(position).setSelected(checkedState);
                                        dataAdapter.notifyItemChanged(position);
                                        if (paymentMethods.get(position).isSelected())
                                            selectedPaymentMethod = paymentMethods.get(position);
                                        else
                                            selectedPaymentMethod = null;
                                        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                                        mainApplication.getCart().setPaymentMethod(selectedPaymentMethod);
                                    }

                                    @Override
                                    public void onMorePaymentOption() {
                                        if(morePaymentOptionListener!=null)
                                            morePaymentOptionListener.handleMoreOptionSelected();
                                    }
                                },show_others_expanded);
                                rvPaymentMethods.setAdapter(dataAdapter);
                                rvPaymentMethods.setLayoutManager(new LinearLayoutManager(getActivity()));
                                rvPaymentMethods.setHasFixedSize(true);

                            }
                            else {
                                dataAdapter.notifyDataSetChanged();
                            }
                            btPay.setEnabled(true);



                            if(loaderListener!=null)
                                loaderListener.onProgressLoad(false);

                            if (!fromNavbar && !fromBigBasket) {
                                updateOrderSummary(order);
                            }

                        } else {
                            pbWalletList.setVisibility(View.GONE);
                            if(loaderListener!=null)
                                loaderListener.onProgressLoad(false);
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbWalletList.setVisibility(View.GONE);
                        if(getActivity()==null)
                            return;
                        if(loaderListener!=null)
                            loaderListener.onProgressLoad(false);

                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(true);
                        }
                    }
                },
                ListWallet.class
        );

        userWalletsSimpleHttpAgent.post(payloadData, new HashMap<String, JsonSerializer>(), tagForApiRequest);
    }

    private int showPineLabs(){
        if(AppUtils.isCafeApp())
            return 1;
        return 0;
    }

    private void createDummyOrder() {
        if(order==null){
            order = new Order();
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setPrice(bb_minimum_balance);
            orderProduct.setQuantity(1);
            order.setPrice(bb_minimum_balance);
            order.getProducts().add(orderProduct);

        }
    }

    private void checkForPaytmPostpaid(List<PaymentMethod> paymentMethodList){
        boolean postPaidPresent  = false;
        checkPostpaidEligibilty = false;
        try {
            if (paymentMethodList.size() > 0) {
                for (PaymentMethod paymentMethod : paymentMethodList) {
                    if (paymentMethod != null && paymentMethod.getPaymentSource() != null
                            && paymentMethod.getPaymentSource().getPaymentData() != null &&
                            paymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_DIRECT_PAYTM_POSTPAID)) {
                        postPaidPresent = true;
                        break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!postPaidPresent && AppUtils.getConfig(getContext()).getPostpaid_not_eligible()!=null && !AppUtils.getConfig(getContext()).getPostpaid_not_eligible().equalsIgnoreCase("")){
            AppUtils.showToast(AppUtils.getConfig(getContext()).getPostpaid_not_eligible(),false,2);
        }
    }

    private boolean checkForPersonalWallets() {
        for (PaymentMethod paymentMethod:paymentMethods){
            if (paymentMethod.getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL)){
                return true;
            }
        }
        return false;
    }

    private void deleteCard(PaymentMethod paymentData) {
        String url = UrlConstant.JUSPAY_DELETE_SAVED_CARD + paymentData.getPaymentDetailsResponse().getPaymentDetails().getToken();
        SimpleHttpAgent<Object> cardDeleteRequest = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        pbWalletList.setVisibility(View.GONE);
                        if(getActivity()==null)
                            return;
//                        try {
//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put(CleverTapEvent.PropertiesNames.getSource(), source);
//                            map.put(CleverTapEvent.PropertiesNames.getUser_action(), "remove");
//                            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getAdd_card(), map, getActivity());
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }

                        getUserPaymentDetails();

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbWalletList.setVisibility(View.GONE);

                    }
                },
                Object.class
        );
        pbWalletList.setVisibility(View.VISIBLE);

        cardDeleteRequest.get(tagForApiRequest);
    }

    private void delinkPaymentMethod(final PaymentMethod paymentData) {
        getActivity().setResult(WALLET_LINK_CODE);
        String url;

        if (paymentData.getPaymentSource() != null)
            url = UrlConstant.DELINK_PAYMENT_SOURCE + paymentData.getPaymentSource().getPaymentData().getCode();
        else
            url = UrlConstant.JUSPAY_DELINK_WALLET + paymentData.getPaymentDetailsResponse().getPaymentDetails().getId();

        SimpleHttpAgent<Object> walletDelinkRequest = new SimpleHttpAgent<Object>(
                getActivity(),
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        pbWalletList.setVisibility(View.GONE);
                        if(getActivity()==null)
                            return;

//                        try {
//                            getActivity().setResult(WALLET_LINK_CODE);
//                            HashMap<String , Object> map = new HashMap<>();
//                            map.put(CleverTapEvent.PropertiesNames.getSource(),source);
//                            map.put(CleverTapEvent.PropertiesNames.getUser_action(),"delink");
//                            map.put(CleverTapEvent.PropertiesNames.getPayment_method_name(),paymentData.getDisplayName());
//                            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLink_delink(),map,getActivity());
//
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }

                        getUserPaymentDetails();

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pbWalletList.setVisibility(View.GONE);

                    }
                },
                Object.class
        );
        pbWalletList.setVisibility(View.VISIBLE);

        walletDelinkRequest.get(tagForApiRequest);

    }

    private void removeIndexes(ArrayList<PaymentMethod> paymentMethodList, ArrayList<Integer> indexesToRemove) {
        int count = 0;
        for (int i : indexesToRemove) {
            paymentMethodList.remove(i - count);
            count++;
        }
        indexesToRemove.clear();
    }

    private void populateDisplayList(ArrayList<PaymentMethod> paymentMethodList) {
        paymentMethodsForDisplay.clear();
        if (paymentMethodList.size() > 0 && paymentMethodList.get(0).isInternal()) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Internal Wallet"));
            paymentMethodsForDisplay.add(paymentMethodList.get(0));
            paymentMethodList.remove(0);
        }


        if (fromNavbar) {
            for (int i = 0; i < paymentMethodList.size(); i++) {
                if(!AppUtils.getConfig(getActivity()).isAdd_card())
                {
                    if (paymentMethodList.get(i).getPaymentMethodType().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)
                            && paymentMethodList.get(i).getPaymentDetails() == null) {
                        paymentMethodList.remove(i);
                        i--;
                    }
                }
                if (paymentMethodList.get(i).getPaymentMethodType()
                        .equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_NETBANKING)) {
                    paymentMethodList.remove(i);
                    i--;
                }
            }
        }

        ArrayList<Integer> indexesToRemove = new ArrayList<>();

        boolean isDefaultSelected = false;

        ArrayList<PaymentMethod> others = new ArrayList<>();
        ArrayList<PaymentMethod> savedCards = new ArrayList<>();
        ArrayList<PaymentMethod> preferredMethods = new ArrayList<>();
        ArrayList<PaymentMethod> walletMethods = new ArrayList<>();
        ArrayList<PaymentMethod> internalWallets = new ArrayList<>();
        ArrayList<PaymentMethod> upi = new ArrayList<>();



        for (int i = 0; i < paymentMethodList.size(); i++) {
            paymentMethodList.get(i).setIsDefault(0);
            if (paymentMethodList.get(i).isPreferred()) {
                if (!isDefaultSelected) {
                    paymentMethodList.get(i).setIsDefault(1);
                    isDefaultSelected = true;
                }
                if(checkIfPaytmUpi(paymentMethodList.get(i))){
                    ArrayList<UpiMethod> upiMethods = getUpiMethodsAvailable(paymentMethodList.get(i).getPaymentUpiMethods().getUpiMethods());
                    if(upiMethods.size()>0) {
                        paymentMethodList.get(i).setPaymentUpiMethods(getPaymentUpiMethods(upiMethods));
                        preferredMethods.add(paymentMethodList.get(i));
                    }else{
                        paymentMethods.remove(paymentMethodList.get(i));
                    }
                }else {
                    preferredMethods.add(paymentMethodList.get(i));
                }
            }
            else if (checkIfPaytmUpi(paymentMethodList.get(i))){

                ArrayList<UpiMethod> upiMethods = getUpiMethodsAvailable(paymentMethodList.get(i).getPaymentUpiMethods().getUpiMethods());
                if(upiMethods.size()>0) {
                    paymentMethodList.get(i).setPaymentUpiMethods(getPaymentUpiMethods(upiMethods));
                    upi.add(paymentMethodList.get(i));
                }else{
                    paymentMethods.remove(paymentMethodList.get(i));
                }

            }
            else if (paymentMethodList.get(i).isWallet() && AppUtils.getConfig(getContext()).isWallet_categorisation()) {
                walletMethods.add(paymentMethodList.get(i));
            } else if (paymentMethodList.get(i).isSavedCard()||paymentMethodList.get(i).getPaymentMethod().equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_NETBANKING)) {
                savedCards.add(paymentMethodList.get(i));
            } else if (fromNavbar && paymentMethodList.get(i).isInternal()) {
                internalWallets.add(paymentMethodList.get(i));
            } else {
                others.add(paymentMethodList.get(i).setPaymentTypeOthers(true));
            }
        }

        removeIndexes(paymentMethodList, indexesToRemove);
        if (internalWallets.size() > 0) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Hungerbox Wallets"));
            paymentMethodsForDisplay.addAll(internalWallets);
        }

        if (preferredMethods.size() > 0) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Preferred Options"));
            paymentMethodsForDisplay.addAll(preferredMethods);
        }

        if(upi.size()>0){
            paymentMethodsForDisplay.add(new PaymentMethodHeader("UPI"));
            paymentMethodsForDisplay.addAll(upi);
        }


        if (walletMethods.size() > 0) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Wallets"));
            paymentMethodsForDisplay.addAll(walletMethods);
        }

        if (savedCards.size() > 0) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Credit/Debit Cards/Net Banking"));
            paymentMethodsForDisplay.addAll(savedCards);
        }

        if (others.size() > 0) {
            paymentMethodsForDisplay.add(new PaymentMethodHeader("Others"));
            paymentMethodsForDisplay.addAll(others);
        }
        if(fromExpressCheckout){
            paymentMethodsForDisplay.add(new MoreOptionHeader());
        }

        //to expand others category by default
        try {
            if (!checkForPersonalWallets()
                    && preferredMethods.size() == 0
                    && upi.size() == 0
                    && walletMethods.size() == 0
                    && savedCards.size() == 0) {
                show_others_expanded = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private boolean checkIfPaytmUpi(PaymentMethod paymentMethod){
        return paymentMethod.getPaymentSource() != null && paymentMethod.getPaymentSource().getPaymentData() != null &&
                paymentMethod.getPaymentSource().getPaymentData().getCode().equalsIgnoreCase(ApplicationConstants.PAYMENT_UPI_METHOD);
    }

    private PaymentUpiMethods getPaymentUpiMethods(ArrayList<UpiMethod> upiMethods){
        PaymentUpiMethods paymentUpiMethods = new PaymentUpiMethods();
        paymentUpiMethods.setUpiMethods(upiMethods);
        return paymentUpiMethods;
    }

    private ArrayList<UpiMethod> getUpiMethodsAvailable(ArrayList<UpiMethod> upiMethods){

        if(getActivity()!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("upi://pay"));
            List<ResolveInfo> resolveInfoList = getActivity().getPackageManager().queryIntentActivities(intent, 0);

            if (resolveInfoList == null || resolveInfoList.size() == 0) {
                return new ArrayList<UpiMethod>();
            }

            Collections.sort(upiMethods, new Comparator<UpiMethod>() {
                @Override
                public int compare(UpiMethod s1, UpiMethod s2) {
                    return (int) (s2.getPriority() - s1.getPriority());
                }
            });



            ArrayList<UpiMethod> filterUpiList = new ArrayList<>();
            for (UpiMethod upiMethod : upiMethods) {
                for (ResolveInfo resolveInfo : resolveInfoList) {
                    if (upiMethod.getPackageName().equalsIgnoreCase(resolveInfo.activityInfo.packageName)) {
                        try {
                            ActivityInfo activityInfo = resolveInfo.activityInfo;
                            upiMethod.setAppInfoPkgName(activityInfo.applicationInfo.packageName);
                            upiMethod.setAppInfoActivityName(activityInfo.name);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        filterUpiList.add(upiMethod);
                        break;
                    }
                }

                if(fromExpressCheckout){
                    if(filterUpiList.size() >= AppUtils.getConfig(getActivity()).getMax_upi_count()){
                        break;
                    }
                }
            }

            return filterUpiList;
        }else{
            return new ArrayList<UpiMethod>();
        }
    }

    public void updateOrder(Order order,double userExternalWalletCharge,double internalWalletPayableAmount, double otherUserWallet){

        if(!isAdded() || getActivity()==null)
            return;
        this.order = order;
        this.otherUserWallet = otherUserWallet;
        this.userExternalWalletCharge = userExternalWalletCharge;
        this.internalWalletPayableAmount = internalWalletPayableAmount;
        if(order == null){
            this.amountToPay = 0;
        }else{
            this.amountToPay = order.getTotalPrice() - otherUserWallet;
        }
        if (!fromNavbar && !fromBigBasket)
        {
            computeConvenienceCharge();
            updateOrderSummary(order);
        }
    }

    private void selectDefault() {
        if (fromNavbar || order==null)
            return;
        PaymentMethod externalDefaultPM = null;
        PaymentMethod internalPM = getInternalPaymentMethod();

        for (PaymentMethod paymentMethod : paymentMethods) {
            if (!paymentMethod.isInternal() && paymentMethod.isDefault()) {
                if (internalPM != null && internalPM.isDefault()) {
                    if (order.getTotalPrice() > internalPM.getAmount()) {
                        internalPM.setSelected(true);
                        paymentMethod.setSelected(true);
                        externalDefaultPM=paymentMethod;
                    } else {
                        internalPM.setSelected(true);
                        paymentMethod.setSelected(false);
                    }
                    break;
                } else {
                    paymentMethod.setSelected(true);
                    externalDefaultPM=paymentMethod;
                    break;
                }
            }
        }
        if(externalDefaultPM == null)
        {
            if(internalPM!=null && !internalPM.isSelected())
                internalPM.setSelected(true);
        }

        if (internalPM!=null && internalPM.isCompulsory()) {
            internalPM.setSelected(true);
        }
    }

    private UpiMethod selectedUpiMethod(){
        ArrayList<UpiMethod> upiMethods = new ArrayList<>();
        for(PaymentMethod paymentMethod : paymentMethods){
            if(checkIfPaytmUpi(paymentMethod)){
                upiMethods = paymentMethod.getPaymentUpiMethods().getUpiMethods();
                break;
            }
        }

        if(upiMethods!=null && upiMethods.size()>0){
            for(UpiMethod upiMethod : upiMethods) {
                if (upiMethod.isSelected()){

                    return upiMethod;
                }
            }
        }
        return null;

    }


    public void showNoNetFragment(boolean callPayment) {
        if(getActivity()==null)
            return;
        ErrorPopFragment genericPopUpFragment = ErrorPopFragment.Companion.newInstance(ApplicationConstants.NO_NET_STRING, "RETRY",true,ApplicationConstants.NO_INTERNET_IMAGE,
                new ErrorPopFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction(){
                        if(callPayment){
                            getUserPaymentDetails();
                        }else{
                            getUserDetailsFromServer();
                        }
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "error")
                .commitAllowingStateLoss();

    }

    public void onEventBackDialog() {
        String message = String.format("Do you want to cancel the event booking request?");
        GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(message,
                "Yes",
                new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        cancelBooking();
                        startEventBookingActivity();
                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(genericPopUpFragment, "event_cancel")
                .commitAllowingStateLoss();
    }

    private void startEventBookingActivity() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(EventUtil.MixpanelEvent.SubProperties.STATUS, "Failed");
            HBMixpanel.getInstance().addEvent(getActivity(), EventUtil.MixpanelEvent.PAYMENT_OPTION_SPENT_TIME, jo);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        getActivity().finish();
    }

    private void retryZeta(String error) {
        if (!isRunning)
            return;


        final GenericPopUpFragment freeOrderErrorHandleDialog = GenericPopUpFragment.newInstance(error, "RETRY", "CANCEL", new GenericPopUpFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                validateAndSubmit();
            }

            @Override
            public void onNegativeInteraction() {
                btPay.setText("Place Order");
                pbPay.setVisibility(View.GONE);
                btPay.setEnabled(true);
            }
        });
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getActivity().getSupportFragmentManager(), "zeta_order_error");
    }

    private void validatinZetaPaymentShow() {
        loaderFragment = LoaderFragment.newInstance("Validating your payment from zeta");

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(loaderFragment, "zeta_loader")
                .commitAllowingStateLoss();
    }

    private void validatinZetaPaymentHide() {

        if (loaderFragment != null && loaderFragment.isShowing()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(loaderFragment)
                    .commitAllowingStateLoss();
            loaderFragment = null;
        }
    }

    private void validateZetaRequestFromServer(final HashMap<String, Object> response, String zetaResponse, boolean status) {
        String url = UrlConstant.ZETA_APP_TO_APP_TRANSACTION;

        validatinZetaPaymentShow();
        SimpleHttpAgent<HashMap> zetaAppToApp = new SimpleHttpAgent<>(
                getActivity(),
                url,
                new ResponseListener<HashMap>() {
                    @Override
                    public void response(HashMap responseObject) {
                        if(getActivity()==null)
                            return;

                        validatinZetaPaymentHide();
                        if ((boolean) responseObject.get("success")) {
                            tempOrder.setOrderStatus(OrderUtil.NEW);
                            clearLocalOrder();
                            tempOrder.getOrderPaymentResponse().getOrderPaymentData().setZetaCertificate(null);
                            startOrderView(tempOrder);
                        } else {
                            cleverTapPlaceOrder("payment_fail");
                            retryZeta("Zeta Payment failed");
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if(getActivity()==null)
                            return;

                        validatinZetaPaymentHide();
                        cleverTapPlaceOrder("payment_fail");
                        retryZeta("Zeta Payment failed");
                    }
                },
                HashMap.class
        );

        double totalValue = Double.parseDouble(String.format("%.2f", (order.getTotalPrice() - internalWalletPayableAmount)));
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("orderId", tempOrder.getId());
        payload.put("auth", response);
        payload.put("amount", userExternalWalletCharge);
        payload.put("status", status);
        zetaAppToApp.post(payload, new HashMap<String, JsonSerializer>(), tagForApiRequest);

    }

    private void checkAndValidateZetaPayment(HashMap<String, Object> response, String zetaResponse) {
        isRunning = true;
        if (response.containsKey("declineCode")) {
            validateZetaRequestFromServer(response, zetaResponse, false);
        } else {
            validateZetaRequestFromServer(response, zetaResponse, true);
        }
    }

    @Override
    public void handlePaymentMethodSelected(PaymentMethod paymentMethod, boolean selected) {
        if (selected) {
            currentPaymentMethod = paymentMethod;
            if (paymentMethod.getWalletSource().equalsIgnoreCase("internal")) {
                if (order.getTotalPrice() <= paymentMethod.getAmount()) {
                    paymentMethod.setSelected(true);
                    unselectAllOtherPaymentMethods(paymentMethod);
                } else {
                    paymentMethod.setSelected(true);
                }
            } else {
                PaymentMethod selectedExternalPaymentMethod = getSelectedExternalPaymentMethod();
                if (!isexternalSelectable()) {
                    if(!unSelectInternalWallet())
                    {
                        Snackbar.make(tvPay, "Can't select external Payment if you have sufficient balance in Wallet",Snackbar.LENGTH_SHORT).show();
                        if (checkIfPaytmUpi(paymentMethod)) {
                            if (paymentMethod.getPaymentUpiMethods() != null && paymentMethod.getPaymentUpiMethods().getUpiMethods() != null) {
                                for (UpiMethod upiMethod : paymentMethod.getPaymentUpiMethods().getUpiMethods()) {
                                    upiMethod.setSelected(false);
                                }
                            }
                        }

                        return;
                    }
                }
                if (selectedExternalPaymentMethod == null) {
                    selectExternalPaymentMethod(paymentMethod);
                } else {
                    selectedExternalPaymentMethod.setSelected(false);
                    selectExternalPaymentMethod(paymentMethod);
                }
                if (paymentMethod.getPaymentMethodType()
                        .equalsIgnoreCase(ApplicationConstants.PAYMENT_METHOD_TYPE_CARD)) {
                    handleCardPayment(paymentMethod);
                }
            }

        } else {
            if (paymentMethod.isCompulsory()) {

            } else {
                paymentMethod.setSelected(false);
                for (PaymentMethod paymentMethodObj : paymentMethods) {
                    if (paymentMethodObj != null && paymentMethodObj.getId() == paymentMethod.getId()) {
                        paymentMethodObj.setSelected(false);
                    }
                }
                cart.getPaymentMethods().remove(paymentMethod);
            }


        }

        if (rvPaymentMethods.getAdapter() != null) {
            rvPaymentMethods.getAdapter().notifyDataSetChanged();
        }
        //selectedUpiMethod();
        if(!fromNavbar)
            computeConvenienceCharge();

    }


    private void computeConvenienceCharge() {
        PaymentMethod internalPaymentMethod = null, externalPaymentMehtod = null;

        for (PaymentMethod paymentMethod : paymentMethods) {
            if(paymentMethod.isSelected()) {
                if (paymentMethod.getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL)) {
                    internalPaymentMethod = paymentMethod;
                } else {
                    //EXTERNAL PAYMENT METHOD
                    externalPaymentMehtod = paymentMethod;
                }
            }
        }

        if(internalPaymentMethod != null){
            if(internalPaymentMethod.getConvenienceFee()>0){
                double rechargableInternalWalletAmount = internalPaymentMethod.getAmount() -
                        internalPaymentMethod.getNonRechargebleWalletBalance();
                if(internalPaymentMethod.getNonRechargebleWalletBalance()< order.getPrice()){
                    if(rechargableInternalWalletAmount > order.getPrice()){
                        convenienceFeeOnInternalWallets= (internalPaymentMethod.getConvenienceFee()*(order.getPrice()- internalPaymentMethod.getNonRechargebleWalletBalance()))/100;
                    }else{
                        if(internalPaymentMethod.getNonRechargebleWalletBalance() ==0 && externalPaymentMehtod == null)
                            convenienceFeeOnInternalWallets= (internalPaymentMethod.getConvenienceFee()*order.getPrice())/100;
                        else
                            convenienceFeeOnInternalWallets= (internalPaymentMethod.getConvenienceFee()*rechargableInternalWalletAmount)/100;
                    }
                }
            }
        }else{
            convenienceFeeOnInternalWallets = 0;
        }

        if(externalPaymentMehtod !=null && externalPaymentMehtod.getConvenienceFee()>0){
            convenienceFeeOnExternalWallets = (externalPaymentMehtod.getConvenienceFee() * userExternalWalletCharge)/100;
        }else{
            convenienceFeeOnExternalWallets=0;
        }

        order.setConvenienceFee(convenienceFeeOnExternalWallets+convenienceFeeOnInternalWallets);
        updatePaymentDetails(order);

    }

    private void handleCardPayment(PaymentMethod cardPaymentMethod) {
        if(getActivity()==null)
            return;

        btPay.setEnabled(false);
        if (cardPaymentMethod.getPaymentDetails() == null) {
            HashSet<String> methods = new HashSet<>();
            for (PaymentMethodMethod cardMethod
                    : cardPaymentMethod.getNetBankingMethods().getNetBankingMethods()) {
                methods.add(cardMethod.getMethod());
            }
            Intent intent = new Intent(getActivity(), CardEditActivity.class);
            intent.putExtra("methods", methods);
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).stopTimer();
            intent.putExtra("fromNavbar",fromNavbar);
            if(fromNavbar && AppUtils.getConfig(getActivity()).isAdd_card())
                startActivityForResult(intent,SAVE_NEW_CARD);
            else
                startActivityForResult(intent, GET_NEW_CARD);
        } else {
            Intent intent = new Intent(getActivity(), CardEditActivity.class);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardPaymentMethod.getPaymentDetails().getNameOnCard());
            intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardPaymentMethod.getPaymentDetails().getFormattedCardNumber());
            intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, cardPaymentMethod.getPaymentDetails().getExpMonth()
                    + "/" + cardPaymentMethod.getPaymentDetails().getExpYear().substring(Math.max(cardPaymentMethod.getPaymentDetails().getExpYear().length() - 2, 0)));
            intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_FRONT);
            intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true);// pass "false" to discard expiry date validation.
            intent.putExtra(CreditCardUtils.EXTRA_TOKEN, cardPaymentMethod.getPaymentDetails().getToken());
            intent.putExtra(CreditCardUtils.EXTRA_ENTRY_START_PAGE, 3);
            intent.putExtra(CreditCardUtils.EXTRA_CARD_BRAND,cardPaymentMethod.getPaymentDetails().getCardBrand());
            LogoutTask.updateTime();
            LogoutTask.getInstance(getActivity()).stopTimer();
            startActivityForResult(intent, EDIT_CARD);
        }
    }


    private PaymentMethod getInternalPaymentMethod() {
        PaymentMethod internalPaymentMethod = null;
        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL)) {
                internalPaymentMethod = paymentMethod;
                break;
            }
        }
        return internalPaymentMethod;
    }

    public boolean unSelectInternalWallet() {
        PaymentMethod internalPaymentMethod = getInternalPaymentMethod();
        if (internalPaymentMethod != null && !internalPaymentMethod.isCompulsory()) {
            internalPaymentMethod.setSelected(false);
            cart.getPaymentMethods().remove(internalPaymentMethod);
            return true;
        }else{
            return false;
        }
    }

    public boolean isexternalSelectable() {
        /**
         * if internal wallet have enough balance
         */
        PaymentMethod internalPaymentMethod = getInternalPaymentMethod();

        if (internalPaymentMethod != null) {
            if (internalPaymentMethod.isSelected() && internalPaymentMethod.getAmount() >= order.getTotalPrice())
                return false;
            else
                return true;
        } else {
            return true;
        }
    }

    private void selectExternalPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod.getPaymentSource() != null && paymentMethod.getPaymentSource().getPaymentData().isLinkingRequired()) {
            paymentMethod.setSelected(true);
            return;
        } else {
            paymentMethod.setSelected(true);
        }

    }

    private PaymentMethod getSelectedExternalPaymentMethod() {
        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.getWalletSource().equalsIgnoreCase(ApplicationConstants.PAYMENT_WALLET_SOURCE_EXTERNAL) && paymentMethod.isSelected()) {
                return paymentMethod;
            }
        }
        return null;
    }

    private void unselectAllOtherPaymentMethods(PaymentMethod paymentMethodToLeave) {
        for (PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod != null && paymentMethod.getId() != paymentMethodToLeave.getId()) {
                paymentMethod.setSelected(false);
            }
        }
    }

    private int getShowMswipe(){
        if(showMswipe)
            return 1;
        else
            return 0;
    }

    public class PayloadData implements Serializable {

        public String getSimplPayload() {
            return simplPayload;
        }

        public void setSimplPayload(String simplPayload) {
            this.simplPayload = simplPayload;
        }

        @SerializedName("simpl_payload")
        String simplPayload = "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isRunning = true;
        if(getActivity()==null)
            return;

        switch (requestCode) {
            case OTP_VERIFICATION_REQ_CODE: {
                LogoutTask.updateTime();
                LogoutTask.getInstance(getActivity()).startTimer();
                if (resultCode == RESULT_OK) {
                    getActivity().setResult(WALLET_LINK_CODE);

                    if(data.getStringExtra("wallet").equalsIgnoreCase(ApplicationConstants.PAYMENT_DIRECT_PAYTM_POSTPAID) ||data.getStringExtra("wallet").equalsIgnoreCase(ApplicationConstants.PAYMENT_DIRECT_PAYTM_WALLET) ){
                        if(!fromExpressCheckout) {
                            checkPostpaidEligibilty = true;
                        }
                    }
                    String msg = "";
                    if(checkPostpaidEligibilty){
                        msg = "Wallet Linked Successfully";
                    }else{
                        msg = data.getStringExtra("wallet_name")+" Linked Successfully";
                    }

                    GenericPopUpFragment popUpFragment;
                    if (fromNavbar) {
                        popUpFragment = GenericPopUpFragment
                                .newInstance(msg , "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {
                                        getUserPaymentDetails();

                                    }

                                    @Override
                                    public void onNegativeInteraction() {
                                        getUserPaymentDetails();
                                    }
                                });
                    } else {
                        popUpFragment = GenericPopUpFragment
                                .newInstance(msg, "OK",true
                                        ,new GenericPopUpFragment.OnFragmentInteractionListener() {
                                            @Override
                                            public void onPositiveInteraction() {
                                                getUserPaymentDetails();
                                            }

                                            @Override
                                            public void onNegativeInteraction() {
                                            }
                                        });
                    }

//                    try {
//                        HashMap<String , Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getSource(),source);
//                        map.put(CleverTapEvent.PropertiesNames.getUser_action(),"link");
//                        map.put(CleverTapEvent.PropertiesNames.getPayment_method_name(),data.getStringExtra("wallet_name"));
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLink_delink(),map,getActivity());
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

                    popUpFragment.setCancelable(false);
                    popUpFragment.show(getActivity().getSupportFragmentManager(),"wallet Linked");
                }else{
                    if(data != null && data.getStringExtra(ApplicationConstants.ERROR_MESSAGE) != null && !data.getStringExtra(ApplicationConstants.ERROR_MESSAGE).trim().isEmpty())
                        showOrderErrorDialog(order, data.getStringExtra(ApplicationConstants.ERROR_MESSAGE), false);
                    else
                        showOrderErrorDialog(order, "", false);
                }
                break;
            }
            case ZETA_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    getActivity().setResult(WALLET_LINK_CODE);
                    AppUtils.HbLog("ZETA_REQ", data.toString());
                    String zetaResponse = data.getStringExtra("data");
                    HashMap<String, Object> response = new Gson().fromJson(zetaResponse, HashMap.class);
                    AppUtils.HbLog("ZETA_RES", response.toString());
                    checkAndValidateZetaPayment(response, zetaResponse);
                } else {
                    try {
                        String zetaResponse = data.getStringExtra("data");
                        HashMap<String, Object> response = new Gson().fromJson(zetaResponse, HashMap.class);
                        AppUtils.HbLog("ZETA_RES", response.toString());
                        validateZetaRequestFromServer(new HashMap<String, Object>(), "", false);
                    } catch (Exception e) {
                        validateZetaRequestFromServer(new HashMap<String, Object>(), "", false);
                    }
                }
                break;
            }

            case PHONE_PE_SDK_REQUEST_CODE: {
                getActivity().setResult(WALLET_LINK_CODE);
                updatePhonePeTransactionStatus(order);
                break;
            }

            case ORDER_PAYMENT_WEB_REQUEST: {
                if (resultCode == RESULT_OK) {
                    getActivity().setResult(WALLET_LINK_CODE);
                    String url = data.getStringExtra(ApplicationConstants.URL);
                    boolean paymentSuccess = data.getBooleanExtra(ApplicationConstants.PAYMENT_STATUS, false);
                    String reason = data.getStringExtra(ApplicationConstants.REASON);
                    Order order = (Order) data.getSerializableExtra(ApplicationConstants.ORDER);
                    if (paymentSuccess) {
                        clearLocalOrder();
                        order.setOrderStatus(OrderUtil.NEW);
                        startOrderView(order);
                    } else {
                        if (reason.equalsIgnoreCase(ApplicationConstants.USER_CANCELLED)) {
                            paymentRejectedbyBackRegisterfor(order);
                        } else {
                            cleverTapPlaceOrder("payment_fail");
                            showPaymentErrorAlert(reason);
                        }
                    }
                } else {
                    showOrderErrorDialog(order, "Oops! something went wrong. Please try again", true);
                }
                break;
            }
            case JUSPAY_OTP_LINK_CODE: {
                if (resultCode == RESULT_OK) {
                    final PaymentMethod tempPaymentMethod = (PaymentMethod) data.getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);
                    getActivity().setResult(WALLET_LINK_CODE);
//                    try {
//                        HashMap<String , Object> map = new HashMap<>();
//                        map.put(CleverTapEvent.PropertiesNames.getSource(),source);
//                        map.put(CleverTapEvent.PropertiesNames.getUser_action(),"link");
//                        map.put(CleverTapEvent.PropertiesNames.getPayment_method_name(),tempPaymentMethod.getDisplayName());
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getLink_delink(),map,getActivity());
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

                    GenericPopUpFragment popUpFragment;
                    if (fromNavbar) {
                        popUpFragment = GenericPopUpFragment
                                .newInstance(tempPaymentMethod.getDisplayName() + " Linked Successfully", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                                    @Override
                                    public void onPositiveInteraction() {
                                        getUserPaymentDetails();

                                    }

                                    @Override
                                    public void onNegativeInteraction() {

                                    }
                                });
                    } else {
                        popUpFragment = GenericPopUpFragment
                                .newInstance(tempPaymentMethod.getDisplayName() + "  Linked Successfully", "OK",true,
                                        new GenericPopUpFragment.OnFragmentInteractionListener() {
                                            @Override
                                            public void onPositiveInteraction() {
                                                getUserPaymentDetails();

                                            }

                                            @Override
                                            public void onNegativeInteraction() {
                                                getUserPaymentDetails();

                                            }
                                        });
                    }

                    popUpFragment.setCancelable(false);
                    popUpFragment.show(getActivity().getSupportFragmentManager(), "wallet_linking");
                } else {
                    showOrderErrorDialog(order, data.getStringExtra(ApplicationConstants.ERROR_MESSAGE), false);
                }
                break;
            }
            case JUSPAY_NETBANK_SELECT: {
                if (resultCode == RESULT_OK) {
                    PaymentMethod externalPaymentMethodUsed = cart.getExternalPaymentMethod();
                    getActivity().setResult(WALLET_LINK_CODE);
                    if (externalPaymentMethodUsed != null) {
                        PaymentMethod paymentMethodData = (PaymentMethod) data.getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);
                        externalPaymentMethodUsed.getNetBankingMethods().setNetBankingMethods(paymentMethodData.getNetBankingMethods().getNetBankingMethods());
                        order.getPaymentModes().setPaymentMethodType(paymentMethodData.getNetBankingMethods().getNetBankingMethods().get(0).getMethod());
                    }
                    redirectionDialog.show();
                    checkDeskRefAndSubmit(order);
                } else {
                    showOrderErrorDialog(order, data.getStringExtra(ApplicationConstants.ERROR_MESSAGE), false);
                }
                break;
            }
            case GET_NEW_CARD: {
                LogoutTask.updateTime();
                LogoutTask.getInstance(getActivity()).startTimer();
                if (resultCode == RESULT_OK) {
                    getActivity().setResult(WALLET_LINK_CODE);
                    String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                    String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                    String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                    String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                    cardPaymentObject = new CardPayment();
                    String[] expiryDate = expiry.split("/");
                    cardPaymentObject.setNameOnCard(cardHolderName)
                            .setCardNumber(cardNumber)
                            .setCardExpDate(expiryDate[0], expiryDate[1])
                            .setAuthMethod("")
                            .setCardSecurityCode(cvv);
                    cardBin = cardNumber.substring(0,6);
                    saveCardToJusPay(cardPaymentObject,null);
                } else {
                    btPay.setEnabled(true);
                    if (currentPaymentMethod!=null){
                        currentPaymentMethod.setSelected(false);
                        dataAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case SAVE_NEW_CARD: {
                if (resultCode == RESULT_OK) {
                    String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                    String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                    String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                    String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                    cardPaymentObject = new CardPayment();
                    String[] expiryDate = expiry.split("/");
                    cardPaymentObject.setNameOnCard(cardHolderName)
                            .setCardNumber(cardNumber)
                            .setCardExpDate(expiryDate[0], expiryDate[1])
                            .setAuthMethod(AbstractPayment.AUTH_USING_POST)
                            .setCardSecurityCode(cvv);
                    cardBin = cardNumber.substring(0,6);
                    saveCardToJusPay(cardPaymentObject,null);
                }
                else{
                    if (currentPaymentMethod!=null){
                        currentPaymentMethod.setSelected(false);
                        dataAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case EDIT_CARD: {
                LogoutTask.updateTime();
                LogoutTask.getInstance(getActivity()).startTimer();
                if (resultCode == RESULT_OK) {
                    String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                    String token = data.getStringExtra(CreditCardUtils.EXTRA_TOKEN);
                    savedCardPaymentObject = new SavedCardPayment();
                    savedCardPaymentObject.setCardToken(token)
                            .setCardSecurityCode(cvv);
                    AppUtils.HbLog("dtap", btPay.isEnabled()+"");
                    cardBin = getSelectedExternalPaymentMethod().getPaymentDetails().getIsin();
                    validateAndSubmit();

                }else{
                    btPay.setEnabled(true);
                    if (currentPaymentMethod!=null){
                        currentPaymentMethod.setSelected(false);
                        dataAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
            case JUSPAY_WALLET_RECAHRGE:{
                LogoutTask.updateTime();
                LogoutTask.getInstance(getActivity()).startTimer();
                GenericPopUpFragment popUpFragment;
                String walletName = data.getStringExtra("walletName");
                final PaymentMethod tempPaymentMethod = (PaymentMethod)data.getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);
                if(resultCode == JPWebViewActivity.SUCCESS){
                    GenericPopUpFragment topUpFragment = GenericPopUpFragment
                            .newInstance(walletName+" Top up Successful", "PAY",
                                    new GenericPopUpFragment.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            cart.getExternalPaymentMethod().setPaymentDetailsResponse(tempPaymentMethod.getPaymentDetailsResponse());
                                            validateAndSubmit();
                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                            getUserPaymentDetails();

                                        }
                                    });
                    topUpFragment.setCancelable(false);
                    topUpFragment.show(getActivity().getSupportFragmentManager(), "wallet_topup");
                    AppUtils.showToast(walletName+" topup was successful", true, 1);
                }else if(resultCode == JPWebViewActivity.FAILURE){
                    showOrderErrorDialog(order, "Topup Failed", false);
                }
                break;
            }


            case PERSONAL_WALLET_RECHARGE: {
                getActivity().setResult(WALLET_LINK_CODE);
                getUserPaymentDetails();
                break;
            }

            case PAYTM_DIRECT_RECHARGE_REQUEST :{
                if (resultCode == PaytmRechargeActivity.SUCCESS){

                    String btPay = "PAY";
                    if(fromBigBasket){
                        btPay = "SELECT";
                    }

                    final PaymentMethod tempPaymentMethod = (PaymentMethod)data.getSerializableExtra(ApplicationConstants.PAYMENT_METHOD);
                    getActivity().setResult(WALLET_LINK_CODE);
                    GenericPopUpFragment topUpFragment = GenericPopUpFragment
                            .newInstance(" Top up Successful", btPay,
                                    new GenericPopUpFragment.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveInteraction() {
                                            cart.getPaymentMethod().setAmount(tempPaymentMethod.getAmount());
                                            validateAndSubmit();
                                        }

                                        @Override
                                        public void onNegativeInteraction() {
                                            getUserPaymentDetails();

                                        }
                                    });
                    topUpFragment.setCancelable(false);
                    topUpFragment.show(getActivity().getSupportFragmentManager(), "wallet_topup");

                }
                else if (resultCode == PaytmRechargeActivity.FAILURE){
                    showOrderErrorDialog(order, "Topup Failed", false);
                }
                break;
            }

            case PAYTM_INTENT_UPI: {
                getActivity().setResult(WALLET_LINK_CODE);
                updatePhonePeTransactionStatus(order);
                break;
            }

            case PINE_LBAS_REQUEST:{
                if(resultCode ==  RESULT_CANCELED){
                    showOrderErrorDialog(order,"Transaction Failed",false);
                }
                break;
            }

            case DYNAMIC_QR_PAYTM_REQUEST:{
                if(resultCode ==  RESULT_CANCELED){
                    showOrderErrorDialog(order,"Transaction Failed",false);
                }
                break;
            }
            case 2002 :
                ((ParentActivity)getActivity()).onActivityResult(requestCode,resultCode,data,PaymentFragment.this);
        }

    }
}
