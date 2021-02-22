package com.hungerbox.customer.order.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import com.hungerbox.customer.bluetooth.HBDevice;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.bluetooth.BluetoothDeviceCheckActivity;
import android.os.Handler;
import java.util.HashMap;

import com.hungerbox.customer.bluetooth.ViolationLogManager;
import com.hungerbox.customer.order.activity.OrderQrActivity;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.QrUtil;
import com.hungerbox.customer.util.ShakeListener;

import org.jetbrains.annotations.NotNull;


public class OrderQrFragment extends DialogFragment {

    private final String ORDER_TYPE_PICKUP = "pickup";
    private final String ORDER_TYPE_DELIVERY = "delivery";
    private final long DELAY_RESET_QR = 2000;
    private final long FLIP_DURATION = 300;
    Order order;
    TextView tvTitle, tvPedningOrderCount,tvPendingOrderLabel, tvOrderPin, tvOrderPrice, tvOrderKotTitle, tvOrderQuantity;
    ImageView ivQrCode, ivClose;
    boolean showingEnterExitQr = false;

    private OnOrderQrFragmentListener mListener;
    private TextView tvVendorName;
    private TextView tvOrderKOT;
    private ImageView close;
    private Context activity;
    RelativeLayout rlClose;
    private TextView tvRestaurantTitle;
    private TextView tvOrderType, tvQrTime, tvOrderPlaceTitle, tvShowQr;
    private AppCompatImageView ivCheck;
    private LinearLayout llOrderType;
    private ShakeListener mShake;
    private View viewForm;
    private boolean animationInProgress = false;
    private int qrDimension = 100;//min value, will be updated
    private String designFor = "";
    private TextView tvLocation, tvDate;
    private boolean fromOffline = false;
    String tagForApiRequest = "";

    public OrderQrFragment() {

    }

