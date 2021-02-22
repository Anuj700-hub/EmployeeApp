package com.hungerbox.customer.offline.fragmentOffline;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.offline.activityOffline.OrderDetailActvityOffline;
import com.hungerbox.customer.offline.adapterOffline.OrderProductSummaryListAdapterOffline;
import com.hungerbox.customer.offline.adapterOffline.OrderWalletListAdapterOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.view.CustomNonScrollingLayoutManager;
//import com.hungerbox.customer.util.view.OrderStatusView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class OrderDetailFragmentOffline extends Fragment {

    RecyclerView rvProductList;
    boolean isOrderQrShown = false;
    TextView tvOrderStatus;
    private long orderId;
    private OnFragmentInteractionListenerOffline mListener;
    private OrderProductSummaryListAdapterOffline productListAdapter;
    private TextView tvVat;
    private TextView tvServiceTax;
    private LinearLayout llServiceTax;
    private LinearLayout llVat;
    private TextView tvContainerCharge;
    private LinearLayout llContainerCharge;
    private TextView tvTotalOrderAmount;
    private TextView tvOrderStatusContent;
    private RelativeLayout  rlProgressBar;
    private LinearLayout llDeliveryCharge;
    private TextView tvDeliveryCharge, orderCancellation;
    private ImageView ivOrderStatus;
    private LinearLayout llSgst, llCgst, llConvinenceCharge;
    private TextView tvSgst, tvCgst, tvConvinenceCharge;
    private LinearLayout llOrderAmountContainer, tvDeskRefNumberBox;
    private TextView tvOrderAmount;
    private OrderDetailActvityOffline orderDetailActivity;
    private RecyclerView rlvOrderWalletList;
    private TextView tvOrderWalletListHeader;
    private TextView tvOrderRef;
    private SwipeRefreshLayout refreshLayout;
    private boolean shouldRefresh = false;
    private boolean isAdd;
//    private OrderStatusView orderStatusView;
    private ImageView ivProcessed;
    private TextView tvPickUpType, tvDeskRefNumber, deskText;


    public OrderDetailFragmentOffline() {
    }

    public static OrderDetailFragmentOffline newInstance(OrderDetailActvityOffline orderDetailActvity, long orderId, TextView orderCancellation) {
        OrderDetailFragmentOffline fragment = new OrderDetailFragmentOffline();
        fragment.orderDetailActivity = orderDetailActvity;
        fragment.orderId = orderId;
        fragment.orderCancellation = orderCancellation;
        fragment.mListener = orderDetailActvity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail_offline, container, false);
        tvPickUpType = view.findViewById(R.id.tv_pick_up_type);
        tvDeskRefNumber = view.findViewById(R.id.tv_desk_ref_number);
        tvDeskRefNumberBox = view.findViewById(R.id.tv_desk_ref_number_box);

        deskText = view.findViewById(R.id.desk_text);

        deskText.setText(AppUtils.getConfig(getContext().getApplicationContext()).getWorkstation_address());
        rlProgressBar = view.findViewById(R.id.rl_progress);
        tvOrderStatus = view.findViewById(R.id.tv_order_status);
