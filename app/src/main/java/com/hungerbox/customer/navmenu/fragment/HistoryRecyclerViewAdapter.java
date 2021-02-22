package com.hungerbox.customer.navmenu.fragment;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.booking.BookingDetailActvity;
import com.hungerbox.customer.model.BookingHistory;
import com.hungerbox.customer.navmenu.fragment.HistoryFragment.OnListFragmentInteractionListener;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;


import java.text.DateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BookingHistory} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private final List<BookingHistory> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private final String historyType;

    public HistoryRecyclerViewAdapter(Activity activity, List<BookingHistory> items,
                                      OnListFragmentInteractionListener listener, String historyType) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
        this.historyType = historyType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvAddress.setText(holder.mItem.getAddress());
        if (historyType.equalsIgnoreCase("meeting")) {
            holder.tvDate.setText("Date : " + holder.mItem.getDate());
            holder.tvName.setText(holder.mItem.getName());
        } else {
            holder.tvName.setText(holder.mItem.getEventName());
            holder.tvDate.setText("Date : " + holder.mItem.getEventStartDate() + " - " + holder.mItem.getEventEndDate());
        }
        holder.tvTime.setText("Time : " + holder.mItem.getStartTime() + " to " + holder.mItem.getEndTime());
        if (historyType.equalsIgnoreCase("meeting"))
            ImageHandling.loadRemoteImage(holder.mItem.getImage(), holder.ivBooking, -1, R.drawable.sharedeconomy, activity);
        else
            ImageHandling.loadRemoteImage(holder.mItem.getImage(), holder.ivBooking, -1, R.drawable.bookevents, activity);


        if (holder.mItem.getCreatedAt() != null) {
            DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(activity);
            holder.tvCreatedAt.setText(dateFormat.format(holder.mItem.getCreatedAt()));
        }
        if (holder.mItem.getRatingsTaken() == 0) {
            holder.btnBookingFeedback.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);
        } else {
            holder.btnBookingFeedback.setVisibility(View.GONE);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Rated " + holder.mItem.getRating() + "\u2605");
        }

        holder.eventMeetingStatus.setText(OrderUtil.getOrderStatusLabel(holder.mItem.getBookingStatus()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, BookingDetailActvity.class);
                intent.putExtra("historyType", historyType);
                intent.putExtra(ApplicationConstants.BOOKING_ID, holder.mItem.getBookingId());

                activity.startActivity(intent);
            }
        });

        holder.btnBookingFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, historyType, null);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvName, tvAddress, tvDate, tvTime, tvStatus, tvCreatedAt;
        public final ImageView ivBooking;
        public final TextView eventMeetingStatus;
        private final Button btnBookingFeedback;
        public BookingHistory mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = view.findViewById(R.id.tv_booking_name);
            tvAddress = view.findViewById(R.id.tv_booking_location);
            tvDate = view.findViewById(R.id.tv_booking_date);
            tvTime = view.findViewById(R.id.tv_booking_time);
            tvStatus = view.findViewById(R.id.tv_booking_status);
            tvCreatedAt = view.findViewById(R.id.tv_created_at);
            ivBooking = view.findViewById(R.id.iv_booking);
            eventMeetingStatus = view.findViewById(R.id.tv_event_meeting_status);
            btnBookingFeedback = view.findViewById(R.id.btn_booking_feedback);
        }


    }
}
