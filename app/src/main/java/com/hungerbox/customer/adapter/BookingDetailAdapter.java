package com.hungerbox.customer.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.BookingDetailSlot;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by peeyush on 22/6/16.
 */
public class BookingDetailAdapter extends RecyclerView.Adapter<BookingDetailTimeViewHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    ArrayList<BookingDetailSlot> bookingDetailSlots = new ArrayList<>();

    BookingSlotListener mListener;

    public BookingDetailAdapter(Activity activty, ArrayList<BookingDetailSlot> bookingDetailSlots, BookingSlotListener listener) {
        this.activity = activty;
        this.bookingDetailSlots = bookingDetailSlots;
        layoutInflater = LayoutInflater.from(activty);

        mListener = listener;
    }


    @Override
    public BookingDetailTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookingDetailTimeViewHolder(layoutInflater.inflate(R.layout.booking_detail__time_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BookingDetailTimeViewHolder holder, final int position) {
        final BookingDetailSlot bookingDetailSlot = bookingDetailSlots.get(position);
        holder.tvTime.setText(String.format("%s %s-%s", bookingDetailSlot.getHumanDate(), bookingDetailSlot.getStartTime(), bookingDetailSlot.getEndTime()));
        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    if (bookingDetailSlot.isCancelable())
                        mListener.onCancelSlot(position, bookingDetailSlot.getId());
                    else
                        AppUtils.showToast("You cant cancel this slot", true, 0);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return bookingDetailSlots.size();
    }

    public interface BookingSlotListener {
        void onCancelSlot(int position, long slotId);
    }

}