//        orderStatusView = view.findViewById(R.id.order_status_view);
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
        rvProductList.setLayoutManager(new CustomNonScrollingLayoutManager(getActivity()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2){
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getOrderDetail();
            }
        }, 500);

        return view;
    }


    private void getOrderDetail() {

        Order order = DbHandler.getDbHandler(getActivity()).getOrder(orderId);
        rlProgressBar.setVisibility(View.GONE);
        orderDetailActivity.setOrder(order);
        updateOrderView(order);
        updateProductList(order.getProducts());
    }

    private void updateOrderWallets(Order order) {
        if (getActivity() != null) {
            if (rlvOrderWalletList.getAdapter() == null) {
                OrderWalletListAdapterOffline orderWalletListAdapter = new OrderWalletListAdapterOffline(getActivity(), order.getWallets().getOrderWallets());
                rlvOrderWalletList.setAdapter(orderWalletListAdapter);
            } else {
                OrderWalletListAdapterOffline orderWalletListAdapter = (OrderWalletListAdapterOffline) rlvOrderWalletList.getAdapter();
                orderWalletListAdapter.updateWallets(order.getWallets().getOrderWallets());
            }
            if (order.getWallets().getOrderWallets().size() == 0) {
                tvOrderWalletListHeader.setText("No Payment Method Available");
            } else {
                tvOrderWalletListHeader.setText("Payment Methods");
            }

        }
    }


    private void updateProductList(ArrayList<OrderProduct> products) {
        if (getActivity() == null) {
            return;
        }
        if (productListAdapter == null) {
            productListAdapter = new OrderProductSummaryListAdapterOffline(getActivity(), products);
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

        if(getActivity() == null){
            return;
        }


        if (getActivity() != null && getActivity() instanceof OrderDetailActvityOffline) {
            OrderDetailActvityOffline orderDetailActvity = (OrderDetailActvityOffline) getActivity();
            orderDetailActvity.setTitle("#" + order.getOrderId());
        }

//        orderStatusView.setOrder(order,getActivity(),false);
//        orderStatusView.setVisibility(View.VISIBLE);
        tvOrderStatus.setText(OrderUtil.getOrderStatusLabel(order.getOrderStatus()));
        tvOrderRef.setText("Order ID: " + order.getOrderId());
        if(OrderUtil
                .getOrderStatusImageId(order.getOrderStatus()) != 0) {
            ivOrderStatus.setVisibility(View.VISIBLE);
            ivOrderStatus.setImageResource(OrderUtil
                    .getOrderStatusImageId(order.getOrderStatus()));
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

        if (order.getPrice() > 0) {
            llOrderAmountContainer.setVisibility(View.VISIBLE);
            tvOrderAmount.setText(String.format("\u20B9 %.2f", order.getCalculateOrderProductPrice()));
        } else {
            llOrderAmountContainer.setVisibility(View.GONE);
        }

        if(order.getOrderPickType() != null && !order.getOrderPickType().equals(ApplicationConstants.PICK_UP_TYPE_DELIVERY)){
            tvPickUpType.setVisibility(View.GONE);
        }else{
            tvPickUpType.setVisibility(View.VISIBLE);
            tvPickUpType.setText(getString(R.string.pickup_type)+" : "+order.getOrderPickType());
        }

        if(order.getDeskReference() != null && order.getDeskReference().trim().isEmpty()){
            tvDeskRefNumberBox.setVisibility(View.GONE);
        }else{
            tvDeskRefNumberBox.setVisibility(View.VISIBLE);
            tvDeskRefNumber.setText(" : "+order.getDeskReference());
        }

        updateOrderWallets(order);


        long minsLeft = DateTimeUtil.getTimeLeftMins(order.getEstimatedDeliveryTime());

        mListener.onFragmentInteraction(View.VISIBLE);

        ivProcessed.setVisibility(View.GONE);

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)|| order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)) {
            String rejectReason = order.getRejectMessage();
            String message = "Your order has been cancelled";
            if (rejectReason.length() > 0) {
                message += "\n(" + rejectReason + ")";
            }
            tvOrderStatusContent.setText(message);
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NEW)) {
            shouldRefresh = true;
            tvOrderStatusContent.setText("Waiting for the vendor to confirm your order");
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER)) {
            tvOrderStatusContent.setText(String.format("It will be ready by %s", new SimpleDateFormat("HH:mm").format(new Date(order.getEstimatedDeliveryTime() * 1000))));
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.CONFIRMED)) {
            shouldRefresh = true;
            if (minsLeft > 0) {
                tvOrderStatusContent.setText(DateTimeUtil.getTimeLeftString(order.getEstimatedDeliveryTime()));
            } else if (minsLeft < 0) {
                tvOrderStatusContent.setText("Please collect your order");
            }
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)) {
            shouldRefresh = false;
            tvOrderStatusContent.setText("Your order has been collected");
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.PROCESSED)) {
            tvOrderStatusContent.setText("Please collect your order");
            ivProcessed.setVisibility(View.VISIBLE);
        } else if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED)) {
            tvOrderStatusContent.setText("Your order was not collected");
        } else {
            tvOrderStatusContent.setVisibility(View.GONE);
        }

        if (!isOrderQrShown &&
                !(order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.CANCELLED) ||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED) ||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER) ||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING) ||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED) ||
                        order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)) ) {
            isOrderQrShown = true;
            if (getActivity() instanceof OrderDetailActvityOffline) {
                OrderDetailActvityOffline orderDetailActvity = (OrderDetailActvityOffline) getActivity();
                orderDetailActvity.showQr();
            }
        }

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED)||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.REJECTED)||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.CANCELLED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.HANDED_OVER) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_PENDING) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.NOT_COLLECTED) ||
                order.getOrderStatus().equalsIgnoreCase(OrderUtil.PAYMENT_FAILED)){
            if (getActivity() instanceof OrderDetailActvityOffline) {
                OrderDetailActvityOffline orderDetailActvity = (OrderDetailActvityOffline) getActivity();
                orderDetailActvity.hideQrButton();
            }
        }
        else{
            if (getActivity() instanceof OrderDetailActvityOffline) {
                OrderDetailActvityOffline orderDetailActvity = (OrderDetailActvityOffline) getActivity();
                orderDetailActvity.showQrButton();
            }
        }

        orderCancellation.setVisibility(View.GONE);
    }

    public interface OnFragmentInteractionListenerOffline {
        void onFragmentInteraction(int visibility);
    }

}
