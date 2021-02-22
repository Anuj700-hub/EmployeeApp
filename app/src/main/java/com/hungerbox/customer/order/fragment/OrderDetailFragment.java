package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FeedbackEta;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderCancellationEligibilityResponse;
import com.hungerbox.customer.model.OrderCancellationResponse;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.OrderDetailActvity;
import com.hungerbox.customer.order.adapter.OrderProductSummaryListAdapter;
import com.hungerbox.customer.order.adapter.OrderWalletListAdapter;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
import com.hungerbox.customer.util.view.GenericFragmentWithTitle;
import com.hungerbox.customer.util.view.GenericPopUpFragment;
import com.hungerbox.customer.util.view.OrderStatusView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


public class OrderDetailFragment extends Fragment {

    private final String TAG = getClass().getName();
    private final String ORDER_TYPE_DELIVERY = "delivery";
    RecyclerView rvProductList;
    boolean isOrderQrShown = false;
    TextView tvOrderStatus;
    private long orderId;
    private OnFragmentInteractionListener mListener;
    private OrderProductSummaryListAdapter productListAdapter;
    private TextView tvVat;
    private TextView tvServiceTax;
    private LinearLayout llServiceTax;
    private LinearLayout llVat;
    private TextView tvContainerCharge;
    private LinearLayout llContainerCharge;
    private TextView tvTotalOrderAmount;
    private TextView tvOrderStatusContent,pickupText;
    private TextView tvNoQrCodeOrderStatusContent;
    private RelativeLayout  rlProgressBar;
    private LinearLayout llDeliveryCharge;
    private TextView tvDeliveryCharge, orderCancellation;
    private ImageView ivOrderStatus;
    private LinearLayout llSgst, llCgst, llConvinenceCharge;
    private TextView tvSgst, tvCgst, tvConvinenceCharge;
    private LinearLayout llOrderAmountContainer, tvDeskRefNumberBox,tvPickUpTypeBox;
    private TextView tvOrderAmount;
    private OrderDetailActvity orderDetailActivity;
    private RecyclerView rlvOrderWalletList;
    private TextView tvOrderWalletListHeader;
    private TextView tvOrderRef;
    private SwipeRefreshLayout refreshLayout;
    private boolean shouldRefresh = false;
    private boolean isAdd;
    private OrderStatusView orderStatusView;
    private ImageView ivProcessed,ivNoQrCodeProcessed;
    private TextView tvPickUpType, tvDeskRefNumber, deskText,tvGroupUsers, tvProjectCodeLabel, tvProjectCodeNumber;
    private RelativeLayout rlOrderStatusContent,rlNoQrCodeOrderStatusContent;
    SpannableString spannableString;
    private TextView tvOrderId,tvOrderItemsCost;
    private TextView tvStatus;
    private ConstraintLayout entryTimingLayout;
    private AppCompatImageView ivEntryTick;
    private TextView tvEntryText,tvEntryTiming;
    private Boolean isArchived = false;
    private Order currentOrder;




    public OrderDetailFragment() {
    }

    public static OrderDetailFragment newInstance(OrderDetailActvity orderDetailActvity, long orderId, TextView orderCancellation, Boolean isArchived) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        fragment.orderDetailActivity = orderDetailActvity;
        fragment.orderId = orderId;
        fragment.orderCancellation = orderCancellation;
        fragment.mListener = orderDetailActvity;
        fragment.isArchived = isArchived;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvOrderItemsCost = view.findViewById(R.id.tv_order_items);
        tvPickUpType = view.findViewById(R.id.tv_pick_up_type);
        tvDeskRefNumber = view.findViewById(R.id.tv_desk_ref_number);
        tvDeskRefNumberBox = view.findViewById(R.id.tv_desk_ref_number_box);
        tvPickUpTypeBox = view.findViewById(R.id.tv_pick_up_type_number_box);
        tvGroupUsers = view.findViewById(R.id.tv_group_users);
        tvStatus = view.findViewById(R.id.iv_order_status);

        deskText = view.findViewById(R.id.desk_text);
        deskText.setText(AppUtils.getConfig(getContext().getApplicationContext()).getWorkstation_address());

