package com.hungerbox.customer.navmenu.fragment;

import android.app.Activity;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.navmenu.OnReorderInterface;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment.OnListFragmentInteractionListener;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.StringUtilKt;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderHistoryViewAdapter extends RecyclerView.Adapter<OrderHistoryViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private final String historyType;
    private final PaginationHandler paginationHandler;
    private ArrayList<Order> mValues;
    private final OnReorderInterface mReorderInterface;
    public OrderHistoryViewAdapter(Activity activity, ArrayList<Order> items, PaginationHandler paginationHandler,
                                   OnListFragmentInteractionListener listener, String historyType, OnReorderInterface reorderInterface) {
        mValues = items;
        mListener = listener;
        this.paginationHandler = paginationHandler;
        this.activity = activity;
        this.historyType = historyType;
        this.mReorderInterface = reorderInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position == mValues.size() - 1 && paginationHandler != null)
            paginationHandler.onLastItemReached();

        Order order = mValues.get(position);
        holder.tvVendorName.setText(mValues.get(position).getVendorName());
        holder.tvVendorName.setSelected(true);
        holder.tvOrderPrice.setText(mValues.get(position).getPriceString());
        holder.tvOrderLocation.setText(mValues.get(position).getLocation().toString());
        holder.tvOrderDate.setText(mValues.get(position).getCreatedAtString());
        holder.tvOrderProducts.setText(mValues.get(position).getProductItemList());

        if(order.getOrderStatus().equalsIgnoreCase(OrderUtil.PRE_ORDER) && !AppUtils.isSocialDistancingActive(order.getLocation())){
            holder.deliveryTimeText.setVisibility(View.VISIBLE);
            holder.deliveryTimeText.setText("Delivery Time: " + DateTimeUtil.getDateStringCustom(order.getPreOrderDeliveryTime() * 1000, "dd MMM, hh:mm aa"));
        }
        else{
            holder.deliveryTimeText.setVisibility(View.GONE);
        }

        int orderStatusImage = OrderUtil.getOrderStatusImageId(order.getOrderStatus());

        if(order.isSpaceBookingOrder()){

            holder.tvVendorName.setText(order.getProducts().get(0).getName());
            holder.tvOrderLocation.setText(order.getLocation().getName()+","+order.getLocation().getAddressLine1()+","+order.getLocation().getCityName());
            orderStatusImage = OrderUtil.isFinalState(order.getOrderStatus())? OrderUtil.getOrderStatusImageId(order.getOrderStatus()):R.drawable.booking_open_status;

            if(AppUtils.getSpaceConfig(activity)!=null && MainApplication.selectedOcassionId>0){

                Config.SpaceType spaceType = AppUtils.getSpaceType(activity,order.getLocation().getType());
                if(spaceType!=null)
                    holder.tvOrderProducts.setText(StringUtilKt.UpperCaseFirstLetter(spaceType.getSpace_name())+" Booking For "+order.getQuantity());


                if(AppUtils.getSpaceConfig(activity).isShow_rebook() && OrderUtil.isFinalState(order.getOrderStatus())){
                    holder.btnReorder.setVisibility(View.VISIBLE);
                    holder.btnReorder.setText("Rebook");
                    order.reorderType = 2;
                }else if(AppUtils.getSpaceConfig(activity).isShow_extend_booking() && !OrderUtil.isFinalState(order.getOrderStatus())){
                    holder.btnReorder.setVisibility(View.VISIBLE);
                    holder.btnReorder.setText("Extend Booking");
                    order.reorderType = 3;
                }else{
                    holder.btnReorder.setVisibility(View.GONE);
                }
            }else{
                holder.btnReorder.setVisibility(View.GONE);
            }

        }
        else {
            if (AppUtils.getConfig(activity).getReordering() != null && AppUtils.getConfig(activity).getReordering().isReordering_enabled() && order.isReorderAvailable()
            ) {
                holder.btnReorder.setText("reorder");
                if (AppUtils.getConfig(activity).getReordering().getStatus() == null || AppUtils.getConfig(activity).getReordering().getStatus().isEmpty()) {
                    if (!isSlotBookingOrder(order)) {
                        holder.btnReorder.setVisibility(View.VISIBLE);
                        order.reorderType = 1;
                    } else {
                        holder.btnReorder.setVisibility(View.GONE);
                    }
                } else {
                    if ((AppUtils.getConfig(activity).getReordering().getStatus().contains(order.getOrderStatus())) && !isSlotBookingOrder(order)) {
                        holder.btnReorder.setVisibility(View.VISIBLE);
                        order.reorderType = 1;
                    } else {
                        holder.btnReorder.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.btnReorder.setVisibility(View.GONE);
            }
        }

        if (order.getOrderStatus().equalsIgnoreCase(OrderUtil.DELIVERED) || order.getOrderStatus().equalsIgnoreCase(OrderUtil.FULFILLED)) {
            if (mValues.get(position).getRating() == null) {
                //todo new visibility logic
                holder.btnOrderFeedback.setVisibility(View.VISIBLE);
                if(AppUtils.getConfig(activity).getOrder_feedback_time_limit()== 0 || ((DateTimeUtil.adjustCalender(MainApplication.appContext).getTimeInMillis() - order.getDeliveredAt()*1000) <= AppUtils.getConfig(activity).getOrder_feedback_time_limit())){
                    holder.btnOrderFeedback.setVisibility(View.VISIBLE);
                }
                else {
                    holder.btnOrderFeedback.setVisibility(View.GONE);
                }
                holder.rbFeedbackRating.setVisibility(View.GONE);
                holder.ivSentimentFeedback.setVisibility(View.GONE);
            } else {

                holder.btnOrderFeedback.setVisibility(View.GONE);
                if(AppUtils.getConfig(activity).isSentiment_feedback()) {

                    holder.ivSentimentFeedback.setVisibility(View.VISIBLE);
                    holder.rbFeedbackRating.setVisibility(View.GONE);

                    int rating = Integer.valueOf(mValues.get(position).getRating());
                    holder.ivSentimentFeedback.setImageResource(OrderUtil.getSentimentReviewImage(rating));
                    //holder.ivSentimentFeedback.setColorFilter(ContextCompat.getColor(activity,OrderUtil.getSentimentReviewColor(rating)));

                }else{

                    holder.rbFeedbackRating.setVisibility(View.VISIBLE);
                    holder.ivSentimentFeedback.setVisibility(View.GONE);

                    holder.rbFeedbackRating.setNumStars(Integer.valueOf(mValues.get(position).getRating()));
                    holder.rbFeedbackRating.setRating(Integer.valueOf(mValues.get(position).getRating()));
                }
            }
        } else {
            holder.btnOrderFeedback.setVisibility(View.GONE);
            holder.rbFeedbackRating.setVisibility(View.GONE);
            holder.ivSentimentFeedback.setVisibility(View.GONE);
        }


        if(orderStatusImage != 0){
            holder.tvOrderStatus.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setImageResource(orderStatusImage);
        }
        else{
            holder.tvOrderStatus.setVisibility(View.INVISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OrderDetailNewActivity.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order History");
                intent.putExtra(ApplicationConstants.BOOKING_ID, mValues.get(position).getId());
                intent.putExtra(ApplicationConstants.IS_ORDER_ARCHIVED, mValues.get(position).isArchived());
                activity.startActivity(intent);
            }
        });

        holder.btnOrderFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(null, historyType, mValues.get(position));
                }
            }
        });

        holder.btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReorderInterface!=null){
                    mReorderInterface.getMenu(mValues.get(position));
                }

