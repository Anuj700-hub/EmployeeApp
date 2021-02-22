package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment.OnListFragmentInteractionListener;
import com.hungerbox.customer.offline.activityOffline.OrderDetailActvityOffline;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.OrderUtil;

import java.util.ArrayList;

public class OrderHistoryViewAdapterOffline extends RecyclerView.Adapter<OrderHistoryViewAdapterOffline.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private final String historyType;
    private final PaginationHandler paginationHandler;
    private ArrayList<Order> mValues;

    public OrderHistoryViewAdapterOffline(Activity activity, ArrayList<Order> items, PaginationHandler paginationHandler,
                                          OnListFragmentInteractionListener listener, String historyType) {
        mValues = items;
        mListener = listener;
        this.paginationHandler = paginationHandler;
        this.activity = activity;
        this.historyType = historyType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_history_list_offline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (position == mValues.size() - 1 && paginationHandler != null)
            paginationHandler.onLastItemReached();

        Order order = mValues.get(position);
        holder.tvVendorName.setText(mValues.get(position).getVendorName());
        holder.tvOrderPrice.setText(mValues.get(position).getPriceString());
        //holder.tvOrderLocation.setText(mValues.get(position).getLocation().toString());
        holder.tvOrderDate.setText(mValues.get(position).getCreatedAtString());
        holder.tvOrderProducts.setText(mValues.get(position).getProductItemList());

        holder.tvOrderLocation.setVisibility(View.GONE);


        holder.btnOrderFeedback.setVisibility(View.GONE);
        holder.rbFeedbackRating.setVisibility(View.GONE);
        holder.ivSentimentFeedback.setVisibility(View.GONE);

        int orderStatusImage = OrderUtil.getOrderStatusImageId(order.getOrderStatus());
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
                Intent intent = new Intent(activity, OrderDetailActvityOffline.class);
                intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Order History");
                intent.putExtra(ApplicationConstants.BOOKING_ID, mValues.get(position).getId());
                activity.startActivity(intent);
            }
        });
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

        private final Button btnOrderFeedback;

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
        }
    }
}
