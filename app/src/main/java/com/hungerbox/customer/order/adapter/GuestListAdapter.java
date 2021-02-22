package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Guests;
import com.hungerbox.customer.order.activity.AddGuestActivity;
import com.hungerbox.customer.order.adapter.viewholder.GuestViewHolder;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by peeyush on 28/6/16.
 */
public class GuestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOADING = 2;
    public static final int ITEM = 1;
    AddGuestActivity activity;
    LayoutInflater inflater;
    ArrayList<Guests> guestArrayList;
    RecyclerView.ViewHolder loaderViewHolder;
    OnGuestRemoveInterface onGuestRemoveInterface;
    private boolean isLoadingFooterRemoved = false;

    public GuestListAdapter(Activity activity, ArrayList<Guests> guestArrayList, OnGuestRemoveInterface onGuestRemoveInterface) {
        this.activity = (AddGuestActivity) activity;
        inflater = LayoutInflater.from(activity);
        this.guestArrayList = guestArrayList;
        this.onGuestRemoveInterface = onGuestRemoveInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new GuestViewHolder(inflater.inflate(R.layout.guest_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        GuestViewHolder guestViewHolder = (GuestViewHolder) holder;
        Guests guest = guestArrayList.get(position);

        Calendar validFrom = Calendar.getInstance();
        validFrom.setTimeInMillis(guest.getValidFrom() * 1000);
        Calendar validTill = Calendar.getInstance();
        validTill.setTimeInMillis(guest.getValidTill() * 1000);

        guestViewHolder.tvGuestName.setText(guest.getName());
        guestViewHolder.tvValidFrom.setText(validFrom.get(Calendar.DAY_OF_MONTH) + "-" +
                (validFrom.get(Calendar.MONTH) + 1) + "-" + validFrom.get(Calendar.YEAR));
        guestViewHolder.tvValidTill.setText(validTill.get(Calendar.DAY_OF_MONTH) + "-" +
                (validTill.get(Calendar.MONTH) + 1) + "-" + validTill.get(Calendar.YEAR));
        guestViewHolder.ivRemoveGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuestRemoveInterface.onGuestRemoveInterface(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return guestArrayList.size();
    }

    public interface OnGuestRemoveInterface {
        void onGuestRemoveInterface(int position);
    }
}