        tvProjectCodeLabel = view.findViewById(R.id.project_code_text);
        tvProjectCodeLabel.setText(AppUtils.getConfig(getContext().getApplicationContext()).getProjectcode_place_holder());
        tvProjectCodeNumber = view.findViewById(R.id.tv_project_code_number);
        rlProgressBar = view.findViewById(R.id.rl_progress);
        tvOrderStatus = view.findViewById(R.id.tv_order_status);
        orderStatusView = view.findViewById(R.id.order_status_view);
        tvOrderStatusContent = view.findViewById(R.id.tv_order_status_content);
        tvOrderRef = view.findViewById(R.id.tv_order_ref);
        rvProductList = view.findViewById(R.id.tv_items_container);
        tvTotalOrderAmount = view.findViewById(R.id.tv_total);
        tvVat = view.findViewById(R.id.tv_vat);
        tvServiceTax = view.findViewById(R.id.tv_st);
        llVat = view.findViewById(R.id.ll_vat_container);
        llServiceTax = view.findViewById(R.id.ll_st_container);
        tvContainerCharge = view.findViewById(R.id.tv_container);
        llContainerCharge = view.findViewById(R.id.ll_container_charge_container);
        tvConvinenceCharge = view.findViewById(R.id.tv_convenience);
        llConvinenceCharge = view.findViewById(R.id.ll_convenience_container);
        llDeliveryCharge = view.findViewById(R.id.ll_delivery_container);
        tvDeliveryCharge = view.findViewById(R.id.tv_delivery);
        llSgst = view.findViewById(R.id.ll_sgst_container);
        tvSgst = view.findViewById(R.id.tv_sgst);
        llCgst = view.findViewById(R.id.ll_cgst_container);
        tvCgst = view.findViewById(R.id.tv_cgst);
        llOrderAmountContainer = view.findViewById(R.id.ll_total_order_container);
        tvOrderAmount = view.findViewById(R.id.tv_total_order);
        ivOrderStatus = view.findViewById(R.id.iv_status);
        tvOrderWalletListHeader = view.findViewById(R.id.tv_payment_list_header);
        rlvOrderWalletList = view.findViewById(R.id.rlv_order_wallet_lists);
        ivProcessed = view.findViewById(R.id.iv_processed);
        tvNoQrCodeOrderStatusContent = view.findViewById(R.id.tv_no_qrcode_order_status_content);
        ivNoQrCodeProcessed = view.findViewById(R.id.iv_no_qrcode_processed);
        rlNoQrCodeOrderStatusContent = view.findViewById(R.id.rl_no_qrcode_order_status_content);
        rlOrderStatusContent = view.findViewById(R.id.rl_order_status_content);
        pickupText = view.findViewById(R.id.pickup_text);
        entryTimingLayout = view.findViewById(R.id.cl_entry_timing);
        ivEntryTick = view.findViewById(R.id.iv_entry_allowed);
        tvEntryText = view.findViewById(R.id.tv_entry_text);
        tvEntryTiming = view.findViewById(R.id.tv_entry_time);

        rvProductList.setLayoutManager(new CustomNonScrollingLayoutManager(getActivity()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1){
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //define span size for this position
                    return 1;
            }
        });
        rlvOrderWalletList.setLayoutManager(gridLayoutManager);