//                try{
//                    HashMap<String,Object> map = new HashMap<>();
//                    map.put(CleverTapEvent.PropertiesNames.getOrderId(), mValues.get(position).getOrderId());
//                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getReorder_click(),map,activity);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            }
        });
    }
    private boolean isSlotBookingOrder(Order order){
        if (order!=null && order.vendor!=null && order.vendor.vendor!=null
                && order.vendor.vendor.isSlotBookingVendor()){
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public void udpateOrders(ArrayList<Order> newOrders) {
        this.mValues = newOrders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvVendorName, tvOrderPrice, tvOrderLocation, tvOrderProducts, tvOrderDate;
        public final ImageView tvOrderStatus,ivSentimentFeedback;
        public final RatingBar rbFeedbackRating;

        private final Button btnOrderFeedback, btnReorder, deliveryTimeText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvVendorName = view.findViewById(R.id.tv_order_vendor_name);
            tvOrderPrice = view.findViewById(R.id.tv_order_price);
            tvOrderLocation = view.findViewById(R.id.tv_order_location);
            tvOrderProducts = view.findViewById(R.id.tv_order_products);
            tvOrderDate = view.findViewById(R.id.tv_order_date);
            btnOrderFeedback = view.findViewById(R.id.btn_order_feedback);
            tvOrderStatus = view.findViewById(R.id.tv_order_status);
            rbFeedbackRating = view.findViewById(R.id.rb_feedback_rating);
            ivSentimentFeedback = view.findViewById(R.id.iv_sentiment_feedback);
            btnReorder = view.findViewById(R.id.btn_reorder);
            deliveryTimeText = view.findViewById(R.id.delivery_date);
        }
    }
}
