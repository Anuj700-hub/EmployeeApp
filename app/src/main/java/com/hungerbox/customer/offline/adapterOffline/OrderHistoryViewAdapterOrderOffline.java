package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.offline.activityOffline.OrderQrCodeActivityOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.OrderUtil;

import java.util.List;

public class OrderHistoryViewAdapterOrderOffline extends RecyclerView.Adapter<OrderHistoryViewAdapterOrderOffline.ViewHolder> {

    private final Activity activity;
    private final String historyType;
    private List<OrderOffline> mValues;

    public OrderHistoryViewAdapterOrderOffline(Activity activity, List<OrderOffline> items, String historyType) {
        mValues = items;
        this.activity = activity;
        this.historyType = historyType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history_list_order_offline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewAdapterOrderOffline.ViewHolder holder, final int position) {

        final OrderOffline order = mValues.get(position);
        try {
            holder.tvVendorName.setText(mValues.get(position).vendorName);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.tvOrderPrice.setText(roundToTwoDigit(mValues.get(position).getTotalPrice())+"");
//        holder.tvOrderLocation.setText(mValues.get(position).getLocation().toString());
        holder.tvOrderDate.setText(mValues.get(position).getCreatedAtString());
        holder.tvOrderProducts.setText(mValues.get(position).getProductItemList());


        int orderStatusImage = OrderUtil.getOrderStatusImageId(order.getOrderStatus());
        if (orderStatusImage != 0) {
            holder.tvOrderStatus.setVisibility(View.VISIBLE);
            holder.tvOrderStatus.setImageResource(orderStatusImage);
        } else {
            holder.tvOrderStatus.setVisibility(View.INVISIBLE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OrderQrCodeActivityOffline.class);
                intent.putExtra("fromHistory",true);
                intent.putExtra(ApplicationConstants.ORDER,order);
                intent.putExtra(ApplicationConstants.BOOKING_ID, mValues.get(position).getId());
                activity.startActivity(intent);
            }
        });

    }

    private double roundToTwoDigit(double price){
        return (double) Math.round(price * 100) / 100;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public void udpateOrders(List<OrderOffline> newOrders) {
        mValues = newOrders;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvVendorName, tvOrderPrice, tvOrderLocation, tvOrderProducts, tvOrderDate;
        public final ImageView tvOrderStatus, ivSentimentFeedback;
        public final RatingBar rbFeedbackRating;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvVendorName = view.findViewById(R.id.tv_order_vendor_name);
            tvOrderPrice = view.findViewById(R.id.tv_order_price);
            tvOrderLocation = view.findViewById(R.id.tv_order_location);
            tvOrderProducts = view.findViewById(R.id.tv_order_products);
            tvOrderDate = view.findViewById(R.id.tv_order_date);
            tvOrderStatus = view.findViewById(R.id.tv_order_status);
            rbFeedbackRating = view.findViewById(R.id.rb_feedback_rating);
            ivSentimentFeedback = view.findViewById(R.id.iv_sentiment_feedback);
        }
    }
}