//        rlvOrderWalletList.setLayoutManager(new CustomNonScrollingLayoutManager(getActivity()));

        refreshLayout = view.findViewById(R.id.srl_history);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderDetail();
            }
        });

        ((OrderDetailActvity) getActivity()).runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    if (!isAdd) {
                        return;
                    }

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
        };
        spannableString = new SpannableString("Your Order reference will be generated \n once the order is ready");
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),5,21, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        showFeedbackDialog();
        return view;
    }

    public void showFeedbackDialog(){
        if (orderDetailActivity!=null && AppUtils.isSocialDistancingActive(currentOrder.getLocation())){
            if (orderDetailActivity.forFeedback && orderDetailActivity.notificationTime>0  && orderDetailActivity.orderRef!=null && !orderDetailActivity.orderRef.equalsIgnoreCase("")){
                String title = "Have you left the cafeteria?";
                String body = "This will help us in reducing congestion";
                if (orderDetailActivity.notifTitle!=null && orderDetailActivity.notifBody!=null){
                    title = orderDetailActivity.notifTitle;
                    body = orderDetailActivity.notifBody;
                }
                GenericFragmentWithTitle genericPopUpFragment = GenericFragmentWithTitle.newInstance(title,body, "Yes", "No", new GenericFragmentWithTitle.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        submitFeedback(orderDetailActivity.orderRef,orderDetailActivity.notificationTime,"yes");
                    }

                    @Override
                    public void onNegativeInteraction() {
                        submitFeedback(orderDetailActivity.orderRef,orderDetailActivity.notificationTime,"no");
                    }
                });
                genericPopUpFragment.show(orderDetailActivity.getSupportFragmentManager(), "dialog");
            }
        }
    }
    private void submitFeedback(String orderId, long notificationTime,String response) {
        String url = UrlConstant.FEEDBACK_ETA+"?order-id="+orderId+"&notification-time="+notificationTime
                +"&response-time="+ DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis()
                +"&response="+response;

        SimpleHttpAgent<FeedbackEta> feedbackRatingReasonResponseSimpleHttpAgent = new SimpleHttpAgent<FeedbackEta>(
                MainApplication.appContext,
                url,
                new ResponseListener<FeedbackEta>() {
                    @Override
                    public void response(FeedbackEta responseObject) {
                        if (responseObject != null && responseObject.getSuccess()) {
                            Log.d(TAG, "response: feedback response submitted successfully");
                        } else{
                            Log.d(TAG, "response: Error in submitting feedback response via notification");
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        Log.d(TAG, "handleError: Error in submitting feedback response via notification");
                    }
                },
                FeedbackEta.class
        );

        feedbackRatingReasonResponseSimpleHttpAgent.get();
    }


    public void getOrderDetail() {

        refreshLayout.setRefreshing(false);
        //with_items=bool, with_vendor=bool, with_user=bool, with_rating=bool
        String url;
        if(isArchived){
            url = UrlConstant.ARCHIVED_ORDER_DETAIL + orderId + "/1/1/0/1";
        }
        else{
            url = UrlConstant.ORDER_DETAIL + orderId + "/1/1/0/1";
        }
        SimpleHttpAgent<OrderResponse> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
                getActivity(),
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {

                        if (responseObject == null) {
                            rlProgressBar.setVisibility(View.GONE);
                            if (getActivity() != null)
                                AppUtils.showToast("Unable to fetch Your Order", false, 0);
                        } else {
                            rlProgressBar.setVisibility(View.GONE);
                            orderDetailActivity.setOrder(responseObject.order);
                            currentOrder = responseObject.order;
                            updateOrderView(responseObject.order);
                            updateProductList(responseObject.order.getProducts());
                            updateGroupUser(responseObject.order);
                            //todo verify once
                            updateOrderWallets(responseObject.order);
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        rlProgressBar.setVisibility(View.GONE);
                        if (getActivity() != null)
                            AppUtils.showToast("Unable to fetch Your Order", false, 0);
                    }
                },
                OrderResponse.class
        );
        orderResponseSimpleHttpAgent.get();
    }

    private void updateGroupUser(Order order){
        if(order!=null && order.groupUser!=null && order.groupUser.getUsers()!=null&& order.groupUser.getUsers().size()>0){

            tvGroupUsers.setVisibility(View.VISIBLE);
            StringBuilder groupUser = new StringBuilder("Group : ");

            for(int i = 0; i<order.groupUser.getUsers().size();i++){
                if(i>0){
                    groupUser.append(", ");
                }
                groupUser.append(order.groupUser.getUsers().get(i).getName());
            }
            tvGroupUsers.setText(groupUser);

        }else{
            tvGroupUsers.setVisibility(View.GONE);
        }
    }

    private void updateOrderWallets(Order order) {
        if (getActivity() != null) {
            if (rlvOrderWalletList.getAdapter() == null) {
                OrderWalletListAdapter orderWalletListAdapter = new OrderWalletListAdapter(getActivity(), order.getWallets().getOrderWallets());
                rlvOrderWalletList.setAdapter(orderWalletListAdapter);
            } else {
                OrderWalletListAdapter orderWalletListAdapter = (OrderWalletListAdapter) rlvOrderWalletList.getAdapter();
                orderWalletListAdapter.updateWallets(order.getWallets().getOrderWallets());
                orderWalletListAdapter.notifyDataSetChanged();
            }
            if (order.getWallets().getOrderWallets().size() == 0) {
                tvOrderWalletListHeader.setText("No Payment Method Available");
            } else {
                tvOrderWalletListHeader.setText("Payment ");
            }

        }
    }


    private void updateProductList(ArrayList<OrderProduct> products) {
        if (getActivity() == null) {
            return;
        }
        if (productListAdapter == null) {
            productListAdapter = new OrderProductSummaryListAdapter(getActivity(), products);
            rvProductList.setAdapter(productListAdapter);
        } else {
            productListAdapter.changeProducts(products);
            productListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAdd = true;
    }

    @Override
    public void onDetach() {
        isAdd = false;
        try {
            mListener = null;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        super.onDetach();
    }


    private void updateOrderView(final Order order) {

        DbHandler.getDbHandler(getActivity()).createOrder(order);

        if(getActivity() == null){
            return;
        }


        if (getActivity() != null && getActivity() instanceof OrderDetailActvity) {
            OrderDetailActvity orderDetailActvity = (OrderDetailActvity) getActivity();
            orderDetailActvity.setTitle("#" + order.getOrderId());
        }

        orderStatusView.setOrder(order,getActivity(),false);
        orderStatusView.setVisibility(View.VISIBLE);
        tvOrderStatus.setText(OrderUtil.getOrderStatusLabel(order.getOrderStatus()));
        tvOrderRef.setText("Order ID: " + order.getOrderId());
        tvOrderId.setText("Order #"+order.getOrderId());
        if(order.getProducts().size()>1) {
            tvOrderItemsCost.setText(order.getProducts().size() + " items  | \u20B9" + String.format("%.2f", order.getPrice()));
        }else{
            tvOrderItemsCost.setText(order.getProducts().size() + " item  | \u20B9" + String.format("%.2f", order.getPrice()));
        }
        if(OrderUtil.getOrderStatusImageIdForOrderDetail(order.getOrderStatus()) != 0) {
            ivOrderStatus.setVisibility(View.VISIBLE);

            ivOrderStatus.setImageResource(OrderUtil
                    .getOrderStatusImageIdForOrderDetail(order.getOrderStatus()));
            switch (order.getOrderStatus()){
                case OrderUtil.DELIVERED:
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText("Delivered");
                    tvStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case OrderUtil.HANDED_OVER:
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setText("Delivered");
                    tvStatus.setTextColor(getResources().getColor(R.color.green));
                    break;
                case OrderUtil.CANCELLED:
                    tvStatus.setText("Cancelled");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.REJECTED:
                    tvStatus.setText("Cancelled");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.NOT_COLLECTED:
                    tvStatus.setText("Not Collected");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                    break;
                case OrderUtil.PRE_ORDER:
                    tvStatus.setText("Pre Order");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                    break;
                case OrderUtil.PAYMENT_FAILED:
                    tvStatus.setText("Cancelled");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                case OrderUtil.PAYMENT_PENDING:
                    tvStatus.setText("Payment Pending");
                    tvStatus.setVisibility(View.VISIBLE);
                    tvStatus.setTextColor(getResources().getColor(R.color.red));
                    break;
                default:
                    tvStatus.setVisibility(View.GONE);
                    ivOrderStatus.setVisibility(View.GONE);

            }
        }
        else {
            tvStatus.setVisibility(View.GONE);
            ivOrderStatus.setVisibility(View.GONE);
        }
        tvTotalOrderAmount.setText(String.format("\u20B9 %.2f", order.getPrice()));
        order.setOrderFromServer(true);
        if (order.getServiceTax() > 0) {
            llServiceTax.setVisibility(View.VISIBLE);
            tvServiceTax.setText(String.format("\u20B9 %.2f", order.getServiceTax()));
        } else
            llServiceTax.setVisibility(View.GONE);
        if (order.getTax() > 0) {
            llVat.setVisibility(View.VISIBLE);
            tvVat.setText(String.format("\u20B9 %.2f", order.getTax()));
        } else
            llVat.setVisibility(View.GONE);

        if (order.getAbsContainerCharge() > 0) {
            llContainerCharge.setVisibility(View.VISIBLE);
            tvContainerCharge.setText(String.format("\u20B9 %.2f", order.getAbsContainerCharge()));
        } else
            llContainerCharge.setVisibility(View.GONE);

        if (order.getDeliveryCharge() > 0) {
            llDeliveryCharge.setVisibility(View.VISIBLE);
            tvDeliveryCharge.setText(String.format("\u20B9 %.2f",order.getDeliveryCharge()));
        } else
            llDeliveryCharge.setVisibility(View.GONE);

        if (order.getConvenienceFee() > 0) {
            llConvinenceCharge.setVisibility(View.VISIBLE);
            tvConvinenceCharge.setText(String.format("\u20B9 %.2f", order.getConvenienceFee()));
        } else
            llConvinenceCharge.setVisibility(View.GONE);

        if (order.getCgst() > 0) {
            llCgst.setVisibility(View.VISIBLE);
            tvCgst.setText(String.format("\u20B9 %.2f", order.getCgst()));
        } else
            llCgst.setVisibility(View.GONE);

        if (order.getSgst() > 0) {
            llSgst.setVisibility(View.VISIBLE);
            tvSgst.setText(String.format("\u20B9 %.2f", order.getSgst()));
        } else {
            llSgst.setVisibility(View.GONE);
        }
        if (!order.getVendor().getVendor().isRestaurant() && AppUtils.getConfig(getActivity()).isShow_gst() && order.getVendor().getVendor().getCustomerGst()>0){
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
            if (!order.getVendor().getVendor().isRestaurant() && AppUtils.getConfig(getActivity()).isShow_gst()&& order.getVendor().getVendor().getCustomerGst()>0){
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

        if(order.getOrderPickType() != null && !order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)){
            tvPickUpType.setVisibility(View.GONE);
            pickupText.setVisibility(View.GONE);

        }else{
            tvPickUpTypeBox.setVisibility(View.VISIBLE);
            pickupText.setVisibility(View.VISIBLE);
            tvPickUpType.setText(" : "+order.getOrderPickType());
        }

        if(order.getDeskReference() != null && order.getDeskReference().trim().isEmpty()){
            tvDeskRefNumber.setVisibility(View.GONE);
            deskText.setVisibility(View.GONE);
        }else{
            tvDeskRefNumberBox.setVisibility(View.VISIBLE);
            deskText.setVisibility(View.VISIBLE);
            tvDeskRefNumber.setText(" : "+order.getDeskReference());
        }

        if (order.getProjectCode()!=null && !order.getProjectCode().trim().isEmpty()){
            tvProjectCodeLabel.setVisibility(View.VISIBLE);
            tvProjectCodeNumber.setVisibility(View.VISIBLE);
            tvProjectCodeNumber.setText(" : "+order.getProjectCode());
        } else{
            tvProjectCodeLabel.setVisibility(View.GONE);
            tvProjectCodeNumber.setVisibility(View.GONE);
        }

        updateOrderWallets(order);


        long minsLeft = DateTimeUtil.getTimeLeftMins(order.getEstimatedDeliveryTime());

        setCancelButtononOrder(order);

        ivProcessed.setVisibility(View.GONE);
        ivNoQrCodeProcessed.setVisibility(View.GONE);

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)|| order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)) {
            shouldRefresh = false;
            rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            rlOrderStatusContent.setVisibility(View.VISIBLE);
            String rejectReason = order.getRejectMessage();
            String message = "Your order has been cancelled";
            if (rejectReason.length() > 0) {
                message += "\n(" + rejectReason + ")";
            }
            tvOrderStatusContent.setText(message);
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW)) {
            shouldRefresh = true;
            if (AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())){
                rlNoQrCodeOrderStatusContent.setVisibility(View.VISIBLE);
                rlOrderStatusContent.setVisibility(View.GONE);
                tvNoQrCodeOrderStatusContent.setText(spannableString);
            }
            else {
                rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
                rlOrderStatusContent.setVisibility(View.VISIBLE);
                tvOrderStatusContent.setText("Waiting for the vendor to confirm your order");
            }
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            shouldRefresh = false;
            rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            rlOrderStatusContent.setVisibility(View.VISIBLE);
            tvOrderStatusContent.setText(String.format("It will be ready by %s", new SimpleDateFormat("HH:mm").format(new Date(order.getEstimatedDeliveryTime() * 1000))));
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)) {
            shouldRefresh = true;
            rlOrderStatusContent.setVisibility(View.VISIBLE);
            if (AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())){
                rlNoQrCodeOrderStatusContent.setVisibility(View.VISIBLE);
                tvNoQrCodeOrderStatusContent.setText(spannableString);
            }else{
                rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            }
            if (minsLeft > 0) {
                if (AppUtils.getConfig(getActivity()).isShow_order_delivery_time()) {
                    tvOrderStatusContent.setText(DateTimeUtil.getTimeLeftString(order.getEstimatedDeliveryTime()));
                } else{
                    tvOrderStatusContent.setVisibility(View.INVISIBLE);
                }
            } else if (minsLeft < 0) {
                tvOrderStatusContent.setVisibility(View.INVISIBLE);
            }

        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER)) {
            rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            rlOrderStatusContent.setVisibility(View.VISIBLE);
            shouldRefresh = false;
            if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER)){
                tvOrderStatusContent.setText("Your order has been handed Over");
            }else {
                tvOrderStatusContent.setText("Your order has been collected");
            }
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {
            shouldRefresh = true;
            if (AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())){
                ivNoQrCodeProcessed.setVisibility(View.VISIBLE);
                rlOrderStatusContent.setVisibility(View.GONE);
                rlNoQrCodeOrderStatusContent.setVisibility(View.VISIBLE);
                tvNoQrCodeOrderStatusContent.setBackground(null);
                tvNoQrCodeOrderStatusContent.setText("You can collect your order now");
                tvNoQrCodeOrderStatusContent.setPadding(0,0,0,0);

            }
            else {

                tvOrderStatusContent.setText("Please collect your order");
                if (AppUtils.getConfig(getActivity()).isShow_order_delivery_time()) {
                    tvOrderStatusContent.setVisibility(View.VISIBLE);
                    ivProcessed.setVisibility(View.VISIBLE);
                } else{
                    tvOrderStatusContent.setVisibility(View.INVISIBLE);
                    ivProcessed.setVisibility(View.INVISIBLE);
                }

            }
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)) {
            shouldRefresh = false;
            rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            rlOrderStatusContent.setVisibility(View.VISIBLE);
            tvOrderStatusContent.setText("Your order was not collected");
        } else {
            shouldRefresh = false;
            rlNoQrCodeOrderStatusContent.setVisibility(View.GONE);
            tvOrderStatusContent.setVisibility(View.INVISIBLE);
        }

        if (AppUtils.getConfig(getContext()).isHide_qrcode(order.getLocation())){
            if (!isOrderQrShown && (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED))){
                isOrderQrShown = true;
                if (getActivity() instanceof OrderDetailActvity) {
                    OrderDetailActvity orderDetailActvity = (OrderDetailActvity) getActivity();
                    orderDetailActvity.showQr();
                }
            }
        }
        else {
            if (!isOrderQrShown &&
                    (order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)
                            ||
                            order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)
                            ||
                            order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW))) {
                isOrderQrShown = true;
                if (getActivity() instanceof OrderDetailActvity) {
                    OrderDetailActvity orderDetailActvity = (OrderDetailActvity) getActivity();
                    orderDetailActvity.showQr();
                }
            }
        }

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)
                ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)
                ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW)) {
            if (getActivity() instanceof OrderDetailActvity) {
                OrderDetailActvity orderDetailActvity = (OrderDetailActvity) getActivity();
                orderDetailActvity.showQrButton();
            }
        } else {
            if (getActivity() instanceof OrderDetailActvity) {
                OrderDetailActvity orderDetailActvity = (OrderDetailActvity) getActivity();
                orderDetailActvity.hideQrButton();
            }
        }

        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
            if(AppUtils.getConfig(getContext()).getDirect_cancellation() != null && !AppUtils.getConfig(getContext()).getDirect_cancellation().isEmpty()){
                orderCancellation.setVisibility(View.VISIBLE);
                orderCancellation.setOnClickListener(v -> {

                    try{
                        HashMap<String,Object> map = new HashMap<>();
                        map.put(CleverTapEvent.PropertiesNames.getUserId(),SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                        map.put(CleverTapEvent.PropertiesNames.getVendor_id(),order.getVendorId());
                        map.put(CleverTapEvent.PropertiesNames.getOrderId(),order.getId());
                        map.put(CleverTapEvent.PropertiesNames.getSource(),"Order Details");
                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCancel_order_click(),map,getActivity());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    GenericPopUpFragment genericPopUpFragment = GenericPopUpFragment.newInstance(
                            "Are you sure do you want to cancel the order ?",
                            "Confirm",
                            "Cancel",
                            new GenericPopUpFragment.OnFragmentInteractionListener() {
                                @Override
                                public void onPositiveInteraction() {
                                    startOrderCancellation(order);
                                }

                                @Override
                                public void onNegativeInteraction() {

                                }
                            }
                    );

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(genericPopUpFragment, "cancel_confirmation")
                            .commitAllowingStateLoss();
                });
            }else{
                orderCancellation.setVisibility(View.GONE);
            }
        }else{
            orderCancellation.setVisibility(View.GONE);
        }

        if (shouldRefresh) {
            startUpdateTimer();
        }
        adjustSocialDistancingFeature(order);
    }

    private void startOrderCancellation(Order order){

        String cancellationEligibilityUrl = UrlConstant.ORDER_CANCELLATION_ELIGIBILITY + "?orderRef=" + order.getOrderId() + "&user_id="+ SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID,0);

        SimpleHttpAgent<OrderCancellationEligibilityResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
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

        objectSimpleHttpAgent.get();
    }

    private void askForReasonThenCancelOrder(String orderRef){

        OrderCancellationReasonDialog orderCancellationReasonDialog = OrderCancellationReasonDialog.newInstance(reason -> callOrderCancellation(orderRef, reason));

        orderCancellationReasonDialog.setCancelable(true);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(orderCancellationReasonDialog, "order_cancellation_reason")
                .commitAllowingStateLoss();
    }

    private  void callOrderCancellation(String orderRef, String reason){

        SimpleHttpAgent<OrderCancellationResponse> objectSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                UrlConstant.ORDER_CANCELLATION,
                responseObject -> {
                    if(responseObject != null) {
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

        objectSimpleHttpAgent.post(orderCancellationInfo.toString());
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

        genericPopUpFragment.show(getActivity().getSupportFragmentManager(), "order_cancellation_dialog");
    }

    private void setCancelButtononOrder(Order order) {
        switch (order.getOrderStatus()) {
            case OrderUtil.PRE_ORDER:
            case OrderUtil.NEW:
                if (mListener != null) {
                    mListener.onFragmentInteraction(View.INVISIBLE);
                }
                break;
            case OrderUtil.CONFIRMED:
                if (order.getVendor().vendor.isBuffet()) {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(View.VISIBLE);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onFragmentInteraction(View.GONE);
                    }
                }
                break;
            default:
                if (mListener != null) {
                    mListener.onFragmentInteraction(View.GONE);
                }
        }
    }

    private void startUpdateTimer() {

        try {
            if (((OrderDetailActvity) getActivity()).handler != null) {
                ((OrderDetailActvity) getActivity()).handler.removeCallbacks(((OrderDetailActvity) getActivity()).runnable);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        try {

            long totalTime = System.currentTimeMillis() - ((OrderDetailActvity) getActivity()).activityStartTime;
            if (AppUtils.getConfig(getActivity()).getOrder_detail_refresh_time() != 0 && totalTime < 30 * 60 * 1000) {
                ((OrderDetailActvity) getActivity()).handler = new Handler();
                ((OrderDetailActvity) getActivity()).handler.postDelayed(((OrderDetailActvity) getActivity()).runnable, AppUtils.getConfig(getActivity()).getOrder_detail_refresh_time());
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int visibility);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            if (!isAdd) {
                return;
            }

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

    private void adjustSocialDistancingFeature(Order order){

        //TODO Review
        if (getActivity()!=null && AppUtils.isSocialDistancingActive(order.getLocation())){
            if (!order.getOrderPickType().equalsIgnoreCase(ORDER_TYPE_DELIVERY)) {
                if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)
                        || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)){
                    rlOrderStatusContent.setVisibility(View.GONE);
                } else{
                    rlOrderStatusContent.setVisibility(View.VISIBLE);
                }
                entryTimingLayout.setVisibility(View.VISIBLE);
                if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
                    if (order.getPreOrderDeliveryTime() != 0) {
                        ivEntryTick.setVisibility(View.GONE);
                        tvEntryText.setText("You can enter the Cafeteria at");
                        tvEntryTiming.setVisibility(View.VISIBLE);
                        tvEntryTiming.setText(new SimpleDateFormat("hh:mm a").format(new Date(order.getPreOrderDeliveryTime() * 1000)));//the time
                    } else {
                        entryTimingLayout.setVisibility(View.GONE);
                    }
                } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)
                        || order.getOrderStatus().equalsIgnoreCase(OrderUtil.CANCELLED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)
                        || order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)|| order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING)) {
                    entryTimingLayout.setVisibility(View.GONE);
                } else {
                    ivEntryTick.setVisibility(View.VISIBLE);
                    tvEntryText.setText("You can enter the Cafeteria now");
                    tvEntryTiming.setVisibility(View.GONE);
                }
            } else{
                entryTimingLayout.setVisibility(View.GONE);
            }
        } else{
            entryTimingLayout.setVisibility(View.GONE);
        }
    }
}
