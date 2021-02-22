package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Guest;
import com.hungerbox.customer.order.activity.AddGuestActivity;
import com.hungerbox.customer.order.adapter.viewholder.GuestViewHolder;

import java.util.ArrayList;

/**
 * Created by peeyush on 28/6/16.
 */
public class AvtiveGuestListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOADING = 2;
    public static final int ITEM = 1;
    AddGuestActivity activity;
    LayoutInflater inflater;
    ArrayList<Guest> guestArrayList;
    RecyclerView.ViewHolder loaderViewHolder;
    private boolean isLoadingFooterRemoved = false;

    public AvtiveGuestListAdapter(Activity activity, ArrayList<Guest> guestArrayList) {
        this.activity = (AddGuestActivity) activity;
        inflater = LayoutInflater.from(activity);
        this.guestArrayList = guestArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new GuestViewHolder(inflater.inflate(R.layout.guest_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        GuestViewHolder guestViewHolder = (GuestViewHolder) holder;
        Guest guest = guestArrayList.get(position);

        guestViewHolder.tvGuestName.setText(guest.getName());
        guestViewHolder.tvValidFrom.setText(guest.getValidFrom());
        guestViewHolder.tvValidTill.setText(guest.getValidTill());
        guestViewHolder.ivRemoveGuest.setVisibility(View.GONE);
        guestViewHolder.tvStatus.setText(guest.getStatus());

    }


    @Override
    public int getItemCount() {
        return guestArrayList.size();
    }


}