    public static OrderQrFragment newInstance(Order order, String designFor, boolean fromOffline ,OnOrderQrFragmentListener listener) {
        OrderQrFragment fragment = new OrderQrFragment();
        fragment.order = order;
        fragment.mListener = listener;
        fragment.designFor = designFor;
        fragment.fromOffline = fromOffline;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        try{
            if(savedInstanceState!=null){
                order = (Order) savedInstanceState.getSerializable("order");
                designFor = savedInstanceState.getString("designFor");
                fromOffline = savedInstanceState.getBoolean("fromOffline");
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }

        if(getActivity() != null){
            if(getActivity() instanceof OrderQrActivity){
                tagForApiRequest = ((OrderQrActivity) getActivity()).getApiTag();
            }
        }

        showingEnterExitQr = (designFor != null && ((designFor.equalsIgnoreCase("enter_qr")) || designFor.equalsIgnoreCase("exit_qr")));

        if(order != null) {

            if (AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())) {
                view = inflater.inflate(R.layout.fragment_order_no_qr, container, false);
                viewForm = view.findViewById(R.id.view);
                tvTitle = view.findViewById(R.id.tv_title);
                tvOrderPrice = view.findViewById(R.id.tv_order_price);
                ivClose = view.findViewById(R.id.iv_close);
                tvVendorName = view.findViewById(R.id.tv_vendor_name);
                tvOrderQuantity = view.findViewById(R.id.tv_order_quantity);
                rlClose = view.findViewById(R.id.ll_header);
                tvOrderPin = view.findViewById(R.id.tv_order_pin);
                tvRestaurantTitle = view.findViewById(R.id.tv_restaurant_title);

            } else if (showingEnterExitQr) {
                view = inflater.inflate(R.layout.fragment_entry_exit_qr, container, false);
                viewForm = view.findViewById(R.id.view);
                ivClose = view.findViewById(R.id.iv_close);
                ivQrCode = view.findViewById(R.id.iv_qr_code);
                tvLocation = view.findViewById(R.id.tv_location);
                tvDate = view.findViewById(R.id.tv_date);
                rlClose = view.findViewById(R.id.ll_header);
                tvShowQr = view.findViewById(R.id.tv_show_qr);
                if (getActivity() != null) {
                    qrDimension = (int) (getActivity().getResources().getDisplayMetrics().widthPixels * 0.60);
                }
                setQrAnimation(ivQrCode);
                ivQrCode.getLayoutParams().height = qrDimension;
                ivQrCode.getLayoutParams().width = qrDimension;
                ivQrCode.setScaleType(ImageView.ScaleType.FIT_XY);

                ShowEnterExitQR();
            } else {
                view = inflater.inflate(R.layout.fragment_order_qr, container, false);
                tvTitle = view.findViewById(R.id.tv_title);
                viewForm = view.findViewById(R.id.view);
                tvPedningOrderCount = view.findViewById(R.id.tv_pending_order_count);
                tvPendingOrderLabel = view.findViewById(R.id.tv_pending_order_title);
                tvOrderPin = view.findViewById(R.id.tv_order_pin);
                tvOrderKOT = view.findViewById(R.id.tv_order_kot);
                tvOrderPrice = view.findViewById(R.id.tv_order_price);
                ivClose = view.findViewById(R.id.iv_close);
                ivQrCode = view.findViewById(R.id.iv_qr_code);
                tvVendorName = view.findViewById(R.id.tv_vendor_name);
                tvOrderKotTitle = view.findViewById(R.id.tv_order_kot_title);
                tvOrderQuantity = view.findViewById(R.id.tv_order_quantity);
                rlClose = view.findViewById(R.id.ll_header);
                tvShowQr = view.findViewById(R.id.tv_show_qr);
                if (getActivity() != null) {
                    qrDimension = (int) (getActivity().getResources().getDisplayMetrics().widthPixels * 0.45);
                }
                setQrAnimation(ivQrCode);
                ivQrCode.getLayoutParams().height = qrDimension;
                ivQrCode.getLayoutParams().width = qrDimension;
                ivQrCode.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            llOrderType = view.findViewById(R.id.ll_order_type);

            rlClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });

            if (!showingEnterExitQr) {
                tvOrderType = view.findViewById(R.id.tv_order_type);
                tvQrTime = view.findViewById(R.id.tv_time);
                tvOrderPlaceTitle = view.findViewById(R.id.tv_order_place_title);
                ivCheck = view.findViewById(R.id.ic_check);
            }


            updateOrderData();
            if (!showingEnterExitQr) {
                adjustSocialDistancingFeature();
            }

            viewForm.setVisibility(View.GONE);
            checkPermissionsBluetoothInit();
            return view;
        }else{
            orderNullCheck();
            return inflater.inflate(R.layout.fragment_order_qr, container, false);
        }

    }

    void ShowEnterExitQR(){
        String textForEntry = "Show this QR at the Entrance";
        String textForExit = "Show this QR at the Exit";

        if(getActivity() instanceof OrderQrActivity){
            if(((OrderQrActivity) getActivity()).qrEntryText != null){
                textForEntry = ((OrderQrActivity) getActivity()).qrEntryText;
            }
            if(((OrderQrActivity) getActivity()).qrExitText != null){
                textForExit = ((OrderQrActivity) getActivity()).qrExitText;
            }
        }
        tvShowQr.setText(designFor.contains("enter")? textForEntry: textForExit);

        if(order.isSpaceBookingOrder()){
            String address  = order.getLocation().getName() + ", " ;
            if(order.getLocation().getAddressLine1() != null){
                address += order.getLocation().getAddressLine1() + ", ";
            }
            if(order.getLocation().getCityName() != null){
                address += order.getLocation().getCityName();
            }
            tvLocation.setText(address);
        }
        else{
            tvLocation.setText(order.getLocation().getName());
        }
        setBarCode(true);

        tvDate.setText(DateTimeUtil.getDateStringCustom(order.getCreatedAt()*1000,"dd MMM yyyy"));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Util.BLUETOOTH_INIT_REQUEST){
            viewForm.setVisibility(View.VISIBLE);
        }
    }

    private void checkPermissionsBluetoothInit(){

        if(Util.isBluetoothDistancingActive(getActivity(), order) && !ViolationLogManager.Companion.isBluetoothAllDayEnabled(getActivity())){
            SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_LOCATION,order.getLocationId());

            AppUtils.settingBTServiceParameters(getActivity().getApplicationContext());

            Util.stopDeviceTracking(getActivity());

            if(AppUtils.isSocialDistancingActive(order.getLocation())){
                if(order!=null){
                    if(order.getPreOrderDeliveryTime()!=0){
                        SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_TIME, order.getPreOrderDeliveryTime()*1000);
                    }else if(order.getCreatedAt()!=0){
                        SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_TIME, order.getCreatedAt()*1000);
                    }
                }
            }else{
                if(order.getCreatedAt()!=0){
                    SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_TIME, order.getCreatedAt()*1000);
                }
            }

            Intent intent = new Intent(getActivity(), BluetoothDeviceCheckActivity.class);
            startActivityForResult(intent, Util.BLUETOOTH_INIT_REQUEST);
        }
        else{
            if(Util.isBluetoothDistancingActive(getActivity(), order) && !ViolationLogManager.Companion.isBluetoothAllDayEnabled(getActivity())){
                Util.stopDeviceTracking(getActivity());
            }
            viewForm.setVisibility(View.VISIBLE);
        }
    }

    private void orderNullCheck(){
        if (order == null) {

            Context context = null;
            if (MainApplication.appContext != null) {
                context = MainApplication.appContext;
            } else if (getActivity() != null) {
                context = getActivity();
            } else {
                context = activity;
            }

            if (context != null) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void updateOrderData() {

        if(order!=null) {

            if (!showingEnterExitQr) {

                tvTitle.setText(String.format("Your Order ID : %s", order.getOrderId()));
                tvOrderPin.setText(order.getPin());
                if (!AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())) {
                    //config based visibility for order pending label
                    if (!AppUtils.getConfig(getActivity()).isShow_order_pending_label()) {
                        tvPendingOrderLabel.setVisibility(View.GONE);
                        tvPedningOrderCount.setVisibility(View.GONE);
                    } else {
                        tvPendingOrderLabel.setVisibility(View.VISIBLE);
                        tvPedningOrderCount.setVisibility(View.VISIBLE);
                    }
                    tvPedningOrderCount.setText(String.format("%d", order.getOrderQueue()));
                    if (order.getOrderStatus().equals(OrderUtil.NEW) || order.getOrderStatus().equals(OrderUtil.PRE_ORDER) || (order.getVendor().vendor.isBuffet() || order.getVendor().vendor.isRestaurant())) {
                        tvPendingOrderLabel.setVisibility(View.GONE);
                        tvPedningOrderCount.setVisibility(View.GONE);
                    }

                    if (order.getKot() > 0)
                        tvOrderKOT.setText("" + order.getKot());
                    else {
                        tvOrderKOT.setVisibility(View.GONE);
                        tvOrderKotTitle.setVisibility(View.GONE);
                    }

                    setBarCode(false);
                } else {
                    SpannableString spannableString = new SpannableString("Use this Order Reference \n to collect your order at");
                    spannableString.setSpan(new StyleSpan(R.style.MediumTextViewBold), 9, 24, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black_60)), 9, 24, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    tvRestaurantTitle.setText(spannableString);
                }
                tvOrderPrice.setText(order.getPriceString());
                tvVendorName.setText(order.getVendorName());

                if (order.getProducts().size() == 1)
                    tvOrderQuantity.setText(order.getProducts().size() + " Item");
                else
                    tvOrderQuantity.setText(order.getProducts().size() + " Items");

            }
        }else{
            orderNullCheck();
        }
    }


    public void setBarCode(boolean forEntryExit) {

        String hashString = "";

        if(forEntryExit){

            if(designFor.contains("enter"))
                hashString = "en_";
            else
                hashString = "ex_";

        }

        try {
            Bitmap bitmap;
            int width = 0;
            if(showingEnterExitQr){
                width = Math.round(AppUtils.convertDpToPixel(220, activity));
            }
            else{
                width = Math.round(AppUtils.convertDpToPixel(175, activity));
            }

            if (order != null) {
                bitmap = QrUtil.encodeAsBitmap(hashString + order.getPin(), width);
            } else {
                ivQrCode.setVisibility(View.INVISIBLE);
                return;
            }

            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void adjustSocialDistancingFeature(){
        if (getActivity()!=null && AppUtils.isSocialDistancingActive(order.getLocation())){
            llOrderType.setVisibility(View.VISIBLE);
            tvQrTime.setVisibility(View.VISIBLE);
            tvOrderType.setVisibility(View.VISIBLE);
            tvOrderPlaceTitle.setVisibility(View.INVISIBLE);
            ivCheck.setVisibility(View.INVISIBLE);
            if (order!=null){
                if (order.getPreOrderDeliveryTime()!=0){
                    tvQrTime.setText("QR generated at: "+ DateTimeUtil.getDateStringCustom(order.getPreOrderDeliveryTime()*1000,"hh:mm aa"));
                } else{
                    if (order.getCreatedAt()!=0) {
                        tvQrTime.setText("QR generated at: " + DateTimeUtil.getDateStringCustom(order.getCreatedAt()*1000,"hh:mm aa"));
                    } else {
                        tvQrTime.setVisibility(View.GONE);
                    }
                }
                if (order.getOrderPickType().equalsIgnoreCase(ORDER_TYPE_DELIVERY)){
                    tvQrTime.setVisibility(View.GONE);
                    tvOrderType.setText("Order Type : Delivery");
                    if (tvShowQr!=null){
                        tvShowQr.setText("Show this QR to delivery personnel.");
                    }
                } else{
                    tvOrderType.setText("Order Type : Eat at Cafe");
                    if (tvShowQr!=null){
                        tvShowQr.setText("Scan this QR to collect your order.");
                    }
                }
            }
        } else{
            if (tvShowQr!=null){
                tvShowQr.setVisibility(View.INVISIBLE);
            }
            tvOrderType.setVisibility(View.GONE);
            llOrderType.setVisibility(View.INVISIBLE);
            tvQrTime.setVisibility(View.INVISIBLE);
            tvOrderPlaceTitle.setVisibility(View.VISIBLE);
            ivCheck.setVisibility(View.VISIBLE);
        }
    }
    private void setQrAnimation(ImageView imageView){
        if (AppUtils.isSocialDistancingActive(order.getLocation()) && !fromOffline){
            try{
                setShakeAction(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getOrderDetail();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void animateQr(ImageView imageView, int drawableId){

        //TODO Review
        final Handler handler = new Handler();
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
        final ObjectAnimator oa3 = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
        final ObjectAnimator oa4 = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
        oa1.setInterpolator(new DecelerateInterpolator());
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
        oa3.setInterpolator(new DecelerateInterpolator());
        oa4.setInterpolator(new AccelerateDecelerateInterpolator());
        oa1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationInProgress = true;
                imageView.setImageResource(drawableId);
                oa2.start();
            }
        });
        oa2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        oa3.start();
                    }
                }, DELAY_RESET_QR);
            }
        });
        oa3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if(showingEnterExitQr)
                    setBarCode(true);
                else
                    setBarCode(false);

                oa4.start();
            }
        });
        oa3.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationInProgress = false;
            }
        });
        oa1.setDuration(FLIP_DURATION);
        oa2.setDuration(FLIP_DURATION);
        oa3.setDuration(FLIP_DURATION);
        oa4.setDuration(FLIP_DURATION);
        oa1.start();
    }

    private void setShakeAction(ImageView imageView){
        if (getActivity()!=null) {
            mShake = new ShakeListener(getActivity().getApplicationContext());
            mShake.setOnShakeListener(new ShakeListener.OnShakeListener() {
                @Override
                public void onShake() {
                    if (!animationInProgress) {
                        getOrderDetail();
                    }
                }
            });
        }
    }

    private void getOrderDetail() {
        if (order!=null) {

            String url = UrlConstant.ORDER_DETAIL + order.getId() + "/1/1/0/1";
            SimpleHttpAgent<OrderResponse> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
                    getActivity(),
                    url,
                    new ResponseListener<OrderResponse>() {
                        @Override
                        public void response(OrderResponse responseObject) {

                            if (responseObject == null) {
                                if (getActivity() != null)
                                    AppUtils.showToast("Unable to fetch Your Order", false, 0);
                            } else {
                                order = responseObject.order;
                                if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.CANCELLED) ||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED) ||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER) ||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING) ||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED) ||
                                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)){
                                    animateQr(ivQrCode,R.drawable.qr_invalid);
                                }
                                else{
                                    animateQr(ivQrCode,R.drawable.big_tick);
                                }
                            }

                            if(showingEnterExitQr){
                                ShowEnterExitQR();
                            }

                        }
                    },
                    new ContextErrorListener() {
                        @Override
                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                            if (getActivity() != null) {
                                AppUtils.showToast("Unable to fetch Your Order", false, 0);
                                animateQr(ivQrCode, R.drawable.qr_invalid);
                            }
                        }
                    },
                    OrderResponse.class
            );
            orderResponseSimpleHttpAgent.get(tagForApiRequest);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("order",order);
        outState.putString("designFor",designFor);
        outState.putBoolean("fromOffline",fromOffline);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnOrderQrFragmentListener {
        void onClose();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mShake!=null) {
            mShake.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShake!=null){
            mShake.resume();
        }
    }
}
